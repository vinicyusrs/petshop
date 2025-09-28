package petshop.dto;

import petshop.enums.TipoProdutoEnum;

public record ProdutoDTO(
	    Long idProduto,
	    String nome,
	    String descricao,
	    TipoProdutoEnum tipo,
	    Double preco,
	    Integer estoque,
	    String fornecedor
	) { }