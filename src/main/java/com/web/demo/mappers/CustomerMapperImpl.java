package com.web.demo.mappers;

import com.product.dtos.customer.AddressRequest;
import com.product.dtos.customer.AddressResponse;
import com.product.dtos.customer.CustomerRequest;
import com.product.dtos.customer.CustomerResponse;
import com.web.demo.models.Address;
import com.web.demo.models.Customer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        return customer;
    }

    @Override
    public Address toEntity(AddressRequest request) {
        return Address.builder()
                .addressType(request.addressType())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .country(request.country())
                .isDefault(request.isDefault())
                .build();
    }

    @Override
    public CustomerResponse toResponse(Customer customer) {
        List<AddressResponse> addresses = customer.getAddresses()
                .stream()
                .map(this::toResponse)
                .toList();

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                addresses
        );
    }

    @Override
    public AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getAddressType(),
                address.getAddressLine1(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getIsDefault()
        );
    }

    @Override
    public Customer mapAndValidateCustomer(CustomerRequest request) {
        Customer customer = this.toEntity(request);

        if (request.addresses() == null || request.addresses().isEmpty()) {
            return customer;
        }

        Set<String> types = new HashSet<>();
        AtomicBoolean defaultFound = new AtomicBoolean(false);

        List<Address> addresses = request.addresses().stream()
                .map(addrReq -> {
                    Address address = this.toEntity(addrReq);

                    // ✅ Duplicate type check
                    if (!types.add(address.getAddressType())) {
                        throw new RuntimeException(
                                "Duplicate address type for customer: " + request.email()
                        );
                    }

                    // ✅ Only one default
                    if (Boolean.TRUE.equals(address.getIsDefault())) {
                        if (!defaultFound.compareAndSet(false, true)) {
                            throw new RuntimeException(
                                    "Multiple default addresses for customer: " + request.email()
                            );
                        }
                    }

                    address.setCustomer(customer);
                    return address;
                })
                .toList();

        customer.setAddresses(addresses);

        return customer;
    }
}
