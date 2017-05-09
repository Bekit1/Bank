package net.ukr.bekit;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Александр on 04.05.2017.
 */
public class Exchange {
    private List<CurrencyExchange> exchange = new ArrayList<>();

    public Exchange(List<CurrencyExchange> exchange) {
        this.exchange = exchange;
    }

    public Exchange() {
    }

    public List<CurrencyExchange> getExchange() {
        return exchange;
    }

    public void setExchange(List<CurrencyExchange> exchange) {
        this.exchange = exchange;
    }

    public void createExchange(EntityManager em) {
        em.getTransaction().begin();
        CurrencyExchange uahUsd = new CurrencyExchange("UAH/USD", 26.5, 27);
        CurrencyExchange uahEur = new CurrencyExchange("UAH/EUR", 29.5, 30);
        CurrencyExchange eurUsd = new CurrencyExchange("EUR/USD", 1.08, 1.1);
        exchange.add(uahUsd);
        exchange.add(uahEur);
        exchange.add(eurUsd);
        em.persist(uahUsd);
        em.persist(uahEur);
        em.persist(eurUsd);
        em.getTransaction().commit();
    }

    public double convert(String from, String to, double amount) {
        double result = 0;
        switch (from) {
            case "UAH":
                switch (to) {
                    case "USD":
                        result = amount / exchange.get(0).getAsk();
                        return result;
                    case "EUR":
                        result = amount / exchange.get(1).getAsk();
                        return result;
                }
            case "USD":
                switch (to) {
                    case "UAH":
                        result = amount * exchange.get(0).getBid();
                        return result;
                    case "EUR":
                        result = amount / exchange.get(2).getAsk();
                        return result;
                }
            case "EUR":
                switch (to) {
                    case "UAH":
                        result = amount * exchange.get(1).getBid();
                        return result;
                    case "USD":
                        result = amount * exchange.get(2).getBid();
                        return result;

                }
        }
        return result;
    }

}