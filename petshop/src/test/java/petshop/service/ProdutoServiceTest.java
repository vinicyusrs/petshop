package petshop.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petshop.enums.TipoProdutoEnum;
import petshop.model.Produto;
import petshop.repository.ProdutoRepository;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    public ProdutoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarProdutos() {
        Produto p1 = new Produto();
        Produto p2 = new Produto();
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Produto> produtos = produtoService.listarProdutos();
        assertEquals(2, produtos.size());
    }

    @Test
    void testSalvarProduto() {
        Produto p = new Produto();
        p.setNome("Ração");
        when(produtoRepository.save(any(Produto.class))).thenReturn(p);

        Produto salvo = produtoService.salvarProduto(p);
        assertEquals("Ração", salvo.getNome());
    }

    @Test
    void testBuscarProdutoPorId() {
        Produto p = new Produto();
        p.setNome("Coleira");
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Produto> result = produtoService.buscarProdutoPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("Coleira", result.get().getNome());
    }

    @Test
    void testDeletarProduto() {
        produtoService.deletarProduto(1L);
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testListarProdutosPorTipo() {
        Produto p = new Produto();
        p.setNome("Serviço Banho");
        p.setTipo(TipoProdutoEnum.SERVICO);

        when(produtoRepository.findByTipo(TipoProdutoEnum.SERVICO))
                .thenReturn(Arrays.asList(p));

        List<Produto> result = produtoService.listarProdutosPorTipo(TipoProdutoEnum.SERVICO);
        assertEquals(1, result.size());
        assertEquals("Serviço Banho", result.get(0).getNome());
    }
}
