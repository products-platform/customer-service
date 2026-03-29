package com.web.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.product.dtos.customer.*;
import com.web.demo.exceptions.ResourceNotFoundException;
import com.web.demo.mappers.CustomerMapper;
import com.web.demo.models.Address;
import com.web.demo.models.Customer;
import com.web.demo.reader.JsonFileReader;
import com.web.demo.repos.AddressRepository;
import com.web.demo.repos.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final String FILE_NAME = "customers.json";

    private final JsonFileReader jsonFileReader;

    private List<CustomerDTO> customers;

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerMapper customerMapper;


    @PostConstruct
    public void loadCustomers() {
        this.customers = jsonFileReader.readListFromFile(
                FILE_NAME,
                new TypeReference<>() {
                }
        );
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);

        if (request.addresses() != null && !request.addresses().isEmpty()) {

            Set<String> types = new HashSet<>();
            boolean defaultFound = false;

            List<Address> addresses = request.addresses().stream()
                    .map(customerMapper::toEntity)
                    .peek(addr -> {

                        // ✅ Unique address type check
                        if (!types.add(addr.getAddressType())) {
                            throw new RuntimeException("Duplicate address type not allowed");
                        }

                        // ✅ Only one default
                        if (Boolean.TRUE.equals(addr.getIsDefault())) {
                            if (defaultFound) {
                                throw new RuntimeException("Only one default address allowed");
                            }
                        }

                        addr.setCustomer(customer);
                    })
                    .toList();

            customer.setAddresses(addresses);
        }

        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public String createCustomers(List<CustomerRequest> requests) {
        List<Customer> customers = requests.stream()
                .map(customerMapper::mapAndValidateCustomer)
                .toList();
        customerRepository.saveAll(customers);
        return "Customers created successfully: " + customers.size();
    }

    @Override
    public @Nullable CustomerOrderResponse getCustomerAndAddress(Long customerId, Long addressId) {
        Address address = addressRepository
                .getCustomerAndAddress(customerId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Customer customer = address.getCustomer();

        return customerMapper.mapToResponse(customer, address);
    }

    @Override
    public AddressResponse addAddress(Long customerId, AddressRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // ✅ Check duplicate type
        boolean exists = customer.getAddresses().stream()
                .anyMatch(a -> a.getAddressType().equals(request.addressType()));

        if (exists) {
            throw new RuntimeException("Address type already exists");
        }

        // ✅ Handle default
        if (Boolean.TRUE.equals(request.isDefault())) {
            customer.getAddresses().forEach(a -> a.setIsDefault(false));
        }

        Address address = customerMapper.toEntity(request);
        address.setCustomer(customer);

        return customerMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> getAddresses(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return customer.getAddresses()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customers;
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.userId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public List<CustomerDTO> getCustomers(List<Long> userIds) {
        Set<Long> userIdSet = new HashSet<>(userIds);
        return customers.stream()
                .filter(customer -> userIdSet.contains(customer.userId())).toList();
    }
}

