package com.CryptoProject.CryptoInfosys.repository;

import com.CryptoProject.CryptoInfosys.model.Notification;
import com.CryptoProject.CryptoInfosys.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user (latest first)
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Count unread notifications
    long countByUserAndIsReadFalse(User user);
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);
}
