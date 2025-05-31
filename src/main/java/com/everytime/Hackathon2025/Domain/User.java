package com.everytime.Hackathon2025.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String bio;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    // ⭐ 평점 관련 필드
    @Column(name = "average_rating")
    private double averageRating = 0.0;

    @Column(name = "rating_count")
    private int ratingCount = 0;

    // ⭐ 받은 리뷰 리스트
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = null;
        this.joinedAt = LocalDateTime.now();
    }

    // ⭐ 별점 갱신 메서드
    public void addRating(int newRating) {
        double total = this.averageRating * this.ratingCount;
        this.ratingCount += 1;
        this.averageRating = (total + newRating) / this.ratingCount;
    }
}
