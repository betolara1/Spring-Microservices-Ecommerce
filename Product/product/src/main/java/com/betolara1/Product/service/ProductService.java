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

import jakarta.transaction.Transactional;

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

    public Page<ProductDTO> getProductById(Long id, int page, int size) {
        Page<Product> products = productRepository.findById(id, PageRequest.of(page, size));
        return products.map(ProductDTO::new);
    }

    public Page<ProductDTO> getProductBySku(String sku, int page, int size) {
        Page<Product> products = productRepository.findBySku(sku, PageRequest.of(page, size));
        return products.map(ProductDTO::new);
    }

    public Page<ProductDTO> getProductByCategoryId(Long categoryId, int page, int size) {
        Page<Product> products = productRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
        return products.map(ProductDTO::new);
    }

    public Page<ProductDTO> getProductByActive(boolean active, int page, int size) {
        Page<Product> products = productRepository.findByActive(active, PageRequest.of(page, size));
        return products.map(ProductDTO::new);
    }

    public Page<ProductDTO> getProductByName(String name, int page, int size) {
        Page<Product> products = productRepository.findByName(name, PageRequest.of(page, size));
        return products.map(ProductDTO::new);
    }

    @Transactional
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

        if (product != null) {
            ProductEvent event = new ProductEvent(product.getId(), product.getSku(), product.getName(), product.getPrice());
            rabbitTemplate.convertAndSend("ecommerce.exchange", "product.created", event);
        }

        return product;
    }

    @Transactional
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

    @Transactional
    public void deleteProduct(Long id){
        Product findProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));
        productRepository.delete(findProduct);
    }
}
