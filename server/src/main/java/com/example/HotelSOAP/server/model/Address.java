package com.example.HotelSOAP.server.model;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

  private String country;
  private String city;
  private String street;
  private String number;
  private String placeName;
  private double latitude;
  private double longitude;

  public Address() {}

  public Address(String country, String city, String street, String number,
                 String placeName, double latitude, double longitude) {
    this.country = country;
    this.city = city;
    this.street = street;
    this.number = number;
    this.placeName = placeName;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getPlaceName() {
    return placeName;
  }

  public void setPlaceName(String placeName) {
    this.placeName = placeName;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}

