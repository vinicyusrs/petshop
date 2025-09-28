package petshop.controller;

import static org.mockito.ArgumentMatchers.any;
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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import petshop.controllerApiRest.PetControllerApi;
import petshop.dto.PetDTO;
import petshop.dto.ClienteResumoDTO;
import petshop.service.PetService;

@WebMvcTest(PetControllerApi.class)
public class PetControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarPetsDTO() throws Exception {
        PetDTO pet1 = new PetDTO(1L, "Rex", "Cachorro", "Labrador", 5, 20.0,
                new ClienteResumoDTO(1L, "João"));
        PetDTO pet2 = new PetDTO(2L, "Mimi", "Gato", "Persa", 3, 5.0,
                new ClienteResumoDTO(2L, "Maria"));
        List<PetDTO> pets = Arrays.asList(pet1, pet2);

        when(petService.listarTodosDTO()).thenReturn(pets);

        mockMvc.perform(get("/api/pets"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nome").value("Rex"))
               .andExpect(jsonPath("$[1].nome").value("Mimi"));
    }

    @Test
    void testCriarPetDTO() throws Exception {
        PetDTO petDTO = new PetDTO(1L, "Rex", "Cachorro", "Labrador", 5, 20.0,
                new ClienteResumoDTO(1L, "João"));

        when(petService.salvarDTO(any(PetDTO.class))).thenReturn(petDTO);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
               .andExpect(status().isOk()) // se no controller você usa CREATED, troque para isCreated()
               .andExpect(jsonPath("$.nome").value("Rex"));
    }

    @Test
    void testAtualizarPetDTO() throws Exception {
        PetDTO petDTO = new PetDTO(1L, "Rex", "Cachorro", "Labrador", 5, 20.0,
                new ClienteResumoDTO(1L, "João"));

        when(petService.atualizarDTO(1L, petDTO)).thenReturn(petDTO);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nome").value("Rex"));
    }

    @Test
    void testDeletarPetDTO() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
               .andExpect(status().isOk()); // ou isNoContent(), dependendo do seu controller

        verify(petService, times(1)).deletarDTO(1L);
    }
}
