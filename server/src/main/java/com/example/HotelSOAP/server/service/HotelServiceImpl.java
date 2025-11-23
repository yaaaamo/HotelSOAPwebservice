package com.example.HotelSOAP.server.service;

import com.example.HotelSOAP.server.model.*;
import com.example.HotelSOAP.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@WebService(
        endpointInterface = "com.example.HotelSOAP.server.service.HotelService")
public class HotelServiceImpl implements HotelService {

  @Autowired
  private HotelRepository hotelRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private AgencyRepository agencyRepository;
  @Autowired
  private AvailabilityOfferRepository offerRepository;
  @Autowired
  private ReservationRepository reservationRepository;


  private final String hotelId;

  public HotelServiceImpl() {
    this.hotelId = System.getProperty("hotel.id", "H1");
  }


  private Hotel currentHotel() {
    return hotelRepository.findById(hotelId)
            .orElseThrow(() -> new WebServiceException("Unknown hotel " + hotelId));
  }

  private static boolean covers(LocalDate winStart, LocalDate winEnd,
                                LocalDate reqStart, LocalDate reqEnd) {

    return !winStart.isAfter(reqStart) && !winEnd.isBefore(reqEnd);
  }


  private static int nightsBetween(String startISO, String endISO) {
    LocalDate s = LocalDate.parse(startISO);
    LocalDate e = LocalDate.parse(endISO);
    int n = (int) ChronoUnit.DAYS.between(s, e);
    return Math.max(n, 1);
  }

  @Override
  public List<AvailabilityOffer> checkAvailability(
          String agencyId, String password, String startISO, String endISO, int persons
  ) {

    Agency agency = authenticateAndGetAgency(agencyId, password);
    double agencyFactor = agency.getDiscountFactor();

    if (startISO == null || endISO == null) {
      throw new WebServiceException("Missing dates");
    }

    LocalDate reqStart;
    LocalDate reqEnd;
    try {
      reqStart = LocalDate.parse(startISO);  // yyyy-MM-dd
      reqEnd   = LocalDate.parse(endISO);
    } catch (Exception ex) {
      throw new WebServiceException("Bad date format. Use yyyy-MM-dd");
    }

    if (!reqEnd.isAfter(reqStart)) {
      throw new WebServiceException("endDate must be after startDate");
    }

    Hotel hotel = currentHotel();
    List<Room> rooms = roomRepository.findByHotel(hotel);

    List<AvailabilityOffer> out = new ArrayList<>();

    for (Room r : rooms) {
      // 1) capacity
      if (r.getBeds() < persons) {
        System.out.println("[DEBUG] Skip room " + r.getId() +
                " because beds=" + r.getBeds() + " < persons=" + persons);
        continue;
      }

      // 2) get all availability windows (offers) for this room
      List<AvailabilityOffer> offersForRoom = offerRepository.findByRoom(r);

      for (AvailabilityOffer o : offersForRoom) {
        LocalDate oStart = LocalDate.parse(o.getStart());
        LocalDate oEnd   = LocalDate.parse(o.getEnd());

        // 3) coverage check: [oStart, oEnd] must include [reqStart, reqEnd]
        if (!covers(oStart, oEnd, reqStart, reqEnd)) {
          System.out.println("[DEBUG] offer " + o.getOfferId() +
                  " window " + oStart + " to " + oEnd +
                  " DOES NOT cover " + reqStart + " to " + reqEnd);
          continue;
        }

        // 4) stock
        if (o.getUnits() <= 0) {
          System.out.println("[DEBUG] offer " + o.getOfferId() +
                  " has no units left");
          continue;
        }

        int nights = nightsBetween(startISO, endISO);

        double basePerNight = r.getPricePerNight() * r.getType().factor();
        double baseTotal    = basePerNight * nights;

// prix pour cette agence
        double agencyPerNight = basePerNight * agencyFactor;
        double total          = agencyPerNight * nights;

        // 6) fill hotel info (in case not in DB)
        o.setHotelName(hotel.getName());
        o.setStars(hotel.getStars());
        o.setImageUrl(hotel.getImageUrl());

        o.setBasePrice(baseTotal); // prix hôtel
        o.setPrice(total);

        Address addr = hotel.getAddress();
        if (addr != null) {
          o.setCountry(addr.getCountry());
          o.setCity(addr.getCity());
          o.setStreet(addr.getStreet());
          o.setNumber(addr.getNumber());
          o.setPlaceName(addr.getPlaceName());
          o.setLatitude(addr.getLatitude());
          o.setLongitude(addr.getLongitude());
        }

        System.out.println("[DEBUG] offer " + o.getOfferId() +
                " MATCHES for " + reqStart + " to " + reqEnd +
                " persons=" + persons + " price=" + total);

        out.add(o);
      }
    }

    System.out.println("[DEBUG] Total offers returned by hotel = " + out.size());
    return out;
  }

