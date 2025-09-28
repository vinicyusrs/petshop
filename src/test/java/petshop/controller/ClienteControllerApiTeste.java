package petshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import petshop.controllerApiRest.ClienteControllerApi;
import petshop.dto.ClienteDTO;
import petshop.model.Cliente;
import petshop.service.ClienteService;

@WebMvcTest(ClienteControllerApi.class)
class ClienteControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarTodos() throws Exception {
        ClienteDTO dto = new ClienteDTO(1L, "Vinicyus", "12345678900", "11999999999",
                                        "teste@teste.com", "Rua Teste, 123");

        when(clienteService.listarTodosDTO()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clientes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nome").value("Vinicyus"))
               .andExpect(jsonPath("$[0].cpf").value("12345678900"));
    }

    @Test
    void testBuscarPorId() throws Exception {
        ClienteDTO dto = new ClienteDTO(1L, "Vinicyus", "12345678900", "11999999999",
                                        "teste@teste.com", "Rua Teste, 123");

        when(clienteService.buscarPorIdDTO(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/clientes/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nome").value("Vinicyus"))
               .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    void testCriarCliente() throws Exception {
        ClienteDTO dto = new ClienteDTO(1L, "Vinicyus", "12345678900", "11999999999",
                                        "teste@teste.com", "Rua Teste, 123");

        when(clienteService.salvarClienteDTO(any())).thenReturn(dto);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nome").value("Vinicyus"));
    }

    @Test
    void testDeletarCliente() throws Exception {
        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.ofNullable(new Cliente()));

        mockMvc.perform(delete("/api/clientes/1"))
               .andExpect(status().isNoContent());

        verify(clienteService, times(1)).deletarCliente(1L);
    }
}
