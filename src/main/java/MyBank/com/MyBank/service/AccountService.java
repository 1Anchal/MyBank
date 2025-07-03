package MyBank.com.MyBank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import MyBank.com.MyBank.model.Account;
import MyBank.com.MyBank.model.Transaction;
import MyBank.com.MyBank.repository.AccountRepository;
import MyBank.com.MyBank.repository.TransactionRepository;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Account not found!"));
    }

    public Account registerAccount(String username,String password){
        if(accountRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exists!");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setBalance(BigDecimal.ZERO); // Initialize balance to zero
        return accountRepository.save(account);
    }

    public void deposit(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
            amount, 
            "DEPOSIT", 
            LocalDateTime.now(), 
            account
        );

        transactionRepository.save(transaction);
    }


        public void withdraw(Account account, BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

            Transaction transaction = new Transaction(
                amount,
                "WITHDRAWAL",
                LocalDateTime.now(),
                account
            );

            transactionRepository.save(transaction);
        }

    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountId(account.getId());
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Account account = findAccountByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found!");
        }
        return new Account(
            account.getUsername(),
            account.getPassword(),
            account.getBalance(),
            account.getTransactions(),
            getAuthorities(account)
        );
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        return Arrays.asList(new SimpleGrantedAuthority("User"));
    }

    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the source account");
        }
        Account toAccount = accountRepository.findByUsername(toUsername).orElseThrow(() -> new RuntimeException("Recipient account not found!"));

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);
        Transaction debitTransaction = new Transaction(
            amount,
            "TRANSFER out to "+ toAccount.getUsername() ,
            LocalDateTime.now(),
            fromAccount
        );
        transactionRepository.save(debitTransaction);

        Transaction creditTransaction = new Transaction(
            amount,
            "TRANSFER in from " + fromAccount.getUsername(),
            LocalDateTime.now(),
            toAccount
        );
        transactionRepository.save(creditTransaction);
    }
}
