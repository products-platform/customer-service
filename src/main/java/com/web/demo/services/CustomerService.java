package com.web.demo.services;

import com.product.dtos.customer.*;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Long id);

    List<CustomerDTO> getCustomers(List<Long> userIds);

    CustomerResponse createCustomer(CustomerRequest request);

    AddressResponse addAddress(Long customerId, AddressRequest request);

    List<AddressResponse> getAddresses(Long customerId);

    String createCustomers(List<CustomerRequest> request);

    @Nullable CustomerOrderResponse getCustomerAndAddress(Long customerId, Long addressId);
}
