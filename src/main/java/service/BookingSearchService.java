package service;

import entities.Booking;
import entities.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.AnswerUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookingSearchService {

    private static final Logger log = LoggerFactory.getLogger(BookingSearchService.class);

    private final XmlLogService xmlLogService;

    public BookingSearchService(XmlLogService xmlLogService) {
        this.xmlLogService = xmlLogService;
    }

    public void odabirNarudzbe(java.util.Scanner sc, List<Booking> bookings, Set<Record> records) {

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("Nema narudžbi!");
            return;
        }

        List<Booking> sortiraneNarudzbe = bookings.stream()
                .sorted(Comparator.comparing(Booking::getTotalPrice))
                .toList();

        Booking cheapestBooking = sortiraneNarudzbe.getFirst();
        Booking expensiveBooking = sortiraneNarudzbe.getLast();

        int odabir = AnswerUtil.odabirOdgovor(sc,
                "Koju narudžbu želite odabrati:\n" +
                        "(1) Najskuplja narudžba\n" +
                        "(2) Najjeftinija narudžba\n" +
                        "(3) Plaćene narudžbe\n" +
                        "Vaš odabir:",
                1, 3
        );

        if (odabir == 1) {
            xmlLogService.addLogEntry("PRETRAZIVANJE_NARUDZBI", "Ispis najskuplje narudžbe");
            System.out.println("Najskuplja narudžba: " + expensiveBooking.getBookingId());
            System.out.println("Naručitelj: " + expensiveBooking.getUser().getUsername());
            System.out.println("Ukupna cijena: " + expensiveBooking.getTotalPrice() + " EUR");
            System.out.println("Status: " + expensiveBooking.getStatus());
        }

        if (odabir == 2) {
            xmlLogService.addLogEntry("PRETRAZIVANJE_NARUDZBI", "Ispis najjeftinije narudžbe");
            System.out.println("Najjeftinija narudžba: " + cheapestBooking.getBookingId());
            System.out.println("Naručitelj: " + cheapestBooking.getUser().getUsername());
            System.out.println("Ukupna cijena: " + cheapestBooking.getTotalPrice() + " EUR");
            System.out.println("Status: " + cheapestBooking.getStatus());
        }

        if (odabir == 3) {
            xmlLogService.addLogEntry("PRETRAZIVANJE_NARUDZBI", "Ispis plaćenih narudžbi");
            System.out.println("Plaćene narudžbe su:");

            AtomicBoolean found = new AtomicBoolean(false);
            records.forEach(record -> {
                System.out.println("Username: " + record.username() +
                        ", Cijena: " + record.price() + " EUR" +
                        ", BookingId: " + record.bookingId() +
                        ", Vrijeme: " + record.time());
                found.set(true);
            });

            if (!found.get()) {
                System.out.println("Nema plaćenih narudžbi!");
            }
        }
    }
}
