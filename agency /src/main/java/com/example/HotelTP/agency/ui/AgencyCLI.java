package com.example.HotelTP.agency.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

/**
 * CLI pour l'agence utilisant les proxies SOAP injectés comme beans Spring
 */
@Component
public class AgencyCLI implements CommandLineRunner {

  // Injection des credentials depuis application.properties
  @Value("${agency.id}")
  private String agencyId;

  @Value("${agency.login}")
  private String login;

  @Value("${agency.password}")
  private String password;

  @Value("${agency.name}")
  private String agencyName;

  // Injection des proxies SOAP comme beans Spring
  @Autowired
  @Qualifier("h1Client")
  private com.example.HotelTP.agency.clients.h1.HotelService h1Client;

  @Autowired
  @Qualifier("h2Client")
  private com.example.HotelTP.agency.clients.h2.HotelService h2Client;

  @Override
  public void run(String... args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    System.out.println("\n" + repeat("=", 60));
    System.out.println("    🏢 Bienvenue à " + agencyName);
    System.out.println("    ID: " + agencyId + " | Login: " + login);
    System.out.println(repeat("=", 60) + "\n");

    while (running) {
      displayMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          searchAllHotels(scanner);
          break;
        case "2":
          searchSpecificHotel(scanner);
          break;
        case "3":
          makeReservation(scanner);
          break;
        case "4":
          listPartnerHotels();
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
    System.out.println("MENU PRINCIPAL");
    System.out.println(repeat("─", 60));
    System.out.println("1. 🔍 Rechercher dans TOUS les hôtels");
    System.out.println("2. 🏨 Rechercher dans un hôtel spécifique");
    System.out.println("3. 📝 Effectuer une réservation");
    System.out.println("4. 📋 Lister les hôtels partenaires");
    System.out.println("0. 🚪 Quitter");
    System.out.println(repeat("─", 60));
    System.out.print("Votre choix : ");
  }

  private void searchAllHotels(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("RECHERCHE DANS TOUS LES HÔTELS");
    System.out.println(repeat("=", 60));

    try {
      System.out.print("Date d'arrivée (yyyy-MM-dd) : ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (yyyy-MM-dd) : ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Nombre de personnes : ");
      int persons = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Recherche en cours...\n");

      // Recherche dans H1
      System.out.println("📍 Hotel H1 (localhost:8080):");
      try {
        List<com.example.HotelTP.agency.clients.h1.AvailabilityOffer> offersH1 =
                h1Client.checkAvailability(agencyId, password, startDate, endDate, persons);

        if (offersH1 == null || offersH1.isEmpty()) {
          System.out.println("   Aucune disponibilité");
        } else {
          for (com.example.HotelTP.agency.clients.h1.AvailabilityOffer offer : offersH1) {
            System.out.println("    ✅ " + offer.getRoomType() +
                    " - " + offer.getBeds() + " lits - " +
                    offer.getPrice() + "€");
            System.out.println("      Offre: " + offer.getOfferId());
          }
        }
      } catch (Exception e) {
        System.out.println("   ⚠️  Erreur: " + e.getMessage());
      }

      // Recherche dans H2
      System.out.println("\n📍 Hotel H2 (localhost:8083):");
      try {
        List<com.example.HotelTP.agency.clients.h2.AvailabilityOffer> offersH2 =
                h2Client.checkAvailability(agencyId, password, startDate, endDate, persons);

        if (offersH2 == null || offersH2.isEmpty()) {
          System.out.println("   Aucune disponibilité");
        } else {
          for (com.example.HotelTP.agency.clients.h2.AvailabilityOffer offer : offersH2) {
            System.out.println("   ✅ " + offer.getRoomType() +
                    " - " + offer.getBeds() + " lits - " +
                    offer.getPrice() + "€");
            System.out.println("      Offre: " + offer.getOfferId());
          }
        }
      } catch (Exception e) {
        System.out.println("   ⚠️  Erreur: " + e.getMessage());
      }

    } catch (Exception e) {
      System.out.println("⚠️  Erreur : " + e.getMessage());
    }
  }

  private void searchSpecificHotel(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("RECHERCHE DANS UN HÔTEL SPÉCIFIQUE");
    System.out.println(repeat("=", 60));

    try {
      System.out.println("Choisissez un hôtel:");
      System.out.println("1. Hotel H1 (localhost:8080)");
      System.out.println("2. Hotel H2 (localhost:8083)");
      System.out.print("Votre choix : ");
      String hotelChoice = scanner.nextLine().trim();

      System.out.print("Date d'arrivée (yyyy-MM-dd) : ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (yyyy-MM-dd) : ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Nombre de personnes : ");
      int persons = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Recherche en cours...\n");

      if ("1".equals(hotelChoice)) {
        List<com.example.HotelTP.agency.clients.h1.AvailabilityOffer> offers =
                h1Client.checkAvailability(agencyId, password, startDate, endDate, persons);
        displayOffersH1(offers);

      } else if ("2".equals(hotelChoice)) {
        List<com.example.HotelTP.agency.clients.h2.AvailabilityOffer> offers =
                h2Client.checkAvailability(agencyId, password, startDate, endDate, persons);
        displayOffersH2(offers);

      } else {
        System.out.println("⚠️  Choix invalide.");
      }

    } catch (Exception e) {
      System.out.println("⚠️  Erreur : " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void makeReservation(Scanner scanner) {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("EFFECTUER UNE RÉSERVATION");
    System.out.println(repeat("=", 60));

    try {
      System.out.println("Choisissez un hôtel:");
      System.out.println("1. Hotel H1 (localhost:8080)");
      System.out.println("2. Hotel H2 (localhost:8083)");
      System.out.print("Votre choix : ");
      String hotelChoice = scanner.nextLine().trim();

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

      if ("1".equals(hotelChoice)) {
        com.example.HotelTP.agency.clients.h1.Client client =
                new com.example.HotelTP.agency.clients.h1.Client();
        client.setName(clientName);
        client.setEmail(clientEmail);
        client.setPhone(clientPhone);

        confirmation = h1Client.book(agencyId, password, offerId, client);

      } else if ("2".equals(hotelChoice)) {
        com.example.HotelTP.agency.clients.h2.Client client =
                new com.example.HotelTP.agency.clients.h2.Client();
        client.setName(clientName);
        client.setEmail(clientEmail);
        client.setPhone(clientPhone);

        confirmation = h2Client.book(agencyId, password, offerId, client);

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

  private void listPartnerHotels() {
    System.out.println("\n" + repeat("=", 60));
    System.out.println("HÔTELS PARTENAIRES");
    System.out.println(repeat("=", 60));
    System.out.println("1. 🏨 Hotel H1 - http://localhost:8080/hotelservice");
    System.out.println("2. 🏨 Hotel H2 - http://localhost:8083/hotelservice");
  }

  private void displayOffersH1(List<com.example.HotelTP.agency.clients.h1.AvailabilityOffer> offers) {
    if (offers == null || offers.isEmpty()) {
      System.out.println("❌ Aucune disponibilité trouvée.");
    } else {
      System.out.println("✅ " + offers.size() + " offre(s) disponible(s):\n");
      int index = 1;
      for (com.example.HotelTP.agency.clients.h1.AvailabilityOffer offer : offers) {
        System.out.println(index++ + ". " + offer.getRoomType() +
                " - " + offer.getBeds() + " lits - " +
                offer.getPrice() + "€");
        System.out.println("   Offre: " + offer.getOfferId());
      }
    }
  }

  private void displayOffersH2(List<com.example.HotelTP.agency.clients.h2.AvailabilityOffer> offers) {
    if (offers == null || offers.isEmpty()) {
      System.out.println("❌ Aucune disponibilité trouvée.");
    } else {
      System.out.println("✅ " + offers.size() + " offre(s) disponible(s):\n");
      int index = 1;
      for (com.example.HotelTP.agency.clients.h2.AvailabilityOffer offer : offers) {
        System.out.println(index++ + ". " + offer.getRoomType() +
                " - " + offer.getBeds() + " lits - " +
                offer.getPrice() + "€");
        System.out.println("   Offre: " + offer.getOfferId());
      }
    }
  }

  // Helper compatible Java 8 (remplace String.repeat)
  private static String repeat(String s, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(s);
    }
    return sb.toString();
  }
}