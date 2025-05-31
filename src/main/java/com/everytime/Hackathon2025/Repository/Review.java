@Entity
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 점수 (예: 1~5)
    private String comment;

    @ManyToOne
    private User reviewer;

    @ManyToOne
    private User targetUser;

    private LocalDate reviewedAt;
}
