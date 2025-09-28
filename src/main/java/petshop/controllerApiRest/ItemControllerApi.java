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
import org.springframework.web.bind.annotation.RestController;

import petshop.model.ItemPedido;
import petshop.service.ItemPedidoService;

@RestController
@RequestMapping("/api/itens")
public class ItemControllerApi {

    @Autowired
    private ItemPedidoService itemPedidoService;

    // Listar todos os itens de um pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedido>> listarItens(@PathVariable Long pedidoId) {
        List<ItemPedido> itens = itemPedidoService.listarItensPorPedido(pedidoId);
        return ResponseEntity.ok(itens);
    }

    // Buscar item por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) {
        Optional<ItemPedido> itemOpt = itemPedidoService.buscarItemPorId(id);
        return itemOpt.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Criar novo item
    @PostMapping
    public ResponseEntity<ItemPedido> criarItem(@RequestBody ItemPedido itemPedido) {
        ItemPedido salvo = itemPedidoService.salvarItemPedido(itemPedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Atualizar item existente
    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> atualizarItem(@PathVariable Long id,
                                                    @RequestBody ItemPedido itemPedido) {
        Optional<ItemPedido> itemOpt = itemPedidoService.buscarItemPorId(id);
        if (itemOpt.isPresent()) {
            itemPedido.setIdItem(id); // garante que o ID seja correto
            ItemPedido atualizado = itemPedidoService.salvarItemPedido(itemPedido);
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        Optional<ItemPedido> itemOpt = itemPedidoService.buscarItemPorId(id);
        if (itemOpt.isPresent()) {
            itemPedidoService.deletarItem(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
