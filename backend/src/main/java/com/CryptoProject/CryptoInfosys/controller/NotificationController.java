package com.CryptoProject.CryptoInfosys.controller;

import com.CryptoProject.CryptoInfosys.security.JwtUtils;
import com.CryptoProject.CryptoInfosys.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtils jwtUtils;

    public NotificationController(
            NotificationService notificationService,
            JwtUtils jwtUtils
    ) {
        this.notificationService = notificationService;
        this.jwtUtils = jwtUtils;
    }

    // --------------------------------------------------
    // GET ALL NOTIFICATIONS (for logged-in user)
    // --------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtils.extractUsername(token);

        return ResponseEntity.ok(
                notificationService.getUserNotifications(email)
        );
    }

    // --------------------------------------------------
    // GET UNREAD COUNT (for 🔔 badge)
    // --------------------------------------------------
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtils.extractUsername(token);

        return ResponseEntity.ok(
                notificationService.getUnreadCount(email)
        );
    }

    // --------------------------------------------------
    // MARK NOTIFICATION AS READ
    // --------------------------------------------------
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtils.extractUsername(token);

        notificationService.markAsRead(id, email);
        return ResponseEntity.ok().build();
    }
 // --------------------------------------------------
 // MARK ALL AS READ
 // --------------------------------------------------
 @PostMapping("/read-all")
 public ResponseEntity<?> markAllAsRead(
         @RequestHeader("Authorization") String authHeader
 ) {
     String token = authHeader.substring(7);
     String email = jwtUtils.extractUsername(token);

     notificationService.markAllAsRead(email);
     return ResponseEntity.ok().build();
 }

}
