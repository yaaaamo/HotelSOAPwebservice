package com.example.HotelSOAP.comparator.model;

public class ComparedOffer {
  private String agencyId;
  private String agencyName;
  private String offerId;
  private double price;

  public ComparedOffer(String agencyId, String agencyName,
                       String offerId, double price) {
    this.agencyId = agencyId;
    this.agencyName = agencyName;
    this.offerId = offerId;
    this.price = price;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public String getAgencyName() {
    return agencyName;
  }

  public String getOfferId() {
    return offerId;
  }

  public double getPrice() {
    return price;
  }
}
