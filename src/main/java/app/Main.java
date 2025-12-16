package app;

import entities.*;
import entities.Record;
import repository.BookingRepository;
import repository.ItemRepository;
import repository.RecordRepository;
import repository.UserRepository;
import service.*;

import java.util.*;

public class Main {

    private static final int NUMBER_OF_ALL = 4;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // --- SERVICES ---
        XmlLogService xmlLogService = new XmlLogService();
        BackupService backupService = new BackupService(); // ako tvoj BackupService prima nešto u konstruktoru, dodaj tu

        LoginService loginService = new LoginService();

        // --- REPOSITORIES ---
        UserRepository userRepository = new UserRepository();
        ItemRepository itemRepository = new ItemRepository();
        BookingRepository bookingRepository = new BookingRepository();
        RecordRepository recordRepository = new RecordRepository();

        // --- DOMAIN SERVICES ---
        BookingService bookingService = new BookingService(
                NUMBER_OF_ALL,
                loginService,
                bookingRepository,
                recordRepository
        );

        ProductSearchService productSearchService = new ProductSearchService(xmlLogService);
        BookingSearchService bookingSearchService = new BookingSearchService(xmlLogService);
        UserSearchService userSearchService = new UserSearchService();

        SearchService searchService = new SearchService(
                productSearchService,
                bookingSearchService,
                userSearchService,
                backupService,
                xmlLogService
        );

        // --- DATA ---
        List<User> users = userRepository.loadUsers();      // <-- ako ti se zove drugačije, promijeni
        List<Item> items = itemRepository.loadAllItems();   // <-- ako ti se zove drugačije, promijeni

        List<Booking> bookings = new ArrayList<>();
        Set<Record> records = new HashSet<>();
        Map<String, List<Booking>> userBookings = new HashMap<>();
        List<Object> arhivaProizvoda = new ArrayList<>();

        // --- BACKUP (ako želiš odmah na startu) ---
        backupService.saveBackup(users, items);
        xmlLogService.addLogEntry("BACKUP_SPREMI", "Kreiran backup.bin");

        // --- BOOKING FLOW ---
        bookings = bookingService.generateBookings(sc, users, items, records, userBookings, arhivaProizvoda);

        // --- SEARCH FLOW ---
        searchService.pretrazivanje(sc, bookings, items, records, userBookings, arhivaProizvoda, users);

        sc.close();
    }
}
