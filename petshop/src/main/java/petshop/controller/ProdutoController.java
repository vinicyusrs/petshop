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

import petshop.enums.TipoProdutoEnum;
import petshop.model.Produto;
import petshop.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Listar todos os produtos
    @GetMapping
    public String listarProdutos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String busca,
            Model model) {

        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<Produto> produtosPage = produtoService.buscarProdutosPaginadosPorNome(busca, pageRequest);

        model.addAttribute("produtosPage", produtosPage);
        model.addAttribute("busca", busca);

        return "produtos/produtos_lista";
    }

    // Formulário para novo produto
    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("tipos", TipoProdutoEnum.values());
        return "produtos/produtos_novo";
    }

    // Salvar produto
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute("produto") Produto produto) {
        produtoService.salvarProduto(produto);
        return "redirect:/produtos";
    }

    // Editar produto
    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Optional<Produto> produtoOpt = produtoService.buscarProdutoPorId(id);
        if (produtoOpt.isPresent()) {
            model.addAttribute("produto", produtoOpt.get());
            model.addAttribute("tipos", TipoProdutoEnum.values());
            return "produtos/produtos_editar";
        } else {
            return "redirect:/produtos";
        }
    }

    // Deletar produto
    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return "redirect:/produtos";
    }

    // Buscar produtos por tipo
    @GetMapping("/tipo/{tipo}")
    public String listarProdutosPorTipo(@PathVariable TipoProdutoEnum tipo, Model model) {
        model.addAttribute("produtos", produtoService.listarProdutosPorTipo(tipo));
        return "produtos/produtos_lista";
    }

    // Buscar produtos por nome
    @GetMapping("/buscar")
    public String buscarProduto(@RequestParam String nome, Model model) {
        model.addAttribute("produtos", produtoService.buscarProdutosPorNome(nome));
        return "produtos/produtos_lista";
    }
}