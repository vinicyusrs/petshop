package petshop.dto;

import java.util.List;

public record ClienteDTO(
	    Long idCliente,
	    String nome,
	    String cpf,
	    String telefone,
	    String email,
	    String endereco
	) { }
