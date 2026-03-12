package com.revshop.userservice.repository;

import com.revshop.userservice.model.Address;
import com.revshop.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByUser(User user);

}
