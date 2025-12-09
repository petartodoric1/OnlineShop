package app;

import entities.*;
import entities.Record;
import exceptions.*;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Služi za pokretanje programa Online Shop
 *
 * <p>Aplikacija omogućava:</p>
 * <ul>
 *   <li>Kreiranje korisnika i artikala</li>
 *   <li>Izradu narudžbi i evidenciju kupnji</li>
 *   <li>Pretraživanje proizvoda i narudžbi</li>
 * </ul>
 */

public class Main {

    private static Logger log= LoggerFactory.getLogger(Main.class);

    private static final Integer NUMBER_OF_ALL = 4;

    private static final String USERS_FILE  = "datoteke/users.json";
    private static final String MAJICE_FILE = "datoteke/majice.json";
    private static final String HLACE_FILE  = "datoteke/hlace.json";
    private static final String CIPELE_FILE = "datoteke/cipele.json";
    private static final String BOOKINGS_FILE = "datoteke/bookings.json";
    private static final String RECORDS_FILE  = "datoteke/records.json";
    private static final String BACKUP_FILE = "datoteke/backup.bin";
    private static final String XML_LOG_FILE = "datoteke/log.xml";
    private static final List<LogEntry> logEntries = new ArrayList<>();


    /**
     * Pokreće generiranje korisnika, artikala i narudžbi te omogućuje pretraživanje.
     */

    static void main() {

        log.info("Pokretanje aplikacije ...");
        Scanner sc= new Scanner(System.in);

        List<User> users;
        List<Item> items;
        List<Booking> bookings;
        Set<Record> records=new HashSet<>();
        Map<String,List<Booking>> userBookings=new HashMap<>();
        List<Object> arhivaProizvoda = new ArrayList<>();


        users=loadUsers();
        items=generateItems(sc);

        saveBackup(users,items);
        addLogEntry("BACKUP_SPREMI", "Kreiran backup.bin");

        bookings=generateBookings(sc,users,items,records,userBookings,arhivaProizvoda);
        pretrazivanje(sc,bookings,items,records,userBookings,arhivaProizvoda,users);

    }

    /**
     * Učitava korisnike iz datoteke users.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link User} objekata
     */
    private static List<User> loadUsers(){
        log.trace("Započeto učitavanje korisnika.");

        try{

            Jsonb jsonb= JsonbBuilder.create();

            String json=Files.readString(Paths.get(USERS_FILE));

            List<User> users= jsonb.fromJson(
                    json,
                    new ArrayList<User>(){}.getClass().getGenericSuperclass());

            log.trace("Završeno učitavanje korisnika.");


            System.out.println("Učitano korisnika: " + users.size());
            users.forEach(u ->
                    System.out.println("Username: "+u.getUsername()+" | Password: "+u.getPassword())
            );

            return users;


        }catch(IOException e){
            log.error("Greška pri učitavanju iz datoteke users.json!",e);
            return new ArrayList<>();

        }



    }

    /**
     * Učitava majice iz datoteke majice.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Majica} objekata
     */
    private static List<Majica> loadMajice(){

        try{
            Jsonb jsonb= JsonbBuilder.create();

            String json=Files.readString(Paths.get(MAJICE_FILE));

            return jsonb.fromJson(
                    json,
                    new ArrayList<Majica>(){}.getClass().getGenericSuperclass());


        }catch(IOException e){
            log.error("Greška pri učitavanju majici iz datoteke majice.json!",e);
            return new ArrayList<>();

        }
    }


    /**
     * Učitava Hlače iz datoteke hlace.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Hlace} objekata
     */
    private static List<Hlace> loadHlace(){

        try{
            Jsonb jsonb= JsonbBuilder.create();

            String json=Files.readString(Paths.get(HLACE_FILE));

            return jsonb.fromJson(
                    json,
                    new ArrayList<Hlace>(){}.getClass().getGenericSuperclass());


        }catch(IOException e){
            log.error("Greška pri učitavanju hlača iz datoteke hlace.json!",e);
            return new ArrayList<>();

        }
    }

    /**
     * Učitava cipele iz datoteke cipele.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Cipele} objekata
     */
    private static List<Cipele> loadCipele(){

        try{
            Jsonb jsonb= JsonbBuilder.create();

            String json=Files.readString(Paths.get(CIPELE_FILE));

            return jsonb.fromJson(
                    json,
                    new ArrayList<Cipele>(){}.getClass().getGenericSuperclass());


        }catch(IOException e){
            log.error("Greška pri učitavanju majici iz datoteke majice.json!",e);
            return new ArrayList<>();

        }
    }

