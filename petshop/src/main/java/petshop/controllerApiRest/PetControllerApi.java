package petshop.controllerApiRest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petshop.dto.PetDTO;
import petshop.service.PetService;

@RestController
@RequestMapping("/api/pets")
public class PetControllerApi {

    @Autowired
    private PetService petService;

    // Buscar pets por nome, raça ou cliente
    @GetMapping
    public List<PetDTO> listarTodosDTO() {
        return petService.listarTodosDTO();
    }

    @GetMapping("/{id}")
    public PetDTO buscarPorIdDTO(@PathVariable Long id) {
        return petService.buscarPorIdDTO(id);
    }

    @PostMapping
    public PetDTO salvarDTO(@RequestBody PetDTO petDTO) {
        return petService.salvarDTO(petDTO);
    }

    @PutMapping("/{id}")
    public PetDTO atualizarDTO(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        return petService.atualizarDTO(id, petDTO);
    }

    @DeleteMapping("/{id}")
    public void deletarDTO(@PathVariable Long id) {
        petService.deletarDTO(id);
    }
}
