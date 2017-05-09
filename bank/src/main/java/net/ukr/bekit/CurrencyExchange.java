package net.ukr.bekit;

import javax.persistence.*;

/**
 * Created by Александр on 04.05.2017.
 */
@Entity
@Table(name="CurrencyExchange")
public class CurrencyExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="Pair")
    private String pair;

    @Column(name="bid")
    private double bid;

    @Column(name="ask")
    private double ask;

    public CurrencyExchange(String pair, double bid, double ask) {
        this.pair = pair;
        this.bid = bid;
        this.ask = ask;
    }

    public CurrencyExchange() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }
}
