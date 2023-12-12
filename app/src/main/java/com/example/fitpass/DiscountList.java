package com.example.fitpass;

import java.util.LinkedList;

public class DiscountList {

    public static LinkedList<Discount> getMyDiscounts() {

        LinkedList<Discount> lista = new LinkedList<>();

        lista.add(new Discount("Do -25%", "nike"));
        lista.add(new Discount("Do -10%", "a1"));
        lista.add(new Discount("Do -20%", "adidas"));
        lista.add(new Discount("Do -25%", "puma"));


        return lista;
    }

}
