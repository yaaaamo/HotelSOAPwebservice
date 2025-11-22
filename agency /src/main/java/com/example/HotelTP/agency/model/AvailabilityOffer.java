package com.example.HotelTP.agency.model;

public class AvailabilityOffer {
  private String offerId;
  private String hotelId;      // "H1" ou "H2"
  private String roomType;
  private int beds;
  private double price;
  private String startDate;
  private String endDate;

  public AvailabilityOffer() {}

  public AvailabilityOffer(String offerId, String hotelId, String roomType,
                           int beds, double price, String startDate, String endDate) {
    this.offerId = offerId;
    this.hotelId = hotelId;
    this.roomType = roomType;
    this.beds = beds;
    this.price = price;
    this.startDate = startDate;
    this.endDate = endDate;
  }


  public String getOfferId() { return offerId; }
  public void setOfferId(String offerId) { this.offerId = offerId; }

  public String getHotelId() { return hotelId; }
  public void setHotelId(String hotelId) { this.hotelId = hotelId; }

  public String getRoomType() { return roomType; }
  public void setRoomType(String roomType) { this.roomType = roomType; }

  public int getBeds() { return beds; }
  public void setBeds(int beds) { this.beds = beds; }

  public double getPrice() { return price; }
  public void setPrice(double price) { this.price = price; }

  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }

  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
}
