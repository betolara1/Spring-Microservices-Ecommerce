package com.betolara1.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betolara1.product.dto.request.CreateProductRequest;
import com.betolara1.product.dto.request.UpdateProductRequest;
import com.betolara1.product.dto.response.ProductDTO;
import com.betolara1.product.exception.NotFoundException;
import com.betolara1.product.model.Product;
import com.betolara1.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<ProductDTO> list = productService.getAllProducts(page, size);
        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum produto cadastrado.");
        }
        return ResponseEntity.ok(list);
    }

    // Busca por ID
    @GetMapping("/id={id}")
    public ResponseEntity<Page<ProductDTO>> getProductById(@PathVariable Long id, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ProductDTO> product;
        product = productService.getProductById(id, page, size);
        return ResponseEntity.ok(product);
    }

    // Busca por nome
    @GetMapping("/name={name}")
    public ResponseEntity<Page<ProductDTO>> getProductByName(@PathVariable String name, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ProductDTO> product;
        product = productService.getProductByName(name, page, size);
        return ResponseEntity.ok(product);
    }

    // Busca por SKU
    @GetMapping("/sku={sku}")
    public ResponseEntity<Page<ProductDTO>> getProductBySku(@PathVariable String sku, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ProductDTO> product;
        product = productService.getProductBySku(sku, page, size);
        return ResponseEntity.ok(product);
    }

    // Busca por categoria
    @GetMapping("/category={categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductByCategoryId(@PathVariable Long categoryId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ProductDTO> product = productService.getProductByCategoryId(categoryId, page, size);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/active={active}")
    public ResponseEntity<Page<ProductDTO>> getProductByActive(@PathVariable boolean active, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<ProductDTO> product = productService.getProductByActive(active, page, size);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            Product newProduct = productService.saveProduct(request);
            ProductDTO createdProductDTO = new ProductDTO(newProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            Product updatedProduct = productService.updateProduct(id, request);
            ProductDTO updatedProductDTO = new ProductDTO(updatedProduct);

            return ResponseEntity.ok(updatedProductDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            productService.deleteProduct(id);
            return ResponseEntity.ok("Produto deletado com sucesso.");
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
