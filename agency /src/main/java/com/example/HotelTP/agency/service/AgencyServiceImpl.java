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
          mapped.setImageUrl(o.getImageUrl());
          mapped.setPrice(o.getPrice());        // prix agence
          mapped.setBasePrice(o.getBasePrice());




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
          mapped.setImageUrl(o.getImageUrl());
          mapped.setPrice(o.getPrice());        // prix agence
          mapped.setBasePrice(o.getBasePrice());




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

    System.out.println("[AGENCY DEBUG] Incoming offers before filter = " + offers.size());
    System.out.println("[AGENCY DEBUG] city=" + city +
            ", minPrice=" + minPrice +
            ", maxPrice=" + maxPrice +
            ", stars=" + stars);

    return offers.stream()
            .filter(offer -> {
              // Ville
              if (city != null && !city.trim().isEmpty()) {
                if (offer.getCity() == null ||
                        !offer.getCity().toLowerCase().contains(city.trim().toLowerCase())) {
                  System.out.println("[AGENCY DEBUG] drop " + offer.getOfferId() +
                          " because city='" + offer.getCity() + "' doesn't match filter='" + city + "'");
                  return false;
                }
              }

              // Prix minimum
              if (minPrice != null) {
                if (offer.getPrice() < minPrice) {
                  System.out.println("[AGENCY DEBUG] drop " + offer.getOfferId() +
                          " because price=" + offer.getPrice() + " < minPrice=" + minPrice);
                  return false;
                }
              }

              // Prix maximum
              if (maxPrice != null) {
                if (offer.getPrice() > maxPrice) {
                  System.out.println("[AGENCY DEBUG] drop " + offer.getOfferId() +
                          " because price=" + offer.getPrice() + " > maxPrice=" + maxPrice);
                  return false;
                }
              }

              // Étoiles
              if (stars != null) {
                if (offer.getStars() != stars) {
                  System.out.println("[AGENCY DEBUG] drop " + offer.getOfferId() +
                          " because stars=" + offer.getStars() + " != filterStars=" + stars);
                  return false;
                }
              }

              return true;
            })
            .peek(o -> System.out.println("[AGENCY DEBUG] KEEP " + o.getOfferId() +
                    " city=" + o.getCity() +
                    " price=" + o.getPrice() +
                    " stars=" + o.getStars()))
            .collect(Collectors.toList());
  }

  @Override
  public List<com.example.HotelTP.agency.model.Reservation> listReservations() {

    List<com.example.HotelTP.agency.model.Reservation> all = new ArrayList<>();

    // ==== H1 ====
    try {
      List<com.example.HotelTP.agency.clients.h1.Reservation> resH1 =
              h1Client.getReservationsForAgencys(agencyId, password);

      if (resH1 != null) {
        for (com.example.HotelTP.agency.clients.h1.Reservation r : resH1) {
          com.example.HotelTP.agency.model.Reservation mapped =
                  new com.example.HotelTP.agency.model.Reservation();

          mapped.setConfirmationRef(r.getConfirmationRef());
          mapped.setOfferId(r.getOfferId());

          mapped.setHotelId(r.getHotelId());
          mapped.setHotelName(r.getHotelName());
          mapped.setCity(r.getCity());
          mapped.setCountry(r.getCountry());

          mapped.setAgencyId(r.getAgencyId());
          mapped.setAgencyName(r.getAgencyName());

          mapped.setRoomId(r.getRoomId());
          mapped.setRoomType(r.getRoomType());
          mapped.setBeds(r.getBeds());

          mapped.setStartDate(r.getStartDate());
          mapped.setEndDate(r.getEndDate());
          mapped.setTotalPrice(r.getTotalPrice());

          mapped.setClientName(r.getClientName());
          mapped.setClientEmail(r.getClientEmail());
          mapped.setClientPhone(r.getClientPhone());

          all.add(mapped);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Error fetching reservations from H1: " + e.getMessage());
    }

    // ==== H2 ====
    try {
      List<com.example.HotelTP.agency.clients.h2.Reservation> resH2 =
              h2Client.getReservationsForAgencys(agencyId, password);

      if (resH2 != null) {
        for (com.example.HotelTP.agency.clients.h2.Reservation r : resH2) {
          com.example.HotelTP.agency.model.Reservation mapped =
                  new com.example.HotelTP.agency.model.Reservation();

          mapped.setConfirmationRef(r.getConfirmationRef());
          mapped.setOfferId(r.getOfferId());

          mapped.setHotelId(r.getHotelId());
          mapped.setHotelName(r.getHotelName());
          mapped.setCity(r.getCity());
          mapped.setCountry(r.getCountry());

          mapped.setAgencyId(r.getAgencyId());
          mapped.setAgencyName(r.getAgencyName());

          mapped.setRoomId(r.getRoomId());
          mapped.setRoomType(r.getRoomType());
          mapped.setBeds(r.getBeds());

          mapped.setStartDate(r.getStartDate());
          mapped.setEndDate(r.getEndDate());
          mapped.setTotalPrice(r.getTotalPrice());

          mapped.setClientName(r.getClientName());
          mapped.setClientEmail(r.getClientEmail());
          mapped.setClientPhone(r.getClientPhone());

          all.add(mapped);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Error fetching reservations from H2: " + e.getMessage());
    }

    return all;
  }



}