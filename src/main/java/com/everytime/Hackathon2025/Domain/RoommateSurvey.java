package com.everytime.Hackathon2025.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Getter
public class RoommateSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "dorm_name", nullable = false)
    private String dormName;

    @Column(name = "clean_level", nullable = false)
    private String cleanLevel;

    @Column(nullable = false)
    private boolean smoking;

    @Column(length = 500) //기타
    private String etc;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RoommateSurvey(User user, String dormName, String cleanLevel, boolean smoking, String etc) {
        this.user = user;
        this.dormName = dormName;
        this.cleanLevel = cleanLevel;
        this.smoking = smoking;
        this.etc = etc;
        this.createdAt = LocalDateTime.now();

    }

}