    /**
     * Upisuje učitane proizvode (majice, hlače i cipele) u jednu listu.
     * @return polje {@link Item} objekata
     */
    private static List<Item> generateItems(Scanner sc)  {
        log.trace("Započeto generiranje proizvoda.");
        List<Item> items=new ArrayList<>();

        items.addAll(loadMajice());
        items.addAll(loadHlace());
        items.addAll(loadCipele());

        System.out.println("Učitano proizvoda: " + items.size());


        log.trace("Završeno generiranje proizvoda.");
        return items;
    }

    /**
     * Generira narudžbe korisnika na temelju dostupnih artikala.
     * Omogućuje prijavu korisnika, odabir proizvoda, količina i odabir plaćanja ili rezervacije.
     *
     * @param sc Scanner objekt za unos
     * @param users lista postojećih korisnika
     * @param items lista dostupnih artikala
     * @param records set zbirka zapisa o plaćenim narudžbama
     * @param arhivaProizvoda log svih prodanih proizvoda
     * @throws InvalidDaNeException ako korisnik na Da/Ne pitanje unese nešto treće
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđena
     * @throws LoginFailedException ako korisnik tri puta unese pogrešnu lozinku
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     * @return polje {@link Booking} objekata
     */

    private static List<Booking> generateBookings(Scanner sc,
                                                  List<User> users,
                                                  List<Item> items,
                                                  Set<Record> records,
                                                  Map<String,List<Booking>> userBookings,
                                                  List<Object> arhivaProizvoda)  {

        log.trace("Započeto generiranje narudžbi.");
        List<Booking> bookings= new ArrayList<>();

        for(Integer bookingIndex=0;bookingIndex< NUMBER_OF_ALL;bookingIndex++) {

            log.debug("Generiranje {}. narudžbe", bookingIndex);

            String confirmation=null;
            while(true) {

                System.out.println("Dobar dan, zelite li nešto kupiti? (Da/Ne):");
                confirmation = sc.nextLine();

                try{
                    if(!confirmation.equalsIgnoreCase("Da") && !confirmation.equalsIgnoreCase("Ne")) {
                        throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                    }
                    break;
                }catch(InvalidDaNeException e) {
                    System.out.println("Greška pri unosu -> "+e.getMessage());
                    log.error("Pogrešan unos 'Da/Ne' od korisnika: {}", confirmation);
                }
            }


            if (confirmation.equalsIgnoreCase("Da")) {

                String answer=null;

                // login korisnika
                User selectedUser=null;
                try {
                    selectedUser = login(sc, users);
                }catch(LoginFailedException e) {
                    log.error("Neuspjela prijava korisnika");
                    System.out.println("Greška prilikom prijave -> "+e.getMessage());
                    System.out.println("Povratak na početak kupnje...");
                    bookingIndex--;
                    continue;
                }

                List<Item> orderedItems = new ArrayList<>();
                List<Integer> orderedQuantity = new ArrayList<>();

                do {
                    //ispis proizvoda korisniku
                    Integer ordinal = 1;
                    for (Item i : items) {
                        if (i.isSold()) {
                            System.out.print("(RASPRODANO) -> ");
                        }
                        System.out.println("(" + ordinal + ")" + "-> " + i.getName() + " (" + i.getCategory() + ")" + " - " + i.getPrice() + " EUR");
                        ordinal++;
                    }


                    //korisnik bira proizvod
                    Integer choice=null;
                    while(true) {

                        System.out.println("Odaberite proizvod:");


                        //provjera jel odabrao postojeci proizvod
                        try {
                            choice = sc.nextInt();
                            if (choice > items.size() || choice < 1) {
                                throw new InvalidOdabirException("Odabrali ste nepostojeći proizvod!");
                            }
                            break;
                        } catch (InvalidOdabirException e) {
                            System.out.println("Greška pri unosu -> " + e.getMessage());
                            log.error("Neispravan odabir proizvoda!");

                        } catch (InputMismatchException e) {
                            System.out.println("Greška: Morate unijeti broj!");
                            sc.nextLine();
                            log.error("Unesen string umjesto brojčane vrijednosti");
                        }
                    }

                    Item selectedItem= items.get(choice - 1);

                        //provjera je li proizvod rasprodan
                        if (selectedItem.isSold()) {
                            System.out.println("Ovaj proizvod je rasprodan!");
                            sc.nextLine();
                            while (true) {
                                System.out.println("Želite li neki drugi proizvod? (Da/Ne):");
                                answer = sc.nextLine();

                                try {
                                    if (!answer.equalsIgnoreCase("Da") && !answer.equalsIgnoreCase("Ne")) {
                                        throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                                    }
                                    break;
                                } catch (InvalidDaNeException e) {
                                    System.out.println("Greška pri unosu -> " + e.getMessage());
                                    log.warn("Pogrešan unos 'Da/Ne' od korisnika: {}", answer);
                                }
                            }
                            continue;

                        }

                        //označavanje proizvoda kao prodan i dodavanje na popis naručenih stvari
                        selectedItem.markAsSold();
                        orderedItems.add(selectedItem);


                    Integer quantity=null;
                    while(true) {
                        System.out.println("Odaberite količinu:");
                        try{
                            quantity=sc.nextInt();
                            break;
                        }catch(InputMismatchException e) {
                            System.out.println("Greška: Morate unijeti broj!");
                            sc.nextLine();
                            log.error("Unesen string umjesto broj!");
                        }

                    }
                    orderedQuantity.add(quantity);

                    sc.nextLine();

                    while(true) {
                        System.out.println("Želite li još neki proizvod? (Da/Ne):");
                        answer = sc.nextLine();

                        try{
                            if(!answer.equalsIgnoreCase("Da")&&!answer.equalsIgnoreCase("Ne")) {
                                throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                            }
                            break;
                        }catch(InvalidDaNeException e) {
                            System.out.println("Greška pri unosu -> "+e.getMessage());
                            log.error("Pogrešan unos 'Da/Ne' od korisnika: {}", answer);
                        }
                    }

                } while ("Da".equalsIgnoreCase(answer));

                //Provjerava jel korisnik nešto naručio, odnosno, jel odustao od kupnje ako je prva stvar bila rasprodana
                if(!orderedItems.isEmpty()){
                bookings.add( new Booking(selectedUser, orderedItems, orderedQuantity,bookingIndex));

                //spremam booking u json datoteku
                saveBookings(bookings);

                //povezivanje korisnika sa njegovom narudžbom
                String username=selectedUser.getUsername();
                //ako je ovo korisniku prva narudzba stvori novi listu i neka ključ bude njegov username
                    if(!userBookings.containsKey(username)) {
                        userBookings.put(username,new ArrayList<>());
                    }
                    userBookings.get(username).add(bookings.get(bookingIndex));

                System.out.println("Ukupna cijena vaše narudžbe je: "+bookings.get(bookingIndex).getTotalPrice()+" EUR\n");
                }
                //ako je odustao od kupnje nakon odabira rasprodanog proizvoda ga pozdravljamo i smanjujemo bookingIndex jer korisnik nije ništa kupio
                else{
                    System.out.println("Doviđenja i dođite nam opet!");
                    bookingIndex--;
                    continue;
                }

                //korisnik bira želi li platiti ili rezervirati narudžbu
                Integer odgovor = null;
                while (true) {
                    System.out.println("Želite li platiti ili rezervirati narudžbu:");
                    System.out.println("(1)Platiti");
                    System.out.println("(2)Rezervirati");
                    System.out.println("Vaš odabir:");

                    try {
                        odgovor = sc.nextInt();
                        sc.nextLine();

                        if (odgovor != 1 && odgovor != 2) {
                            throw new InvalidOdabirException("Neispravan unos! Unesite 1 ili 2.");
                        }

                        break;
                    } catch (InvalidOdabirException e) {
                        System.out.println("Greška pri unosu -> " + e.getMessage());
                        log.error("Neispravan unos za odabir plaćanja ili rezerviranja");

                    } catch (InputMismatchException e) {
                        System.out.println("Greška: Morate unijeti broj!");
                        sc.nextLine();
                        log.error("Unesen string umjesto brojčane vrijednosti");
                    }
                }




                //ako je plaćena, stavljamo status Plaćeno, dodajemo te proizvode u arhivu i dodajemo tu narudžbu u zapis plaćenih narudžbi
                if (odgovor.equals(1)) {
                    System.out.println("Vaša narudžba je plaćena i poslana na vašu adresu!");
                    bookings.get(bookingIndex).setStatus(BookingStatus.PLAĆENO);

                    arhivirajProizvode(orderedItems, arhivaProizvoda);

                    records.add(new Record(selectedUser.getUsername(),
                            bookings.get(bookingIndex).getTotalPrice(),
                            bookings.get(bookingIndex).getBookingId(),
                                                 LocalDateTime.now()));

                    saveRecords(records);

                }
                if(odgovor.equals(2)) {
                    System.out.println("Vaša narudžba vas čeka u našoj poslovnici!");
                    bookings.get(bookingIndex).setStatus(BookingStatus.REZERVIRANO);
                }

            }
            else {
                System.out.println("U redu, doviđenja!");
                bookingIndex--;
            }
        }
        log.trace("Završeno generiranje narudžbi.");
        return bookings;
    }

