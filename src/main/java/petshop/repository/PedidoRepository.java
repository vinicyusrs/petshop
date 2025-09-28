package petshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petshop.enums.StatusPedidoEnum;
import petshop.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos de um cliente
    List<Pedido> findByClienteIdCliente(Long idCliente);

    // Buscar pedidos por status
    List<Pedido> findByStatus(StatusPedidoEnum status);
    
 // Busca por cliente (nome) ou status
    Page<Pedido> findByClienteNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Pedido> findByStatus(StatusPedidoEnum status, Pageable pageable);

    Page<Pedido> findByClienteNomeContainingIgnoreCaseAndStatus(String cliente, StatusPedidoEnum status, Pageable pageable);
    
    List<Pedido> findByClienteNomeContainingIgnoreCaseAndStatus(String nome, StatusPedidoEnum status);

    List<Pedido> findByClienteNomeContainingIgnoreCase(String nome);
    
}