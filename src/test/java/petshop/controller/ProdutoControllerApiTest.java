package petshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import petshop.controllerApiRest.ProdutoControllerApi;
import petshop.dto.ProdutoDTO;
import petshop.enums.TipoProdutoEnum;
import petshop.model.Produto;
import petshop.service.ProdutoService;

@WebMvcTest(ProdutoControllerApi.class)
class ProdutoControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCriarProdutoDTO() throws Exception {
        ProdutoDTO dto = new ProdutoDTO(1L, "Racao", "Alimento para cães", TipoProdutoEnum.PRODUTO, 50.0, 10, "FornecedorX");

        // Mock do método correto do service
        when(produtoService.salvarProdutoDTO(any())).thenReturn(dto);

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nome").value("Racao"))
               .andExpect(jsonPath("$.tipo").value("PRODUTO"));
    }

    @Test
    void testAtualizarProdutoDTO() throws Exception {
        ProdutoDTO dto = new ProdutoDTO(1L, "Coleira", "Coleira de cachorro", TipoProdutoEnum.PRODUTO, 30.0, 5, "PetShopX");

        // Mock: retorna Produto genérico ao buscar
        when(produtoService.buscarProdutoPorId(1L)).thenReturn(Optional.of(new Produto()));
        // Mock: atualizarProdutoDTO retorna o DTO
        when(produtoService.atualizarProdutoDTO(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/produtos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nome").value("Coleira"))
               .andExpect(jsonPath("$.tipo").value("PRODUTO"));
    }

    @Test
    void testListarProdutosDTO() throws Exception {
        ProdutoDTO dto1 = new ProdutoDTO(1L, "Ração", "Alimento", TipoProdutoEnum.PRODUTO, 50.0, 10, "FornecedorX");
        ProdutoDTO dto2 = new ProdutoDTO(2L, "Banho", "Serviço de banho", TipoProdutoEnum.SERVICO, 30.0, 0, "PetShopX");

        when(produtoService.listarProdutosDTO()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/produtos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nome").value("Ração"))
               .andExpect(jsonPath("$[1].tipo").value("SERVICO"));
    }

    @Test
    void testDeletarProdutoDTO() throws Exception {
        mockMvc.perform(delete("/api/produtos/1"))
               .andExpect(status().isNoContent());

        verify(produtoService, times(1)).deletarProdutoDTO(1L);
    }
}

