package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockUpdateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StockUpdateStatus status;

    private int kospiCount;

    private int kosdaqCount;

    private int totalSavedCount;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    public static StockUpdateLog start() {
        StockUpdateLog log = new StockUpdateLog();
        log.status = StockUpdateStatus.RUNNING;
        log.startedAt = LocalDateTime.now();
        return log;
    }

    public void success(int kospiCount, int kosdaqCount, int totalSavedCount) {
        this.status = StockUpdateStatus.SUCCESS;
        this.kospiCount = kospiCount;
        this.kosdaqCount = kosdaqCount;
        this.totalSavedCount = totalSavedCount;
        this.completedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = StockUpdateStatus.FAILED;
        this.errorMessage = errorMessage != null && errorMessage.length() > 1000
                ? errorMessage.substring(0, 1000)
                : errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public enum StockUpdateStatus {
        RUNNING, SUCCESS, FAILED
    }
}
