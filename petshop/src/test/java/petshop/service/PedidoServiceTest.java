package petshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petshop.dto.PedidoDTO;
import petshop.enums.StatusPedidoEnum;
import petshop.model.Cliente;
import petshop.model.Pedido;
import petshop.repository.PedidoRepository;

class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("João");

        pedido1 = new Pedido();
        pedido1.setIdPedido(1L);
        pedido1.setCliente(cliente);
        pedido1.setStatus(StatusPedidoEnum.PENDENTE);

        pedido2 = new Pedido();
        pedido2.setIdPedido(2L);
        pedido2.setCliente(cliente);
        pedido2.setStatus(StatusPedidoEnum.ENTREGUE);
    }

    @Test
    void testSalvarPedido() {
        when(pedidoRepository.save(pedido1)).thenReturn(pedido1);
        Pedido salvo = pedidoService.salvarPedido(pedido1);
        assertEquals(pedido1, salvo);
        verify(pedidoRepository).save(pedido1);
    }

    @Test
    void testListarPedidos() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido1, pedido2));
        List<Pedido> pedidos = pedidoService.listarPedidos();
        assertEquals(2, pedidos.size());
    }

    @Test
    void testBuscarPorId() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));
        Optional<Pedido> resultado = pedidoService.buscarPedidoPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdPedido());
    }

    @Test
    void testBuscarPorClienteOuStatus() {
        when(pedidoRepository.findByClienteNomeContainingIgnoreCaseAndStatus("João", StatusPedidoEnum.PENDENTE))
            .thenReturn(Arrays.asList(pedido1));

        List<PedidoDTO> resultado = pedidoService.buscarPorClienteOuStatusDTO("João", StatusPedidoEnum.PENDENTE);
        assertEquals(1, resultado.size());
        assertEquals(StatusPedidoEnum.PENDENTE.name(), resultado.get(0).status());
    }

    @Test
    void testDeletarPedido() {
        doNothing().when(pedidoRepository).deleteById(1L);
        pedidoService.deletarPedido(1L);
        verify(pedidoRepository).deleteById(1L);
    }
}
