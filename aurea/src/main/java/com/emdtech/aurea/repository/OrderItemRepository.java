package com.emdtech.aurea.repository;

import com.emdtech.aurea.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_IdOrderByIdAsc(Long orderId);

    Optional<OrderItem> findByIdAndOrder_Id(
            Long itemId,
            Long orderId
    );
}