package com.web.demo.mappers;

import com.product.dtos.customer.AddressRequest;
import com.product.dtos.customer.AddressResponse;
import com.product.dtos.customer.CustomerRequest;
import com.product.dtos.customer.CustomerResponse;
import com.web.demo.models.Address;
import com.web.demo.models.Customer;

public interface CustomerMapper {

    Customer toEntity(CustomerRequest request);

    Address toEntity(AddressRequest request);

    CustomerResponse toResponse(Customer customer);

    AddressResponse toResponse(Address address);

    Customer mapAndValidateCustomer(CustomerRequest customerRequest);
}
