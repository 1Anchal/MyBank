package MyBank.com.MyBank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private BigDecimal amount;
    private String type; 
    // e.g., "DEPOSIT", "WITHDRAWAL"
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    public Transaction() {
        // Default constructor
    }
    public Transaction(BigDecimal amount, String type, LocalDateTime timestamp, Account account) {
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.account = account;
    }
}
