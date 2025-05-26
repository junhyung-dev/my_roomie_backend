package com.everytime.Hackathon2025.Controller;

import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.MatchingResultResponseDto;
import com.everytime.Hackathon2025.Dto.RoommateSurveyRequestDto;
import com.everytime.Hackathon2025.Dto.RoommateSurveyResponseDto;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Service.AuthService;
import com.everytime.Hackathon2025.Service.RoommateSurveyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoommateSurveyController {
    private final AuthService authService;
    private final RoommateSurveyService roommateSurveyService;

    @Autowired
    public RoommateSurveyController(AuthService authService, RoommateSurveyService roommateSurveyService) {
        this.authService = authService;
        this.roommateSurveyService = roommateSurveyService;

    }

    //설문조사 만들기
    @PostMapping("/surveys")
    public ResponseEntity<RoommateSurveyResponseDto> createSurvey(@RequestBody RoommateSurveyRequestDto roommateSurveyRequestDto, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request); // 현재 로그인한 유저의 ID
        RoommateSurveyResponseDto roommateSurveyResponseDto = roommateSurveyService.createSurvey(currentUser, roommateSurveyRequestDto);
        return ResponseEntity.ok(roommateSurveyResponseDto);

    }
    //설문조사 목록 가져오기
    @GetMapping("/surveys/list")
    public ResponseEntity<List<RoommateSurveyResponseDto>> getSurveyList(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        List<RoommateSurveyResponseDto> list =
                roommateSurveyService.getAllList(currentUser);
        return ResponseEntity.ok(list);
    }

    //내 설문조사 목록 가져오기
    @GetMapping("/surveys/my")
    public ResponseEntity<List<RoommateSurveyResponseDto>> getMySurveyList(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        List<RoommateSurveyResponseDto> list = roommateSurveyService.getMySurveyList(currentUser);
        return ResponseEntity.ok(list);

    }

    //매칭 하기 EX) GET /surveys/matching?id=123
    @GetMapping("/surveys/matching")
    public ResponseEntity<List<MatchingResultResponseDto>> getMatchingSurveyList(@RequestParam("id") long id, HttpServletRequest request) {
        //해당 survey id를 받음
        User currentUser = authService.getCurrentUser(request);
        List<MatchingResultResponseDto> list = roommateSurveyService.getMatchingResult(id, currentUser);

        return ResponseEntity.ok(list);
    }

    //설문 조사 삭제하기 EX) DELETE /surveys/delete?id=123
    @DeleteMapping("/surveys/delete")
    public ResponseEntity<Void> deleteSurvey(@RequestParam("id") long id, HttpServletRequest request) {
        //해당 survey id를 받음
        User currentUser = authService.getCurrentUser(request);
        roommateSurveyService.deleteSurvey(id, currentUser);

        return ResponseEntity.ok().build();

    }

}

