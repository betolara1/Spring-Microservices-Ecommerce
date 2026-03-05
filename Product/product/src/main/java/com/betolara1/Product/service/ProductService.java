package com.betolara1.product.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import com.betolara1.product.dto.request.CreateProductRequest;
import com.betolara1.product.dto.request.UpdateProductRequest;
import com.betolara1.product.dto.response.ProductDTO;
import com.betolara1.product.dto.response.ProductEvent;
import com.betolara1.product.exception.NotFoundException;
import com.betolara1.product.model.Product;
import com.betolara1.product.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;
    public ProductService(ProductRepository productRepository, RabbitTemplate rabbitTemplate){
        this.productRepository = productRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Page<ProductDTO> getAllProducts(int page, int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page, size));

        if (products.isEmpty()) {
            throw new NotFoundException("Nenhum produto cadastrado.");
        }
        
        return products.map(ProductDTO::new);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));
        return new ProductDTO(product);
    }

    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku).orElseThrow(() -> new NotFoundException("Produto não encontrado com SKU: " + sku));
        return new ProductDTO(product);
    }

    public ProductDTO getProductByCategoryId(Long categoryId) {
        Product product = productRepository.findByCategoryId(categoryId).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID da categoria: " + categoryId));
        return new ProductDTO(product);
    }

    public ProductDTO getProductByActive(boolean active) {
        Product product = productRepository.findByActive(active).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID da categoria: " + active));
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

    public Product saveProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        product = productRepository.save(product);

        ProductEvent event = new ProductEvent(product.getId(), product.getSku(), product.getName(), product.getPrice());

        rabbitTemplate.convertAndSend("ecommerce.exchange", "product.created", event);

        return product;
    }
}
