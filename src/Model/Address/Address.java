package Model.Address;

import java.io.Serializable;

/**
 * Created by Matt on 27-03-2017.
 */
public class Address implements Comparable<Address>, Serializable {

    private final String street, house, postcode, city;

    /**
     * Constructs an instance
     * @param street
     * @param house
     * @param floor
     * @param side
     * @param postcode
     * @param city
     */
    public Address(String street, String house, String floor, String side, String postcode, String city) {
        this.street = street;
        this.house = house;
        this.postcode = postcode;
        this.city = city;
    }

    public String street()   { return this.street; }
    public String house()    { return this.house; }
    public String postcode() { return this.postcode; }
    public String city()     { return this.city; }

    @Override
    public int compareTo(Address a) {
        if (postcode.compareTo(a.postcode()) != 0) return postcode.compareTo(a.postcode());
        if (city.compareTo(a.city()) != 0) return city.compareTo(a.city());
        if (street.compareTo(a.street()) != 0) return street.compareTo(a.street());
        if (house.compareTo(a.house()) != 0) return house.compareTo(a.house());
        return 0;
    }

    @Override
    public String toString() {
        return street + " " + house + "\n" +
                postcode + " " + city;
    }
}
