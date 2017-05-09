package net.ukr.bekit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Александр on 04.05.2017.
 */
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "UsersTransactions",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "transaction_id", referencedColumnName = "id")}
    )
    List<Transaction> transactions = new ArrayList<>();


    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(Account account) {
        account.setUser(this);
        if (!accounts.contains(account))
            accounts.add(account);
    }

    public int makeTransactionOut(Transaction transaction) {
        int check = 0;
        for (Account account : accounts) {
            if (transaction.getCurrency().equalsIgnoreCase(account.getCurrency())) {
                for (Account acc : transaction.getTo().accounts) {
                    if (transaction.getCurrency().equalsIgnoreCase(acc.getCurrency())) {
                        double temp = account.getMoney() - transaction.getAmount();
                        if (temp > 0) {
                            account.setMoney(temp);
                            check = 1;
                        } else {
                            System.out.println("Not enough money");
                        }
                    } else {
                        System.out.println("There is no account with such currency");
                    }
                }
            } else {
                System.out.println("There is no account with such currency");
            }
        }
        return check;
    }

    public int makeTransactionIn(Transaction transaction) {
        int check = 0;
        for (Account account : accounts) {
            if (transaction.getCurrency().equalsIgnoreCase(account.getCurrency())) {
                double temp = account.getMoney() + transaction.getAmount();
                account.setMoney(temp);
                check = 1;
            }
        }
        if(check==0){
            System.out.println("There is no account with such currency");
        }
        return check;
    }

    public void addTransactionFrom(Transaction transaction) {
        transaction.setFrom(this);
        if (!transactions.contains(transaction))
            transactions.add(transaction);
    }

    public void addTransactionTo(Transaction transaction) {
        transaction.setTo(this);
        if (!transactions.contains(transaction))
            transactions.add(transaction);
    }

    public void addFunds(String currency, double amount) {
        Account ac = null;
        int check = 0;
        for (Account account : accounts) {
            if (currency.equalsIgnoreCase(account.getCurrency())) {
                account.setMoney(amount + account.getMoney());
                check = 0;
                break;
            } else {
                check = 1;
            }
        }
        if (check == 1) {
            if (currency.equalsIgnoreCase("USD") || currency.equalsIgnoreCase("EUR")
                    || currency.equalsIgnoreCase("UAH")) {
                ac = new Account(amount, currency);
                this.addAccount(ac);
            }
        }
    }

    public void convert(String from, String to, double amount, Exchange ex) {
        Account temp = null;
        int test = 0;
        for (Account account : accounts) {
            if (account.getCurrency().equalsIgnoreCase(from)) {
                int check = 0;
                for (Account ac : accounts) {
                    if (ac.getCurrency().equalsIgnoreCase(to)) {
                        if (account.getMoney() >= amount) {
                            account.setMoney(account.getMoney() - amount);
                            ac.setMoney(ac.getMoney() + ex.convert(from, to, amount));
                        } else {
                            System.out.println("Not enough money");
                        }
                        check = 1;
                    }
                }
                if (check == 0) {
                    if (to.equalsIgnoreCase("USD") || to.equalsIgnoreCase("EUR")
                            || to.equalsIgnoreCase("UAH")) {
                        if (account.getMoney() >= amount) {
                            temp = new Account(ex.convert(from, to, amount), to);
                            account.setMoney(account.getMoney() - amount);
                            test = 1;
                        } else {
                            System.out.println("Not enough money");
                        }
                    }
                }
            }
        }
        if (test == 1) {
            this.addAccount(temp);
        }
    }
    public void showTotal(Exchange ex){
        double result=0;
        for(Account account:accounts){
            result=result+ex.convert(account.getCurrency(),"UAH",account.getMoney());
        }
        System.out.println("Available money in UAH are: "+result+" UAH");
    }
}