    /**
     * Zapisuje narudžbe u bookings.json datoteku.
     * @param bookings lista narudžbi
     */
    private static void saveBookings(List<Booking> bookings){

        try {
            //da se ljepse zapisuju stvari
            JsonbConfig config = new JsonbConfig()
                    .withFormatting(true);

            Jsonb jsonb = JsonbBuilder.create(config);
            String json = jsonb.toJson(bookings);
            Files.writeString(Paths.get(BOOKINGS_FILE), json);
        } catch (IOException e) {
            log.error("Greška kod pisanja u datoteku bookings.json!",e);
            System.out.println("Greška kod pisanja u datoteku bookings.json -> "+e.getMessage());
        }

    }
    /**
     * Zapisuje plaćene narudžbe u records.json datoteku.
     * @param records lista plaćenih narudžbi
     */
    private static void saveRecords(Set<Record> records){

        try {

            JsonbConfig config = new JsonbConfig()
                    .withFormatting(true);

            Jsonb jsonb = JsonbBuilder.create(config);
            String json = jsonb.toJson(records);  // ✔ radi normalno
            Files.writeString(Paths.get(RECORDS_FILE), json);
        } catch (IOException e) {
            log.error("Greška kod pisanja u datoteku records.json!", e);
            System.out.println("Greška kod pisanja u datoteku record.json -> "+e.getMessage());
        }
    }

