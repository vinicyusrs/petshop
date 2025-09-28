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
import petshop.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    public String listarClientes(@RequestParam(defaultValue = "0") int pagina,
                                 @RequestParam(defaultValue = "10") int tamanho,
                                 Model model) {
        Page<Cliente> clientesPage = clienteService.listarClientesPaginados(pagina, tamanho);
        model.addAttribute("activePage", "clientes"); // indica qual link ficará ativo
        model.addAttribute("clientesPage", clientesPage);
        return "clientes/clientes_lista";
    }

    // Formulário para criar novo cliente
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente()); // vazio
        return "clientes/clientes_novo";
    }

    // Salvar cliente
    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute("cliente") Cliente cliente) {
        clienteService.salvarCliente(cliente);
        return "redirect:/clientes";
    }

    // Formulário para editar cliente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if (clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            return "clientes/clientes_editar";
        } else {
            return "redirect:/clientes";
        }
    }
    
    // Deletar cliente
    @GetMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return "redirect:/clientes";
    }

    // Buscar cliente por CPF (opcional)
    @GetMapping("/buscar")
    public String buscarCliente(@RequestParam(required = false) String cpf,
                                @RequestParam(required = false) String nome,
                                Model model) {

        List<Cliente> clientes;
        
        if (cpf != null && !cpf.isEmpty()) {
            // Busca pelo CPF (único) e transforma em lista
            clientes = clienteService.buscarClientePorCpf(cpf)
                                     .map(List::of)      // transforma Optional<Cliente> em List<Cliente>
                                     .orElse(List.of()); // lista vazia se não encontrado
        } else if (nome != null && !nome.isEmpty()) {
            // Busca por nome (pode retornar vários)
            clientes = clienteService.buscarClientesPorNome(nome);
        } else {
            // Se não informou nada, lista todos
            clientes = clienteService.listarClientes();
        }

        model.addAttribute("clientes", clientes);
        return "clientes/clientes_lista"; // Thymeleaf: clientes/lista.html
    }
    
    
    @GetMapping("/buscar_cpf_nome")
    public String buscarCliente(@RequestParam(required = false) String busca,
                                @RequestParam(defaultValue = "0") int pagina,
                                @RequestParam(defaultValue = "10") int tamanho,
                                Model model) {

        if (busca != null) {
        	busca = busca.trim();
        }
        
        Page<Cliente> clientesPage;

        if (busca != null && !busca.isEmpty()) {
            clientesPage = clienteService.buscarClientesPaginadosPorCpfOuNome(busca, PageRequest.of(pagina, tamanho));
        } else {
            clientesPage = clienteService.listarClientesPaginados(pagina, tamanho);
        }

        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("activePage", "clientes");
        return "clientes/clientes_lista";
    }
}