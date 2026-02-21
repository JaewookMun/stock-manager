package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 종목코드
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 종목명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 시장구분 (KOSPI, KOSDAQ 등)
     */
    @Column(nullable = false)
    private String marketType;

    /**
     * 업종명
     */
    private String sectorName;

    @Builder
    public Stock(String code, String name, String marketType, String sectorName) {
        this.code = code;
        this.name = name;
        this.marketType = marketType;
        this.sectorName = sectorName;
    }
}
