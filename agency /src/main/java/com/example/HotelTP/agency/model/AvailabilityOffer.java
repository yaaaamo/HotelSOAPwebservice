package com.example.HotelTP.agency.model;

public class AvailabilityOffer {
  private String offerId;
  private String hotelId;

  // Informations de la chambre
  private String roomType;
  private int beds;
  private double price;
  private String startDate;
  private String endDate;
  private String agencyId;
  private String agencyName;
  private String hotelName;
  private String country;
  private String city;
  private String street;
  private String number;
  private String placeName;
  private double latitude;
  private double longitude;
  private int stars;


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

  public String getAgencyId() { return agencyId; }
  public void setAgencyId(String agencyId) { this.agencyId = agencyId; }

  public String getAgencyName() { return agencyName; }
  public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

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

  // Nouveaux Getters et Setters pour les informations de l'hôtel
  public String getHotelName() { return hotelName; }
  public void setHotelName(String hotelName) { this.hotelName = hotelName; }

  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getStreet() { return street; }
  public void setStreet(String street) { this.street = street; }



  public int getStars() { return stars; }
  public void setStars(int stars) { this.stars = stars; }

  public String getFullAddress() {
    java.util.List<String> parts = new java.util.ArrayList<>();

    // "42 Rue de la Loge"
    if (number != null && !number.isEmpty()) {
      if (street != null && !street.isEmpty()) {
        parts.add(number + " " + street);
      } else {
        parts.add(number);
      }
    } else if (street != null && !street.isEmpty()) {
      parts.add(street);
    }

    if (placeName != null && !placeName.isEmpty()) {
      parts.add(placeName);
    }
    if (city != null && !city.isEmpty()) {
      parts.add(city);
    }
    if (country != null && !country.isEmpty()) {
      parts.add(country);
    }

    return String.join(", ", parts);
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

  // Méthode helper pour afficher les étoiles
  public String getStarsDisplay() {
    StringBuilder starsStr = new StringBuilder();
    for (int i = 0; i < stars; i++) {
      starsStr.append("⭐");
    }
    return starsStr.toString();
  }
}