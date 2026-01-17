package com.CryptoProject.CryptoInfosys.service;

import com.CryptoProject.CryptoInfosys.model.Notification;
import com.CryptoProject.CryptoInfosys.model.User;
import com.CryptoProject.CryptoInfosys.repository.NotificationRepository;
import com.CryptoProject.CryptoInfosys.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    public NotificationService(
            NotificationRepository notificationRepo,
            UserRepository userRepo
    ) {
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }

    // --------------------------------------------------
    // CREATE NOTIFICATION
    // --------------------------------------------------
    public void createNotification(
            String email,
            String title,
            String message,
            String type
    ) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepo.save(notification);
    }

    // --------------------------------------------------
    // GET ALL NOTIFICATIONS FOR USER
    // --------------------------------------------------
    public List<Notification> getUserNotifications(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepo.findByUserOrderByCreatedAtDesc(user);
    }

    // --------------------------------------------------
    // COUNT UNREAD NOTIFICATIONS
    // --------------------------------------------------
    public long getUnreadCount(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepo.countByUserAndIsReadFalse(user);
    }

    // --------------------------------------------------
    // MARK NOTIFICATION AS READ
    // --------------------------------------------------
    public void markAsRead(Long notificationId, String email) {

        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // 🔐 Security check: ensure notification belongs to user
        if (!notification.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        notification.setRead(true);
        notificationRepo.save(notification);
    }
    public boolean hasNotification(
            String email,
            String title,
            String type
    ) {
    	
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepo
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .anyMatch(n ->
                        n.getTitle().equals(title)
                        && n.getType().equals(type)
                );
    }
 // --------------------------------------------------
 // MARK ALL NOTIFICATIONS AS READ
 // --------------------------------------------------
 public void markAllAsRead(String email) {

     User user = userRepo.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("User not found"));

     List<Notification> notifications =
             notificationRepo.findByUserOrderByCreatedAtDesc(user);

     notifications.forEach(n -> n.setRead(true));
     notificationRepo.saveAll(notifications);
 }

}
