package com.gsrm.maas.repository;

import com.gsrm.maas.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdOrderByDateEnvoiDesc(Long idDestinataire);
    List<Notification> findByDestinataireIdAndLuFalseOrderByDateEnvoiDesc(Long idDestinataire);
    long countByDestinataireIdAndLuFalse(Long idDestinataire);
}
