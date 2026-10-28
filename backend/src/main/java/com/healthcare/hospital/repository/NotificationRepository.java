package com.healthcare.hospital.repository;

import com.healthcare.hospital.entity.Notification;
import com.healthcare.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);
    long countByUserAndReadStatusFalse(User user);
}
