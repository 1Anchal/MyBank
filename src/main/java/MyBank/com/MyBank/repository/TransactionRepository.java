package MyBank.com.MyBank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import MyBank.com.MyBank.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccountId(Long accountId);
    
    
}
