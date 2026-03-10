package com.betolara1.inventory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.request.UpdateInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryDTO;
import com.betolara1.inventory.exception.NotFoundException;
import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.repository.InventoryRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void testGetAllInventory_Success() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStatus(Inventory.Status.AVAILABLE);

        Page<Inventory> page = new PageImpl<>(Collections.singletonList(inventory));
        when(inventoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<InventoryDTO> result = inventoryService.getAllInventory(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetInventoryByStatus_Success() {
        Inventory inventory = new Inventory();
        inventory.setStatus(Inventory.Status.AVAILABLE);

        Page<Inventory> page = new PageImpl<>(Collections.singletonList(inventory));
        when(inventoryRepository.findByStatus(eq(Inventory.Status.AVAILABLE), any(PageRequest.class))).thenReturn(page);

        Page<InventoryDTO> result = inventoryService.getInventoryByStatus(Inventory.Status.AVAILABLE, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Inventory.Status.AVAILABLE, result.getContent().get(0).getStatus());
    }

    @Test
    void testGetInventoryById_Success() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        InventoryDTO result = inventoryService.getInventoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetInventoryById_NotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> inventoryService.getInventoryById(1L));
    }

    @Test
    void testFindBySkuEntity_Success() {
        Inventory inventory = new Inventory();
        inventory.setSku("SKU123");

        when(inventoryRepository.findBySku("SKU123")).thenReturn(Optional.of(inventory));

        Inventory result = inventoryService.findBySkuEntity("SKU123");

        assertNotNull(result);
        assertEquals("SKU123", result.getSku());
    }

    @Test
    void testGetInventoryBySku_Success() {
        Inventory inventory = new Inventory();
        inventory.setSku("SKU123");

        when(inventoryRepository.findBySku("SKU123")).thenReturn(Optional.of(inventory));

        InventoryDTO result = inventoryService.getInventoryBySku("SKU123");

        assertNotNull(result);
        assertEquals("SKU123", result.getSku());
    }

    @Test
    void testSaveInventory_Success() {
        SaveInventoryRequest request = new SaveInventoryRequest();
        request.setSku("SKU123");
        request.setQuantity(100);

        Inventory savedInventory = new Inventory();
        savedInventory.setId(1L);
        savedInventory.setSku("SKU123");
        savedInventory.setQuantity(100);
        savedInventory.setStatus(Inventory.Status.AVAILABLE);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        Inventory result = inventoryService.saveInventory(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateInventoryEntity_Success() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(50);

        Inventory updatedInventory = new Inventory();
        updatedInventory.setId(1L);
        updatedInventory.setQuantity(50);
        updatedInventory.setStatus(Inventory.Status.AVAILABLE);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        Inventory result = inventoryService.updateInventoryEntity(inventory);

        assertNotNull(result);
        assertEquals(Inventory.Status.AVAILABLE, result.getStatus());
    }

    @Test
    void testUpdateInventory_Success() {
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setQuantity(50);

        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setQuantity(100);

        Inventory updatedInventory = new Inventory();
        updatedInventory.setId(1L);
        updatedInventory.setQuantity(50);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        Inventory result = inventoryService.updateInventory(1L, request);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
    }

    @Test
    void testDeleteInventory_Success() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        doNothing().when(inventoryRepository).delete(inventory);

        inventoryService.deleteInventory(1L);

        verify(inventoryRepository, times(1)).delete(inventory);
    }
}
