package com.web.demo.repos;

import com.web.demo.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a JOIN FETCH a.customer c " +
            "WHERE c.id = :customerId AND a.id = :addressId")
    Optional<Address> getCustomerAndAddress(@Param("customerId") Long customerId,
                                            @Param("addressId") Long addressId);
}