    /**
     * Autentificira korisnika prema unesenom korisničkom imenu i lozinci.
     * <p>Dozvoljena su tri pokušaja unosa lozinke prije nego što se baci iznimka i postupak autentifikacije ponovo pokrene.</p>
     *
     * @param sc Scanner objekt za unos
     * @param users lista korisnika za provjeru prijave
     * @return prijavljeni {@link User}
     * @throws LoginFailedException ako korisnik tri puta unese pogrešnu lozinku
     */

    private static User login(Scanner sc, List<User> users) throws LoginFailedException {

        System.out.println("Unesite vaš username:");

        String username = sc.nextLine();

        Optional<User> selectedUser = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (selectedUser.isEmpty()) {
            System.out.println("Krivi username! Probajte ponovo.");
            return login(sc,users);
        }

        User user = selectedUser.get();

        int attempts=0;

        while(true) {
            System.out.println("Unesite password:");
            String password = sc.nextLine();

            if (password.equals(user.getPassword())) {
                log.info("Korisnik {} uspješno prijavljen.", user.getUsername());
                System.out.println("Uspiješna prijava! Nastavite s kupnjom!");
                return user;
            }
            else {
                attempts++;

                if (attempts>=3) {
                    throw new LoginFailedException("Previše neuspjelih pokušaja unosa passworda!");
                }
                System.out.println("Krivi password! Pokušajte ponovo:");
                log.warn("Neuspješna prijava korisnika: {}", user.getUsername());

            }
        }

    }

