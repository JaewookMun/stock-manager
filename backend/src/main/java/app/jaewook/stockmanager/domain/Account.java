package app.jaewook.stockmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    private String alias;
    /**
     * 하이픈(-) 생략
     */
    @Column(unique = true)
    private String accountNumber;
    private String appkey;
    private String secretkey;

    public Account(AccountType type, String alias) {
        this.type = type;
        this.alias = alias;
    }
}
