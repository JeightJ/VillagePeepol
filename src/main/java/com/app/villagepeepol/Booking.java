package com.app.villagepeepol;

import javafx.collections.ObservableList;

import java.time.LocalDate;

/**
 * Booking-luokka edustaa mökin varausta.
 */
public class Booking {
    private Cottage cottage;
    private String customerName;
    private LocalDate startDate;
    private int days;

    /**
     * Luo Booking-olion annetuilla mökillä, asiakkaan nimellä, alkamispäivämäärällä ja päivien määrällä.
     *
     * @param cottage      varattu mökki
     * @param customerName varausta tekevän asiakkaan nimi
     * @param startDate    varauksen alkamispäivämäärä
     * @param days         varauksen kesto päivissä
     */
    public Booking(Cottage cottage, String customerName, LocalDate startDate, int days) {
        this.cottage = cottage;
        this.customerName = customerName;
        this.startDate = startDate;
        this.days = days;
    }

    /**
     * @return varaukseen liittyvä mökki
     */
    public Cottage getCottage() {
        return cottage;
    }

    /**
     * @param cottage asetettava mökki
     */
    public void setCottage(Cottage cottage) {
        this.cottage = cottage;
    }

    /**
     * @return varaukseen liittyvän asiakkaan nimi
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName asetettava asiakkaan nimi
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return varauksen alkamispäivämäärä
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate asetettava alkamispäivämäärä
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return varauksen kesto päivissä
     */
    public int getDays() {
        return days;
    }

    /**
     * @param days asetettava päivien määrä
     */
    public void setDays(int days) {
        this.days = days;
    }

    /**
     * Luo Booking-olion tiedostomerkkijonosta.
     *
     * @param fileString tiedostomerkkijono, josta olio luodaan
     * @param cottageList lista saatavilla olevista mökeistä
     * @return tiedostomerkkijonosta luotu Booking-olio
     */
    public static Booking fromFileString(String fileString, ObservableList<Cottage> cottageList) {
        String[] parts = fileString.split(";");
        Cottage cottage = cottageList.stream()
                .filter(c -> c.getName().equals(parts[0]))
                .findFirst()
                .orElse(null);
        return new Booking(cottage, parts[1], LocalDate.parse(parts[2]), Integer.parseInt(parts[3]));
    }

    /**
     * Muuntaa tämän Booking-olion tiedostomerkkijonoksi.
     *
     * @return tämän Booking-olion tiedostomerkkijonoesitys
     */
    public String toFileString() {
        return cottage.getName() + ";" + customerName + ";" + startDate + ";" + days;
    }
}