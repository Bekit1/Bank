package net.ukr.bekit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

/**
 * Created by Александр on 04.05.2017.
 */
public class App {
    private static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bank");
        EntityManager em = emf.createEntityManager();
        Exchange ec = new Exchange();
        try {
            try {
                em.getTransaction().begin();
                createUsersAndAccounts(em);
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
            }
            createExchangeTable(em, ec);

            while (true) {
                try {
                    System.out.println(" Good day, user One! Instruction:" + "\t\n" + "1. Type '1' to add funds;"
                            + "\t\n" + "2. Type '2' to make transaction;" + "\t\n" + "3. Type '3' to convert currency;"
                            + "\t\n" + "4. Type '4' to show your total balance in UAH");
                    Scanner sc = new Scanner(System.in);
                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addFunds(em, users.get(0));
                            break;
                        case "2":
                            makeFullTransaction(em);
                            break;
                        case "3":
                            convertCurrency(users.get(0), ec, em);
                            break;
                        case "4":
                            users.get(0).showTotal(ec);
                            break;
                        default:
                            return;
                    }
                } catch (Exception e) {
                    System.out.println("Something goes wrong, try again");
                }
            }
        } finally {
            em.close();
            emf.close();
        }
    }

    public static void makeTransaction(User userFrom, User userTo, Transaction transaction, EntityManager em) {
        em.getTransaction().begin();
        int out = userFrom.makeTransactionOut(transaction);
        if (out == 1) {
            userFrom.addTransactionFrom(transaction);
            int to = userTo.makeTransactionIn(transaction);
            if (to == 1) {
                userTo.addTransactionTo(transaction);
                transaction.setStatus("Success");
            } else {
                transaction.setStatus("Fail");
            }
        } else {
            transaction.setStatus("Fail");
        }
        em.persist(userTo);
        em.persist(userFrom);
        em.persist(transaction);
    }

    public static void createExchangeTable(EntityManager em, Exchange ec) {
        try {
            ec.createExchange(em);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public static void createUsersAndAccounts(EntityManager em) {
        User one = new User("one");
        User two = new User("two");
        Account forOne = new Account(45, "USD");
        Account forTwo = new Account(45, "USD");
        one.addAccount(forOne);
        two.addAccount(forTwo);
        users.add(one);
        users.add(two);
        em.persist(one);
        em.persist(two);
        em.persist(forOne);
        em.persist(forTwo);
    }

    public static void setTranasction(User one, User two, Double amount, String currency, EntityManager em) {
        em.getTransaction().begin();
        Transaction test = new Transaction(one, two, amount, currency);
        test.addUser(one);
        test.addUser(two);
        em.persist(test);
        em.getTransaction().commit();
        makeTransaction(one, two, test, em);
        em.getTransaction().commit();

    }

    public static void addFunds(EntityManager em, User user) {
        em.getTransaction().begin();
        Scanner sc = new Scanner(System.in);
        System.out.println("Type currency of account that you wanna increase (USD, UAH, EUR)");
        String temp = sc.nextLine();
        System.out.println("Type amount of increasing");
        Double amount = sc.nextDouble();
        user.addFunds(temp, amount);
        em.persist(user);
        em.getTransaction().commit();
    }

    public static void makeFullTransaction(EntityManager em) {
        User to = null;
        Scanner sc = new Scanner(System.in);
        System.out.println("Type user`s name for whom you wanna make transaction");
        String name = sc.nextLine();
        System.out.println("Type currency of transaction");
        String currency = sc.nextLine();
        System.out.println("Type amount of transaction");
        Double amount = sc.nextDouble();
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                setTranasction(users.get(0), user, amount, currency, em);
            }
        }
    }

    public static void convertCurrency(User user, Exchange ec, EntityManager em) {
        em.getTransaction().begin();
        Scanner sc = new Scanner(System.in);
        System.out.println("Type currency that you wanna convert (UAH, USD, EUR sensitive to register) ");
        String from = sc.nextLine();
        System.out.println("Type currency in which you wanna convert (UAH, USD, EUR sensitive to register)");
        String to = sc.nextLine();
        System.out.println("Type amount of conversion");
        Double amount = sc.nextDouble();
        user.convert(from, to, amount, ec);
        em.persist(user);
        em.getTransaction().commit();
    }
}

