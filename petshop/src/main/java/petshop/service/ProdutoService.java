package petshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petshop.dto.ClienteDTO;
import petshop.dto.ProdutoDTO;
import petshop.enums.TipoProdutoEnum;
import petshop.model.Cliente;
import petshop.model.Produto;
import petshop.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }
    
    public Page<Produto> buscarProdutosPaginadosPorNome(String nome, Pageable pageable) {
        if (nome != null && !nome.trim().isEmpty()) {
            return produtoRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        }
        return produtoRepository.findAll(pageable);
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> listarProdutosPorTipo(TipoProdutoEnum tipo) {
        return produtoRepository.findByTipo(tipo);
    }

    public List<Produto> buscarProdutosPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }
    
    // DTO
    
    // Salvar Produto e retornar DTO
    public ProdutoDTO salvarProdutoDTO(Produto produto) {
        Produto salvo = produtoRepository.save(produto);
        return toDTO(salvo);
    }

    // Atualizar Produto e retornar DTO
    public ProdutoDTO atualizarProdutoDTO(Long id, Produto produto) {
        produto.setIdProduto(id);
        Produto atualizado = produtoRepository.save(produto);
        return toDTO(atualizado);
    }


    // Buscar todos os Produtos DTO
    public List<ProdutoDTO> listarProdutosDTO() {
        return produtoRepository.findAll().stream().map(this::toDTO).toList();
    }


    // Buscar Produto por ID DTO
    public Optional<ProdutoDTO> buscarProdutoPorIdDTO(Long id) {
        return produtoRepository.findById(id).map(this::toDTO);
    }

    // Deletar Produto DTO
    public void deletarProdutoDTO(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        produtoRepository.delete(produto);
    }

    // Conversão Produto -> ProdutoDTO
    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
            produto.getIdProduto(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getTipo(),
            produto.getPreco(),
            produto.getEstoque(),
            produto.getFornecedor()
        );
    }
    
 // Conversão ProdutoDTO -> Produto
    public Produto fromDTO(ProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setIdProduto(dto.idProduto());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setTipo(dto.tipo()); // enum
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        produto.setFornecedor(dto.fornecedor());
        return produto;
    }
}