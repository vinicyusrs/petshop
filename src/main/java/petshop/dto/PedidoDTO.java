package petshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
	    Long idPedido,
	    ClienteDTO cliente,
	    LocalDateTime dataPedido,
	    String status,
	    Double valorTotal,
	    List<ItemPedidoDTO> itens
	) { }
