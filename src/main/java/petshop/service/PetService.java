package petshop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petshop.dto.ClienteResumoDTO;
import petshop.dto.PetDTO;
import petshop.model.Cliente;
import petshop.model.Pet;
import petshop.repository.PetRepository;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Pet salvarPet(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> listarPets() {
        return petRepository.findAll();
    }
    
    public Page<Pet> listarPetsPaginados(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    // Buscar por nome ou raça
    public Page<Pet> buscarPetsPaginadosPorNomeOuRaca(String busca, Pageable pageable) {
        return petRepository.findByNomeOrRaca(busca, pageable);
    }

    // Buscar por nome do cliente
    public Page<Pet> buscarPetsPorClienteNome(String cliente, Pageable pageable) {
        return petRepository.findByClienteNome(cliente, pageable);
    }

    // Buscar por nome, raça e cliente
    public Page<Pet> buscarPorNomeRacaECliente(String busca, String cliente, Pageable pageable) {
        return petRepository.findByNomeRacaAndCliente(busca, cliente, pageable);
    }  
    
    public Optional<Pet> buscarPetPorId(Long id) {
        return petRepository.findById(id);
    }

    public List<Pet> listarPetsPorCliente(Long idCliente) {
        return petRepository.findByClienteIdCliente(idCliente);
    }

    public void deletarPet(Long id) {
        petRepository.deleteById(id);
    }
    
    // Buscar pets por nome, raça e cliente
    public List<Pet> buscarPorNomeRacaECliente(String busca, String clienteNome) {
        return petRepository.findByNomeContainingIgnoreCaseOrRacaContainingIgnoreCaseAndClienteNomeContainingIgnoreCase(busca, busca, clienteNome);
    }

    // Buscar pets por nome ou raça
    public List<Pet> buscarPorNomeOuRaca(String busca) {
        return petRepository.findByNomeContainingIgnoreCaseOrRacaContainingIgnoreCase(busca, busca);
    }

    // Buscar pets por cliente
    public List<Pet> buscarPorCliente(String clienteNome) {
        return petRepository.findByClienteNomeContainingIgnoreCase(clienteNome);        
    }
    
    // DTO
    public List<PetDTO> listarTodosDTO() {
        return petRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PetDTO buscarPorIdDTO(Long id) {
        return petRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + id));
    }

    public PetDTO salvarDTO(PetDTO petDTO) {
        Pet pet = toEntity(petDTO);
        Pet salvo = petRepository.save(pet);
        return toDTO(salvo);
    }

    public PetDTO atualizarDTO(Long id, PetDTO petDTO) {
        Pet existente = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com ID: " + id));

        existente.setNome(petDTO.nome());
        existente.setEspecie(petDTO.especie());
        existente.setRaca(petDTO.raca());
        existente.setIdade(petDTO.idade());
        existente.setPeso(petDTO.peso());

        Pet atualizado = petRepository.save(existente);
        return toDTO(atualizado);
    }

    public void deletarDTO(Long id) {
        if (!petRepository.existsById(id)) {
            throw new RuntimeException("Pet não encontrado com ID: " + id);
        }
        petRepository.deleteById(id);
    }

    // 🔹 Converte Entity -> DTO
    private PetDTO toDTO(Pet pet) {
        ClienteResumoDTO clienteResumo = new ClienteResumoDTO(
                pet.getCliente().getIdCliente(),
                pet.getCliente().getNome()
        );
        return new PetDTO(
                pet.getIdPet(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getPeso(),
                clienteResumo
        );
    }

    // 🔹 Converte DTO -> Entity
    private Pet toEntity(PetDTO dto) {
        Pet pet = new Pet();
        pet.setIdPet(dto.idPet());
        pet.setNome(dto.nome());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());

        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.cliente().idCliente());
        pet.setCliente(cliente);

        return pet;
    }
}