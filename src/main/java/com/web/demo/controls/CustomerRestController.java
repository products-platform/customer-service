package com.web.demo.controls;

import com.product.dtos.customer.*;
import com.web.demo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @PostMapping("single")
    public ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PostMapping("multiple")
    public ResponseEntity<String> createCustomers(
            @RequestBody List<CustomerRequest> request) {
        return ResponseEntity.ok(customerService.createCustomers(request));
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable Long customerId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(customerService.addAddress(customerId, request));
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getAddresses(customerId));
    }

    @GetMapping("/{customerId}/{addressId}")
    public ResponseEntity<CustomerOrderResponse> getCustomerAndAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(customerService.getCustomerAndAddress(customerId,addressId));
    }

    @GetMapping("list")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping("/bulk")
    public List<CustomerDTO> getCustomers(
            @RequestBody List<Long> userIds) {
        return customerService.getCustomers(userIds);
    }
}

