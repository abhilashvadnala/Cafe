package com.abhilashvadnala.cafe;

import java.io.Serializable;

public class CafeBasic implements Serializable {

    String name;
    String address;
    double rating;

    public CafeBasic() {

    }

    public CafeBasic(String name, String address, double rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
