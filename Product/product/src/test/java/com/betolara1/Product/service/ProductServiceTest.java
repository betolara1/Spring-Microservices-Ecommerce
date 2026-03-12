package com.betolara1.Product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betolara1.product.dto.request.CreateProductRequest;
import com.betolara1.product.dto.request.UpdateProductRequest;
import com.betolara1.product.dto.response.ProductDTO;
import com.betolara1.product.exception.NotFoundException;
import com.betolara1.product.model.Product;
import com.betolara1.product.repository.ProductRepository;
import com.betolara1.product.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProductService productService;

    @Test
    void testFindById_Success(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        when(productRepository.findById(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));

        Page<ProductDTO> result = productService.getProductById(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ProductDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Produto 1", dto.getName());
        assertEquals("SKU1", dto.getSku());
        assertEquals("Descrição 1", dto.getDescription());
        assertEquals(BigDecimal.valueOf(10.0), dto.getPrice());
        assertEquals(1L, dto.getCategoryId());
        assertEquals("http://localhost:8080/images/1.jpg", dto.getImageUrl());
        assertEquals(true, dto.isActive());
    }

    @Test
    void testFindById_NotFound(){
        when(productRepository.findById(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDTO> result = productService.getProductById(1L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindBySku_Success(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        when(productRepository.findBySku(eq("SKU1"), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));

        Page<ProductDTO> result = productService.getProductBySku("SKU1", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ProductDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Produto 1", dto.getName());
        assertEquals("SKU1", dto.getSku());
    }

    @Test
    void testFindBySku_NotFound(){
        when(productRepository.findBySku(eq("SKU1"), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDTO> result = productService.getProductBySku("SKU1", 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findProductByCategory_Success(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        when(productRepository.findByCategoryId(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));

        Page<ProductDTO> result = productService.getProductByCategoryId(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getCategoryId());
    }

    @Test
    void findProductByCategory_NotFound(){
        when(productRepository.findByCategoryId(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDTO> result = productService.getProductByCategoryId(1L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findProductByActive_Success(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        when(productRepository.findByActive(eq(true), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));

        Page<ProductDTO> result = productService.getProductByActive(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).isActive());
    }

    @Test
    void findProductByActive_NotFound(){
        when(productRepository.findByActive(eq(true), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDTO> result = productService.getProductByActive(true, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findProductByName_Success(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        when(productRepository.findByName(eq("Produto 1"), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));

        Page<ProductDTO> result = productService.getProductByName("Produto 1", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Produto 1", result.getContent().get(0).getName());
    }

    @Test
    void findProductByName_NotFound(){
        when(productRepository.findByName(eq("Produto 1"), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<ProductDTO> result = productService.getProductByName("Produto 1", 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveProduct_Success(){
        CreateProductRequest request = new CreateProductRequest();
        request.setSku("SKU1");
        request.setName("Produto 1");
        request.setDescription("Descrição 1");
        request.setPrice(BigDecimal.valueOf(10.0));
        request.setCategoryId(1L);
        request.setImageUrl("http://localhost:8080/images/1.jpg");
        request.setActive(true);

        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        Product result = productService.saveProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Produto 1", result.getName());
        assertEquals("SKU1", result.getSku());
        assertEquals("Descrição 1", result.getDescription());
        assertEquals(BigDecimal.valueOf(10.0), result.getPrice());
        assertEquals(1L, result.getCategoryId());
        assertEquals("http://localhost:8080/images/1.jpg", result.getImageUrl());
        assertEquals(true, result.isActive());
    }

    @Test
    void testSaveProduct_NotFound(){
        CreateProductRequest request = new CreateProductRequest();
        request.setSku("SKU1");
        request.setName("Produto 1");
        request.setDescription("Descrição 1");
        request.setPrice(BigDecimal.valueOf(10.0));
        request.setCategoryId(1L);
        request.setImageUrl("http://localhost:8080/images/1.jpg");
        request.setActive(true);

        when(productRepository.save(any(Product.class))).thenReturn(null);

        Product result = productService.saveProduct(request);

        assertNull(result);
    }

    @Test
    void testUpdateProduct_Success(){
        Product oldUser = new Product();
        oldUser.setSku("SKU1");
        oldUser.setName("Produto 1");
        oldUser.setDescription("Descrição 1");
        oldUser.setPrice(BigDecimal.valueOf(10.0));
        oldUser.setCategoryId(1L);
        oldUser.setImageUrl("http://localhost:8080/images/1.jpg");
        oldUser.setActive(true);

        UpdateProductRequest updatedUser = new UpdateProductRequest();
        updatedUser.setSku("SKU1");
        updatedUser.setName("Produto 1");
        updatedUser.setDescription("Descrição 1");
        updatedUser.setPrice(BigDecimal.valueOf(10.0));
        updatedUser.setCategoryId(1L);
        updatedUser.setImageUrl("http://localhost:8080/images/1.jpg");
        updatedUser.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        Product result = productService.updateProduct(1L, updatedUser);

        assertNotNull(result);
        assertEquals("Produto 1", result.getName());
        assertEquals("Descrição 1", result.getDescription());
    }

    @Test
    void testUpdateProduct_NotFound(){
        UpdateProductRequest updatedUser = new UpdateProductRequest();
        updatedUser.setSku("SKU1");
        updatedUser.setName("Produto 1");

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(999L, updatedUser);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
    }

    @Test
    void testDeleteProduct_Success(){
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
    }
}
