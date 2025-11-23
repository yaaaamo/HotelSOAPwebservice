package com.example.HotelSOAP.server.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity
@Table(name = "availability_offers")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilityOffer")
public class AvailabilityOffer {

  // DB id → NOT in SOAP
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlTransient
  private Long id;

  // JPA relation → NOT in SOAP
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  @XmlTransient
  private Room room;

  // business + SOAP fields
  private String offerId;

  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  private int beds;

  @Column(name = "start_date")
  private String start;   // yyyy-MM-dd

  @Column(name = "end_date")
  private String end;     // yyyy-MM-dd

  private double price;

  private String hotelName;
  private int stars;
  private String country;
  private String city;
  private String street;
  private String number;
  private String placeName;
  private double latitude;
  private double longitude;
  private String imageUrl;
  private double basePrice;  // prix hôtel sans réduction d'agence

  public double getBasePrice() {
    return basePrice;
  }

  public void setBasePrice(double basePrice) {
    this.basePrice = basePrice;
  }


  // units = stock for this period → server-side only
  @XmlTransient
  private int units;

  public AvailabilityOffer() {}

  // getters/setters...

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Room getRoom() { return room; }
  public void setRoom(Room room) { this.room = room; }

  public String getOfferId() {
    return offerId;
  }

  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  public RoomType getRoomType() {
    return roomType;
  }

  public void setRoomType(RoomType roomType) {
    this.roomType = roomType;
  }

  public int getBeds() {
    return beds;
  }

  public void setBeds(int beds) {
    this.beds = beds;
  }

  public String getPlaceName() {
    return placeName;
  }

  public void setPlaceName(String placeName) {
    this.placeName = placeName;
  }

  public int getUnits() {
    return units;
  }

  public void setUnits(int units) {
    this.units = units;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public int getStars() {
    return stars;
  }

  public void setStars(int stars) {
    this.stars = stars;
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
}
