package com.example.HotelTP.agency.service;
import com.example.HotelTP.agency.model.AvailabilityOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@WebService(endpointInterface = "com.example.HotelTP.agency.service.AgencyService")
public class AgencyServiceImpl implements AgencyService {

  @Value("${agency.id}")
  private String agencyId;

  @Value("${agency.password}")
  private String password;

  @Autowired
  @Qualifier("h1Client")
  private com.example.HotelTP.agency.clients.h1.HotelService h1Client;

  @Autowired
  @Qualifier("h2Client")
  private com.example.HotelTP.agency.clients.h2.HotelService h2Client;

  @Override
  public List<AvailabilityOffer> searchAvailability(
          String city,
          String startDate,
          String endDate,
          Double minPrice,
          Double maxPrice,
          Integer stars,
          int persons) {

    List<AvailabilityOffer> allOffers = new ArrayList<>();

    // Recherche dans H1
    try {
      List<com.example.HotelTP.agency.clients.h1.AvailabilityOffer> offersH1 =
              h1Client.checkAvailability(agencyId, password, startDate, endDate, persons);

      if (offersH1 != null) {
        for (com.example.HotelTP.agency.clients.h1.AvailabilityOffer offer : offersH1) {
          AvailabilityOffer mappedOffer = new AvailabilityOffer(
                  offer.getOfferId(),
                  "H1",
                  offer.getRoomType().name(),
                  offer.getBeds(),
                  offer.getPrice(),
                  startDate,
                  endDate,
                  "Grand Hotel H1",              // Nom de l'hôtel
                  "France",                      // Pays
                  city != null ? city : "Paris", // Ville
                  "Avenue des Champs-Élysées",   // Rue
                  "123",                         // Numéro
                  "Centre-ville",                // Lieu-dit
                  4                              // Étoiles
          );
          allOffers.add(mappedOffer);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Erreur recherche H1: " + e.getMessage());
    }

    // Recherche dans H2
    try {
      List<com.example.HotelTP.agency.clients.h2.AvailabilityOffer> offersH2 =
              h2Client.checkAvailability(agencyId, password, startDate, endDate, persons);

      if (offersH2 != null) {
        for (com.example.HotelTP.agency.clients.h2.AvailabilityOffer offer : offersH2) {
          AvailabilityOffer mappedOffer = new AvailabilityOffer(
                  offer.getOfferId(),
                  "H2",
                  offer.getRoomType().name(),
                  offer.getBeds(),
                  offer.getPrice(),
                  startDate,
                  endDate,
                  "Hotel Boutique H2",           // Nom de l'hôtel
                  "France",                      // Pays
                  city != null ? city : "Paris", // Ville
                  "Rue de Rivoli",               // Rue
                  "456",                         // Numéro
                  "Quartier du Louvre",          // Lieu-dit
                  5                              // Étoiles
          );
          allOffers.add(mappedOffer);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Erreur recherche H2: " + e.getMessage());
    }

    // Filtrage selon les critères
    return filterOffers(allOffers, city, minPrice, maxPrice, stars);
  }

  /**
   * Filtre les offres selon les critères de recherche
   */
  private List<AvailabilityOffer> filterOffers(
          List<AvailabilityOffer> offers,
          String city,
          Double minPrice,
          Double maxPrice,
          Integer stars) {

    return offers.stream()
            .filter(offer -> {
              // Filtre par ville
              if (city != null && !city.isEmpty()) {
                if (offer.getCity() == null ||
                        !offer.getCity().toLowerCase().contains(city.toLowerCase())) {
                  return false;
                }
              }

              // Filtre par prix minimum
              if (minPrice != null && offer.getPrice() < minPrice) {
                return false;
              }

              // Filtre par prix maximum
              if (maxPrice != null && offer.getPrice() > maxPrice) {
                return false;
              }

              // Filtre par étoiles
              if (stars != null && offer.getStars() != stars) {
                return false;
              }

              return true;
            })
            .collect(Collectors.toList());
  }

  @Override
  public String makeReservation(String offerId, String clientName, String clientEmail, String clientPhone) {
    String hotelId = extractHotelIdFromOfferId(offerId);

    try {
      if ("H1".equals(hotelId)) {
        com.example.HotelTP.agency.clients.h1.Client client =
                new com.example.HotelTP.agency.clients.h1.Client();
        client.setName(clientName);
        client.setEmail(clientEmail);
        client.setPhone(clientPhone);

        return h1Client.book(agencyId, password, offerId, client);

      } else if ("H2".equals(hotelId)) {
        com.example.HotelTP.agency.clients.h2.Client client =
                new com.example.HotelTP.agency.clients.h2.Client();
        client.setName(clientName);
        client.setEmail(clientEmail);
        client.setPhone(clientPhone);

        return h2Client.book(agencyId, password, offerId, client);

      } else {
        return "ERROR: Hôtel inconnu pour l'offre " + offerId;
      }

    } catch (Exception e) {
      return "ERROR: " + e.getMessage();
    }
  }

  private String extractHotelIdFromOfferId(String offerId) {
    if (offerId == null) return null;

    if (offerId.toUpperCase().contains("H1")) {
      return "H1";
    } else if (offerId.toUpperCase().contains("H2")) {
      return "H2";
    }

    return null;
  }
}