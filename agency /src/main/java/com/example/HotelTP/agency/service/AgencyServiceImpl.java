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

  @Value("${agency.name}")
  private String agencyName;



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

    // ===== H1 =====
    try {
      List<com.example.HotelTP.agency.clients.h1.AvailabilityOffer> offersH1 =
              h1Client.checkAvailability(agencyId, password, startDate, endDate, persons);

      if (offersH1 != null) {
        for (com.example.HotelTP.agency.clients.h1.AvailabilityOffer o : offersH1) {

          AvailabilityOffer mapped = new AvailabilityOffer();
          mapped.setOfferId(o.getOfferId());
          mapped.setHotelId("H1");
          mapped.setRoomType(
                  o.getRoomType() != null ? o.getRoomType().name() : null
          );
          mapped.setBeds(o.getBeds());
          mapped.setPrice(o.getPrice());
          mapped.setStartDate(startDate);
          mapped.setEndDate(endDate);

          // hotel + address from server AvailabilityOffer
          mapped.setHotelName(o.getHotelName());
          mapped.setCountry(o.getCountry());
          mapped.setCity(o.getCity());
          mapped.setStreet(o.getStreet());
          mapped.setNumber(o.getNumber());
          mapped.setPlaceName(o.getPlaceName());
          mapped.setLatitude(o.getLatitude());
          mapped.setLongitude(o.getLongitude());
          mapped.setStars(o.getStars());

          // agency info from VM args
          mapped.setAgencyId(agencyId);
          mapped.setAgencyName(agencyName);

          allOffers.add(mapped);
        }
      }

    } catch (Exception e) {
      System.err.println("⚠️ Erreur recherche H1: " + e.getMessage());
    }

    // ===== H2 =====
    try {
      List<com.example.HotelTP.agency.clients.h2.AvailabilityOffer> offersH2 =
              h2Client.checkAvailability(agencyId, password, startDate, endDate, persons);

      if (offersH2 != null) {
        for (com.example.HotelTP.agency.clients.h2.AvailabilityOffer o : offersH2) {

          AvailabilityOffer mapped = new AvailabilityOffer();
          mapped.setOfferId(o.getOfferId());
          mapped.setHotelId("H2");
          mapped.setRoomType(
                  o.getRoomType() != null ? o.getRoomType().name() : null
          );
          mapped.setBeds(o.getBeds());
          mapped.setPrice(o.getPrice());
          mapped.setStartDate(startDate);
          mapped.setEndDate(endDate);

          mapped.setHotelName(o.getHotelName());
          mapped.setCountry(o.getCountry());
          mapped.setCity(o.getCity());
          mapped.setStreet(o.getStreet());
          mapped.setNumber(o.getNumber());
          mapped.setPlaceName(o.getPlaceName());
          mapped.setLatitude(o.getLatitude());
          mapped.setLongitude(o.getLongitude());
          mapped.setStars(o.getStars());

          mapped.setAgencyId(agencyId);
          mapped.setAgencyName(agencyName);

          allOffers.add(mapped);
        }
      }

    } catch (Exception e) {
      System.err.println("⚠️ Erreur recherche H2: " + e.getMessage());
    }

    // if you want to debug:
    System.out.println("Total mapped offers before filter = " + allOffers.size());

    return filterOffers(allOffers, city, minPrice, maxPrice, stars);
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


  private List<AvailabilityOffer> filterOffers(
          List<AvailabilityOffer> offers,
          String city,
          Double minPrice,
          Double maxPrice,
          Integer stars) {

    return offers.stream()
            .filter(offer -> {
              // Ville
              if (city != null && !city.isEmpty()) {
                if (offer.getCity() == null ||
                        !offer.getCity().toLowerCase().contains(city.toLowerCase())) {
                  return false;
                }
              }

              // Prix minimum
              if (minPrice != null && offer.getPrice() < minPrice) {
                return false;
              }

              // Prix maximum
              if (maxPrice != null && offer.getPrice() > maxPrice) {
                return false;
              }

              // Étoiles
              if (stars != null && offer.getStars() != stars) {
                return false;
              }

              return true;
            })
            .collect(Collectors.toList());
  }

}