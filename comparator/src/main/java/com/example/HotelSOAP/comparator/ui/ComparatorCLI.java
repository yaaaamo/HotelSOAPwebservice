package com.example.HotelSOAP.comparator.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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
    System.out.println("    🔍 COMPARATEUR D'OFFRES HÔTELIÈRES");
    System.out.println(repeat("=", 60) + "\n");

    while (running) {
      displayMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          searchAndCompare(scanner);
          break;
        case "2":
          makeReservation(scanner);
          break;
        case "3":
          listAgencies();
          break;
        case "0":
          running = false;
          System.out.println("\n👋 Au revoir !\n");
          break;
        default:
          System.out.println("\n⚠️  Choix invalide.\n");
      }
    }
  }

  private void displayMenu() {
    System.out.println("\n" + repeat("─", 60));
    System.out.println("MENU COMPARATEUR");
    System.out.println(repeat("─", 60));
    System.out.println("1. 🔍 Rechercher et comparer les offres");
    System.out.println("2. 📝 Effectuer une réservation");
    System.out.println("3. 📋 Lister les agences");
    System.out.println("0. 🚪 Quitter");
    System.out.println(repeat("─", 60));
    System.out.print("Votre choix : ");
  }

  private void searchAndCompare(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("RECHERCHE ET COMPARAISON");
    System.out.println(repeat("=", 60));

    try {
      System.out.print("Date d'arrivée (yyyy-MM-dd) : ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (yyyy-MM-dd) : ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Nombre de personnes : ");
      int persons = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Recherche dans toutes les agences...\n");

      List<OfferWithAgency> allOffers = new ArrayList<>();

      // Recherche via Agence A1
      System.out.println("📍 Agence A1 (localhost:9090):");
      try {
        List<com.example.HotelSOAP.comparator.clients.a1.AvailabilityOffer> offersA1 =
                a1Client.searchAvailability(startDate, endDate, persons);

        if (offersA1 == null || offersA1.isEmpty()) {
          System.out.println("   Aucune offre trouvée");
        } else {
          System.out.println("   ✅ " + offersA1.size() + " offre(s) trouvée(s)");
          for (com.example.HotelSOAP.comparator.clients.a1.AvailabilityOffer offer : offersA1) {
            allOffers.add(new OfferWithAgency(
                    "A1",
                    offer.getOfferId(),
                    offer.getHotelId(),
                    offer.getRoomType(),
                    offer.getBeds(),
                    offer.getPrice()
            ));
          }
        }
      } catch (Exception e) {
        System.out.println("   ⚠️  Erreur: " + e.getMessage());
      }

      // Recherche via Agence A2
      System.out.println("\n📍 Agence A2 (localhost:9091):");
      try {
        List<com.example.HotelSOAP.comparator.clients.a2.AvailabilityOffer> offersA2 =
                a2Client.searchAvailability(startDate, endDate, persons);

        if (offersA2 == null || offersA2.isEmpty()) {
          System.out.println("   Aucune offre trouvée");
        } else {
          System.out.println("   ✅ " + offersA2.size() + " offre(s) trouvée(s)");
          for (com.example.HotelSOAP.comparator.clients.a2.AvailabilityOffer offer : offersA2) {
            allOffers.add(new OfferWithAgency(
                    "A2",
                    offer.getOfferId(),
                    offer.getHotelId(),
                    offer.getRoomType(),
                    offer.getBeds(),
                    offer.getPrice()
            ));
          }
        }
      } catch (Exception e) {
        System.out.println("   ⚠️  Erreur: " + e.getMessage());
      }

      // Afficher le comparatif
      displayComparison(allOffers);

    } catch (Exception e) {
      System.out.println("⚠️  Erreur : " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void displayComparison(List<OfferWithAgency> allOffers) {
    if (allOffers.isEmpty()) {
      System.out.println("\n❌ Aucune offre disponible.\n");
      return;
    }

    System.out.println("\n" + repeat("=", 80));
    System.out.println("RÉSULTATS COMPARATIFS (" + allOffers.size() + " offre(s))");
    System.out.println(repeat("=", 80));

    // Trier par prix croissant
    allOffers.sort(Comparator.comparingDouble(OfferWithAgency::getPrice));

    int index = 1;
    for (OfferWithAgency offer : allOffers) {
      System.out.println("\n" + index++ + ". 💰 " + offer.getPrice() + "€");
      System.out.println("   🏨 Hôtel: " + offer.getHotelId());
      System.out.println("   🛏️  Type: " + offer.getRoomType() + " (" + offer.getBeds() + " lits)");
      System.out.println("   🏢 Via: Agence " + offer.getAgencyId());
      System.out.println("   🔖 ID Offre: " + offer.getOfferId());
    }

    System.out.println("\n" + repeat("─", 80));
    System.out.println("💡 Meilleure offre : " + allOffers.get(0).getPrice() + "€ via Agence "
            + allOffers.get(0).getAgencyId());
    System.out.println(repeat("─", 80));
  }

  private void makeReservation(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("EFFECTUER UNE RÉSERVATION");
    System.out.println(repeat("=", 60));

    try {
      System.out.println("Choisissez une agence:");
      System.out.println("1. Agence A1 (localhost:9090)");
      System.out.println("2. Agence A2 (localhost:9091)");
      System.out.print("Votre choix : ");
      String agencyChoice = scanner.nextLine().trim();

      System.out.print("ID de l'offre : ");
      String offerId = scanner.nextLine().trim();

      System.out.print("Nom du client : ");
      String clientName = scanner.nextLine().trim();

      System.out.print("Email du client : ");
      String clientEmail = scanner.nextLine().trim();

      System.out.print("Téléphone du client : ");
      String clientPhone = scanner.nextLine().trim();

      System.out.println("\n📝 Réservation en cours...\n");

      String confirmation;

      if ("1".equals(agencyChoice)) {
        confirmation = a1Client.makeReservation(offerId, clientName, clientEmail, clientPhone);
      } else if ("2".equals(agencyChoice)) {
        confirmation = a2Client.makeReservation(offerId, clientName, clientEmail, clientPhone);
      } else {
        System.out.println("⚠️  Choix invalide.");
        return;
      }

      if (confirmation != null && confirmation.startsWith("ERROR")) {
        System.out.println("❌ Erreur : " + confirmation);
      } else {
        System.out.println("✅ Réservation confirmée !");
        System.out.println("   Référence : " + confirmation);
      }

    } catch (Exception e) {
      System.out.println("⚠️  Erreur : " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void listAgencies() {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("AGENCES PARTENAIRES");
    System.out.println(repeat("=", 60));
    System.out.println("1. 🏢 Agence A1 - http://localhost:9090/agencyservice");
    System.out.println("2. 🏢 Agence A2 - http://localhost:9091/agencyservice");
  }

  // Helper compatible Java 8
  private static String repeat(String s, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  /**
   * Classe interne pour stocker une offre avec son agence source
   */
  private static class OfferWithAgency {
    private String agencyId;
    private String offerId;
    private String hotelId;
    private String roomType;
    private int beds;
    private double price;

    public OfferWithAgency(String agencyId, String offerId, String hotelId,
                           String roomType, int beds, double price) {
      this.agencyId = agencyId;
      this.offerId = offerId;
      this.hotelId = hotelId;
      this.roomType = roomType;
      this.beds = beds;
      this.price = price;
    }

    public String getAgencyId() { return agencyId; }
    public String getOfferId() { return offerId; }
    public String getHotelId() { return hotelId; }
    public String getRoomType() { return roomType; }
    public int getBeds() { return beds; }
    public double getPrice() { return price; }
  }
}