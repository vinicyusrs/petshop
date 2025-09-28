package petshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petshop.enums.TipoProdutoEnum;
import petshop.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar produtos pelo tipo (PRODUTO ou SERVICO)
    List<Produto> findByTipo(TipoProdutoEnum tipo);

    // Buscar por nome contendo palavra (para pesquisa)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}