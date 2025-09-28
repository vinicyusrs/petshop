package petshop.controllerApiRest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petshop.dto.PedidoDTO;
import petshop.enums.StatusPedidoEnum;
import petshop.mapper.PedidoMapper;
import petshop.model.Pedido;
import petshop.service.ItemPedidoService;
import petshop.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControllerApi {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemPedidoService itemPedidoService;

    // Listar todos os pedidos
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        List<PedidoDTO> pedidosDTO = pedidoService.listarPedidos()
                .stream()
                .map(PedidoService::toDTO)
                .toList();
        return ResponseEntity.ok(pedidosDTO);
    }

    // Buscar pedidos por cliente ou status
    @GetMapping("/buscar")
    public ResponseEntity<List<PedidoDTO>> buscarPedidos(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) StatusPedidoEnum status
    ) {
        List<PedidoDTO> resultado = pedidoService.buscarPorClienteOuStatusDTO(cliente, status);
        return ResponseEntity.ok(resultado);
    }

    // Buscar pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(id);
        return pedidoOpt.map(PedidoService::toDTO)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // Criar novo pedido
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = PedidoMapper.fromDTO(pedidoDTO);

        if (pedido.getStatus() == null) {
            pedido.setStatus(StatusPedidoEnum.PENDENTE);
        }

        // Calcula valor total
        double valorTotal = pedido.getItens() != null
                ? pedido.getItens().stream().mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade()).sum()
                : 0.0;
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoService.salvarPedido(pedido);

        // Salva itens do pedido
        if (pedido.getItens() != null) {
            pedido.getItens().forEach(i -> i.setPedido(pedidoSalvo));
            itemPedidoService.salvarTodos(pedido.getItens());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoService.toDTO(pedidoSalvo));
    }

    // Atualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> atualizarPedido(@PathVariable Long id, @RequestBody PedidoDTO pedidoDTO) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(id);

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = PedidoMapper.fromDTO(pedidoDTO);
        pedido.setIdPedido(id);

        // Recalcula valor total
        double valorTotal = pedido.getItens() != null
                ? pedido.getItens().stream().mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade()).sum()
                : 0.0;
        pedido.setValorTotal(valorTotal);

        Pedido atualizado = pedidoService.salvarPedido(pedido);

        // Atualiza itens
        if (pedido.getItens() != null) {
            pedido.getItens().forEach(i -> i.setPedido(atualizado));
            itemPedidoService.salvarTodos(pedido.getItens());
        }

        return ResponseEntity.ok(PedidoService.toDTO(atualizado));
    }

    // Deletar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(id);
        if (pedidoOpt.isEmpty()) return ResponseEntity.notFound().build();

        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // Listar pedidos por cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorCliente(@PathVariable Long idCliente) {
        List<PedidoDTO> pedidosDTO = pedidoService.listarPedidosPorCliente(idCliente)
                .stream().map(PedidoService::toDTO).toList();
        return ResponseEntity.ok(pedidosDTO);
    }

    // Listar pedidos por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorStatus(@PathVariable StatusPedidoEnum status) {
        List<PedidoDTO> pedidosDTO = pedidoService.listarPedidosPorStatus(status)
                .stream().map(PedidoService::toDTO).toList();
        return ResponseEntity.ok(pedidosDTO);
    }
}
