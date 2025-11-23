package com.example.HotelSOAP.server.model;
import javax.persistence.*;

@Entity
public class Agency {

  @Id
  private String agencyId;

  private String login;
  private String password;
  private String name;


  private double discountFactor;

  @ManyToOne(optional = false)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  public Agency() {
  }

  public Agency(String agencyId, String login, String password, String name,
                double discountFactor, Hotel hotel) {
    this.agencyId = agencyId;
    this.login = login;
    this.password = password;
    this.name = name;
    this.discountFactor = discountFactor;
    this.hotel = hotel;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getDiscountFactor() {
    return discountFactor;
  }

  public void setDiscountFactor(double discountFactor) {
    this.discountFactor = discountFactor;
  }

  public Hotel getHotel() {
    return hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }
}