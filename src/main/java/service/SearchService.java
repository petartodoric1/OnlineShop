package service;

import entities.*;
import entities.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.AnswerUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ProductSearchService productSearchService;
    private final BookingSearchService bookingSearchService;
    private final UserSearchService userSearchService;
    private final BackupService backupService;
    private final XmlLogService xmlLogService;

    public SearchService(ProductSearchService productSearchService,
                         BookingSearchService bookingSearchService,
                         UserSearchService userSearchService,
                         BackupService backupService,
                         XmlLogService xmlLogService) {
        this.productSearchService = productSearchService;
        this.bookingSearchService = bookingSearchService;
        this.userSearchService = userSearchService;
        this.backupService = backupService;
        this.xmlLogService = xmlLogService;
    }

    public void pretrazivanje(java.util.Scanner sc,
                              List<Booking> bookings,
                              List<Item> items,
                              Set<Record> records,
                              Map<String, List<Booking>> userBookings,
                              List<Object> arhivaProizvoda,
                              List<User> users) {

        boolean confirmation = AnswerUtil.DaNeOdgovor(sc, "Želite li započeti pretraživanje? (Da/Ne):");
        if (!confirmation) {
            System.out.println("Hvala i ugodan dan!");
            return;
        }

        boolean answer;
        do {
            int odabir = AnswerUtil.odabirOdgovor(sc,
                    "Pretraži:\n" +
                            "(1) Proizvode\n" +
                            "(2) Narudžbe\n" +
                            "(3) Korisnika\n" +
                            "(4) Učitaj trenutni backup i pregazi ga sa novim podatcima\n" +
                            "(5) Prikaži XML log korisničkih akcija\n" +
                            "Odabir:",
                    1, 5
            );

            switch (odabir) {
                case 1 -> {
                    xmlLogService.addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje proizvoda");
                    productSearchService.odabirProizvoda(sc, items, arhivaProizvoda);
                }
                case 2 -> {
                    xmlLogService.addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje narudžbi");
                    bookingSearchService.odabirNarudzbe(sc, bookings, records);
                }
                case 3 -> {
                    xmlLogService.addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje korisnika");
                    userSearchService.pretrazivanjeKorisnika(sc, userBookings);
                }
                case 4 -> {
                    xmlLogService.addLogEntry("IZBORNIK", "Korisnik odabrao učitavanje i 'gaženje' backupa");
                    Backup backup = backupService.loadBackup();
                    if (backup != null) {
                        backupService.pregaziBackup(backup, users, items);
                    }
                }
                case 5 -> {
                    xmlLogService.addLogEntry("IZBORNIK", "Korisnik zatražio ispis XML loga");
                    xmlLogService.printLogFromXml();
                }
            }

            answer = AnswerUtil.DaNeOdgovor(sc, "Želite li nastaviti pretraživanje? (Da/Ne):");
        } while (answer);
    }
}
