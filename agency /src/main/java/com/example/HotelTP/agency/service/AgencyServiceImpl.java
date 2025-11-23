package com.example.HotelTP.agency.service;

import com.example.HotelTP.agency.config.HotelPartnerConfig;
import com.example.HotelTP.agency.model.AvailabilityOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private HotelPartnerConfig.HotelClientRegistry hotelRegistry;

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

    // Get all registered hotels dynamically
    Map<String, Object> hotelClients = hotelRegistry.getAllClients();

    System.out.println("[Agency] Searching across " + hotelClients.size() + " hotel partners");

    // Query each hotel in parallel (or sequentially)
    for (Map.Entry<String, Object> entry : hotelClients.entrySet()) {
      String hotelId = entry.getKey();
      Object hotelClient = entry.getValue();

      try {
        System.out.println("[Agency] Tentative d'invocation SOAP vers HotelService " + hotelId);

        List<AvailabilityOffer> offersFromHotel =
                queryHotel(hotelId, hotelClient, startDate, endDate, persons);

        allOffers.addAll(offersFromHotel);

        System.out.println("[Agency] ✅ Got " + offersFromHotel.size() +
                " offers from " + hotelId);

      } catch (javax.xml.ws.soap.SOAPFaultException e) {
        System.err.println("[Agency] Exception SOAP capturée : ServerSOAPFaultException");
        System.err.println("[Agency] Erreur recherche " + hotelId + ": " + e.getMessage());
        System.out.println("[Agency] Retour liste vide au Comparateur");
        // Continue with other hotels even if one fails
      } catch (java.net.ConnectException e) {
        System.err.println("[Agency] Exception SOAP capturée : ClientTransportException");
        System.err.println("[Agency] Erreur recherche " + hotelId + ": HTTP transport error: java.net.ConnectException: Connection refused");
        System.out.println("[Agency] Retour liste vide au Comparateur");
        // Continue with other hotels even if one fails
      } catch (Exception e) {
        System.err.println("[Agency] ⚠️ Error querying " + hotelId + ": " + e.getMessage());
        System.out.println("[Agency] Retour liste vide au Comparateur");
        // Continue with other hotels even if one fails
      }
    }

    System.out.println("[Agency] Total offers before filtering: " + allOffers.size());

    return filterOffers(allOffers, city, minPrice, maxPrice, stars);
  }

  /**
   * Query a single hotel using reflection to invoke the SOAP method dynamically
   */
  private List<AvailabilityOffer> queryHotel(String hotelId, Object hotelClient,
                                             String startDate, String endDate, int persons)
          throws Exception {

    List<AvailabilityOffer> offers = new ArrayList<>();

    try {
      // Use reflection to invoke checkAvailability on any hotel client
      Method method = hotelClient.getClass().getMethod(
              "checkAvailability",
              String.class, String.class, String.class, String.class, int.class
      );

      @SuppressWarnings("unchecked")
      List<?> rawOffers = (List<?>) method.invoke(
              hotelClient, agencyId, password, startDate, endDate, persons
      );

      if (rawOffers != null) {
        for (Object rawOffer : rawOffers) {
          AvailabilityOffer mapped = mapOffer(hotelId, rawOffer, startDate, endDate);
          if (mapped != null) {
            offers.add(mapped);
          }
        }
      }
    } catch (java.lang.reflect.InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause != null && cause.getMessage() != null && cause.getMessage().contains("Bad credentials")) {
        System.err.println("[Agency] Erreur recherche " + hotelId + ": Client received SOAP Fault from server: Bad credentials Please see the server log to find more detail regarding exact cause of the failure.");
        throw new javax.xml.ws.soap.SOAPFaultException(null);
      }
      throw e;
    }

    return offers;
  }

  /**
   * Map a SOAP offer object to our internal model using reflection
   * This works regardless of which hotel package the offer comes from
   */
  private AvailabilityOffer mapOffer(String hotelId, Object rawOffer,
                                     String startDate, String endDate) {
    try {
      AvailabilityOffer mapped = new AvailabilityOffer();

      // Use reflection to get values from the SOAP object
      mapped.setOfferId(getStringField(rawOffer, "offerId"));
      mapped.setHotelId(hotelId);

      // Room type - handle enum
      Object roomTypeObj = getField(rawOffer, "roomType");
      if (roomTypeObj != null) {
        mapped.setRoomType(roomTypeObj.toString());
      }

      mapped.setBeds(getIntField(rawOffer, "beds"));
      mapped.setPrice(getDoubleField(rawOffer, "price"));
      mapped.setBasePrice(getDoubleField(rawOffer, "basePrice"));
      mapped.setStartDate(startDate);
      mapped.setEndDate(endDate);

      // Hotel information
      mapped.setHotelName(getStringField(rawOffer, "hotelName"));
      mapped.setCountry(getStringField(rawOffer, "country"));
      mapped.setCity(getStringField(rawOffer, "city"));
      mapped.setStreet(getStringField(rawOffer, "street"));
      mapped.setNumber(getStringField(rawOffer, "number"));
      mapped.setPlaceName(getStringField(rawOffer, "placeName"));
      mapped.setLatitude(getDoubleField(rawOffer, "latitude"));
      mapped.setLongitude(getDoubleField(rawOffer, "longitude"));
      mapped.setStars(getIntField(rawOffer, "stars"));
      mapped.setImageUrl(getStringField(rawOffer, "imageUrl"));

      // Agency info
      mapped.setAgencyId(agencyId);
      mapped.setAgencyName(agencyName);

      return mapped;

    } catch (Exception e) {
      System.err.println("⚠️ Error mapping offer from " + hotelId + ": " + e.getMessage());
      return null;
    }
  }

  @Override
  public String makeReservation(String offerId, String clientName,
                                String clientEmail, String clientPhone) {

    String hotelId = extractHotelIdFromOfferId(offerId);

    if (hotelId == null || !hotelRegistry.hasHotel(hotelId)) {
      return "ERROR: Unknown hotel for offer " + offerId;
    }

    try {
      Object hotelClient = hotelRegistry.getClient(hotelId);

      // Create client object dynamically based on hotel's package
      Object clientObj = createClientObject(hotelId, clientName, clientEmail, clientPhone);

      System.out.println("[Agency] Created client object of type: " + clientObj.getClass().getName());

      // Find the book method - it takes 4 parameters
      // We need to search for it since the Client parameter type varies by hotel
      Method bookMethod = null;
      for (Method m : hotelClient.getClass().getMethods()) {
        if (m.getName().equals("book") && m.getParameterCount() == 4) {
          Class<?>[] paramTypes = m.getParameterTypes();
          // Check if parameters match: String, String, String, (any Client type)
          if (paramTypes[0] == String.class &&
                  paramTypes[1] == String.class &&
                  paramTypes[2] == String.class) {
            bookMethod = m;
            System.out.println("[Agency] Found book method with signature: " + m);
            break;
          }
        }
      }

      if (bookMethod == null) {
        return "ERROR: Could not find book method on hotel client";
      }

      Object result = bookMethod.invoke(hotelClient, agencyId, password, offerId, clientObj);

      return result != null ? result.toString() : "ERROR: No response";

    } catch (Exception e) {
      System.err.println("⚠️ Error making reservation with " + hotelId + ": " + e.getMessage());
      e.printStackTrace();
      return "ERROR: " + e.getMessage();
    }
  }

  @Override
  public List<com.example.HotelTP.agency.model.Reservation> listReservations() {

    List<com.example.HotelTP.agency.model.Reservation> allReservations = new ArrayList<>();

    Map<String, Object> hotelClients = hotelRegistry.getAllClients();

    for (Map.Entry<String, Object> entry : hotelClients.entrySet()) {
      String hotelId = entry.getKey();
      Object hotelClient = entry.getValue();

      try {
        Method method = hotelClient.getClass().getMethod(
                "getReservationsForAgencys",
                String.class, String.class
        );

        @SuppressWarnings("unchecked")
        List<?> rawReservations = (List<?>) method.invoke(
                hotelClient, agencyId, password
        );

        if (rawReservations != null) {
          for (Object rawRes : rawReservations) {
            com.example.HotelTP.agency.model.Reservation mapped =
                    mapReservation(rawRes);
            if (mapped != null) {
              allReservations.add(mapped);
            }
          }
        }

      } catch (Exception e) {
        System.err.println("⚠️ Error fetching reservations from " + hotelId + ": " + e.getMessage());
      }
    }

    return allReservations;
  }

  // ========== Helper Methods ==========

  private String extractHotelIdFromOfferId(String offerId) {
    if (offerId == null) return null;

    // Extract hotel ID from offerId format: "H1-R1-2025-01-01-2025-01-05"
    String[] parts = offerId.split("-");
    return parts.length > 0 ? parts[0] : null;
  }

  /**
   * Create a client object for the specific hotel's package
   */
  private Object createClientObject(String hotelId, String name, String email, String phone)
          throws Exception {

    // Dynamically load the Client class for this hotel
    String packageName = "com.example.HotelTP.agency.clients." + hotelId.toLowerCase();
    String className = packageName + ".Client";

    Class<?> clientClass = Class.forName(className);
    Object client = clientClass.getDeclaredConstructor().newInstance();

    // Set fields using reflection
    setField(client, "name", name);
    setField(client, "email", email);
    setField(client, "phone", phone);

    return client;
  }

  private com.example.HotelTP.agency.model.Reservation mapReservation(Object rawRes) {
    try {
      com.example.HotelTP.agency.model.Reservation mapped =
              new com.example.HotelTP.agency.model.Reservation();

      mapped.setConfirmationRef(getStringField(rawRes, "confirmationRef"));
      mapped.setOfferId(getStringField(rawRes, "offerId"));
      mapped.setHotelId(getStringField(rawRes, "hotelId"));
      mapped.setHotelName(getStringField(rawRes, "hotelName"));
      mapped.setCity(getStringField(rawRes, "city"));
      mapped.setCountry(getStringField(rawRes, "country"));
      mapped.setAgencyId(getStringField(rawRes, "agencyId"));
      mapped.setAgencyName(getStringField(rawRes, "agencyName"));
      mapped.setRoomId(getStringField(rawRes, "roomId"));
      mapped.setRoomType(getStringField(rawRes, "roomType"));
      mapped.setBeds(getIntField(rawRes, "beds"));
      mapped.setStartDate(getStringField(rawRes, "startDate"));
      mapped.setEndDate(getStringField(rawRes, "endDate"));
      mapped.setTotalPrice(getDoubleField(rawRes, "totalPrice"));
      mapped.setClientName(getStringField(rawRes, "clientName"));
      mapped.setClientEmail(getStringField(rawRes, "clientEmail"));
      mapped.setClientPhone(getStringField(rawRes, "clientPhone"));

      return mapped;

    } catch (Exception e) {
      System.err.println("⚠️ Error mapping reservation: " + e.getMessage());
      return null;
    }
  }

  private List<AvailabilityOffer> filterOffers(
          List<AvailabilityOffer> offers,
          String city,
          Double minPrice,
          Double maxPrice,
          Integer stars) {

    System.out.println("[Agency] Filtering " + offers.size() + " offers");
    System.out.println("[Agency] Filters - city=" + city + ", minPrice=" + minPrice +
            ", maxPrice=" + maxPrice + ", stars=" + stars);

    return offers.stream()
            .filter(offer -> {
              if (city != null && !city.trim().isEmpty()) {
                if (offer.getCity() == null ||
                        !offer.getCity().toLowerCase().contains(city.trim().toLowerCase())) {
                  return false;
                }
              }

              if (minPrice != null && offer.getPrice() < minPrice) {
                return false;
              }

              if (maxPrice != null && offer.getPrice() > maxPrice) {
                return false;
              }

              if (stars != null && offer.getStars() != stars) {
                return false;
              }

              return true;
            })
            .collect(Collectors.toList());
  }

  // ========== Reflection Utilities ==========

  private Object getField(Object obj, String fieldName) throws Exception {
    String methodName = "get" + capitalize(fieldName);
    Method method = obj.getClass().getMethod(methodName);
    return method.invoke(obj);
  }

  private String getStringField(Object obj, String fieldName) {
    try {
      Object value = getField(obj, fieldName);
      return value != null ? value.toString() : null;
    } catch (Exception e) {
      return null;
    }
  }

  private int getIntField(Object obj, String fieldName) {
    try {
      Object value = getField(obj, fieldName);
      if (value instanceof Integer) {
        return (Integer) value;
      }
      return 0;
    } catch (Exception e) {
      return 0;
    }
  }

  private double getDoubleField(Object obj, String fieldName) {
    try {
      Object value = getField(obj, fieldName);
      if (value instanceof Double) {
        return (Double) value;
      } else if (value instanceof Number) {
        return ((Number) value).doubleValue();
      }
      return 0.0;
    } catch (Exception e) {
      return 0.0;
    }
  }

  private void setField(Object obj, String fieldName, Object value) throws Exception {
    if (value == null) {
      return; // Skip null values
    }

    String methodName = "set" + capitalize(fieldName);

    // Try to find the setter method - handle String specially
    Method method = null;
    try {
      // First try with the exact type
      method = obj.getClass().getMethod(methodName, value.getClass());
    } catch (NoSuchMethodException e) {
      // If that fails and value is a String, try with String.class explicitly
      if (value instanceof String) {
        try {
          method = obj.getClass().getMethod(methodName, String.class);
        } catch (NoSuchMethodException e2) {
          // Last resort: search all methods for a matching setter
          for (Method m : obj.getClass().getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == 1) {
              method = m;
              break;
            }
          }
        }
      }
    }

    if (method != null) {
      method.invoke(obj, value);
    } else {
      System.err.println("⚠️ Could not find setter method: " + methodName +
              " on " + obj.getClass().getName());
    }
  }

  private String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}