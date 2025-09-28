package petshop.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petshop.model.ItemPedido;
import petshop.model.Pedido;
import petshop.service.ItemPedidoService;
import petshop.service.PedidoService;
import petshop.service.ProdutoService;

@Controller
@RequestMapping("/itens")
public class ItemPedidoController {

    @Autowired
    private ItemPedidoService itemPedidoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    // Listar todos os itens
    @GetMapping("/itens/{pedidoId}")
    public String listarItens(@PathVariable Long pedidoId,
                              @RequestParam(defaultValue = "0") int pagina,
                              @RequestParam(defaultValue = "10") int tamanho,
                              Model model) {

        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<ItemPedido> itensPage = itemPedidoService.buscarItensPaginadosPorPedido(pedidoId, pageRequest);
        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId).orElse(null);

        model.addAttribute("itensPage", itensPage);
        model.addAttribute("pedido", pedido);

        return "itens/itens_pedido_lista";
    }
    
    // Formulário para novo item de pedido
    @GetMapping("/novo")
    public String novoItem(Model model) {
        model.addAttribute("itemPedido", new ItemPedido());
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "itens/itens_form";
    }

    // Salvar item
    @PostMapping("/salvar")
    public String salvarItem(@ModelAttribute("itemPedido") ItemPedido itemPedido) {
        itemPedidoService.salvarItemPedido(itemPedido);
        return "redirect:/itens";
    }

    // Editar item
    @GetMapping("/editar/{id}")
    public String editarItem(@PathVariable Long id, Model model) {
        Optional<ItemPedido> itemOpt = itemPedidoService.buscarItemPorId(id);
        if (itemOpt.isPresent()) {
            model.addAttribute("itemPedido", itemOpt.get());
            model.addAttribute("pedidos", pedidoService.listarPedidos());
            model.addAttribute("produtos", produtoService.listarProdutos());
            return "itens/itens_form";
        } else {
            return "redirect:/itens";
        }
    }

    // Deletar item
    @GetMapping("/deletar/{id}")
    public String deletarItem(@PathVariable Long id) {
        itemPedidoService.deletarItem(id);
        return "redirect:/itens";
    }

    // Listar itens de um pedido específico
    @GetMapping("/pedido/{idPedido}")
    public String listarItensPorPedido(@PathVariable Long idPedido, Model model) {
        model.addAttribute("itens", itemPedidoService.listarItensPorPedido(idPedido));
        return "itens/itens_lista";
    }
}