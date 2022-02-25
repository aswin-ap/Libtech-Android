package com.wetwo.librarymanagment.data.model;

public class AddressModel {
    private String name;
    private String address;
    private String apartment;
    private String city;
    private String phone;
    private String zipcode;

    public AddressModel(String name, String address, String apartment, String city, String phone, String zipcode) {
        this.name = name;
        this.address = address;
        this.apartment = apartment;
        this.city = city;
        this.phone = phone;
        this.zipcode = zipcode;
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

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", apartment='" + apartment + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", zipcode='" + zipcode + '\'' +
                '}';
    }
}
