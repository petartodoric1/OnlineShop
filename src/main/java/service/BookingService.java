package service;

import entities.*;
import entities.Record;
import exceptions.InvalidInputException;
import exceptions.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.BookingRepository;
import repository.RecordRepository;
import ui.AnswerUtil;

import java.time.LocalDateTime;
import java.util.*;

public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final int NUMBER_OF_ALL;
    private final LoginService loginService;
    private final BookingRepository bookingRepository;
    private final RecordRepository recordRepository;

    public BookingService(int numberOfAll,
                          LoginService loginService,
                          BookingRepository bookingRepository,
                          RecordRepository recordRepository) {
        this.NUMBER_OF_ALL = numberOfAll;
        this.loginService = loginService;
        this.bookingRepository = bookingRepository;
        this.recordRepository = recordRepository;
    }

    public List<Booking> generateBookings(Scanner sc,
                                          List<User> users,
                                          List<Item> items,
                                          Set<Record> records,
                                          Map<String, List<Booking>> userBookings,
                                          List<Object> arhivaProizvoda) {

        log.trace("Započeto generiranje narudžbi.");
        List<Booking> bookings = new ArrayList<>();

        for (int bookingIndex = 0; bookingIndex < NUMBER_OF_ALL; bookingIndex++) {

            log.debug("Generiranje {}. narudžbe", bookingIndex);

            boolean confirmation = AnswerUtil.DaNeOdgovor(
                    sc, "Dobar dan, zelite li nešto kupiti? (Da/Ne):"
            );

            if (!confirmation) {
                System.out.println("U redu, doviđenja!");
                bookingIndex--;
                continue;
            }

            // login korisnika
            User selectedUser;
            try {
                selectedUser = loginService.login(sc, users);
            } catch (LoginFailedException e) {
                log.error("Neuspjela prijava korisnika");
                System.out.println("Greška prilikom prijave -> " + e.getMessage());
                System.out.println("Povratak na početak kupnje...");
                bookingIndex--;
                continue;
            }

            List<Item> orderedItems = new ArrayList<>();
            List<Integer> orderedQuantity = new ArrayList<>();

            boolean answer;
            do {
                // ispis proizvoda
                int ordinal = 1;
                for (Item i : items) {
                    if (i.isSold()) {
                        System.out.print("(RASPRODANO) -> ");
                    }
                    System.out.println("(" + ordinal + ")-> " +
                            i.getName() + " (" + i.getCategory() + ") - " +
                            i.getPrice() + " EUR");
                    ordinal++;
                }

                int choice = AnswerUtil.odabirOdgovor(
                        sc, "Odaberite proizvod:", 1, items.size()
                );

                Item selectedItem = items.get(choice - 1);

                if (selectedItem.isSold()) {
                    System.out.println("Ovaj proizvod je rasprodan!");
                    answer = AnswerUtil.DaNeOdgovor(
                            sc, "Želite li neki drugi proizvod? (Da/Ne):"
                    );
                    continue;
                }

                selectedItem.markAsSold();
                orderedItems.add(selectedItem);

                Integer quantity=null;
                while(true) {
                    System.out.println("Odaberite količinu:");
                    try{
                        quantity=sc.nextInt();
                        sc.nextLine();
                        if(quantity<1){ throw new InvalidInputException("Količina mora biti veća ili jednaka 1");
                        }
                        break;
                    }catch(InvalidInputException e){
                        System.out.println("Greška: Morate unijeti broj jednak ili veći od 1!");
                        sc.nextLine();
                        log.error("Unesen broj manji od 1!");

                    } catch(InputMismatchException e) {
                        System.out.println("Greška: Morate unijeti broj!");
                        sc.nextLine();
                        log.error("Unesen string umjesto broj!");
                    }

                }
                orderedQuantity.add(quantity);

                answer = AnswerUtil.DaNeOdgovor(
                        sc, "Želite li još neki proizvod? (Da/Ne):"
                );

            } while (answer);

            if (orderedItems.isEmpty()) {
                System.out.println("Doviđenja i dođite nam opet!");
                bookingIndex--;
                continue;
            }

            Booking booking = new Booking(
                    selectedUser, orderedItems, orderedQuantity, bookingIndex
            );
            bookings.add(booking);

            bookingRepository.save(bookings);

            //povezivanje korisnika sa njegovom narudžbom
            String username=selectedUser.getUsername();
            //ako je ovo korisniku prva narudzba stvori novi listu i neka ključ bude njegov username
            if(!userBookings.containsKey(username)) {
                userBookings.put(username,new ArrayList<>());
            }
            userBookings.get(username).add(bookings.get(bookingIndex));

            System.out.println(
                    "Ukupna cijena vaše narudžbe je: " +
                            booking.getTotalPrice() + " EUR\n"
            );

            int odgovor = AnswerUtil.odabirOdgovor(sc,
                    "Želite li platiti ili rezervirati narudžbu:\n" +
                            "(1)Platiti\n" +
                            "(2)Rezervirati\n" +
                            "Vaš odabir:", 1, 2
            );

            if (odgovor == 1) {
                System.out.println("Vaša narudžba je plaćena i poslana na vašu adresu!");
                booking.setStatus(BookingStatus.PLAĆENO);

                arhivirajProizvode(orderedItems, arhivaProizvoda);

                records.add(new Record(
                        selectedUser.getUsername(),
                        booking.getTotalPrice(),
                        booking.getBookingId(),
                        LocalDateTime.now()
                ));
                recordRepository.save(records);
            }
            if (odgovor == 2) {
                System.out.println("Vaša narudžba vas čeka u našoj poslovnici!");
                booking.setStatus(BookingStatus.REZERVIRANO);
            }
        }
        log.trace("Završeno generiranje narudžbi.");
        return bookings;
    }

    public void arhivirajProizvode(List<? extends Item> naruceniProizvodi, List<? super Item> arhiva) {
        naruceniProizvodi.forEach(arhiva::add);
    }

}
