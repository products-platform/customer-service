package com.web.demo.mappers;

import com.product.dtos.customer.*;
import com.web.demo.models.Address;
import com.web.demo.models.Customer;
import org.jspecify.annotations.Nullable;

public interface CustomerMapper {

    Customer toEntity(CustomerRequest request);

    Address toEntity(AddressRequest request);

    CustomerResponse toResponse(Customer customer);

    AddressResponse toResponse(Address address);

    Customer mapAndValidateCustomer(CustomerRequest customerRequest);

    @Nullable CustomerOrderResponse mapToResponse(Customer customer, Address address);
}
