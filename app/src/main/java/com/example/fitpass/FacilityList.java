package com.example.fitpass;

import java.util.LinkedList;

public class FacilityList {

    public static LinkedList<Facility> getMyFacilities() {

        LinkedList<Facility> lista = new LinkedList<>();

        lista.add(new Facility("Swimming pool"));
        lista.add(new Facility("Yoga"));
        lista.add(new Facility("Gym"));
        lista.add(new Facility("For children"));

        return lista;
    }

}
