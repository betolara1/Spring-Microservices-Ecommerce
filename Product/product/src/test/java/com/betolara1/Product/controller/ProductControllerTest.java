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
import com.betolara1.product.dto.request.CreateProductRequest;
import com.betolara1.product.dto.request.UpdateProductRequest;
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
        Page<ProductDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1, body.getContent().size());
    }

    @Test
    void testListAll_Empty(){
        Page<Product> page = new PageImpl<>(Collections.emptyList());

        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);
        when(productService.getAllProducts(0, 10)).thenReturn(productDTOPage);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productController.getAllProducts(0, 10);
        });

        assertTrue(exception.getMessage().contains("Nenhum produto cadastrado"));
    }

    @Test
    void testGetProductByIdentifierById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto 1");
        
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);

        when(productService.getProductById(1L, 0, 10)).thenReturn(productDTOPage);

        ResponseEntity<Page<ProductDTO>> response = productController.getProductById(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<ProductDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1L, body.getContent().get(0).getId());
    }

    @Test
    void testGetProductByIdentifierByName() {
        Product product = new Product();
        product.setName("Produto Teste");
        
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);

        when(productService.getProductByName("Produto Teste", 0, 10)).thenReturn(productDTOPage);

        ResponseEntity<Page<ProductDTO>> response = productController.getProductByName("Produto Teste", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<ProductDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals("Produto Teste", body.getContent().get(0).getName());
    }

    @Test
    void testGetProductByCategoryId() {
        Product product = new Product();
        product.setCategoryId(1L);
        
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);

        when(productService.getProductByCategoryId(1L, 0, 10)).thenReturn(productDTOPage);

        ResponseEntity<Page<ProductDTO>> response = productController.getProductByCategoryId(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<ProductDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1L, body.getContent().get(0).getCategoryId());
    }

    @Test
    void testGetProductByActive() {
        Product product = new Product();
        product.setActive(true);
        
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Page<ProductDTO> productDTOPage = page.map(ProductDTO::new);

        when(productService.getProductByActive(true, 0, 10)).thenReturn(productDTOPage);

        ResponseEntity<Page<ProductDTO>> response = productController.getProductByActive(true, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<ProductDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertTrue(body.getContent().get(0).isActive());
    }

    @Test
    void testCreateProduct() {
        CreateProductRequest request = new CreateProductRequest();
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto Novo");

        when(productService.saveProduct(any(CreateProductRequest.class))).thenReturn(product);

        ResponseEntity<ProductDTO> response = productController.createProduct(request, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
        assertEquals("Produto Novo", body.getName());
    }

    @Test
    void testCreateProductError() {
        CreateProductRequest request = new CreateProductRequest();

        ResponseEntity<ProductDTO> response = productController.createProduct(request, "USER");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateProduct() {
        UpdateProductRequest request = new UpdateProductRequest();
        Product product = new Product();
        product.setId(1L);
        product.setName("Produto Atualizado");

        when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class))).thenReturn(product);

        ResponseEntity<ProductDTO> response = productController.updateProduct(1L, request, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
        assertEquals("Produto Atualizado", body.getName());
    }

    
    @Test
    void testUpdateProductError() {
        UpdateProductRequest request = new UpdateProductRequest();

        ResponseEntity<ProductDTO> response = productController.updateProduct(1L, request, "USER");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<String> response = productController.deleteProduct(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Produto deletado com sucesso.", response.getBody());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductError() {
        ResponseEntity<String> response = productController.deleteProduct(1L, "USER");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
