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

    @Column(length = 500)
    private String etc;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sleep_time")
    private String sleepTime;

    @Column(name = "wake_up_time")
    private String wakeUpTime;

    @Column(name = "phone_time")
    private String phoneTime;

    @Column(name = "shower_time")
    private String showerTime;

    public RoommateSurvey(User user, String dormName, String cleanLevel, boolean smoking, String etc,
                          String sleepTime, String wakeUpTime, String phoneTime, String showerTime) {
        this.user = user;
        this.dormName = dormName;
        this.cleanLevel = cleanLevel;
        this.smoking = smoking;
        this.etc = etc;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.phoneTime = phoneTime;
        this.showerTime = showerTime;
        this.createdAt = LocalDateTime.now();
    }
}
