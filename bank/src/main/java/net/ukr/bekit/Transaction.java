package net.ukr.bekit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Александр on 04.05.2017.
 */
@Entity
@Table(name="Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="userFrom_id")
    private User from=new User();

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="userTo_id")
    private User to=new User();

    @ManyToMany(mappedBy = "transactions", cascade = CascadeType.ALL)
    List<User> users=new ArrayList<>();

    @Column(name="amount")
    private double amount;

    @Column (name="currency")
    private String currency;

    @Column (name="status")
    private String status;

    public Transaction() {
    }

    public Transaction(User from, User to, double amount, String currency) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addUser (User user){
        users.add(user);
        user.transactions.add(this);
    }
}
