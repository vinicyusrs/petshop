package petshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import petshop.controllerApiRest.PedidoControllerApi;
import petshop.dto.PedidoDTO;
import petshop.enums.StatusPedidoEnum;
import petshop.mapper.PedidoMapper;
import petshop.model.ItemPedido;
import petshop.model.Pedido;
import petshop.service.ItemPedidoService;
import petshop.service.PedidoService;

class PedidoControllerApiTest {

	@InjectMocks
    private PedidoControllerApi controller;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private ItemPedidoService itemPedidoService;

    private Pedido pedido;
    private PedidoDTO pedidoDTO;

    private MockedStatic<PedidoMapper> mockedMapper; // manter como campo

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criar ItemPedido com preço
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(50.0); // define algum valor para os testes

        // Pedido real
        pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setStatus(StatusPedidoEnum.PENDENTE);
        pedido.setItens(Arrays.asList(item));
        pedido.setValorTotal(50.0);

        // Mock estático do mapper
        mockedMapper = mockStatic(PedidoMapper.class);

        // toDTO mockado
        mockedMapper.when(() -> PedidoMapper.toDTO(any(Pedido.class)))
                    .thenAnswer(invocation -> {
                        Pedido arg = invocation.getArgument(0);
                        return new PedidoDTO(
                            arg.getIdPedido(),
                            null,
                            null,
                            arg.getStatus() != null ? arg.getStatus().name() : null,
                            arg.getValorTotal(),
                            arg.getItens() != null ? List.of() : List.of()
                        );
                    });

        // fromDTO mockado
        mockedMapper.when(() -> PedidoMapper.fromDTO(any(PedidoDTO.class)))
                    .thenAnswer(invocation -> {
                        PedidoDTO dto = invocation.getArgument(0);
                        Pedido p = new Pedido();
                        p.setIdPedido(dto.idPedido());
                        p.setStatus(dto.status() != null ? StatusPedidoEnum.valueOf(dto.status()) : null);

                        ItemPedido i = new ItemPedido();
                        i.setPrecoUnitario(50.0);
                        i.setQuantidade(2);// também precisa definir aqui
                        p.setItens(List.of(i));

                        p.setValorTotal(dto.valorTotal());
                        return p;
                    });

        // DTO usando mock
        pedidoDTO = PedidoMapper.toDTO(pedido);

        // Mock comportamento dos serviços
        when(pedidoService.listarPedidos()).thenReturn(List.of(pedido));
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pedidoService.salvarPedido(any(Pedido.class))).thenReturn(pedido);
    }

    @Test
    void testListarPedidos() {
        when(pedidoService.listarPedidos()).thenReturn(Arrays.asList(pedido));

        ResponseEntity<List<PedidoDTO>> response = controller.listarPedidos();

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).idPedido()).isEqualTo(1L);
        verify(pedidoService, times(1)).listarPedidos();
    }

    @Test
    void testBuscarPorId_Found() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.of(pedido));

        ResponseEntity<PedidoDTO> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().idPedido()).isEqualTo(1L);
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<PedidoDTO> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testCriarPedido() {
        when(pedidoService.salvarPedido(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<PedidoDTO> response = controller.criarPedido(pedidoDTO);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().idPedido()).isEqualTo(1L);
        verify(itemPedidoService, times(1)).salvarTodos(anyList());
    }

    @Test
    void testAtualizarPedido_Found() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pedidoService.salvarPedido(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<PedidoDTO> response = controller.atualizarPedido(1L, pedidoDTO);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().idPedido()).isEqualTo(1L);
        verify(itemPedidoService, times(1)).salvarTodos(anyList());
    }

    @Test
    void testAtualizarPedido_NotFound() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<PedidoDTO> response = controller.atualizarPedido(1L, pedidoDTO);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void testDeletarPedido_Found() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.of(pedido));

        ResponseEntity<Void> response = controller.deletarPedido(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(pedidoService, times(1)).deletarPedido(1L);
    }

    @Test
    void testDeletarPedido_NotFound() {
        when(pedidoService.buscarPedidoPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deletarPedido(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
    
    @AfterEach
    void tearDown() {
        mockedMapper.close();
    }

}
