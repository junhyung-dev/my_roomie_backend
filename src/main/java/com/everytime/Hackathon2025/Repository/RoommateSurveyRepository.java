package com.everytime.Hackathon2025.Repository;

import com.everytime.Hackathon2025.Domain.RoommateSurvey;
import com.everytime.Hackathon2025.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoommateSurveyRepository extends JpaRepository<RoommateSurvey, Long> {
    List<RoommateSurvey> findAllByUser(User user);
    List<RoommateSurvey> findByUser(User user);

    RoommateSurvey findById(long id);

    List<RoommateSurvey> findByUserIdNot(long userId);
}