  @Override
  public String book(String agencyId, String password, String offerId, Client mainGuest) {

    if (offerId == null)
      throw new WebServiceException("Missing offerId");

    Agency agency = authenticateAndGetAgency(agencyId, password);

    Pattern p = Pattern.compile(
            "^([A-Z0-9]+)-([A-Z0-9]+)-(\\d{4}-\\d{2}-\\d{2})-(\\d{4}-\\d{2}-\\d{2})$"
    );
    Matcher m = p.matcher(offerId);

    if (!m.matches())
      return "ERROR: Malformed offerId";

    String hotelCode = m.group(1);
    String roomId = m.group(2);
    LocalDate s = LocalDate.parse(m.group(3));
    LocalDate e = LocalDate.parse(m.group(4));

    Hotel hotel = currentHotel();
    if (!hotel.getId().equals(hotelCode))
      return "ERROR: Unknown offer";

    Room room = roomRepository.findById(roomId).orElse(null);
    if (room == null || !room.getHotel().getId().equals(hotel.getId()))
      return "ERROR: Unknown room";

    // Retrieve the exact offer we are booking
    AvailabilityOffer offer = offerRepository.findByOfferId(offerId)
            .orElse(null);

    if (offer == null) {
      return "ERROR: Offer not found";
    }

    // Check dates match / still valid
    LocalDate oStart = LocalDate.parse(offer.getStart());
    LocalDate oEnd   = LocalDate.parse(offer.getEnd());
    if (!covers(oStart, oEnd, s, e)) {
      return "ERROR: Offer no longer valid for these dates";
    }

    if (offer.getUnits() <= 0) {
      return "ERROR: No availability";
    }

    // Decrement units for this offer
    offer.setUnits(offer.getUnits() - 1);
    offerRepository.save(offer);

    // recompute price
    int nights = nightsBetween(s.toString(), e.toString());
    double perNight = room.getPricePerNight()
            * room.getType().factor()
            * agency.getDiscountFactor();
    double total = perNight * nights;

    String clientName  = (mainGuest != null ? mainGuest.getName()  : "UNKNOWN");
    String clientEmail = (mainGuest != null ? mainGuest.getEmail() : null);
    String clientPhone = (mainGuest != null ? mainGuest.getPhone() : null);

    String ref = "CONF-" + hotel.getId() + "-" + room.getId() + "-"
            + (1000 + new Random().nextInt(9000));

    // Save Reservation entity (also used in SOAP)
    Reservation res = new Reservation();
    res.setConfirmationRef(ref);
    res.setOfferId(offerId);

    res.setHotelId(hotel.getId());
    res.setHotelName(hotel.getName());
    res.setCity(hotel.getAddress() != null ? hotel.getAddress().getCity() : null);
    res.setCountry(hotel.getAddress() != null ? hotel.getAddress().getCountry() : null);

    res.setAgencyId(agency.getAgencyId());
    res.setAgencyName(agency.getName());

    res.setRoomId(room.getId());
    res.setRoomType(room.getType() != null ? room.getType().name() : null);
    res.setBeds(room.getBeds());

    res.setStartDate(s.toString());  // String yyyy-MM-dd
    res.setEndDate(e.toString());
    res.setTotalPrice(total);

    res.setClientName(clientName);
    res.setClientEmail(clientEmail);
    res.setClientPhone(clientPhone);

    reservationRepository.save(res);

    System.out.println(
            "✅ Booking confirmed for " + hotel.getName() +
                    " (" + hotel.getId() + ") for client " + clientName +
                    " via agency " + agency.getAgencyId() +
                    " → " + ref
    );

    return ref;
  }



  /**
   * Authentifie l'agence et retourne l'objet Agency
   */
  private Agency authenticateAndGetAgency(String agencyId, String password) {
    if (agencyId == null || password == null) {
      throw new WebServiceException("Missing credentials");
    }
    Agency agency = agencyRepository.findByAgencyIdAndPassword(agencyId, password)
            .orElseThrow(() -> new WebServiceException("Bad credentials"));

    if (!agency.getHotel().getId().equals(hotelId)) {
      throw new WebServiceException("Agency not partner of this hotel");
    }
    System.out.println("✅ Agency authenticated: " + agency.getName() +
            " (discount factor: " + agency.getDiscountFactor() + ")");
    return agency;
  }

  @Override
  public List<Reservation> getReservationsForAgencys(String agencyId, String password) {
    Agency agency = authenticateAndGetAgency(agencyId, password);
    return reservationRepository.findByAgencyIdOrderByCreatedAtDesc(agency.getAgencyId());
  }




}
