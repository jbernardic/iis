package com.example.iis.config;

import com.example.iis.domain.Billing;
import com.example.iis.domain.LineItemEntity;
import com.example.iis.domain.OrderEntity;
import com.example.iis.domain.UserAccount;
import com.example.iis.repo.OrderRepository;
import com.example.iis.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final OrderRepository orders;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, OrderRepository orders, PasswordEncoder encoder) {
        this.users = users;
        this.orders = orders;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            users.save(new UserAccount("admin", encoder.encode("admin123"), "FULL"));
            users.save(new UserAccount("reader", encoder.encode("reader123"), "READ"));
        }

        if (orders.count() == 0) {
            orders.save(order("1001", "processing", "EUR", "129.98", "Ivan", "Horvat",
                    "ivan.horvat@example.com", "Zagreb", "HR", "Sample Product", 2, "64.99"));
            orders.save(order("1002", "completed", "EUR", "49.99", "Marko", "Marić",
                    "marko.maric@example.com", "Split", "HR", "USB Cable", 1, "49.99"));
            orders.save(order("1003", "pending", "USD", "199.00", "Ana", "Anić",
                    "ana.anic@example.com", "Osijek", "HR", "Wireless Mouse", 1, "199.00"));
            orders.save(order("1004", "completed", "EUR", "15.00", "Petra", "Perić",
                    "petra.peric@example.com", "Rijeka", "HR", "Sticker Pack", 3, "5.00"));
            orders.save(order("1005", "cancelled", "EUR", "999.99", "Luka", "Lukić",
                    "luka.lukic@example.com", "Zagreb", "HR", "Laptop Stand", 1, "999.99"));
        }
    }

    private OrderEntity order(String number, String status, String currency, String total,
                              String first, String last, String email, String city, String country,
                              String productName, int qty, String price) {
        OrderEntity o = new OrderEntity();
        o.setNumber(number);
        o.setStatus(status);
        o.setCurrency(currency);
        o.setTotal(new BigDecimal(total));
        o.setCustomerNote("Seeded order");
        o.setDateCreated(LocalDateTime.now());

        Billing b = new Billing();
        b.setFirstName(first);
        b.setLastName(last);
        b.setEmail(email);
        b.setPhone("+385910000000");
        b.setCity(city);
        b.setCountry(country);
        o.setBilling(b);

        LineItemEntity li = new LineItemEntity();
        li.setProductId(55L);
        li.setName(productName);
        li.setQuantity(qty);
        li.setPrice(new BigDecimal(price));
        li.setSubtotal(new BigDecimal(price).multiply(BigDecimal.valueOf(qty)));
        o.addLineItem(li);

        return o;
    }
}
