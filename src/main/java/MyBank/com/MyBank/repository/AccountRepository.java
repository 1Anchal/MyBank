package MyBank.com.MyBank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import MyBank.com.MyBank.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // Additional query methods can be defined here if needed
    Optional<Account> findByUsername(String username);
}