    /**
     * Omogućuje pretraživanje proizvoda, narudžbi i korisnika.
     * <p>Korisnik može odabrati između pregleda proizvoda, narudžbi i pretraživanja korisnika.</p>
     *
     * @param sc Scanner objekt za unos
     * @param bookings lista svih narudžbi
     * @param items lista svih artikala
     * @param records set zbirka svih zapisa o plaćenim narudžbama
     * @param arhivaProizvoda log svih prodanih proizvoda
     * @throws InvalidDaNeException ako korisnik na Da/Ne pitanje unese nešto treće
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void pretrazivanje(Scanner sc,
                                      List<Booking> bookings,
                                      List<Item> items,
                                      Set<Record> records,
                                      Map<String,List<Booking>> userBookings,
                                      List<Object> arhivaProizvoda,
                                      List<User> users) {

        String confirmation=null;
        while(true) {
            System.out.println("Želite li započeti pretraživanje? (Da/Ne):");
            confirmation = sc.nextLine();

            try{
                if(!confirmation.equalsIgnoreCase("Da")&&!confirmation.equalsIgnoreCase("Ne")) {
                    throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                }
                break;
            }catch(InvalidDaNeException e) {
                System.out.println("Greška pri unosu -> "+e.getMessage());
                log.error("Pogrešan unos 'Da/Ne' od korisnika: {}", confirmation);
            }
        }

        if (confirmation.equalsIgnoreCase("Da")) {
            String answer=null;
            do {
                Integer odabir = null;
                while (true) {
                    System.out.println("Pretraži:");
                    System.out.println("(1) Proizvode");
                    System.out.println("(2) Narudžbe");
                    System.out.println("(3) Korisnika");
                    System.out.println("(4) Učitaj trenutni backup i pregazi ga sa novim podatcima");
                    System.out.println("(5) Prikaži XML log korisničkih akcija");
                    System.out.println("Odabir:");

                    try {
                        odabir = sc.nextInt();
                        sc.nextLine();

                        if (odabir != 1 && odabir != 2 && odabir != 3 && odabir !=4 && odabir !=5) {
                            throw new InvalidOdabirException("Neispravan unos! Unesite 1, 2, 3, 4 ili 5.");
                        }

                        break;
                    } catch (InvalidOdabirException e) {
                        System.out.println("Greška pri unosu -> " + e.getMessage());
                        log.error("Pogrešan odabir kod pretraživanja proizvoda ili narudžbi");

                    } catch (InputMismatchException e) {
                        System.out.println("Greška: Morate unijeti broj!");
                        log.error("Unesen string umjesto broja kod odabira pretraživanja proizvoda ili narudžbi ");
                        sc.nextLine();
                    }
                }


                if (odabir.equals(1)) {
                    addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje proizvoda");
                    odabirProizvoda(sc, items,arhivaProizvoda);
                }

                if (odabir.equals(2)) {
                    addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje narudžbi");
                    odabirNarudzbe(sc, bookings, records);
                }
                if(odabir.equals(3)) {
                    addLogEntry("IZBORNIK", "Korisnik odabrao pretraživanje kosrisnika");
                    pretrazivanjeKorisnika(sc,userBookings);
                }
                if(odabir.equals(4)){
                    addLogEntry("IZBORNIK", "Korisnik odabrao učitavanje i 'gaženje' backupa ");
                    Backup backup=loadBackup();
                    if(backup!=null){
                        pregaziBackup(backup,users,items);
                    }
                }
                if(odabir.equals(5)){
                    addLogEntry("IZBORNIK", "Korisnik zatražio ispis XML loga");
                    printLogFromXml();
                }


                while (true) {
                    System.out.println("Želite li nastaviti pretraživanje? (Da/Ne):");
                    answer = sc.nextLine();

                    try {
                        if (!answer.equalsIgnoreCase("Da") && !answer.equalsIgnoreCase("Ne")) {
                            throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                        }
                        break;
                    } catch (InvalidDaNeException e) {
                        System.out.println("Greška pri unosu -> " + e.getMessage());
                        log.error("Pogrešan unos 'Da/Ne' od korisnika: {}", answer);
                    }
                }

            }while(answer.equalsIgnoreCase("Da"));
        }
        else
            System.out.println("Hvala i ugodan dan!");
    }

    /**
     * Prikazuje najskuplji, najjeftiniji proizvod, nudi opciju sortiranja proizvoda po cijeni, pretraživanje proizvoda po
     * kategoriji, ispis prodanih i dostupnih proizvoda te ispis arhive prodanih proizvoda.
     * <p>Uključuje i informaciju o dostupnosti proizvoda.</p>
     *
     * @param sc Scanner objekt za unos
     * @param items lista artikala za pretragu
     * @param arhivaProizvoda log svih prodanih proizvoda
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void odabirProizvoda(Scanner sc,List<Item> items, List<Object> arhivaProizvoda) {


        items.sort(Comparator.comparing(Item::getPrice));
        Item cheapestItem=items.getFirst();
        Item expensiveItem=items.getLast();


        Integer odabir = null;
        while (true) {
            System.out.println("Pretraživanje proizvoda:");
            System.out.println("(1) Najskuplji proizvod");
            System.out.println("(2) Najjeftiniji proizvod");
            System.out.println("(3) Sortiraj proizvode");
            System.out.println("(4) Pretraživanje po kategoriji");
            System.out.println("(5) Ispis prodanih proizvoda");
            System.out.println("(6) Ispis dostupnih proizvoda");
            System.out.println("(7) Arhiva proizvoda");
            System.out.println("Vaš odabir: ");

            try {
                odabir = sc.nextInt();
                sc.nextLine();

                if (odabir<1 || odabir>7) {
                    throw new InvalidOdabirException("Neispravan odabir! Birajte opciju od 1 do 7");
                }

                break;
            } catch (InvalidOdabirException e) {
                System.out.println("Greška pri unosu -> " + e.getMessage());
                log.error("Neispravan odabir", e);

            } catch (InputMismatchException e) {
                System.out.println("Greška: Morate unijeti broj!");
                log.error("Neispravan odabir", e);
                sc.nextLine();
            }
        }

        switch (odabir) {
            case 1 -> {
                // Najskuplji proizvod
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao ispis najskupljeg proizvoda");
                System.out.println("Najskuplji proizvod je: " + expensiveItem.getName() + " " + expensiveItem.getCategory());
                System.out.println("Ukupna cijena je: " + expensiveItem.getPrice() + " EUR");
                System.out.print("Dostupnost proizvoda: ");
                if (expensiveItem.isSold()) {
                    System.out.println("Rasprodano");
                } else {
                    System.out.println("Dostupno");
                }
            }

            case 2 -> {
                // Najjeftiniji proizvod
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao ispis najjeftinijeg proizvoda");
                System.out.println("Najjeftiniji proizvod je: " + cheapestItem.getName() + " " + cheapestItem.getCategory());
                System.out.println("Ukupna cijena je: " + cheapestItem.getPrice() + " EUR");
                System.out.print("Dostupnost proizvoda: ");
                if (cheapestItem.isSold()) {
                    System.out.println("Rasprodano");
                } else {
                    System.out.println("Dostupno");
                }
            }

            case 3 -> {
                // Sortiranje proizvoda
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao sortiranje proizvoda");
                Integer choice = null;
                while (true) {

                    System.out.println("Sortiraj po:");
                    System.out.println("(1) Cijeni -> UZLAZNO");
                    System.out.println("(2) Cijeni -> SILAZNO");

                    try {
                        choice = sc.nextInt();
                        sc.nextLine();

                        if (choice != 1 && choice != 2) {
                            throw new InvalidOdabirException("Neispravan unos! Unesite 1 ili 2");
                        }
                        break;

                    } catch (InvalidOdabirException e) {
                        System.out.println("Greška pri unosu -> " + e.getMessage());
                        log.error("Neispravan odabir", e);

                    } catch (InputMismatchException e) {
                        System.out.println("Greška: Morate unijeti broj!");
                        log.error("Neispravan odabir", e);
                        sc.nextLine();
                    }
                }

                if (choice == 2) {
                    items.sort(Comparator.comparing(Item::getPrice).reversed());
                }

                System.out.println("Sortirani proizvodi:");
                items.forEach(i ->
                        System.out.println(i.getName() + " | " + i.getCategory() + " | " + i.getPrice() + " EUR")
                );
            }

            case 4 -> {
                // Pretraživanje po kategoriji
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao pretraživanje proizvoda po kategoriji");
                pretrazivanjePoKategoriji(sc, items);
            }

            case 5 -> {
                // Ispis svih PRODANIH proizvoda
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao ispis prodanih proizvoda");
                ispisProdanihProizvoda(items);
            }

            case 6 -> {
                // Ispis svih DOSTUPNIH proizvoda
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao ispis dostupnih proizvoda");
                ispisDostupnihProizvoda(items);
            }

            case 7 ->{
                // Ispis arhive proizvoda
                addLogEntry("PRETRAZIVANJE_PROIZVODA", "Korisnik odabrao ispis arhiviranih (svih prodanih ikad) proizvoda");
                System.out.println("Arhivirani proizvodi (svi proizvodi ikad kupljeni):");
                arhivaProizvoda.forEach(p->{
                    Item i= (Item) p;
                    System.out.println(i.getName() + " | " + i.getCategory() + " | " + i.getPrice() + " EUR");
                });
            }

            default -> System.out.println("Neispravan odabir!");
        }


    }

    /**
     * Omogućuje pregled narudžbi prema cijeni (najskuplja, najjeftinija) ili pregled svih plaćenih narudžbi.
     *
     * @param sc Scanner objekt za unos
     * @param bookings lista svih narudžbi
     * @param records set zbirka svih zapisa o plaćenim narudžbama
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void odabirNarudzbe(Scanner sc,List<Booking> bookings,Set<Record> records) {

        List<Booking> sortiraneNarudzbe= bookings.stream()
                .sorted(Comparator.comparing(Booking::getTotalPrice))
                .toList();

        Booking cheapestBooking=sortiraneNarudzbe.getFirst();
        Booking expensiveBooking=sortiraneNarudzbe.getLast();


        Integer odabir = null;
        while (true) {
            System.out.println("Koju narudžbu želite odabrati:");
            System.out.println("(1) Najskuplja narudžba");
            System.out.println("(2) Najjeftinija narudžba");
            System.out.println("(3) Plaćene narudžbe");
            System.out.println("Vaš odabir: ");

            try {
                odabir = sc.nextInt();
                sc.nextLine();

                if (odabir != 1 && odabir != 2 && odabir != 3) {
                    throw new InvalidOdabirException("Neispravan unos! Unesite 1,2 ili 3.");
                }

                break;
            } catch (InvalidOdabirException e) {
                System.out.println("Greška pri unosu -> " + e.getMessage());
                log.error("Neispravan odabir", e);

            } catch (InputMismatchException e) {
                System.out.println("Greška: Morate unijeti broj!");
                log.error("Neispravan odabir", e);
                sc.nextLine();
            }
        }

        if(odabir.equals(1)){
            addLogEntry("PRETRAZIVANJE_NARUDZBI", "Korisnik odabrao ispis najskuplje narudžbe");
            System.out.println("Najskuplja narudžba je narudžba sa indexom: "+expensiveBooking.getBookingId());
            System.out.println("Naručitelj: "+expensiveBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+expensiveBooking.getTotalPrice()+" EUR");
            System.out.print("Status narudžbe: ");
            System.out.println(expensiveBooking.getStatus());

        }

        if(odabir.equals(2)){
            addLogEntry("PRETRAZIVANJE_NARUDZBI", "Korisnik odabrao ispis najjeftinije narudžbe");
            System.out.println("Najjeftinija narudžba je narudžba sa indexom: "+cheapestBooking.getBookingId());
            System.out.println("Naručitelj: "+cheapestBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+cheapestBooking.getTotalPrice()+" EUR");
            System.out.print("Status narudžbe: ");
            System.out.println(cheapestBooking.getStatus());

        }

        if (odabir.equals(3)){
            addLogEntry("PRETRAZIVANJE_NARUDZBI", "Korisnik odabrao ispis svih plaćenih narudžbi");
            System.out.println("Plaćene narudžbe su:");

            //koristim AtomicBoolean kao wrapper jer se u lambdi nemoze mijenjat vrijednost
            AtomicBoolean found= new AtomicBoolean(false);
            records.forEach(record->{
                System.out.println("Username: " + record.username() +
                    ", Cijena narudžbe: " + record.price() + " EUR" +
                    ", BookingId: " + record.bookingId() +
                    ", Vrijeme: " + record.time());
                found.set(true);});

            if(!found.get()){
                System.out.println("Nema plaćenih narudžbi!");
            }

        }
    }


    /**
     * Omogućuje pretraživanje korisnika i ispis svih njegovih narudžbi
     *
     * @param sc Scanner objekt za unos
     * @param userBookings mapa koja povezuje korisnikov username sa njegovim narudžbama
     */
    private static void pretrazivanjeKorisnika(Scanner sc, Map<String,List<Booking>> userBookings) {
        addLogEntry("PRETRAZIVANJE_KORISNIKA", "Korisnik odabrao pretraživanje korisnika");
        System.out.println("Unesite korisnikov username:");
        String username = sc.nextLine();

        List<Booking> korisnikoveNarudzbe = userBookings.get(username);

        if(korisnikoveNarudzbe == null || korisnikoveNarudzbe.isEmpty()){
            System.out.println("Korisnik "+username+" nema narudžbi!");
            return;
        }

        System.out.println("Narudžbe korisnika "+username+" :");

        korisnikoveNarudzbe.forEach(b-> System.out.println(
                "Id narudžbe: " + b.getBookingId() +
                        " | Ukupna cijena: " + b.getTotalPrice() + " EUR " +
                        " | Status: " + (b.getStatus())));
    }

