package com.betolara1.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.request.UpdateInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryDTO;
import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @GetMapping("/get/getAll")
    public ResponseEntity<Page<InventoryDTO>> getAllInventory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            return ResponseEntity.ok(inventoryService.getAllInventory(page, size));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/get/status={status}")
    public ResponseEntity<Page<InventoryDTO>> getInventoryByStatus(
        @PathVariable Inventory.Status status,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            return ResponseEntity.ok(inventoryService.getInventoryByStatus(status, page, size));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/get/id={id}")
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long id,
        @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            InventoryDTO inventoryId = inventoryService.getInventoryById(id);
            return ResponseEntity.ok(inventoryId);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/get/sku={sku}")
    public ResponseEntity<InventoryDTO> getInventoryBySku(@PathVariable String sku,
        @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            InventoryDTO inventorySku = inventoryService.getInventoryBySku(sku);
            return ResponseEntity.ok(inventorySku);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(
            @Valid @RequestBody SaveInventoryRequest inventory,
            @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            Inventory newInventory = inventoryService.saveInventory(inventory);
            InventoryDTO inventoryDTO = new InventoryDTO(newInventory);
            return ResponseEntity.ok(inventoryDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateInventoryRequest inventory,
            @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            Inventory inventoryDb = inventoryService.updateInventory(id, inventory);
            InventoryDTO inventoryDTO = new InventoryDTO(inventoryDb);
            return ResponseEntity.ok(inventoryDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInventory(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role){
            
        if(role.equals("ADMIN")){
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok("Estoque deletado com sucesso!");
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}