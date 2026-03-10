package com.betolara1.Product.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.product.model.Product;
import com.betolara1.product.service.ProductService;
import com.betolara1.product.controller.ProductController;
import com.betolara1.product.dto.response.ProductDTO;
import com.betolara1.product.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;

    @Test
    void testListAll_Success(){
        Product product = new Product();

        product.setId(1L);
        product.setName("Produto 1");
        product.setSku("SKU1");
        product.setDescription("Descrição 1");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setCategoryId(1L);
        product.setImageUrl("http://localhost:8080/images/1.jpg");
        product.setActive(true);

        Page<Product> page = new PageImpl<>(Collections.singletonList(product));

        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);
        when(productService.getAllProducts(0, 10)).thenReturn(productDTOPage);

        ResponseEntity<Page<ProductDTO>> response = productController.getAllProducts(0, 10);

        // COMANDO PARA VER O MOCKITO NO PROMPT:
        System.out.println("\n--- DETALHES DO MOCK ---");
        System.out.println(Mockito.mockingDetails(productService).getInvocations());
        System.out.println("------------------------\n");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testListAll_Empty(){
        Page<Product> page = new PageImpl<>(Collections.emptyList());

        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);
        when(productService.getAllProducts(0, 10)).thenReturn(productDTOPage);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productController.getAllProducts(0, 10);
        });

        // COMANDO PARA VER O MOCKITO NO PROMPT:
        System.out.println("\n--- DETALHES DO MOCK ---");
        System.out.println(Mockito.mockingDetails(productService).getInvocations());
        System.out.println("------------------------\n");

        assertTrue(exception.getMessage().contains("Nenhum produto cadastrado"));
    }

    

}