    /**
     * Grupira proizvode po kategoriji i omogućava korisniku prikazivanje pojedinačne kategorije proizvoda
     *
     * @param sc Scanner objekt za unos
     * @param items lista svih proizvoda
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void pretrazivanjePoKategoriji(Scanner sc,List<Item> items){

        Map<String, List<Item>> poKategoriji = items.stream()
                .collect(Collectors.groupingBy(Item::getCategory));


        Integer odabir;
        while (true) {
            System.out.println("Odaberite kategoriju:");
            System.out.println("(1) Majice");
            System.out.println("(2) Hlače");
            System.out.println("(3) Cipele");
            System.out.println("Vaš odabir: ");

            try {
                odabir = sc.nextInt();
                sc.nextLine();

                if (odabir != 1 && odabir != 2 && odabir != 3) {
                    throw new InvalidOdabirException("Neispravan unos! Unesite 1,2 ili 3.");
                }

                break;
            } catch (InvalidOdabirException e) {
                System.out.println("Greška pri unosu -> " + e.getMessage());
                log.error("Neispravan odabir", e);

            } catch (InputMismatchException e) {
                System.out.println("Greška: Morate unijeti broj!");
                log.error("Neispravan odabir", e);
                sc.nextLine();
            }
        }

        String kategorija = switch (odabir) {
            case 1 -> "Majica";
            case 2 -> "Hlače";
            case 3 -> "Cipele";
            default -> null;
        };

        if (kategorija != null && poKategoriji.containsKey(kategorija)) {
            System.out.println("Proizvodi u kategoriji: " + kategorija+ " su:");
            poKategoriji.get(kategorija).forEach(i ->
                    System.out.println(i.getName() + " | " + i.getPrice() + " EUR"));
        } else {
            System.out.println("Nema proizvoda u odabranoj kategoriji!");
        }

    }

    /**
     * Ispisuje sve proizvode koji su prodani
     *@param items lista svih proizvoda
     */
    private static <T extends Item & Sold>void ispisProdanihProizvoda(List<T> items){

        items.stream().filter(T::isSold)
                .forEach(i-> System.out.println(
                    i.getName()+" | "+i.getPrice()+" EUR | "+i.getCategory()));
    }

