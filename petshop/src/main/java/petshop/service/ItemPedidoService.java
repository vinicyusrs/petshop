package petshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petshop.model.ItemPedido;
import petshop.repository.ItemPedidoRepository;

@Service
public class ItemPedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    public ItemPedido salvarItemPedido(ItemPedido itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    public List<ItemPedido> listarItens() {
        return itemPedidoRepository.findAll();
    }
    
    // Buscar itens paginados de um pedido
    public Page<ItemPedido> buscarItensPaginadosPorPedido(Long pedidoId, Pageable pageable) {
        return itemPedidoRepository.findByPedidoId(pedidoId, pageable);
    }

    public Optional<ItemPedido> buscarItemPorId(Long id) {
        return itemPedidoRepository.findById(id);
    }

    public List<ItemPedido> listarItensPorPedido(Long idPedido) {
        return itemPedidoRepository.findByPedidoIdPedido(idPedido);
    }

    public void deletarItem(Long id) {
        itemPedidoRepository.deleteById(id);
    }
    
    public void salvarTodos(List<ItemPedido> itens) {
        itemPedidoRepository.saveAll(itens);
    }
    
}