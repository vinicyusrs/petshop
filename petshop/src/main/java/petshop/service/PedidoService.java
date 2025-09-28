package petshop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petshop.dto.ClienteDTO;
import petshop.dto.ItemPedidoDTO;
import petshop.dto.PedidoDTO;
import petshop.dto.ProdutoDTO;
import petshop.enums.StatusPedidoEnum;
import petshop.mapper.PedidoMapper;
import petshop.model.ItemPedido;
import petshop.model.Pedido;
import petshop.repository.ItemPedidoRepository;
import petshop.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido salvarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    public Page<Pedido> listarPedidosPaginados(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

 // Busca pedidos filtrando opcionalmente por cliente e status
    public Page<Pedido> buscarPorClienteOuStatus(String cliente, StatusPedidoEnum status, Pageable pageable) {

        if ((cliente != null && !cliente.isBlank()) && status != null) {
            // Cliente e status
            return pedidoRepository.findByClienteNomeContainingIgnoreCaseAndStatus(cliente, status, pageable);
        } else if (cliente != null && !cliente.isBlank()) {
            // Apenas cliente
            return pedidoRepository.findByClienteNomeContainingIgnoreCase(cliente, pageable);
        } else if (status != null) {
            // Apenas status
            return pedidoRepository.findByStatus(status, pageable);
        } else {
            // Nenhum filtro, lista tudo
            return pedidoRepository.findAll(pageable);
        }
    }
    

    public Optional<Pedido> buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPedidosPorCliente(Long idCliente) {
        return pedidoRepository.findByClienteIdCliente(idCliente);
    }

    public List<Pedido> listarPedidosPorStatus(StatusPedidoEnum status) {
        return pedidoRepository.findByStatus(status);
    }

    public void deletarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
    
    // DTO
    
    // Listar todos os pedidos
    public List<PedidoDTO> listarPedidosDTO() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar pedido por ID
    public Optional<PedidoDTO> buscarPedidoPorIdDTO(Long id) {
        return pedidoRepository.findById(id)
                .map(PedidoMapper::toDTO);
    }

    // Salvar pedido usando DTO
    public PedidoDTO salvarPedidoDTO(PedidoDTO dto) {
        Pedido pedido = PedidoMapper.fromDTO(dto);

        // Calcula valor total e vincula itens ao pedido
        if (pedido.getItens() != null) {
            double valorTotal = pedido.getItens().stream()
                    .mapToDouble(item -> item.getQuantidade() * item.getPrecoUnitario())
                    .sum();
            pedido.setValorTotal(valorTotal);
            pedido.getItens().forEach(item -> item.setPedido(pedido));
        }

        Pedido salvo = pedidoRepository.save(pedido);
        return PedidoMapper.toDTO(salvo);
    }

    // Atualizar pedido usando DTO
    public Optional<PedidoDTO> atualizarPedidoDTO(Long id, PedidoDTO dto) {
        return pedidoRepository.findById(id).map(existing -> {
            Pedido pedidoAtualizado = PedidoMapper.fromDTO(dto);
            pedidoAtualizado.setIdPedido(id);

            if (pedidoAtualizado.getItens() != null) {
                pedidoAtualizado.getItens().forEach(item -> item.setPedido(pedidoAtualizado));
            }

            Pedido salvo = pedidoRepository.save(pedidoAtualizado);
            return PedidoMapper.toDTO(salvo);
        });
    }

    // Listar pedidos por cliente
    public List<PedidoDTO> listarPedidosPorClienteDTO(Long idCliente) {
        return pedidoRepository.findByClienteIdCliente(idCliente)
                .stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Listar pedidos por status
    public List<PedidoDTO> listarPedidosPorStatusDTO(StatusPedidoEnum status) {
        return pedidoRepository.findByStatus(status)
                .stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar pedidos filtrando por cliente e status
    public List<PedidoDTO> buscarPorClienteOuStatusDTO(String cliente, StatusPedidoEnum status) {
        List<Pedido> pedidos;

        if ((cliente != null && !cliente.isBlank()) && status != null) {
            pedidos = pedidoRepository.findByClienteNomeContainingIgnoreCaseAndStatus(cliente, status);
        } else if (cliente != null && !cliente.isBlank()) {
            pedidos = pedidoRepository.findByClienteNomeContainingIgnoreCase(cliente);
        } else if (status != null) {
            pedidos = pedidoRepository.findByStatus(status);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return pedidos.stream().map(PedidoMapper::toDTO).collect(Collectors.toList());
    }

    // Conversões DTO <-> entidade

    public static PedidoDTO toDTO(Pedido pedido) {
        List<ItemPedidoDTO> itensDTO = pedido.getItens() != null
                ? pedido.getItens().stream()
                    .map(item -> new ItemPedidoDTO(
                            item.getIdItem(),
                            item.getProduto() != null ? new ProdutoDTO(
                                    item.getProduto().getIdProduto(),
                                    item.getProduto().getNome(),
                                    item.getProduto().getDescricao(),
                                    item.getProduto().getTipo(),
                                    item.getProduto().getPreco(),
                                    item.getProduto().getEstoque(),
                                    item.getProduto().getFornecedor()
                            ) : null,
                            item.getQuantidade(),
                            item.getPrecoUnitario()
                    ))
                    .collect(Collectors.toList())
                : null;

        ClienteDTO clienteDTO = null;
        if (pedido.getCliente() != null) {
            clienteDTO = new ClienteDTO(
                    pedido.getCliente().getIdCliente(),
                    pedido.getCliente().getNome(),
                    pedido.getCliente().getCpf(),
                    pedido.getCliente().getTelefone(),
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getEndereco()
            );
        }

        return new PedidoDTO(
                pedido.getIdPedido(),
                clienteDTO,
                pedido.getDataPedido(),
                pedido.getStatus() != null ? pedido.getStatus().name() : null,
                pedido.getValorTotal(),
                itensDTO
        );
    }

    private void calcularValorTotal(Pedido pedido) {
        double valorTotal = 0.0;
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                valorTotal += item.getPrecoUnitario() * item.getQuantidade();
            }
        }
        pedido.setValorTotal(valorTotal);
    }
}