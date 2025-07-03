package MyBank.com.MyBank.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import MyBank.com.MyBank.model.Account;
import MyBank.com.MyBank.service.AccountService;

@Controller
public class BankController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/dashboard")
    public String dashboard(org.springframework.ui.Model model) {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        model.addAttribute("account", account);
        return "dashboard";
    }


    @GetMapping("/register")
    public String showRegisterationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(@RequestParam String username, @RequestParam String password,Model model) {
        try{
            accountService.registerAccount(username, password);
            return "redirect:/login";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/deposit")
    public String depositMoney(@RequestParam BigDecimal amount,Model model) {
        try {
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = accountService.findAccountByUsername(username);
            accountService.deposit(account, amount);
            return "redirect:/dashboard";
        }  catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        }
        
    }
    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount, Model model) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = accountService.findAccountByUsername(username);
            accountService.deposit(account, amount);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", accountService.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
            return "dashboard";
        }
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam BigDecimal amount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Account account = accountService.findAccountByUsername(username);
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
           
            
            accountService.withdraw(account, amount);
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", account);
            return "dashboard";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/transactions")
    public String transactionHistory(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        model.addAttribute("transactions", accountService.getTransactionHistory(account));
        return "transactions";
    }

    @PostMapping("/transfer")
    public String transferAmount(@RequestParam String toUsername,@RequestParam BigDecimal amount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account fromAccount = accountService.findAccountByUsername(username);
        try {
            accountService.transferAmount(fromAccount, toUsername, amount);
            Account toAccount = accountService.findAccountByUsername(toUsername);
            if (toAccount == null) {
                throw new IllegalArgumentException("Recipient account does not exist");
            }
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", fromAccount);
            return "dashboard";
        }
        return "redirect:/dashboard";
    }
}
