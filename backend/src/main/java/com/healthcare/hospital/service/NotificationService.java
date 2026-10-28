package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.Notification;
import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        if (user == null) {
            return 0;
        }
        return notificationRepository.countByUserAndReadStatusFalse(user);
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, String type) {
        if (user == null) {
            return null;
        }
        Notification notification = new Notification(user, title, message, type);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        if (user != null) {
            List<Notification> unread = notificationRepository.findByUserAndReadStatusFalseOrderByCreatedAtDesc(user);
            unread.forEach(n -> n.setReadStatus(true));
            notificationRepository.saveAll(unread);
        }
    }
}
