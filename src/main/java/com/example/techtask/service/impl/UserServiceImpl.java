package com.example.techtask.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.OrderStatus;
import com.example.techtask.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findUser() {
        LocalDateTime startDate = LocalDateTime.of(2003, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2003, 12, 31, 23, 59, 59);
        return findUsersWithStatusBetweenDates(startDate, endDate, OrderStatus.DELIVERED, true);
    }

    @Override
    public List<User> findUsers() {
        LocalDateTime startDate = LocalDateTime.of(2010, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2010, 12, 31, 23, 59, 59);
        return findUsersWithStatusBetweenDates(startDate, endDate, OrderStatus.PAID, false);
    }

    private <T> T findUsersWithStatusBetweenDates(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status, boolean singleResult) {
        if (startDate.isAfter(endDate)) {
            return null;
        }

        String jpql = "SELECT u "
                + "FROM User u "
                + "JOIN Order o ON u.id = o.userId "
                + "WHERE o.createdAt BETWEEN :startDate AND :endDate "
                + "AND CAST(o.orderStatus AS string) = :status "
                + (singleResult ? "GROUP BY u.id ORDER BY SUM(o.price * o.quantity) DESC" : "");

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("status", status.toString());

        try {
            if (singleResult) {
                query.setMaxResults(1);
                return (T) query.getSingleResult();
            } else {
                return (T) query.getResultList();
            }
        } catch (NoResultException e) {
            return null;
        }
    }
}
