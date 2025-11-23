package com.example.HotelTP.agency.model;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reservation")
public class Reservation {

  private String confirmationRef;
  private String offerId;

  private String hotelId;
  private String hotelName;
  private String city;
  private String country;

  private String agencyId;
  private String agencyName;

  private String roomId;
  private String roomType;
  private int beds;

  private String startDate;
  private String endDate;

  private Double totalPrice;

  private String clientName;
  private String clientEmail;
  private String clientPhone;

  public Reservation() {}

  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public int getBeds() {
    return beds;
  }

  public void setBeds(int beds) {
    this.beds = beds;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public String getAgencyName() {
    return agencyName;
  }

  public void setAgencyName(String agencyName) {
    this.agencyName = agencyName;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
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

  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public String getOfferId() {
    return offerId;
  }

  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  public String getConfirmationRef() {
    return confirmationRef;
  }

  public void setConfirmationRef(String confirmationRef) {
    this.confirmationRef = confirmationRef;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientEmail() {
    return clientEmail;
  }

  public void setClientEmail(String clientEmail) {
    this.clientEmail = clientEmail;
  }

  public String getClientPhone() {
    return clientPhone;
  }

  public void setClientPhone(String clientPhone) {
    this.clientPhone = clientPhone;
  }
}

