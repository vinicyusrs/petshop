package petshop.dto;

public record ItemPedidoDTO(
	    Long idItem,
	    ProdutoDTO produto,
	    Integer quantidade,
	    Double precoUnitario
	) { }