package com.app.villagepeepol;

/**
 Tämä luokka edustaa mökkiä, jolla on nimi, koko ja kapasiteetti.
 */
public class Cottage {

    /**
     Mökin nimi.
     */
    private String name;

    /**
     Mökin koko.
     */
    private int size;

    /**
     Mökin kapasiteetti.
     */
    private int capacity;

    /**
     Luo uuden mökin annetulla nimellä, koolla ja kapasiteetilla.
     @param name Mökin nimi
     @param size Mökin koko
     @param capacity Mökin kapasiteetti
     */
    public Cottage(String name, int size, int capacity) {
        this.name = name;
        this.size = size;
        this.capacity = capacity;
    }

    /**
     Palauttaa mökin nimen.
     @return Mökin nimi
     */
    public String getName() {
        return name;
    }

    /**
     Asettaa mökin nimen.
     @param name Mökin nimi
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Palauttaa mökin koon.
     @return Mökin koko
     */
    public int getSize() {
        return size;
    }

    /**
     Asettaa mökin koon.
     @param size Mökin koko
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     Palauttaa mökin kapasiteetin.
     @return Mökin kapasiteetti
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     Asettaa mökin kapasiteetin.
     @param capacity Mökin kapasiteetti
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     Muuntaa annetun tiedostostringin mökiksi.
     @param fileString Tiedostostringi, joka sisältää mökin tiedot erotettuna puolipisteellä (;)
     @return Uusi mökki, joka on luotu tiedostostringin perusteella
     */
    public static Cottage fromFileString(String fileString) {
        String[] parts = fileString.split(";");
        return new Cottage(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    /**
     Muuntaa mökin tiedot tiedostostringiksi, jossa nimi, koko ja kapasiteetti erotetaan puolipisteellä (;).
     @return Mökin tiedot tiedostostringinä
     */
    public String toFileString() {
        return name + ";" + size + ";" + capacity;
    }

    /**
     Palauttaa merkkijonoesityksen mökistä, jossa on mökin nimi ja kapasiteetti henkilömääränä.
     @return Mökin merkkijonoesitys
     */
    @Override
    public String toString() {
        return name + " (" + capacity + " henkilöä)";
    }
}