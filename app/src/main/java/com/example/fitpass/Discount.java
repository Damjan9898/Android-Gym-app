package com.example.fitpass;

public class Discount {

    private String discount, firma;

    public Discount(String discount, String firma) {
        this.discount = discount;
        this.firma = firma;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}
