package com.example.techtask.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.techtask.model.Order;
import com.example.techtask.model.enumiration.UserStatus;
import com.example.techtask.service.OrderService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class OrderServiceImpl implements OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Order findOrder() {
        int quantity = 1;
        return findOrderLatestMoreThatQuantityInternal(quantity);
    }

    @Override
    public List<Order> findOrders() {
        UserStatus status = UserStatus.ACTIVE;
        return findOrdersByStatusInternal(status);
    }

    private Order findOrderLatestMoreThatQuantityInternal(int quantity) {
        if (quantity < 0) {
            return null;
        }

        String jpql = "SELECT o FROM Order o WHERE quantity > :quantity ORDER BY createdAt DESC";

        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        query.setParameter("quantity", quantity);

        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<Order> findOrdersByStatusInternal(UserStatus status) {
        String jpql = "SELECT o "
                + "FROM Order o "
                + "WHERE o.userId IN "
                + "(SELECT u.id FROM User u "
                + "WHERE CAST(u.userStatus AS string) = :status) "
                + "ORDER BY o.createdAt";

        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);

        query.setParameter("status", status.toString());

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
