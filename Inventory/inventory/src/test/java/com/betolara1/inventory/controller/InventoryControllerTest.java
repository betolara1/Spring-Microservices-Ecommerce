package com.betolara1.inventory.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.request.UpdateInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryDTO;
import com.betolara1.inventory.exception.NotFoundException;
import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.service.InventoryService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void testGetAllInventory_Success() {
        InventoryDTO inventory = new InventoryDTO();
        inventory.setId(1L);
        inventory.setSku("SKU123");
        inventory.setQuantity(100);
        inventory.setStatus(Inventory.Status.AVAILABLE);

        Page<InventoryDTO> page = new PageImpl<>(Collections.singletonList(inventory));

        when(inventoryService.getAllInventory(0, 10)).thenReturn(page);

        ResponseEntity<Page<InventoryDTO>> response = inventoryController.getAllInventory(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetInventoryByStatus_Success() {
        InventoryDTO inventory = new InventoryDTO();
        inventory.setStatus(Inventory.Status.AVAILABLE);

        Page<InventoryDTO> page = new PageImpl<>(Collections.singletonList(inventory));

        when(inventoryService.getInventoryByStatus(Inventory.Status.AVAILABLE, 0, 10)).thenReturn(page);

        ResponseEntity<Page<InventoryDTO>> response = inventoryController.getInventoryByStatus(Inventory.Status.AVAILABLE, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Inventory.Status.AVAILABLE, response.getBody().getContent().get(0).getStatus());
    }

    @Test
    void testGetInventoryById_Success() {
        InventoryDTO inventory = new InventoryDTO();
        inventory.setId(1L);

        when(inventoryService.getInventoryById(1L)).thenReturn(inventory);

        ResponseEntity<InventoryDTO> response = inventoryController.getInventoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetInventoryBySku_Success() {
        InventoryDTO inventory = new InventoryDTO();
        inventory.setSku("SKU123");

        when(inventoryService.getInventoryBySku("SKU123")).thenReturn(inventory);

        ResponseEntity<InventoryDTO> response = inventoryController.getInventoryBySku("SKU123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SKU123", response.getBody().getSku());
    }

    @Test
    void testCreateInventory_Success() {
        SaveInventoryRequest request = new SaveInventoryRequest();
        Inventory newInventory = new Inventory();
        newInventory.setId(1L);
        newInventory.setSku("SKU123");
        newInventory.setQuantity(100);
        newInventory.setStatus(Inventory.Status.AVAILABLE);

        when(inventoryService.saveInventory(any(SaveInventoryRequest.class))).thenReturn(newInventory);

        ResponseEntity<InventoryDTO> response = inventoryController.createInventory(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testUpdateInventory_Success() {
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setSku("SKU123");
        inventory.setQuantity(50);
        inventory.setStatus(Inventory.Status.AVAILABLE);

        when(inventoryService.updateInventory(eq(1L), any(UpdateInventoryRequest.class))).thenReturn(inventory);

        ResponseEntity<InventoryDTO> response = inventoryController.updateInventory(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(50, response.getBody().getQuantity());
    }

    @Test
    void testDeleteInventory_Success() {
        doNothing().when(inventoryService).deleteInventory(1L);

        ResponseEntity<String> response = inventoryController.deleteInventory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estoque deletado com sucesso!", response.getBody());
        verify(inventoryService, times(1)).deleteInventory(1L);
    }
}