    /**
     * Ispisuje sve proizvode koji nisu prodani
     * @param items lista svih proizvoda
     */
    private static void ispisDostupnihProizvoda(List<? extends Item> items){

        items.stream().filter(i-> !i.isSold())
                .forEach(i-> System.out.println(
                    i.getName()+" | "+i.getPrice()+" EUR | "+i.getCategory()));

    }

    /**
     * Dodaje prodane proizvode u arhivu - log svih prodanih proizvoda
     * @param naruceniProizvodi proizvodi koje je korisnik naručio
     * @param arhiva popis odnosno log svih prodanih proizvoda
     */
    private static void arhivirajProizvode(List<? extends Item> naruceniProizvodi, List<? super Item> arhiva) {
        naruceniProizvodi.forEach(arhiva::add);
    }


    private static void saveBackup(List<User> users, List<Item> items){

        Backup backup= new Backup(users,items);

        try(ObjectOutputStream oos=
                    new ObjectOutputStream(new FileOutputStream(BACKUP_FILE))){

            oos.writeObject(backup);
            System.out.println("Backup učitanih podataka je spremljen u backup.bin!");

        } catch (IOException e) {
            log.error("Greška pri serijalizaciji datoteke backup.bin",e);
            System.out.println("Greška pri backupiranju podataka -> "+e.getMessage());
        }

    }

