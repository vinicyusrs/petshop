package petshop.controllerApiRest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petshop.dto.ProdutoDTO;
import petshop.model.Produto;
import petshop.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoControllerApi {

    @Autowired
    private ProdutoService produtoService;

    // Listar todos Produtos DTO
    @GetMapping
    public List<ProdutoDTO> listarProdutosDTO() {
        return produtoService.listarProdutosDTO();
    }

    // Buscar Produto por ID DTO
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorIdDTO(@PathVariable Long id) {
        Optional<ProdutoDTO> produtoDTO = produtoService.buscarProdutoPorIdDTO(id);
        return produtoDTO.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // Criar Produto DTO
    @PostMapping
    public ResponseEntity<ProdutoDTO> criarProdutoDTO(@RequestBody ProdutoDTO dto) {
        Produto produto = produtoService.fromDTO(dto);          // Converte DTO -> Entidade
        ProdutoDTO salvo = produtoService.salvarProdutoDTO(produto); // Salva e converte de volta para DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Atualizar Produto DTO
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProdutoDTO(@PathVariable Long id, @RequestBody ProdutoDTO dto) {
        Optional<Produto> existente = produtoService.buscarProdutoPorId(id);
        if (existente.isPresent()) {
            Produto produto = produtoService.fromDTO(dto);          // DTO -> Entidade
            ProdutoDTO atualizado = produtoService.atualizarProdutoDTO(id, produto); // Atualiza e converte para DTO
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar Produto DTO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProdutoDTO(@PathVariable Long id) {
        produtoService.deletarProdutoDTO(id);
        return ResponseEntity.noContent().build();
    }
}
