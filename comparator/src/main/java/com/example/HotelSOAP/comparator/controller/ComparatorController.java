package com.example.HotelSOAP.comparator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ComparatorController {

  @Autowired
  @Qualifier("a1Client")
  private com.example.HotelSOAP.comparator.clients.a1.AgencyService a1;

  @Autowired
  @Qualifier("a2Client")
  private com.example.HotelSOAP.comparator.clients.a2.AgencyService a2;

  // Values from VM options / application.properties
  @Value("${comparator.a1.agencyId}")
  private String a1AgencyId;

  @Value("${comparator.a2.agencyId}")
  private String a2AgencyId;

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

    if (city != null && startDate != null && endDate != null) {
      try {
        List<Object> allOffers = new ArrayList<>();

        // appels SOAP vers A1
        java.util.List<com.example.HotelSOAP.comparator.clients.a1.AvailabilityOffer> offersA1 =
                a1.searchAvailability(city, startDate, endDate, minPrice, maxPrice, stars, persons);

        // appels SOAP vers A2
        java.util.List<com.example.HotelSOAP.comparator.clients.a2.AvailabilityOffer> offersA2 =
                a2.searchAvailability(city, startDate, endDate, minPrice, maxPrice, stars, persons);

        if (offersA1 != null) {
          allOffers.addAll(offersA1);
        }
        if (offersA2 != null) {
          allOffers.addAll(offersA2);
        }

        model.addAttribute("offers", allOffers);
        model.addAttribute("hasResults", true);

        if (allOffers.isEmpty()) {
          model.addAttribute("message",
                  "Aucune offre trouvée auprès des agences partenaires.");
        }
      } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
      }
    }

    // renvoyer les critères dans le modèle pour pré-remplir le formulaire
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
          @RequestParam String agencyId,   // comes from offer.agencyId
          Model model) {

    model.addAttribute("offerId", offerId);
    model.addAttribute("agencyId", agencyId);
    return "reserve";
  }

  @PostMapping("/reserve")
  public String processReservation(
          @RequestParam String offerId,
          @RequestParam String agencyId,   // real AGENCY1 / AGENCY2 / whatever from offers
          @RequestParam String clientName,
          @RequestParam String clientEmail,
          @RequestParam String clientPhone,
          Model model) {

    try {
      String confirmationCode;

      // route to the right SOAP client based on agencyId from VM options
      if (agencyId.equals(a1AgencyId)) {
        confirmationCode = a1.makeReservation(
                offerId, clientName, clientEmail, clientPhone
        );
      } else if (agencyId.equals(a2AgencyId)) {
        confirmationCode = a2.makeReservation(
                offerId, clientName, clientEmail, clientPhone
        );
      } else {
        model.addAttribute("error", "Agence inconnue: " + agencyId);
        return "confirmation";
      }

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
    }

    return "confirmation";
  }
}
