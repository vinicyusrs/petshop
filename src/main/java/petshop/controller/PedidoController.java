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

import petshop.enums.StatusPedidoEnum;
import petshop.model.ItemPedido;
import petshop.model.Pedido;
import petshop.service.ClienteService;
import petshop.service.ItemPedidoService;
import petshop.service.PedidoService;
import petshop.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private ItemPedidoService itemPedidoService;

    
    // Listar todos os pedidos
    // Listagem paginada
    @GetMapping
    public String listarPedidos(@RequestParam(defaultValue = "0") int pagina,
                                @RequestParam(defaultValue = "10") int tamanho,
                                Model model) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<Pedido> pedidosPage = pedidoService.listarPedidosPaginados(pageRequest);
        model.addAttribute("pedidosPage", pedidosPage);
        return "pedidos/pedidos_lista";
    }

    // Busca
    @GetMapping("/buscar")
    public String buscarPedidos(@RequestParam(required = false) String cliente,
                                @RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "0") int pagina,
                                @RequestParam(defaultValue = "10") int tamanho,
                                Model model) {
    	
    	 // Converte status para enum, se houver
        StatusPedidoEnum statusEnum = null;
        if (status != null && !status.isBlank()) {  // usar isBlank() ou !status.equals("")
            statusEnum = StatusPedidoEnum.valueOf(status);
        }
        
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<Pedido> pedidosPage = pedidoService.buscarPorClienteOuStatus(cliente, statusEnum, pageRequest);

        model.addAttribute("pedidosPage", pedidosPage);
        model.addAttribute("cliente", cliente);
        model.addAttribute("status", status);

        return "pedidos/pedidos_lista";
    }

    // Formulário para novo pedido
    @GetMapping("/novo")
    public String novoPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("produtos", produtoService.listarProdutos());
        model.addAttribute("status", StatusPedidoEnum.values());
        return "pedidos/pedidos_novo";
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido) {

        // 1. Define status como PENDENTE se estiver nulo
        if (pedido.getStatus() == null) {
            pedido.setStatus(StatusPedidoEnum.PENDENTE);
        }

        // 2. Calcula o valor total do pedido
        double valorTotal = 0.0;
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                // Calcula subtotal do item
                double subtotal = item.getPrecoUnitario() * item.getQuantidade();
                valorTotal += subtotal;
            }
        }
        pedido.setValorTotal(valorTotal);

        // 3. Salva o pedido e gera o ID
        Pedido pedidoSalvo = pedidoService.salvarPedido(pedido);

        // 4. Vincula os itens ao pedido e salva
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedidoSalvo);
            }
            itemPedidoService.salvarTodos(pedido.getItens());
        }

        return "redirect:/pedidos";
    }

    // Editar pedido
    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(id);
        if (pedidoOpt.isPresent()) {
            model.addAttribute("pedido", pedidoOpt.get());
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("produtos", produtoService.listarProdutos());
            model.addAttribute("status", StatusPedidoEnum.values());
            return "pedidos/pedidos_editar";
        } else {
            return "redirect:/pedidos";
        }
    }
    
    @GetMapping("/{id}/itens")
    public String verItensPedido(@PathVariable Long id, Model model) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(id);
        if (pedidoOpt.isPresent()) {
            model.addAttribute("pedido", pedidoOpt.get());
            return "pedidos/itens_pedido"; // nome do HTML
        } else {
            return "redirect:/pedidos";
        }
    }

    // Deletar pedido
    @GetMapping("/deletar/{id}")
    public String deletarPedido(@PathVariable Long id) {
        pedidoService.deletarPedido(id);
        return "redirect:/pedidos";
    }

    // Listar pedidos por cliente
    @GetMapping("/cliente/{idCliente}")
    public String listarPedidosPorCliente(@PathVariable Long idCliente, Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidosPorCliente(idCliente));
        return "pedidos/pedidos_lista";
    }

    // Listar pedidos por status
    @GetMapping("/status/{status}")
    public String listarPedidosPorStatus(@PathVariable StatusPedidoEnum status, Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidosPorStatus(status));
        return "pedidos/pedidos_lista";
    }
}