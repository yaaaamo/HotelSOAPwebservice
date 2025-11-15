package com.example.HotelSOAP.server.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Hotel {

  @Id
  private String id;    // "H1", "H2", ...

  private String name;

  private int stars;

  @Embedded
  private Address address;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Room> rooms = new ArrayList<>();

  public Hotel() {}

  public Hotel(String id, String name, int stars, Address address) {
    this.id = id;
    this.name = name;
    this.stars = stars;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getStars() {
    return stars;
  }

  public void setStars(int stars) {
    this.stars = stars;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<Room> getRooms() {
    return rooms;
  }

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
  }

  public void addRoom(Room r) {
    rooms.add(r);
    r.setHotel(this);
  }
}

