package com.everytime.Hackathon2025.Service;

import com.everytime.Hackathon2025.Domain.RoommateSurvey;
import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.MatchingResultResponseDto;
import com.everytime.Hackathon2025.Dto.RoommateSurveyRequestDto;
import com.everytime.Hackathon2025.Dto.RoommateSurveyResponseDto;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Exception.InvalidAccessException;
import com.everytime.Hackathon2025.Repository.RoommateSurveyRepository;
import com.everytime.Hackathon2025.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoommateSurveyService {
    private final RoommateSurveyRepository roommateSurveyRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public RoommateSurveyService(RoommateSurveyRepository roommateSurveyRepository, UserService userService, UserRepository userRepository) {
        this.roommateSurveyRepository = roommateSurveyRepository;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    public void saveSurvey(RoommateSurvey survey) {
        RoommateSurvey savedSurvey = roommateSurveyRepository.save(survey);
    }

    public RoommateSurveyResponseDto createSurvey(User user, RoommateSurveyRequestDto requestDto) {
        RoommateSurvey survey = new RoommateSurvey(
                user,
                requestDto.getDormName(),
                requestDto.getCleanLevel(),
                requestDto.isSmoking(),
                requestDto.isSnoring(),
                requestDto.getEtc(),
                requestDto.getSleepTime(),
                requestDto.getWakeUpTime(),
                requestDto.getDormImportance(),
                requestDto.getCleanImportance(),
                requestDto.getSleepTimeImportance(),
                requestDto.getWakeUpTimeImportance(),
                requestDto.getSmokingPreference(),
                requestDto.getSnoringPreference()
        );

        saveSurvey(survey);
        return convertSurveyToDto(user, survey);

    }

    private RoommateSurveyResponseDto convertSurveyToDto(User currentUser, RoommateSurvey survey) {
        User author = survey.getUser();
        UserSimpleResponseDto userSimpleResponseDto = userService.convertUserToSimpleDto(author, author);
        boolean isMine = currentUser.getUsername().equals(author.getUsername());

       return new RoommateSurveyResponseDto(
        survey.getId(),
        userSimpleResponseDto,
        survey.getDormName(),
        survey.getCleanLevel(),
        survey.isSmoking(),
        survey.isSnoring(),
        survey.getEtc(),
        survey.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")),
        isMine,
        survey.getSleepTime(),
        survey.getWakeUpTime()

       );

    }

    public List<RoommateSurveyResponseDto> getAllList(User currentUser){
        List<RoommateSurvey> list = roommateSurveyRepository.findAll();
        return list.stream()
                .map(roommateSurvey -> convertSurveyToDto(currentUser, roommateSurvey))
                .toList();
    }

    /** 현재 로그인한 사용자의 설문만 반환 */
    public List<RoommateSurveyResponseDto> getMySurveyList(User currentUser){
        List<RoommateSurvey> mySurveys = roommateSurveyRepository.findByUser(currentUser);

        return mySurveys.stream()
                .map(survey -> convertSurveyToDto(currentUser, survey))
                .toList();


    }

    /** 매칭 결과의 List를 반환 */
    public List<MatchingResultResponseDto> getMatchingResult(long id, User currentUser){
        RoommateSurvey submittedSurvey = roommateSurveyRepository.findById(id);
        if (submittedSurvey == null) {
            throw new EntityNotFoundException("Survey not found with id: " + id);
        }
        //모든 설문조사의 list를 받되, 내 설문조사들은 제외해야함
        List<RoommateSurvey> surveysExcludingCurrentUser = roommateSurveyRepository
                .findByUserIdNot(currentUser.getId());

        List<MatchingResultResponseDto> matchingResults = surveysExcludingCurrentUser.stream()
                .map(survey -> {
                    User author = survey.getUser();
                    UserSimpleResponseDto userSimpleResponseDto = userService.convertUserToSimpleDto(author, author);
                    double matchingRate = calculateMatchingRate(submittedSurvey, survey);
                    double matchingScore = calculateWeightedMatchingScore(submittedSurvey, survey);
                    return new MatchingResultResponseDto(
                            survey.getId(),
                            userSimpleResponseDto,
                            survey.getDormName(),
                            survey.getCleanLevel(),
                            survey.isSmoking(),
                            survey.isSnoring(),
                            survey.getSleepTime(),
                            survey.getWakeUpTime(),
                            survey.getEtc(),
                            survey.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")),
                            matchingRate,  //단순 설문조사끼리 일치율
                            matchingScore //선호도 기반 매칭점수

                    );
                })
                .sorted(Comparator.comparing(MatchingResultResponseDto::getMatchingScore).reversed()) // 일치율 높은 순으로 정렬
                .collect(Collectors.toList());

        return matchingResults;

    }


    /**
     * 코골이 관련 점수 계산
     * @param mySurvey 내 설문
     * @param otherSurvey 상대방 설문
     * @return 0.0 ~ 1.0 사이의 점수
     */
    private double calculateSnoringScore(RoommateSurvey mySurvey, RoommateSurvey otherSurvey) {
        int myPreference = mySurvey.getSnoringPreference(); //1: 코 안 골았으면 좋겠음, 2: 상관없음
        boolean otherSnoring = otherSurvey.isSnoring();

        switch (myPreference) {
            case 1: //코 안 골았으면 좋겠음
                return otherSnoring ? 0.0 : 1.0; //다른 설문조사가 코골면 - 0점
            case 2: //상관없음
                return 0.7; //높은 중간 점수
            default:
                return 0.5;
        }
    }


    /**
     * update방안 : 나한테는 ‘청결도’가 훨씬 중요한데 ‘흡연 여부’는 별로 신경 쓰지 않는다” 같은 상황을 반영할 수 있도록 수정
     * 각 항목에 중요도 1(전혀 중요x) ~ 5(매우 중요함)을 매길 수 있도록 하여,
     * 또한, cleanLevel같이 순서형(1회~5회)의 경우, 비교대상이 3회면, 기준설문조사가 1회면, 1 - (abs(3-1))/ (5 - 1) 등으로, 비교대상과 기준 설문조사 간격이 좁을수록 높은 점수를 부여하도록
     * 기숙사명은 동일시에만 점수부여
     * (각 점수에 각 중요도가중치 (1~5)를 곱해서 더함 / 총 중요도가중치합) * 100 = 최종 유사도 점수
     * @param survey1
     * @param survey2
     * @return
     */
    private double calculateMatchingRate(RoommateSurvey survey1, RoommateSurvey survey2) {
        int totalCriteria = 6;
        int matchCount = 0;

        // 기숙사 이름 비교
        if (survey1.getDormName().equals(survey2.getDormName())) {
            matchCount++;
        }

        // 청결도 비교
        if (survey1.getCleanLevel().equals(survey2.getCleanLevel())) {
            matchCount++;
        }

        // 흡연 여부 비교
        if (survey1.isSmoking() == survey2.isSmoking()) {
            matchCount++;
        }
        if (survey1.isSnoring() == survey2.isSnoring())                matchCount++;
        if (survey1.getSleepTime().equals(survey2.getSleepTime()))     matchCount++;
        if (survey1.getWakeUpTime().equals(survey2.getWakeUpTime()))   matchCount++;

        // 일치율 계산 (백분율)
        return (double) matchCount / totalCriteria * 100.0;
    }

    //가중치를 반영한 매칭 점수 계산
    //고려할점 -> 각자 지정한 가중치가 다르기 때문에 내 기준 상대방의 점수와, 상대방 기준 내 점수가 다름을 유의
    /**
     * 가중치를 반영한 매칭 점수 계산
     * @param mySurvey 내 설문
     * @param otherSurvey 비교 대상 설문조사((상대)
     * @return 매칭 점수 (0.0 ~ 100.0)
     */
    private double calculateWeightedMatchingScore(RoommateSurvey mySurvey, RoommateSurvey otherSurvey) {
        double weightedSum = 0.0;
        int totalWeight = 0;

        //1. 기숙사명 매칭(완전 일치만 점수 부여)
        if (mySurvey.getDormImportance() > 0) {
            totalWeight += mySurvey.getDormImportance();
            if (mySurvey.getDormName().equals(otherSurvey.getDormName())) {
                weightedSum += mySurvey.getDormImportance();
            }
        }

        //2. 청소 횟수 매칭 (거리 기반 점수)
        if (mySurvey.getCleanImportance() > 0) {
            totalWeight += mySurvey.getCleanImportance();
            double cleanScore = calculateProximityScore(
                    Integer.parseInt(mySurvey.getCleanLevel()),
                    Integer.parseInt(otherSurvey.getCleanLevel()),
                    5
            );
            weightedSum += mySurvey.getCleanImportance() * cleanScore;
        }

        //3. 취침시간 매칭
        if (mySurvey.getSleepTimeImportance() > 0) {
            totalWeight += mySurvey.getSleepTimeImportance();
            double sleepScore = calculateProximityScore(
                    Integer.parseInt(mySurvey.getSleepTime()),
                    Integer.parseInt(otherSurvey.getSleepTime()),
                    5
            );
            weightedSum += mySurvey.getSleepTimeImportance() * sleepScore;
        }

        //4. 기상시간 매칭
        if (mySurvey.getWakeUpTimeImportance() > 0) {
            totalWeight += mySurvey.getWakeUpTimeImportance();
            double wakeUpScore = calculateProximityScore(
                    Integer.parseInt(mySurvey.getWakeUpTime()),
                    Integer.parseInt(otherSurvey.getWakeUpTime()),
                    5
            );
            weightedSum += mySurvey.getWakeUpTimeImportance() * wakeUpScore;
        }

        //5. 흡연 선호도 매칭 (중요) -> 별도 가중치 적용)
        double smokingScore = calculateSmokingScore(mySurvey, otherSurvey);
        weightedSum += smokingScore * 3; //흡연은 중요도 3고정
        totalWeight += 3;

        //6. 코골이 선호도 매칭
        double snoringScore = calculateSnoringScore(mySurvey, otherSurvey);
        weightedSum += snoringScore * 3;
        totalWeight += 3;


        if (totalWeight == 0) return 0.0;

        //백분율
        return (weightedSum / totalWeight) * 100.0;
    }
    /**
     * 근접도 기반 점수 계산 (1~5 범위의 값들에 대해)
     * ex) 내가 주3회, 상대1: 주 7회, 상대2: 주 4회 (나와 같지는 않지만 더 유사한 상대2에게 높은 점수)
     * @param myValue 내 값
     * @param otherValue 상대방 값
     * @param maxValue 최대값(5)
     * @return 0.0 ~ 1.0 사이의 점수
     */
    private double calculateProximityScore(int myValue, int otherValue, int maxValue) {
        if (myValue == otherValue) return 1.0; //완전 일치시 1

        int difference = Math.abs(myValue - otherValue);
        int maxDifference = maxValue - 1; //최대 차이값 (4)

        return Math.max(0.0, 1.0 - (double) difference / maxDifference);
    }

    /**
     * 흡연 관련 점수 계산
     * @param mySurvey 내 설문
     * @param otherSurvey 상대방 설문
     * @return 0.0 ~ 1.0 사이의 점수
     */
    private double calculateSmokingScore(RoommateSurvey mySurvey, RoommateSurvey otherSurvey) {
        int myPreference = mySurvey.getSmokingPreference(); //1: 비흡연 선호, 2: 상관없음, 3: 흡연 선호
        boolean otherSmoking = otherSurvey.isSmoking();

        switch (myPreference) {
            case 1: //비흡연자 선호
                return otherSmoking ? 0.0 : 1.0;
            case 2: //상관없음
                return 0.7; //중간 점수
            case 3: //흡연자 선호
                return otherSmoking ? 1.0 : 0.3;
            default:
                return 0.5;
        }
    }

    public void deleteSurvey(long id, User requestUser) {
        RoommateSurvey survey = roommateSurveyRepository.findById(id);

        if (survey == null) {
            throw new EntityNotFoundException("Survey not found with id: " + id);
        }
        //만약 survey의 author가 user게 아니라면
        if(!survey.getUser().getUsername().equals(requestUser.getUsername())) {
            throw new InvalidAccessException();

        }

        roommateSurveyRepository.deleteById(id);


    }

}
