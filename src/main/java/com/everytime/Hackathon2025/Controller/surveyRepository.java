@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyRepository surveyRepository;

    public SurveyController(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    // 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Survey not found");
        }

        surveyRepository.deleteById(id);
        return ResponseEntity.ok("Survey deleted successfully");
    }
}
DELETE /api/surveys/1
"Survey deleted successfully"
"Survey not found"
@Entity
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate deadline;

    // Getters and setters
}
public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
