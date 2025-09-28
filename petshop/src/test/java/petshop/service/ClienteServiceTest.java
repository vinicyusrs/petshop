package petshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petshop.dto.ClienteDTO;
import petshop.model.Cliente;
import petshop.model.Pet;
import petshop.model.Pedido;
import petshop.repository.ClienteRepository;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("Vinicyus");
        cliente.setCpf("12345678900");
        cliente.setTelefone("11999999999");
        cliente.setEmail("teste@teste.com");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setPets(List.of(new Pet()));
        cliente.setPedidos(List.of(new Pedido()));
    }

    @Test
    void testListarTodosDTO() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteDTO> listaDTO = clienteService.listarTodosDTO();

        assertEquals(1, listaDTO.size());
        assertEquals("Vinicyus", listaDTO.get(0).nome());
    }

    @Test
    void testBuscarPorIdDTO() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<ClienteDTO> clienteOpt = clienteService.buscarPorIdDTO(1L);

        assertTrue(clienteOpt.isPresent());
        assertEquals("Vinicyus", clienteOpt.get().nome());
    }

    @Test
    void testSalvarClienteDTO() {
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        ClienteDTO dto = clienteService.salvarClienteDTO(cliente);

        assertNotNull(dto);
        assertEquals(cliente.getNome(), dto.nome());
    }
}
