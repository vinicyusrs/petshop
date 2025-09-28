package petshop.dto;

public record PetDTO(
	    Long idPet,
	    String nome,
	    String especie,
	    String raca,
	    Integer idade,
	    Double peso,
	    ClienteResumoDTO cliente // apenas dados resumidos do cliente
	) { }