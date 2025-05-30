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

    @Column(name = "sleep_time")
    private String sleepTime;

    @Column(name = "wake_up_time")
    private String wakeUpTime;

    @Column(nullable = false)
    private boolean snoring; //코골이 여부

//    @Column(name = "shower_time")
//    private String showerTime;



    //선호도/중요도 필드들 (1: 매우 안중요, 3: 보통, 5: 매우 중요)
    @Column(name = "dorm_importance", nullable = false)
    private int dormImportance = 3; //기본값 3

    @Column(name = "clean_importance", nullable = false)
    private int cleanImportance = 3;

    @Column(name = "sleep_time_importance", nullable = false)
    private int sleepTimeImportance = 3;

    @Column(name = "wake_up_time_importance", nullable = false)
    private int wakeUpTimeImportance = 3;

    //흡연 선호도(1: 비흡연자 선호, 2: 상관없음, 3: 흡연자 선호)
    @Column(name = "smoking_preference", nullable = false)
    private int smokingPreference = 2;

    //코골이 선호도(1: 코 안 골았으면 좋겠음, 2: 상관없음)
    @Column(name = "snoring_preference", nullable = false)
    private int snoringPreference = 2;

    @Column(length = 500)
    private String etc;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RoommateSurvey(User user, String dormName, String cleanLevel, boolean smoking, boolean snoring, String etc,
                          String sleepTime, String wakeUpTime,
                          int dormImportance, int cleanImportance, int sleepTimeImportance,
                          int wakeUpTimeImportance, int smokingPreference, int snoringPreference /*,String showerTime*/) {
        this.user = user;
        this.dormName = dormName;
        this.cleanLevel = cleanLevel;
        this.smoking = smoking;
        this.snoring = snoring;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.dormImportance = dormImportance;
        this.cleanImportance = cleanImportance;
        this.sleepTimeImportance = sleepTimeImportance;
        this.wakeUpTimeImportance = wakeUpTimeImportance;
        this.smokingPreference = smokingPreference;
        this.snoringPreference = snoringPreference;
        this.etc = etc;
        this.createdAt = LocalDateTime.now();
    }
}
