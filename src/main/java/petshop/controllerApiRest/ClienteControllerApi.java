package petshop.controllerApiRest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petshop.dto.ClienteDTO;
import petshop.model.Cliente;
import petshop.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControllerApi {

    @Autowired
    private ClienteService clienteService;

    // --- LISTAR TODOS ---
    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteService.listarTodosDTO();
    }

    // --- BUSCAR POR ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        Optional<ClienteDTO> clienteOpt = clienteService.buscarPorIdDTO(id);
        return clienteOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- BUSCAR POR NOME OU CPF ---
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarCliente(@RequestParam(required = false) String busca) {
        List<ClienteDTO> clientesDTO;
        if (busca != null && !busca.isEmpty()) {
            busca = busca.trim();
            clientesDTO = clienteService.buscarClientesPorNomeDTO(busca); // crie esse método no service
        } else {
            clientesDTO = clienteService.listarTodosDTO();
        }
        return ResponseEntity.ok(clientesDTO);
    }

    // --- CRIAR CLIENTE ---
    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@RequestBody Cliente cliente) {
        ClienteDTO clienteDTO = clienteService.salvarClienteDTO(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDTO);
    }

    // --- ATUALIZAR CLIENTE ---
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if (clienteOpt.isPresent()) {
            cliente.setIdCliente(id);
            ClienteDTO atualizadoDTO = clienteService.salvarClienteDTO(cliente);
            return ResponseEntity.ok(atualizadoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- DELETAR CLIENTE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if (clienteOpt.isPresent()) {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
