package com.example.HotelSOAP.comparator.controller;

import com.example.HotelSOAP.comparator.config.ComparatorConfig;
import com.example.HotelSOAP.comparator.model.GlobalReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Dynamic Reservation Controller - works with unlimited agencies
 * No more hardcoded a1Client, a2Client!
 */
@Controller
public class ReservationController {

  @Autowired
  private ComparatorConfig.AgencyClientRegistry agencyRegistry;

  @GetMapping("/reservations")
  public String listAllReservations(Model model) {

    List<GlobalReservation> all = new ArrayList<>();

    // Query ALL registered agencies dynamically
    Map<String, Object> agencyClients = agencyRegistry.getAllClients();

    System.out.println("[Comparator] Fetching reservations from " + agencyClients.size() + " agencies");

    for (Map.Entry<String, Object> entry : agencyClients.entrySet()) {
      String agencyId = entry.getKey();
      Object agencyClient = entry.getValue();

      try {
        System.out.println("[Comparator] Fetching reservations from agency: " + agencyId);

        List<?> reservations = queryReservations(agencyClient);

        if (reservations != null) {
          for (Object rawRes : reservations) {
            GlobalReservation g = mapReservation(rawRes, agencyId);
            if (g != null) {
              all.add(g);
            }
          }
          System.out.println("[Comparator] ✅ Got " + reservations.size() +
                  " reservations from agency " + agencyId);
        }

      } catch (Exception e) {
        System.err.println("[Comparator] ⚠️ Error calling " + agencyId +
                ".listReservations(): " + e.getMessage());
      }
    }

    // Sort: by agency, then hotel, then start date
    all.sort(Comparator
            .comparing(GlobalReservation::getAgencyName,
                    Comparator.nullsLast(String::compareTo))
            .thenComparing(GlobalReservation::getHotelId,
                    Comparator.nullsLast(String::compareTo))
            .thenComparing(GlobalReservation::getStartDate,
                    Comparator.nullsLast(String::compareTo)));

    model.addAttribute("reservations", all);
    return "reservations"; // Thymeleaf template name
  }

  // ========== Helper Methods ==========

  /**
   * Query reservations from a single agency using reflection
   */
  private List<?> queryReservations(Object agencyClient) throws Exception {
    Method method = agencyClient.getClass().getMethod("listReservations");

    @SuppressWarnings("unchecked")
    List<?> result = (List<?>) method.invoke(agencyClient);

    return result;
  }

  /**
   * Map a reservation object from any agency to GlobalReservation using reflection
   */
  private GlobalReservation mapReservation(Object rawRes, String agencyId) {
    try {
      GlobalReservation g = new GlobalReservation();

      g.setAgencyName(getStringField(rawRes, "agencyName"));
      g.setHotelId(getStringField(rawRes, "hotelId"));
      g.setHotelName(getStringField(rawRes, "hotelName"));
      g.setCity(getStringField(rawRes, "city"));
      g.setConfirmationRef(getStringField(rawRes, "confirmationRef"));
      g.setClientName(getStringField(rawRes, "clientName"));
      g.setStartDate(getStringField(rawRes, "startDate"));
      g.setEndDate(getStringField(rawRes, "endDate"));
      g.setTotalPrice(getDoubleField(rawRes, "totalPrice"));

      // If agencyName is null, use the agencyId
      if (g.getAgencyName() == null) {
        g.setAgencyName(agencyRegistry.getAgencyName(agencyId));
      }

      return g;

    } catch (Exception e) {
      System.err.println("⚠️ Error mapping reservation from " + agencyId + ": " + e.getMessage());
      return null;
    }
  }

  /**
   * Get a String field from object using reflection
   */
  private String getStringField(Object obj, String fieldName) {
    try {
      String methodName = "get" + capitalize(fieldName);
      Method method = obj.getClass().getMethod(methodName);
      Object value = method.invoke(obj);
      return value != null ? value.toString() : null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get a Double field from object using reflection
   */
  private Double getDoubleField(Object obj, String fieldName) {
    try {
      String methodName = "get" + capitalize(fieldName);
      Method method = obj.getClass().getMethod(methodName);
      Object value = method.invoke(obj);
      if (value instanceof Double) {
        return (Double) value;
      } else if (value instanceof Number) {
        return ((Number) value).doubleValue();
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Capitalize first letter of string
   */
  private String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}