    private static Backup loadBackup(){

        try(ObjectInputStream ois= new ObjectInputStream(new FileInputStream(BACKUP_FILE))){

            Object o=ois.readObject();
            return (Backup) o;

        }catch(FileNotFoundException e){

            log.error("Backup datoteka ne postoji!",e);
            System.out.println("Backup datoteka ne postoji -> "+ e.getMessage());
            return null;

        } catch (IOException | ClassNotFoundException e)  {
            log.error("Greška pri otvaranju backup datoteke",e);
            System.out.println("Greška pri učitavanju backupa! ->"+e.getMessage());
            return null;
        }

    }

    private static void pregaziBackup(Backup backup, List<User> users, List<Item> items) {

        users.clear();
        items.clear();

        users.addAll(backup.getUsers());
        items.addAll(backup.getItems());

        System.out.println("Podatci iz backupa su uspješno zamjenjeni novim podatcima.");
    }

    private static void saveLogToXml() {
        try {
            jakarta.xml.bind.JAXBContext context =
                    jakarta.xml.bind.JAXBContext.newInstance(LogEntries.class);

            jakarta.xml.bind.Marshaller marshaller =
                    context.createMarshaller();

            marshaller.setProperty(
                    jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);

            LogEntries wrapper = new LogEntries(logEntries);

            marshaller.marshal(wrapper, new java.io.File(XML_LOG_FILE));

        } catch (jakarta.xml.bind.JAXBException e) {
            log.error("Greška pri zapisivanju XML loga!", e);
        }
    }

    private static void addLogEntry(String action, String details) {
        LogEntry entry = new LogEntry(action, details);
        logEntries.add(entry);
        saveLogToXml();
    }

    private static void printLogFromXml() {
        java.io.File file = new java.io.File(XML_LOG_FILE);
        if (!file.exists()) {
            System.out.println("Nema spremljenog XML loga.");
            return;
        }

        try {
            jakarta.xml.bind.JAXBContext context =
                    jakarta.xml.bind.JAXBContext.newInstance(LogEntries.class);

            jakarta.xml.bind.Unmarshaller unmarshaller =
                    context.createUnmarshaller();

            LogEntries wrapper =
                    (LogEntries) unmarshaller.unmarshal(file);

            System.out.println("Zapisane korisničke akcije:");

            for (LogEntry e : wrapper.getEntries()) {
                // BEZ XML TAGOVA – samo vrijednosti
                System.out.println(
                        e.getTime() + " | " +
                                e.getAction() + " | " +
                                e.getDetails()
                );
            }

        } catch (jakarta.xml.bind.JAXBException e) {
            System.out.println("Greška pri čitanju XML loga.");
            log.error("Greška pri čitanju XML loga!", e);
        }
    }


}
