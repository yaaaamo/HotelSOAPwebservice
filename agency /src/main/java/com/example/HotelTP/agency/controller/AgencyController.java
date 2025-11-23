package com.example.HotelTP.agency.controller;
import com.example.HotelTP.agency.model.AvailabilityOffer;
import com.example.HotelTP.agency.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class AgencyController {

  @Autowired
  private AgencyService agencyService;

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
        // Appel du service avec tous les critères
        List<AvailabilityOffer> offers = agencyService.searchAvailability(
                city, startDate, endDate, minPrice, maxPrice, stars, persons);

        model.addAttribute("offers", offers);
        model.addAttribute("city", city);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("stars", stars);
        model.addAttribute("persons", persons);
        model.addAttribute("hasResults", true);

        if (offers.isEmpty()) {
          model.addAttribute("message",
                  "Aucune offre disponible pour vos critères de recherche. " +
                          "Essayez d'élargir votre recherche.");
        }
      } catch (Exception e) {
        model.addAttribute("error",
                "Erreur lors de la recherche des offres: " + e.getMessage());
      }
    }

    return "search";
  }

  @GetMapping("/reserve")
  public String reserveForm(
          @RequestParam String offerId,
          Model model) {

    model.addAttribute("offerId", offerId);
    return "reserve";
  }

  @PostMapping("/reserve")
  public String processReservation(
          @RequestParam String offerId,
          @RequestParam String clientName,
          @RequestParam String clientEmail,
          @RequestParam String clientPhone,
          Model model) {

    try {
      String confirmationCode = agencyService.makeReservation(
              offerId, clientName, clientEmail, clientPhone);

      if (confirmationCode != null && !confirmationCode.startsWith("ERROR")) {
        model.addAttribute("success", true);
        model.addAttribute("confirmationCode", confirmationCode);
        model.addAttribute("clientName", clientName);
        model.addAttribute("clientEmail", clientEmail);
      } else {
        model.addAttribute("error",
                "La réservation a échoué: " + confirmationCode);
      }
    } catch (Exception e) {
      model.addAttribute("error",
              "Erreur lors du traitement de la réservation: " + e.getMessage());
    }

    return "confirmation";
  }

  @GetMapping("/hotels")
  public String listHotels(Model model) {
    model.addAttribute("message",
            "Nos hôtels partenaires offrent le meilleur service et confort.");
    return "hotels";
  }
}