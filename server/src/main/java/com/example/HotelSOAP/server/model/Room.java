package com.example.HotelSOAP.server.model;

import javax.persistence.*;

@Entity
public class Room {

  @Id
  private String id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @Enumerated(EnumType.STRING)
  private RoomType type;

  private int beds;
  private double pricePerNight;
  private int stock;

  public Room() {}

  public Room(String id, Hotel hotel, RoomType type,
              int beds, double pricePerNight, int stock) {
    this.id = id;
    this.hotel = hotel;
    this.type = type;
    this.beds = beds;
    this.pricePerNight = pricePerNight;
    this.stock = stock;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Hotel getHotel() {
    return hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }

  public RoomType getType() {
    return type;
  }

  public void setType(RoomType type) {
    this.type = type;
  }

  public int getBeds() {
    return beds;
  }

  public void setBeds(int beds) {
    this.beds = beds;
  }

  public double getPricePerNight() {
    return pricePerNight;
  }

  public void setPricePerNight(double pricePerNight) {
    this.pricePerNight = pricePerNight;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public String getHotelId() {
    return hotel != null ? hotel.getId() : null;
  }
}
