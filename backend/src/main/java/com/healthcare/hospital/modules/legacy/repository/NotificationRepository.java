package com.healthcare.hospital.modules.legacy.repository;

import com.healthcare.hospital.modules.legacy.entity.Notification;
import com.healthcare.hospital.modules.legacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);
    long countByUserAndReadStatusFalse(User user);
    Optional<Notification> findByIdAndUser(Long id, User user);
}
