package service;

import entities.Booking;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserSearchService {

    public void pretrazivanjeKorisnika(Scanner sc, Map<String, List<Booking>> userBookings) {
        System.out.println("Unesite korisnikov username:");
        String username = sc.nextLine();

        List<Booking> korisnikoveNarudzbe = userBookings.get(username);

        if (korisnikoveNarudzbe == null || korisnikoveNarudzbe.isEmpty()) {
            System.out.println("Korisnik " + username + " nema narudžbi!");
            return;
        }

        System.out.println("Narudžbe korisnika " + username + " :");

        korisnikoveNarudzbe.forEach(b -> System.out.println(
                "Id narudžbe: " + b.getBookingId() +
                        " | Ukupna cijena: " + b.getTotalPrice() + " EUR" +
                        " | Status: " + b.getStatus()
        ));
    }
}
