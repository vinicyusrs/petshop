package petshop.mapper;

import java.util.List;
import java.util.stream.Collectors;

import petshop.dto.ClienteDTO;
import petshop.dto.ItemPedidoDTO;
import petshop.dto.PedidoDTO;
import petshop.dto.ProdutoDTO;
import petshop.enums.StatusPedidoEnum;
import petshop.model.Cliente;
import petshop.model.ItemPedido;
import petshop.model.Pedido;
import petshop.model.Produto;

public class PedidoMapper {

    // Converte Pedido -> PedidoDTO
    public static PedidoDTO toDTO(Pedido pedido) {
        List<ItemPedidoDTO> itensDTO = pedido.getItens() != null
                ? pedido.getItens().stream()
                    .map(item -> new ItemPedidoDTO(
                            item.getIdItem(),
                            item.getProduto() != null ? new ProdutoDTO(
                                    item.getProduto().getIdProduto(),
                                    item.getProduto().getNome(),
                                    item.getProduto().getDescricao(),
                                    item.getProduto().getTipo(),
                                    item.getProduto().getPreco(),
                                    item.getProduto().getEstoque(),
                                    item.getProduto().getFornecedor()
                            ) : null,
                            item.getQuantidade(),
                            item.getPrecoUnitario()
                    ))
                    .collect(Collectors.toList())
                : null;

        ClienteDTO clienteDTO = null;
        if (pedido.getCliente() != null) {
            clienteDTO = new ClienteDTO(
                    pedido.getCliente().getIdCliente(),
                    pedido.getCliente().getNome(),
                    pedido.getCliente().getCpf(),
                    pedido.getCliente().getTelefone(),
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getEndereco()
            );
        }

        return new PedidoDTO(
                pedido.getIdPedido(),
                clienteDTO,
                pedido.getDataPedido(),
                pedido.getStatus() != null ? pedido.getStatus().name() : null,
                pedido.getValorTotal(),
                itensDTO
        );
    }

    // Converte PedidoDTO -> Pedido
    public static Pedido fromDTO(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(dto.idPedido());
        pedido.setDataPedido(dto.dataPedido());
        pedido.setStatus(dto.status() != null ? StatusPedidoEnum.valueOf(dto.status()) : null);
        pedido.setValorTotal(dto.valorTotal());

        // Cliente
        if (dto.cliente() != null) {
            Cliente cliente = new Cliente(); // usa construtor vazio
            cliente.setIdCliente(dto.cliente().idCliente());
            cliente.setNome(dto.cliente().nome());
            cliente.setCpf(dto.cliente().cpf());
            cliente.setTelefone(dto.cliente().telefone());
            cliente.setEmail(dto.cliente().email());
            cliente.setEndereco(dto.cliente().endereco());
            pedido.setCliente(cliente);
        }

        // Itens
        if (dto.itens() != null) {
            List<ItemPedido> itens = dto.itens().stream()
                    .map(itemDTO -> {
                        ItemPedido item = new ItemPedido();
                        item.setIdItem(itemDTO.idItem());
                        item.setQuantidade(itemDTO.quantidade());
                        item.setPrecoUnitario(itemDTO.precoUnitario());

                        // Produto
                        if (itemDTO.produto() != null) {
                            Produto produto = new Produto(); // usa construtor vazio
                            produto.setIdProduto(itemDTO.produto().idProduto());
                            produto.setNome(itemDTO.produto().nome());
                            produto.setDescricao(itemDTO.produto().descricao());
                            produto.setTipo(itemDTO.produto().tipo());
                            produto.setPreco(itemDTO.produto().preco());
                            produto.setEstoque(itemDTO.produto().estoque());
                            produto.setFornecedor(itemDTO.produto().fornecedor());
                            item.setProduto(produto);
                        }

                        item.setPedido(pedido); // linka ao pedido
                        return item;
                    })
                    .collect(Collectors.toList());
            pedido.setItens(itens);
        }

        return pedido;
    }
}
