package com.urlshortenerserver.server.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "click", indexes = {
        @Index(name = "idx_click_code", columnList = "code"),
        @Index(name = "idx_click_clicked_at", columnList = "clicked_at") // Fixed: changed from clickedAt to clicked_at
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Click {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(name = "ip_address")
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 1000)
    private String referer;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    @PrePersist
    protected void onCreate() {
        if (this.clickedAt == null) {
            this.clickedAt = Instant.now();
        }
    }
}
