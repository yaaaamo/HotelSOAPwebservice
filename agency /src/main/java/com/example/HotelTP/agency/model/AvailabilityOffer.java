package com.example.HotelTP.agency.model;

public class AvailabilityOffer {
  private String offerId;
  private String hotelId;      // "H1" ou "H2"

  // Informations de la chambre
  private String roomType;
  private int beds;
  private double price;
  private String startDate;
  private String endDate;

  // Informations de l'hôtel
  private String hotelName;
  private String country;      // Pays
  private String city;         // Ville
  private String street;       // Rue
  private String streetNumber; // Numéro
  private String locality;     // Lieu-dit
  private int stars;           // Nombre d'étoiles

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

  // Constructor avec toutes les informations
  public AvailabilityOffer(String offerId, String hotelId, String roomType,
                           int beds, double price, String startDate, String endDate,
                           String hotelName, String country, String city,
                           String street, String streetNumber, String locality, int stars) {
    this.offerId = offerId;
    this.hotelId = hotelId;
    this.roomType = roomType;
    this.beds = beds;
    this.price = price;
    this.startDate = startDate;
    this.endDate = endDate;
    this.hotelName = hotelName;
    this.country = country;
    this.city = city;
    this.street = street;
    this.streetNumber = streetNumber;
    this.locality = locality;
    this.stars = stars;

  }

  // Getters et Setters existants
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

  public String getStreetNumber() { return streetNumber; }
  public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }

  public String getLocality() { return locality; }
  public void setLocality(String locality) { this.locality = locality; }

  public int getStars() { return stars; }
  public void setStars(int stars) { this.stars = stars; }

  // Méthode helper pour obtenir l'adresse complète
  public String getFullAddress() {
    StringBuilder address = new StringBuilder();
    if (streetNumber != null && !streetNumber.isEmpty()) {
      address.append(streetNumber).append(" ");
    }
    if (street != null && !street.isEmpty()) {
      address.append(street).append(", ");
    }
    if (locality != null && !locality.isEmpty()) {
      address.append(locality).append(", ");
    }
    if (city != null && !city.isEmpty()) {
      address.append(city).append(", ");
    }
    if (country != null && !country.isEmpty()) {
      address.append(country);
    }
    return address.toString();
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