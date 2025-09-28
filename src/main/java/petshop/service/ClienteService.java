package petshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petshop.dto.ClienteDTO;
import petshop.dto.ItemPedidoDTO;
import petshop.dto.PedidoDTO;
import petshop.dto.PetDTO;
import petshop.dto.ProdutoDTO;
import petshop.model.Cliente;
import petshop.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Salvar ou atualizar cliente
    public Cliente salvarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Buscar todos os clientes
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Buscar cliente por ID
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Buscar cliente por CPF (opcional)
    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        return Optional.ofNullable(clienteRepository.findByCpf(cpf));
    }
    
    // Buscar clientes por nome (parcial, case-insensitive)
    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
    
 // Buscar clientes por CPF e Nome juntos
    public List<Cliente> buscarClientesPorCpfENome(String cpf, String nome) {
        return clienteRepository.findByCpfAndNomeContainingIgnoreCase(cpf, nome);
    }
    
    // Buscar clientes por CPF ou Nome (paginação)
    public Page<Cliente> buscarClientesPaginadosPorCpfOuNome(String busca, PageRequest pageRequest) {
    	busca = busca == null ? "" : busca.replaceAll("[.-]", "");
        return clienteRepository.findByCpfOuNome(busca, pageRequest);
    }

    public Page<Cliente> listarClientesPaginados(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return clienteRepository.findAll(pageable);
    }

    // Deletar cliente
    public void deletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    
    // DTOs
    
    public List<ClienteDTO> listarTodosDTO() {
        return clienteRepository.findAll().stream().map(this::toDTO).toList();
    }

    public Optional<ClienteDTO> buscarPorIdDTO(Long id) {
        return clienteRepository.findById(id).map(this::toDTO);
    }

    public ClienteDTO salvarClienteDTO(Cliente cliente) {
        Cliente salvo = clienteRepository.save(cliente);
        return toDTO(salvo);
    }
    
    // Buscar clientes por nome como DTO
    public List<ClienteDTO> buscarClientesPorNomeDTO(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(this::toDTO)
                .toList();
    }

    // Conversão para DTO
    private ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getIdCliente(),
            cliente.getNome(),
            cliente.getCpf(),
            cliente.getTelefone(),
            cliente.getEmail(),
            cliente.getEndereco()
        );
    }
    
}