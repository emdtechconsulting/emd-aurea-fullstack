package com.emdtech.aurea.repository;

import com.emdtech.aurea.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long>,
                JpaSpecificationExecutor<Order> {

    Optional<Order> findByFirebaseDocumentId(
            String firebaseDocumentId
    );

    boolean existsByFirebaseDocumentId(
            String firebaseDocumentId
    );
}