package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.Order;
import com.rileyeatz.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}