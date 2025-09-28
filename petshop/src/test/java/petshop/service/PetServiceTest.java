package petshop.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petshop.dto.ClienteResumoDTO;
import petshop.dto.PetDTO;
import petshop.model.Cliente;
import petshop.model.Pet;
import petshop.repository.PetRepository;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarTodosDTO() {
        Pet pet1 = new Pet();
        pet1.setIdPet(1L);
        pet1.setNome("Rex");
        pet1.setEspecie("Cachorro");
        pet1.setRaca("Labrador");
        pet1.setIdade(5);
        pet1.setPeso(20.0);
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("João");
        pet1.setCliente(cliente);

        Pet pet2 = new Pet();
        pet2.setIdPet(2L);
        pet2.setNome("Mimi");
        pet2.setEspecie("Gato");
        pet2.setRaca("Persa");
        pet2.setIdade(3);
        pet2.setPeso(5.0);
        Cliente cliente2 = new Cliente();
        cliente2.setIdCliente(2L);
        cliente2.setNome("Maria");
        pet2.setCliente(cliente2);

        when(petRepository.findAll()).thenReturn(Arrays.asList(pet1, pet2));

        List<PetDTO> result = petService.listarTodosDTO();

        assertEquals(2, result.size());
        assertEquals("Rex", result.get(0).nome());
        assertEquals("Mimi", result.get(1).nome());
        assertEquals("João", result.get(0).cliente().nome());
    }

    @Test
    void testSalvarDTO() {
        PetDTO dto = new PetDTO(1L, "Rex", "Cachorro", "Labrador", 5, 20.0,
                new ClienteResumoDTO(1L, "João"));

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("João");

        Pet pet = new Pet();
        pet.setIdPet(1L);
        pet.setNome("Rex");
        pet.setCliente(cliente); // 🔑 importante

        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetDTO salvo = petService.salvarDTO(dto);

        assertNotNull(salvo);
        assertEquals("Rex", salvo.nome());
        assertEquals("João", salvo.cliente().nome());
    }

    @Test
    void testAtualizarDTO() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("João");

        Pet pet = new Pet();
        pet.setIdPet(1L);
        pet.setNome("Rex");
        pet.setCliente(cliente); // 🔑 importante

        PetDTO dtoAtualizado = new PetDTO(1L, "Rex Atualizado", "Cachorro", "Labrador", 6, 22.0,
                new ClienteResumoDTO(1L, "João"));

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetDTO atualizado = petService.atualizarDTO(1L, dtoAtualizado);

        assertNotNull(atualizado);
        assertEquals("Rex Atualizado", atualizado.nome());
        assertEquals("João", atualizado.cliente().nome());
    }
    
    void testDeletarDTO() {
        // Cria um cliente para o pet
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNome("Carlos");

        // Cria o pet
        Pet pet = new Pet();
        pet.setIdPet(1L);
        pet.setNome("Rex");
        pet.setCliente(cliente);

        // Mock do repository para retornar o pet quando buscar por ID
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        // Chama o método do service
        petService.deletarDTO(1L);

        // Verifica se o deleteById foi chamado
        verify(petRepository, times(1)).deleteById(1L);
    }
}
