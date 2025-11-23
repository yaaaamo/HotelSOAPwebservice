package com.example.HotelSOAP.comparator.model;


public class GlobalReservation {

  private String agencyName;
  private String hotelId;
  private String hotelName;
  private String city;
  private String confirmationRef;
  private String clientName;
  private String startDate;
  private String endDate;
  private Double totalPrice;



  public String getAgencyName() { return agencyName; }
  public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

  public String getHotelId() { return hotelId; }
  public void setHotelId(String hotelId) { this.hotelId = hotelId; }

  public String getHotelName() { return hotelName; }
  public void setHotelName(String hotelName) { this.hotelName = hotelName; }

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getConfirmationRef() { return confirmationRef; }
  public void setConfirmationRef(String confirmationRef) { this.confirmationRef = confirmationRef; }

  public String getClientName() { return clientName; }
  public void setClientName(String clientName) { this.clientName = clientName; }

  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }

  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }

  public Double getTotalPrice() { return totalPrice; }
  public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}

