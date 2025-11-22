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

@WebService(
        endpointInterface = "com.example.HotelSOAP.server.service.HotelService"
)
@Service
public class HotelServiceImpl implements HotelService {

  @Autowired
  private HotelRepository hotelRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private AvailabilityWindowRepository availabilityRepository;

  @Autowired
  private AgencyRepository agencyRepository;

  // one hotel per server, like before
  private final String hotelId;

  public HotelServiceImpl() {
    this.hotelId = System.getProperty("hotel.id", "H1");
  }

  /* ========== helpers ========== */

  private Hotel currentHotel() {
    return hotelRepository.findById(hotelId)
            .orElseThrow(() -> new WebServiceException("Unknown hotel " + hotelId));
  }

  private double authenticateAgency(String agencyId, String password) {
    if (agencyId == null || password == null) {
      System.err.println("⚠️ Agency credentials missing");
      return 1.0;
    }

    Optional<Agency> agency = agencyRepository.findByAgencyIdAndPassword(agencyId, password);

    if (!agency.isPresent()) {
      System.err.println("⚠️ Agency authentication failed for: " + agencyId);
      return 1.0;
    }

    if (!agency.get().getHotel().getId().equals(hotelId)) {
      System.err.println("⚠️ Agency " + agencyId + " is not a partner of hotel " + hotelId);
      return 1.0;
    }

    System.out.println("✅ Agency authenticated: " + agency.get().getName() +
            " (discount factor: " + agency.get().getDiscountFactor() + ")");

    return agency.get().getDiscountFactor();
  }


  private static boolean covers(LocalDate winStart, LocalDate winEnd,
                                LocalDate reqStart, LocalDate reqEnd) {
    // cover if [winStart,winEnd) ⊇ [reqStart,reqEnd)
    return !winStart.isAfter(reqStart) && !winEnd.isBefore(reqEnd);
  }

  private int unitsAvailableFor(Room room, LocalDate reqStart, LocalDate reqEnd) {
    return availabilityRepository.findByRoom(room).stream()
            .filter(w -> covers(w.getStart(), w.getEnd(), reqStart, reqEnd))
            .mapToInt(AvailabilityWindow::getUnits)
            .sum();
  }

  private static int nightsBetween(String startISO, String endISO) {
    LocalDate s = LocalDate.parse(startISO);
    LocalDate e = LocalDate.parse(endISO);
    int n = (int) ChronoUnit.DAYS.between(s, e);
    return Math.max(n, 1);
  }


  @Override
  public List<AvailabilityOffer> checkAvailability(
          String token, String startISO, String endISO, int persons
  ) {
    // 🔐 Validate token → retrieve agency
    Agency agency = requireAgencyByToken(token);
    double agencyFactor = agency.getDiscountFactor();

    if (startISO == null || endISO == null)
      throw new WebServiceException("Missing dates");

    LocalDate s, e;
    try {
      s = LocalDate.parse(startISO);
      e = LocalDate.parse(endISO);
    } catch (Exception ex) {
      throw new WebServiceException("Bad date format. Use yyyy-MM-dd");
    }

    int nights = nightsBetween(startISO, endISO);

    Hotel hotel = currentHotel();
    List<Room> rooms = roomRepository.findByHotel(hotel);

    List<AvailabilityOffer> out = new ArrayList<>();

    for (Room r : rooms) {

      // must support required number of persons
      if (r.getBeds() < persons)
        continue;

      // check if any units available
      int left = unitsAvailableFor(r, s, e);
      if (left <= 0)
        continue;

      // Calculate pricing
      double perNight = r.getPricePerNight()
              * r.getType().factor()
              * agencyFactor;

      double total = perNight * nights;

      // Build offer
      AvailabilityOffer o = new AvailabilityOffer();
      o.setOfferId(
              hotel.getId() + "-" + r.getId() + "-" + startISO + "-" + endISO
      );
      o.setRoomType(r.getType());
      o.setBeds(r.getBeds());
      o.setStart(startISO);
      o.setEnd(endISO);
      o.setPrice(total);

      out.add(o);
    }

    return out;
  }


  @Override
  public String book(String token, String offerId, Client mainGuest) {

    if (offerId == null)
      throw new WebServiceException("Missing offerId");

    // 🔐 Validate token → retrieve agency
    Agency agency = requireAgencyByToken(token);

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

    if (unitsAvailableFor(room, s, e) <= 0)
      return "ERROR: No availability";

    // Decrement room availability
    List<AvailabilityWindow> wins = availabilityRepository.findByRoom(room);
    for (AvailabilityWindow w : wins) {
      if (covers(w.getStart(), w.getEnd(), s, e) && w.getUnits() > 0) {
        w.setUnits(w.getUnits() - 1);
        availabilityRepository.save(w);
        break;
      }
    }

    String clientName = (mainGuest != null ? mainGuest.getName() : "UNKNOWN");
    String ref = "CONF-" + hotel.getId() + "-" + room.getId() + "-"
            + (1000 + new Random().nextInt(9000));

    System.out.println(
            "✅ Booking confirmed for " + hotel.getName() +
                    " (" + hotel.getId() + ") for client " + clientName +
                    " via agency " + agency.getAgencyId() +
                    " → " + ref
    );

    return ref;
  }


  private Agency requireAgencyByToken(String token) {
    if (token == null) {
      throw new WebServiceException("Missing token");
    }

    Agency agency = agencyRepository.findByAuthToken(token)
            .orElseThrow(() -> new WebServiceException("Invalid token"));

    if (!agency.getHotel().getId().equals(hotelId)) {
      throw new WebServiceException("Token not valid for this hotel");
    }

    return agency;
  }

  @Override
  public String login(String agencyId, String password) {
    if (agencyId == null || password == null) {
      throw new WebServiceException("Missing credentials");
    }

    Agency agency = agencyRepository
            .findByAgencyIdAndPassword(agencyId, password)
            .orElseThrow(() -> new WebServiceException("Bad credentials"));

    // Vérifier que l'agence est partenaire de cet hôtel
    if (!agency.getHotel().getId().equals(hotelId)) {
      throw new WebServiceException("Agency not partner of this hotel");
    }

    // Générer un token
    String token = java.util.UUID.randomUUID().toString();
    agency.setAuthToken(token);
    agencyRepository.save(agency);

    System.out.println("✅ Login success for agency " + agency.getAgencyId()
            + " on hotel " + hotelId + " → token=" + token);

    return token;
  }

}