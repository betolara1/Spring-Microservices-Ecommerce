package com.betolara1.Product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.betolara1.Product.dto.request.CreateProductRequest;
import com.betolara1.Product.dto.request.UpdateProductRequest;
import com.betolara1.Product.dto.response.ProductDTO;
import com.betolara1.Product.exception.NotFoundException;
import com.betolara1.Product.model.Product;
import com.betolara1.Product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDTO> getAllProducts(int page, int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page, size));

        if (products.isEmpty()) {
            throw new NotFoundException("Nenhum produto cadastrado.");
        }
        
        return products.map(ProductDTO::new);
    }

    public Product saveProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        return productRepository.save(product);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));
        return new ProductDTO(product);
    }

    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku).orElseThrow(() -> new NotFoundException("Produto não encontrado com SKU: " + sku));
        return new ProductDTO(product);
    }

    public ProductDTO getProductByName(String name) {
        Product product = productRepository.findByName(name).orElseThrow(() -> new NotFoundException("Produto não encontrado com nome: " + name));
        return new ProductDTO(product);
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product findProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));

        if (request.getSku() != null) findProduct.setSku(request.getSku());
        if (request.getName() != null) findProduct.setName(request.getName());
        if (request.getDescription() != null) findProduct.setDescription(request.getDescription());
        if (request.getPrice() != null) findProduct.setPrice(request.getPrice());
        if (request.getCategoryId() != null) findProduct.setCategoryId(request.getCategoryId());
        if (request.getImageUrl() != null) findProduct.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) findProduct.setActive(request.getActive());

        return productRepository.save(findProduct);
    }

    public void deleteProduct(Long id){
        Product findProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));
        productRepository.delete(findProduct);
    }
}
