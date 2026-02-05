package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashFlowFetchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private LocalDateTime fetchedAt;

    public CashFlowFetchHistory(String accountNumber, LocalDate targetDate) {
        this.accountNumber = accountNumber;
        this.targetDate = targetDate;
        this.fetchedAt = LocalDateTime.now();
    }
}
