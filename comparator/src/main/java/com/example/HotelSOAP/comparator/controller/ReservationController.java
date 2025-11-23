package com.example.HotelSOAP.comparator.controller;

import com.example.HotelSOAP.comparator.model.GlobalReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ReservationController {

  @Autowired
  @Qualifier("a1Client")
  private com.example.HotelSOAP.comparator.clients.a1.AgencyService a1Client;

  @Autowired
  @Qualifier("a2Client")
  private com.example.HotelSOAP.comparator.clients.a2.AgencyService a2Client;

  @GetMapping("/reservations")
  public String listAllReservations(Model model) {

    List<GlobalReservation> all = new ArrayList<>();

    // ===== AGENCY 1 =====
    try {
      List<com.example.HotelSOAP.comparator.clients.a1.Reservation> resA1 =
              a1Client.listReservations();

      if (resA1 != null) {
        for (com.example.HotelSOAP.comparator.clients.a1.Reservation r : resA1) {
          GlobalReservation g = new GlobalReservation();

          g.setAgencyName(r.getAgencyName());              // filled in agency service
          g.setHotelId(r.getHotelId());
          g.setHotelName(r.getHotelName());
          g.setCity(r.getCity());
          g.setConfirmationRef(r.getConfirmationRef());
          g.setClientName(r.getClientName());
          g.setStartDate(r.getStartDate());
          g.setEndDate(r.getEndDate());
          g.setTotalPrice(r.getTotalPrice());

          all.add(g);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Error calling a1.listReservations(): " + e.getMessage());
    }

    // ===== AGENCY 2 =====
    try {
      List<com.example.HotelSOAP.comparator.clients.a2.Reservation> resA2 =
              a2Client.listReservations();

      if (resA2 != null) {
        for (com.example.HotelSOAP.comparator.clients.a2.Reservation r : resA2) {
          GlobalReservation g = new GlobalReservation();

          g.setAgencyName(r.getAgencyName());
          g.setHotelId(r.getHotelId());
          g.setHotelName(r.getHotelName());
          g.setCity(r.getCity());
          g.setConfirmationRef(r.getConfirmationRef());
          g.setClientName(r.getClientName());
          g.setStartDate(r.getStartDate());
          g.setEndDate(r.getEndDate());
          g.setTotalPrice(r.getTotalPrice());

          all.add(g);
        }
      }
    } catch (Exception e) {
      System.err.println("⚠️ Error calling a2.listReservations(): " + e.getMessage());
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
}
