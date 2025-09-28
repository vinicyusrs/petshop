package petshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petshop.model.ItemPedido;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Buscar todos os itens de um pedido específico
    List<ItemPedido> findByPedidoIdPedido(Long idPedido);
    
 // Buscar itens por pedido com paginação
    @Query("SELECT i FROM ItemPedido i WHERE i.pedido.idPedido = :pedidoId")
    Page<ItemPedido> findByPedidoId(Long pedidoId, Pageable pageable);
    
}