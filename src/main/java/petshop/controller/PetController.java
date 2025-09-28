package petshop.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petshop.model.Cliente;
import petshop.model.Pet;
import petshop.service.ClienteService;
import petshop.service.PetService;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private ClienteService clienteService;

    // Listagem paginada de pets
    @GetMapping
    public String listarPets(@RequestParam(defaultValue = "0") int pagina,
                             @RequestParam(defaultValue = "10") int tamanho,
                             Model model) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<Pet> petsPage = petService.listarPetsPaginados(pageRequest);
        model.addAttribute("petsPage", petsPage);
        return "pets/pets_lista";
    }

    // Formulário para novo pet
    @GetMapping("/novo")
    public String novoPet(Model model) {
        model.addAttribute("pet", new Pet());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "pets/pets_novo";
    }

    // Salvar pet
    @PostMapping("/salvar")
    public String salvarPet(@ModelAttribute("pet") Pet pet) {
        petService.salvarPet(pet);
        return "redirect:/pets";
    }

    // Editar pet
    @GetMapping("/editar/{id}")
    public String editarPet(@PathVariable Long id, Model model) {
        Optional<Pet> petOpt = petService.buscarPetPorId(id);
        if (petOpt.isPresent()) {
            model.addAttribute("pet", petOpt.get());
            List<Cliente> clientes = clienteService.listarClientes();
            model.addAttribute("clientes", clientes);
            return "pets/pets_editar";
        } else {
            return "redirect:/pets";
        }
    }
    
    @GetMapping("/buscar")
    public String buscarPets(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String cliente,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            Model model) {

    	 // Remove espaços em branco no início e no fim
        if (busca != null) {
            busca = busca.trim();
        }
        if (cliente != null) {
            cliente = cliente.trim();
        }
        
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<Pet> petsPage;

        if ((busca != null && !busca.isEmpty()) && (cliente != null && !cliente.isEmpty())) {
            petsPage = petService.buscarPorNomeRacaECliente(busca, cliente, pageRequest);
        } else if (busca != null && !busca.isEmpty()) {
            petsPage = petService.buscarPetsPaginadosPorNomeOuRaca(busca, pageRequest);
        } else if (cliente != null && !cliente.isEmpty()) {
            petsPage = petService.buscarPetsPorClienteNome(cliente, pageRequest);
        } else {
            petsPage = petService.listarPetsPaginados(pageRequest);
        }

        model.addAttribute("petsPage", petsPage);
        model.addAttribute("busca", busca);
        model.addAttribute("cliente", cliente);

        return "pets/pets_lista";
    }

    // Deletar pet
    @GetMapping("/deletar/{id}")
    public String deletarPet(@PathVariable Long id) {
        petService.deletarPet(id);
        return "redirect:/pets";
    }

    // Listar pets de um cliente específico
    @GetMapping("/cliente/{idCliente}")
    public String listarPetsPorCliente(@PathVariable Long idCliente, Model model) {
        model.addAttribute("pets", petService.listarPetsPorCliente(idCliente));
        return "pets/pets_lista";
    }
}