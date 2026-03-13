package com.betolara1.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.request.UpdateInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryDTO;
import com.betolara1.inventory.exception.NotFoundException;
import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public Page<InventoryDTO> getAllInventory(int page, int size){
        Page<Inventory> inventoryPage = inventoryRepository.findAll(PageRequest.of(page, size));
        return inventoryPage.map(InventoryDTO::new);
    }

    public Page<InventoryDTO> getInventoryByStatus(Inventory.Status status, int page, int size){
        Page<Inventory> inventoryPage = inventoryRepository.findByStatus(status, PageRequest.of(page, size));
        return inventoryPage.map(InventoryDTO::new);
    }

    public InventoryDTO getInventoryById(Long id){
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Estoque não encontrado com ID: " + id));
        return new InventoryDTO(inventory);
    }

    public Inventory findBySkuEntity(String sku) {
        return inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Estoque não encontrado com SKU: " + sku));
    }

    public InventoryDTO getInventoryBySku(String sku){
        return new InventoryDTO(findBySkuEntity(sku));
    }

    @Transactional
    public Inventory saveInventory(SaveInventoryRequest inventory){
        Inventory newInventory = new Inventory();
        newInventory.setSku(inventory.getSku());
        newInventory.setQuantity(inventory.getQuantity());
        newInventory.setStatus(newInventory.hasItemStatus());

        return inventoryRepository.save(newInventory);
    }

    @Transactional
    public Inventory updateInventoryEntity(Inventory inventory) {
        inventory.setStatus(inventory.hasItemStatus());
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateInventory(Long id, UpdateInventoryRequest request){
        Inventory findInventory = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Estoque não encontrado com ID: " + id));
        
        if(request.getSku() != null){
            findInventory.setSku(request.getSku());
        }
        if(request.getQuantity() != null){
            findInventory.setQuantity(request.getQuantity());
        }
        if(request.getStatus() != null){
            findInventory.setStatus(request.getStatus());
        }

        return inventoryRepository.save(findInventory);
    }

    @Transactional
    public void deleteInventory(Long id){
        Inventory findInventory = inventoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Estoque não encontrado com ID: " + id));
        findInventory.setStatus(Inventory.Status.CANCELLED);
        inventoryRepository.save(findInventory);
    }
}
