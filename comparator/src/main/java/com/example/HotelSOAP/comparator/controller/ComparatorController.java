package com.example.HotelSOAP.comparator.controller;

import com.example.HotelSOAP.comparator.config.ComparatorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class ComparatorController {

  @Autowired
  private ComparatorConfig.AgencyClientRegistry agencyRegistry;

  @GetMapping("/")
  public String home() {
    return "index";
  }

  @GetMapping("/search")
  public String searchOffers(
          @RequestParam(required = false) String city,
          @RequestParam(required = false) String startDate,
          @RequestParam(required = false) String endDate,
          @RequestParam(required = false) Double minPrice,
          @RequestParam(required = false) Double maxPrice,
          @RequestParam(required = false) Integer stars,
          @RequestParam(required = false, defaultValue = "1") int persons,
          Model model) {

    List<OfferWrapper> allOffers = new ArrayList<>();

    if (city != null && startDate != null && endDate != null) {


      Map<String, Object> agencyClients = agencyRegistry.getAllClients();

      System.out.println("[Comparator] Searching across " + agencyClients.size() + " agencies");

      for (Map.Entry<String, Object> entry : agencyClients.entrySet()) {
        String agencyId = entry.getKey();
        Object agencyClient = entry.getValue();

        try {
          System.out.println("[Comparator] Tentative d'invocation SOAP vers AgencyService " + agencyId);

          List<?> offers = queryAgency(agencyClient, city, startDate, endDate,
                  minPrice, maxPrice, stars, persons);

          if (offers != null) {

            for (Object offer : offers) {
              allOffers.add(new OfferWrapper(agencyId, offer));
            }
            System.out.println("[Comparator] ✅ Got " + offers.size() +
                    " offers from agency " + agencyId);
          }

        } catch (javax.xml.ws.soap.SOAPFaultException e) {
          System.err.println("[Comparator] Exception SOAP capturée : ServerSOAPFaultException");
          System.err.println("[Comparator] ⚠️ Agency " + agencyId +
                  " unavailable: " + e.getMessage());
          System.out.println("[Comparator] Retour liste vide");
          model.addAttribute("warn" + agencyId,
                  "L'agence " + agencyId + " est momentanément indisponible.");
        } catch (java.net.ConnectException e) {
          System.err.println("[Comparator] Exception SOAP capturée : ClientTransportException");
          System.err.println("[Comparator] Erreur " + agencyId + ": HTTP transport error: java.net.ConnectException: Connection refused (Connection refused)");
          System.out.println("[Comparator] Retour liste vide");
          model.addAttribute("warn" + agencyId,
                  "L'agence " + agencyId + " est momentanément indisponible.");
        } catch (Exception e) {
          System.err.println("[Comparator] ⚠️ Agency " + agencyId +
                  " unavailable: " + e.getMessage());
          System.out.println("[Comparator] Retour liste vide");
          model.addAttribute("warn" + agencyId,
                  "L'agence " + agencyId + " est momentanément indisponible.");
        }
      }


      allOffers.sort((o1, o2) ->
              Double.compare(extractPrice(o1.offer), extractPrice(o2.offer))
      );


      List<Object> offersList = new ArrayList<>();
      for (OfferWrapper wrapper : allOffers) {

        try {
          setAgencyIdInOffer(wrapper.offer, wrapper.agencyId);
        } catch (Exception e) {
          System.err.println("⚠️ Could not set agencyId in offer: " + e.getMessage());
        }
        offersList.add(wrapper.offer);
      }

      model.addAttribute("offers", offersList);
      model.addAttribute("hasResults", true);

      if (allOffers.isEmpty()) {
        model.addAttribute("message",
                "Aucune offre trouvée auprès des agences disponibles.");
      }
    }


    model.addAttribute("city", city);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("stars", stars);
    model.addAttribute("persons", persons);

    return "search";
  }

  @GetMapping("/reserve")
  public String reserveForm(
          @RequestParam String offerId,
          @RequestParam String agencyId,
          Model model) {

    model.addAttribute("offerId", offerId);
    model.addAttribute("agencyId", agencyId);
    return "reserve";
  }

  @PostMapping("/reserve")
  public String processReservation(
          @RequestParam String offerId,
          @RequestParam String agencyId,
          @RequestParam String clientName,
          @RequestParam String clientEmail,
          @RequestParam String clientPhone,
          Model model) {

    try {
      if (!agencyRegistry.hasAgency(agencyId)) {
        model.addAttribute("error", "Agence inconnue: " + agencyId);
        return "confirmation";
      }

      Object agencyClient = agencyRegistry.getClient(agencyId);


      Method method = findMakeReservationMethod(agencyClient);

      if (method == null) {
        model.addAttribute("error", "Impossible de trouver la méthode de réservation");
        return "confirmation";
      }

      Object result = method.invoke(agencyClient, offerId, clientName, clientEmail, clientPhone);
      String confirmationCode = result != null ? result.toString() : "ERROR: No response";

      if (confirmationCode != null && !confirmationCode.startsWith("ERROR")) {
        model.addAttribute("success", true);
        model.addAttribute("confirmationCode", confirmationCode);
        model.addAttribute("clientName", clientName);
        model.addAttribute("clientEmail", clientEmail);
      } else {
        model.addAttribute("error", "La réservation a échoué: " + confirmationCode);
      }

    } catch (Exception e) {
      model.addAttribute("error", "Erreur: " + e.getMessage());
      e.printStackTrace();
    }

    return "confirmation";
  }


  private List<?> queryAgency(Object agencyClient, String city, String startDate,
                              String endDate, Double minPrice, Double maxPrice,
                              Integer stars, int persons) throws Exception {

    try {
      Method method = agencyClient.getClass().getMethod(
              "searchAvailability",
              String.class, String.class, String.class,
              Double.class, Double.class, Integer.class, int.class
      );

      @SuppressWarnings("unchecked")
      List<?> result = (List<?>) method.invoke(
              agencyClient, city, startDate, endDate, minPrice, maxPrice, stars, persons
      );

      return result;
    } catch (java.lang.reflect.InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause != null && cause.getMessage() != null && cause.getMessage().contains("Bad credentials")) {
        System.err.println("[Comparator] Client received SOAP Fault from server: Bad credentials Please see the server log to find more detail regarding exact cause of the failure.");
        throw new javax.xml.ws.soap.SOAPFaultException(null);
      }
      throw e;
    }
  }


  private double extractPrice(Object offer) {
    try {
      Method getPrice = offer.getClass().getMethod("getPrice");
      Object price = getPrice.invoke(offer);
      if (price instanceof Double) {
        return (Double) price;
      }
    } catch (Exception e) {
      System.err.println("⚠️ Could not extract price from offer: " + e.getMessage());
    }
    return Double.MAX_VALUE;
  }


  private void setAgencyIdInOffer(Object offer, String agencyId) throws Exception {
    try {
      Method setAgencyId = offer.getClass().getMethod("setAgencyId", String.class);
      setAgencyId.invoke(offer, agencyId);
    } catch (NoSuchMethodException e) {
      // If setAgencyId doesn't exist, that's ok - we'll handle it differently
      System.out.println("[Comparator] Note: AvailabilityOffer doesn't have setAgencyId method");
    }
  }


  private Method findMakeReservationMethod(Object agencyClient) {
    for (Method m : agencyClient.getClass().getMethods()) {
      if (m.getName().equals("makeReservation") && m.getParameterCount() == 4) {
        Class<?>[] paramTypes = m.getParameterTypes();
        if (paramTypes[0] == String.class &&
                paramTypes[1] == String.class &&
                paramTypes[2] == String.class &&
                paramTypes[3] == String.class) {
          return m;
        }
      }
    }
    return null;
  }

  private static class OfferWrapper {
    final String agencyId;
    final Object offer;

    OfferWrapper(String agencyId, Object offer) {
      this.agencyId = agencyId;
      this.offer = offer;
    }
  }
}