package service;

import entities.Item;
import entities.Sold;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.AnswerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchService.class);

    private final XmlLogService xmlLogService;

    public ProductSearchService(XmlLogService xmlLogService) {
        this.xmlLogService = xmlLogService;
    }

    public void odabirProizvoda(java.util.Scanner sc, List<Item> items, List<Object> arhivaProizvoda) {

        items.sort(Comparator.comparing(Item::getPrice));
        Item cheapestItem = items.getFirst();
        Item expensiveItem = items.getLast();

        int odabir = AnswerUtil.odabirOdgovor(sc,
                "Pretraživanje proizvoda:\n" +
                        "(1) Najskuplji proizvod\n" +
                        "(2) Najjeftiniji proizvod\n" +
                        "(3) Sortiraj proizvode\n" +
                        "(4) Pretraživanje po kategoriji\n" +
                        "(5) Ispis prodanih proizvoda\n" +
                        "(6) Ispis dostupnih proizvoda\n" +
                        "(7) Arhiva proizvoda\n" +
                        "Vaš odabir:",
                1, 7
        );

        switch (odabir) {
            case 1 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Ispis najskupljeg proizvoda");
                System.out.println("Najskuplji proizvod je: " + expensiveItem.getName() + " " + expensiveItem.getCategory());
                System.out.println("Ukupna cijena je: " + expensiveItem.getPrice() + " EUR");
                System.out.println("Dostupnost proizvoda: " + (expensiveItem.isSold() ? "Rasprodano" : "Dostupno"));
            }
            case 2 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Ispis najjeftinijeg proizvoda");
                System.out.println("Najjeftiniji proizvod je: " + cheapestItem.getName() + " " + cheapestItem.getCategory());
                System.out.println("Ukupna cijena je: " + cheapestItem.getPrice() + " EUR");
                System.out.println("Dostupnost proizvoda: " + (cheapestItem.isSold() ? "Rasprodano" : "Dostupno"));
            }
            case 3 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Sortiranje proizvoda");
                sortirajProizvode(sc, items);
            }
            case 4 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Pretraživanje po kategoriji");
                pretrazivanjePoKategoriji(sc, items);
            }
            case 5 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Ispis prodanih proizvoda");
                ispisProdanihProizvoda(items);
            }
            case 6 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Ispis dostupnih proizvoda");
                ispisDostupnihProizvoda(items);
            }
            case 7 -> {
                xmlLogService.addLogEntry("PRETRAZIVANJE_PROIZVODA", "Ispis arhive proizvoda");
                System.out.println("Arhivirani proizvodi (svi proizvodi ikad kupljeni):");
                arhivaProizvoda.forEach(p -> {
                    Item i = (Item) p;
                    System.out.println(i.getName() + " | " + i.getCategory() + " | " + i.getPrice() + " EUR");
                });
            }
        }
    }

    private void sortirajProizvode(java.util.Scanner sc, List<Item> items) {
        int choice = AnswerUtil.odabirOdgovor(sc,
                "Sortiraj po:\n(1) Cijeni -> UZLAZNO\n(2) Cijeni -> SILAZNO",
                1, 2
        );

        if (choice == 1) items.sort(Comparator.comparing(Item::getPrice));
        if (choice == 2) items.sort(Comparator.comparing(Item::getPrice).reversed());

        System.out.println("Sortirani proizvodi:");
        items.forEach(i -> System.out.println(i.getName() + " | " + i.getCategory() + " | " + i.getPrice() + " EUR"));
    }

    private void pretrazivanjePoKategoriji(java.util.Scanner sc, List<Item> items) {
        Map<String, List<Item>> poKategoriji = items.stream()
                .collect(Collectors.groupingBy(Item::getCategory));

        int odabir = AnswerUtil.odabirOdgovor(sc,
                "Odaberite kategoriju:\n(1) Majice\n(2) Hlače\n(3) Cipele\nVaš odabir:",
                1, 3
        );

        String kategorija = switch (odabir) {
            case 1 -> "Majica";
            case 2 -> "Hlače";
            case 3 -> "Cipele";
            default -> null;
        };

        if (kategorija != null && poKategoriji.containsKey(kategorija)) {
            System.out.println("Proizvodi u kategoriji: " + kategorija + " su:");
            poKategoriji.get(kategorija).forEach(i -> System.out.println(i.getName() + " | " + i.getPrice() + " EUR"));
        } else {
            System.out.println("Nema proizvoda u odabranoj kategoriji!");
        }
    }

    private <T extends Item & Sold> void ispisProdanihProizvoda(List<T> items) {
        items.stream().filter(T::isSold)
                .forEach(i -> System.out.println(i.getName() + " | " + i.getPrice() + " EUR | " + i.getCategory()));
    }

    private void ispisDostupnihProizvoda(List<? extends Item> items) {
        items.stream().filter(i -> !i.isSold())
                .forEach(i -> System.out.println(i.getName() + " | " + i.getPrice() + " EUR | " + i.getCategory()));
    }
}
