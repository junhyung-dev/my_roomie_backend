@Entity
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate deadline;

    // Getter, Setter
}
public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
public class SurveyUpdateRequest {
    private String title;
    private String description;
    private LocalDate deadline;

    // Getter, Setter
}
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyRepository surveyRepository;

    public SurveyController(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSurvey(@PathVariable Long id, @RequestBody SurveyUpdateRequest request) {
        Optional<Survey> optionalSurvey = surveyRepository.findById(id);
        if (optionalSurvey.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Survey not found");
        }

        Survey survey = optionalSurvey.get();

        if (request.getTitle() != null) {
            survey.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            survey.setDescription(request.getDescription());
        }
        if (request.getDeadline() != null) {
            survey.setDeadline(request.getDeadline());
        }

        surveyRepository.save(survey);
        return ResponseEntity.ok("Survey updated successfully");
    }
}
