package com.example.HotelSOAP.comparator.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI for the Comparator - compares offers from multiple agencies
 * NO tokens needed - just calls agency services
 */
@Component
public class ComparatorCLI implements CommandLineRunner {

  @Autowired
  @Qualifier("a1Client")
  private com.example.HotelSOAP.comparator.clients.a1.AgencyService a1Client;

  @Autowired
  @Qualifier("a2Client")
  private com.example.HotelSOAP.comparator.clients.a2.AgencyService a2Client;

  @Override
  public void run(String... args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    System.out.println("\n" + repeat("=", 60));
    System.out.println("    🔍 HOTEL PRICE COMPARATOR");
    System.out.println("    Compare offers from multiple agencies");
    System.out.println(repeat("=", 60) + "\n");

    while (running) {
      displayMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          compareOffers(scanner);
          break;
        case "2":
          searchSpecificAgency(scanner);
          break;
        case "3":
          bookOffer(scanner);
          break;
        case "4":
          listAgencies();
          break;
        case "0":
          running = false;
          System.out.println("\n👋 Goodbye!\n");
          break;
        default:
          System.out.println("\n⚠️  Invalid choice.\n");
      }
    }
  }

  private void displayMenu() {
    System.out.println("\n" + repeat("─", 60));
    System.out.println("MAIN MENU");
    System.out.println(repeat("─", 60));
    System.out.println("1. 💰 Compare offers from ALL agencies");
    System.out.println("2. 🏢 Search specific agency");
    System.out.println("3. 📝 Book an offer");
    System.out.println("4. 📋 List partner agencies");
    System.out.println("0. 🚪 Exit");
    System.out.println(repeat("─", 60));
    System.out.print("Your choice: ");
  }

  private void compareOffers(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("COMPARE OFFERS FROM ALL AGENCIES");
    System.out.println(repeat("=", 60));

    try {
      System.out.print("City: ");
      String city = scanner.nextLine().trim();

      System.out.print("Check-in date (yyyy-MM-dd): ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Check-out date (yyyy-MM-dd): ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Number of guests: ");
      int persons = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Comparing offers...\n");

      List<ComparatorOffer> allOffers = new ArrayList<>();

      try {
        List<com.example.HotelSOAP.comparator.clients.a1.AgencyOffer> offersA1 =
                a1Client.search(city, startDate, endDate, persons);

        if (offersA1 != null) {
          for (com.example.HotelSOAP.comparator.clients.a1.AgencyOffer o : offersA1) {
            allOffers.add(new ComparatorOffer(
                    "Agency1",
                    o.getOfferId(),
                    o.getHotelName() != null ? o.getHotelName() : "Hotel " + o.getHotelId(),
                    o.getRoomType(),
                    o.getBeds(),
                    o.getPrice(),
                    o.getAgencyName()
            ));
          }
          System.out.println("✅ Found " + offersA1.size() + " offers from Agency1");
        }
      } catch (Exception e) {
        System.err.println("⚠️  Error from Agency1: " + e.getMessage());
      }

      try {
        List<com.example.HotelSOAP.comparator.clients.a2.AgencyOffer> offersA2 =
                a2Client.search(city, startDate, endDate, persons);

        if (offersA2 != null) {
          for (com.example.HotelSOAP.comparator.clients.a2.AgencyOffer o : offersA2) {
            allOffers.add(new ComparatorOffer(
                    "Agency2",
                    o.getOfferId(),
                    o.getHotelName() != null ? o.getHotelName() : "Hotel " + o.getHotelId(),
                    o.getRoomType(),
                    o.getBeds(),
                    o.getPrice(),
                    o.getAgencyName()
            ));
          }
          System.out.println("✅ Found " + offersA2.size() + " offers from Agency2");
        }
      } catch (Exception e) {
        System.err.println("⚠️  Error from Agency2: " + e.getMessage());
      }

      if (allOffers.isEmpty()) {
        System.out.println("\n❌ No offers found from any agency.");
      } else {
        displayComparisonResults(allOffers);
      }

    } catch (Exception e) {
      System.out.println("⚠️  Error: " + e.getMessage());
    }
  }

  private void displayComparisonResults(List<ComparatorOffer> offers) {
    List<ComparatorOffer> sorted = offers.stream()
            .sorted(Comparator.comparingDouble(ComparatorOffer::getPrice))
            .collect(Collectors.toList());

    System.out.println("\n" + repeat("=", 80));
    System.out.println("COMPARISON RESULTS (" + sorted.size() + " offers, sorted by price)");
    System.out.println(repeat("=", 80));
    System.out.printf("%-5s %-15s %-20s %-12s %-6s %-10s%n",
            "Rank", "Agency", "Hotel", "Room", "Beds", "Price");
    System.out.println(repeat("-", 80));

    int rank = 1;
    for (ComparatorOffer offer : sorted) {
      System.out.printf("%-5d %-15s %-20s %-12s %-6d €%-9.2f%n",
              rank++,
              offer.getAgencySource(),
              truncate(offer.getHotelName(), 20),
              truncate(offer.getRoomType(), 12),
              offer.getBeds(),
              offer.getPrice()
      );
    }

    System.out.println(repeat("=", 80));

    if (!sorted.isEmpty()) {
      ComparatorOffer best = sorted.get(0);
      System.out.println("\n🏆 BEST OFFER:");
      System.out.println("   Agency: " + best.getAgencySource());
      System.out.println("   Hotel: " + best.getHotelName());
      System.out.println("   Room: " + best.getRoomType() + " (" + best.getBeds() + " beds)");
      System.out.println("   Price: €" + best.getPrice());
      System.out.println("   Offer ID: " + best.getOfferId());
    }
  }

  private void searchSpecificAgency(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("SEARCH SPECIFIC AGENCY");
    System.out.println(repeat("=", 60));

    try {
      System.out.println("Choose an agency:");
      System.out.println("1. Agency1 (localhost:9090)");
      System.out.println("2. Agency2 (localhost:9091)");
      System.out.print("Your choice: ");
      String agencyChoice = scanner.nextLine().trim();

      System.out.print("City: ");
      String city = scanner.nextLine().trim();

      System.out.print("Check-in date (yyyy-MM-dd): ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Check-out date (yyyy-MM-dd): ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Number of guests: ");
      int persons = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Searching...\n");

      if ("1".equals(agencyChoice)) {
        List<com.example.HotelSOAP.comparator.clients.a1.AgencyOffer> offers =
                a1Client.search(city, startDate, endDate, persons);
        displayOffersA1(offers);

      } else if ("2".equals(agencyChoice)) {
        List<com.example.HotelSOAP.comparator.clients.a2.AgencyOffer> offers =
                a2Client.search(city, startDate, endDate, persons);
        displayOffersA2(offers);

      } else {
        System.out.println("  Invalid choice.");
      }

    } catch (Exception e) {
      System.out.println("  Error: " + e.getMessage());
    }
  }

  private void bookOffer(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("BOOK AN OFFER");
    System.out.println(repeat("=", 60));

    try {
      System.out.println("Which agency has the offer?");
      System.out.println("1. Agency1 (localhost:9090)");
      System.out.println("2. Agency2 (localhost:9091)");
      System.out.print("Your choice: ");
      String agencyChoice = scanner.nextLine().trim();

      System.out.print("Offer ID: ");
      String offerId = scanner.nextLine().trim();

      System.out.print("Client name: ");
      String clientName = scanner.nextLine().trim();

      System.out.print("Client email: ");
      String clientEmail = scanner.nextLine().trim();

      System.out.print("Client phone: ");
      String clientPhone = scanner.nextLine().trim();

      System.out.println("\n📝 Booking in progress...\n");

      String confirmation;

      if ("1".equals(agencyChoice)) {
        confirmation = a1Client.book(offerId, clientName, clientEmail, clientPhone);
      } else if ("2".equals(agencyChoice)) {
        confirmation = a2Client.book(offerId, clientName, clientEmail, clientPhone);
      } else {
        System.out.println("  Invalid choice.");
        return;
      }

      if (confirmation != null && confirmation.startsWith("ERROR")) {
        System.out.println("❌ Error: " + confirmation);
      } else {
        System.out.println("✅ Booking confirmed!");
        System.out.println("   Reference: " + confirmation);
      }

    } catch (Exception e) {
      System.out.println("⚠️  Error: " + e.getMessage());
    }
  }

  private void listAgencies() {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("PARTNER AGENCIES");
    System.out.println(repeat("=", 60));
    System.out.println("1. 🏢 Agency1 - http://localhost:9090/agencyservice");
    System.out.println("2. 🏢 Agency2 - http://localhost:9091/agencyservice");
  }

  private void displayOffersA1(List<com.example.HotelSOAP.comparator.clients.a1.AgencyOffer> offers) {
    if (offers == null || offers.isEmpty()) {
      System.out.println("   No offers found.");
    } else {
      System.out.println("✅ " + offers.size() + " offer(s) available:\n");
      int index = 1;
      for (com.example.HotelSOAP.comparator.clients.a1.AgencyOffer o : offers) {
        System.out.println("   " + index++ + ". " +
                (o.getHotelName() != null ? o.getHotelName() : "Hotel " + o.getHotelId()) +
                " - " + o.getRoomType() + " - " + o.getBeds() + " beds - €" + o.getPrice());
      }
    }
  }

  private void displayOffersA2(List<com.example.HotelSOAP.comparator.clients.a2.AgencyOffer> offers) {
    if (offers == null || offers.isEmpty()) {
      System.out.println("   No offers found.");
    } else {
      System.out.println("✅ " + offers.size() + " offer(s) available:\n");
      int index = 1;
      for (com.example.HotelSOAP.comparator.clients.a2.AgencyOffer o : offers) {
        System.out.println("   " + index++ + ". " +
                (o.getHotelName() != null ? o.getHotelName() : "Hotel " + o.getHotelId()) +
                " - " + o.getRoomType() + " - " + o.getBeds() + " beds - €" + o.getPrice());
      }
    }
  }

  private static String repeat(String s, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) sb.append(s);
    return sb.toString();
  }

  private static String truncate(String s, int maxLen) {
    if (s == null) return "";
    return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
  }

  private static class ComparatorOffer {
    private final String agencySource;
    private final String offerId;
    private final String hotelName;
    private final String roomType;
    private final int beds;
    private final double price;
    private final String agencyName;

    public ComparatorOffer(String agencySource, String offerId, String hotelName,
                           String roomType, int beds, double price, String agencyName) {
      this.agencySource = agencySource;
      this.offerId = offerId;
      this.hotelName = hotelName;
      this.roomType = roomType;
      this.beds = beds;
      this.price = price;
      this.agencyName = agencyName;
    }

    public String getAgencySource() { return agencySource; }
    public String getOfferId() { return offerId; }
    public String getHotelName() { return hotelName; }
    public String getRoomType() { return roomType; }
    public int getBeds() { return beds; }
    public double getPrice() { return price; }
    public String getAgencyName() { return agencyName; }
  }
}