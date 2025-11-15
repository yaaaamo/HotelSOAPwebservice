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

  public AvailabilityOffer() {}

  public AvailabilityOffer(String offerId, RoomType roomType, int beds,
                           String start, String end, double price) {
    this.offerId = offerId;
    this.roomType = roomType;
    this.beds = beds;
    this.start = start;
    this.end = end;
    this.price = price;
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

