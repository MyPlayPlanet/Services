package net.myplayplanet.services.cache_new;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

public class exanokl {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        Timestamp stamp = new Timestamp(new Date().getTime());
        System.out.println(stamp.toString());
    }
}
