package com.web.demo.services;

import com.web.demo.records.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Customer getCustomerById(Long id);

    List<Customer> getCustomers(List<Long> userIds);
}
