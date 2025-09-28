package petshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petshop.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por CPF (opcional)
    Cliente findByCpf(String cpf);
    
    // Buscar clientes cujo nome contenha a string (case-insensitive)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar clientes pelo CPF e nome juntos
    List<Cliente> findByCpfAndNomeContainingIgnoreCase(String cpf, String nome);
    
    
 // Busca paginada por CPF ou Nome (CPF normalizado)
    @Query("SELECT c FROM Cliente c " +
           "WHERE REPLACE(REPLACE(c.cpf, '.', ''), '-', '') LIKE %:busca% " +
           "OR lower(c.nome) LIKE lower(concat('%', :busca, '%'))")
    Page<Cliente> findByCpfOuNome(@Param("busca") String busca, Pageable pageable);
    
    Page<Cliente> findAll(Pageable pageable);
    
    // Busca por nome ou CPF (contendo parte do termo)
    List<Cliente> findByNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf);
}