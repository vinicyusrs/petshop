package petshop.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petshop.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // Buscar pets de um cliente específico
    List<Pet> findByClienteIdCliente(Long idCliente);
    
    // Buscar por nome ou raça
    @Query("SELECT p FROM Pet p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
           "OR LOWER(p.raca) LIKE LOWER(CONCAT('%', :busca, '%'))")
    Page<Pet> findByNomeOrRaca(@Param("busca") String busca, Pageable pageable);

    // Buscar por cliente nome
    @Query("SELECT p FROM Pet p WHERE LOWER(p.cliente.nome) LIKE LOWER(CONCAT('%', :cliente, '%'))")
    Page<Pet> findByClienteNome(@Param("cliente") String cliente, Pageable pageable);

    // Buscar por nome, raça e cliente juntos (opcional)
    @Query("SELECT p FROM Pet p WHERE (LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
           "OR LOWER(p.raca) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
           "AND LOWER(p.cliente.nome) LIKE LOWER(CONCAT('%', :cliente, '%'))")
    Page<Pet> findByNomeRacaAndCliente(@Param("busca") String busca, @Param("cliente") String cliente, Pageable pageable);
    
    // Buscar pets por nome ou raça
    List<Pet> findByNomeContainingIgnoreCaseOrRacaContainingIgnoreCase(String nome, String raca);

    // Buscar pets por nome, raça e cliente
    List<Pet> findByNomeContainingIgnoreCaseOrRacaContainingIgnoreCaseAndClienteNomeContainingIgnoreCase(
            String nome, String raca, String clienteNome);

    // Buscar pets por cliente
    List<Pet> findByClienteNomeContainingIgnoreCase(String clienteNome);

    // Listar pets de um cliente específico
    List<Pet> findByCliente_IdCliente(Long idCliente);
    
}
