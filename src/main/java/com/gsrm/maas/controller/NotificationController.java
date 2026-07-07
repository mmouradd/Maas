package com.gsrm.maas.controller;

import com.gsrm.maas.dto.ApiMessage;
import com.gsrm.maas.entity.Notification;
import com.gsrm.maas.entity.Utilisateur;
import com.gsrm.maas.service.AuthService;
import com.gsrm.maas.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<Notification>> mesNotifications(
            @RequestParam(defaultValue = "false") boolean nonLues,
            @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur u = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.mesNotifications(u.getId(), nonLues));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> compterNonLues(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur u = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("nonLues", notificationService.compterNonLues(u.getId())));
    }

    @PatchMapping("/{id}/lu")
    public ResponseEntity<ApiMessage> marquerCommeLue(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur u = authService.getCurrentUser(userDetails.getUsername());
        notificationService.marquerCommeLue(id, u.getId());
        return ResponseEntity.ok(new ApiMessage("Notification marquée comme lue."));
    }
}
