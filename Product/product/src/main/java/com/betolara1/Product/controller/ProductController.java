package com.betolara1.Product.controller;

import org.springframework.data.domain.Page;
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

import com.betolara1.Product.dto.response.ProductDTO;
import com.betolara1.Product.dto.request.CreateProductRequest;
import com.betolara1.Product.dto.request.UpdateProductRequest;
import com.betolara1.Product.exception.NotFoundException;
import com.betolara1.Product.model.Product;
import com.betolara1.Product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/listAll")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<ProductDTO> list = productService.getAllProducts(page, size);
        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum produto cadastrado.");
        }
        return ResponseEntity.ok(list);
    }

    // Busca por ID ou SKU
    @GetMapping("/{identifier}")
    public ResponseEntity<ProductDTO> getProductByIdentifier(@PathVariable String identifier) {
        ProductDTO product;

        if (identifier.matches("\\d+")) {
            product = productService.getProductById(Long.parseLong(identifier));
        } else {
            product = productService.getProductBySku(identifier);
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ProductDTO> getProductByName(@PathVariable String name) {
        ProductDTO product = productService.getProductByName(name);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product newProduct = productService.saveProduct(request);
        ProductDTO createdProductDTO = new ProductDTO(newProduct);

        return ResponseEntity.ok(createdProductDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product updatedProduct = productService.updateProduct(id, request);
        ProductDTO updatedProductDTO = new ProductDTO(updatedProduct);

        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
