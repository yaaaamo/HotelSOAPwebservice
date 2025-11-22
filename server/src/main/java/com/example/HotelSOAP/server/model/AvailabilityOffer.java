package com.example.HotelSOAP.server.model;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilityOffer")
public class AvailabilityOffer {

  private String offerId;
  private RoomType roomType;
  private int beds;
  private String start;   // ISO date yyyy-MM-dd
  private String end;     // ISO date yyyy-MM-dd
  private double price;   // total price for the stay

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



  public AvailabilityOffer() {}

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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

  public AvailabilityOffer(String offerId, RoomType roomType, int beds, String start, String end, double price, String hotelName, int stars, String country, String city, String street, String number, String placeName, double latitude, double longitude) {
    this.offerId = offerId;
    this.roomType = roomType;
    this.beds = beds;
    this.start = start;
    this.end = end;
    this.price = price;
    this.hotelName = hotelName;
    this.stars = stars;
    this.country = country;
    this.city = city;
    this.street = street;
    this.number = number;
    this.placeName = placeName;
    this.latitude = latitude;
    this.longitude = longitude;
  }

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
}

