package app;

import entities.*;
import entities.Record;
import exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;




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

    private static final Integer NUMBER_OF_ALL = 2 ;

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



        users=generateUsers(sc);
        items=generateItems(sc);

        bookings=generateBookings(sc,users,items,records,userBookings,arhivaProizvoda);
        pretrazivanje(sc,bookings,items,records,userBookings,arhivaProizvoda);

    }

    /**
     * Generira korisnike prema unosu korisnika preko konzole i svakom korisniku dodjeljuje username i password.
     * <p>Korisnicima se automatski dodjeljuje ID</p>
     *
     * @param sc Scanner objekt za unos podataka s konzole
     * @throws InvalidNameException ako korisnik pokuša unijeti prazan Username ili je Username već zauzet
     * @throws InvalidInputException ako korisnik pokuša postaviti prazan password
     * @return polje {@link User} objekata
     */
    private static List<User> generateUsers(Scanner sc) {
        log.trace("Započeto generiranje korisnika.");
        List<User> users=new ArrayList<>();
        System.out.println("Generirajte korisnike!");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){
            System.out.println("Unesite "+(i+1)+". korisnika:");

            String username=null;
            while(true) {
                System.out.println("Username:");
                try{
                    username=sc.nextLine();

                    if(username.isEmpty()){
                        throw new InvalidNameException("Username ne smije biti prazan!");
                    }

                    final String usernameCopy=username;
                    boolean exist=users.stream()
                            .anyMatch(u ->u.getUsername().equalsIgnoreCase(usernameCopy));

                    if(exist){
                        throw new InvalidNameException("Username već postoji!");
                    }

                    break;
                }catch(InvalidNameException e){
                    System.out.println("Greška pri unosu ->"+e.getMessage());
                    log.error("Neispravan unos username-a!",e);
                }
            }

            String password=null;

            while(true) {
                System.out.println("Password:");
                try{
                  password=sc.nextLine();
                  if(password.isEmpty()){
                      throw new InvalidInputException("Password ne smije biti prazan!");
                  }
                  break;
                }catch(InvalidInputException e){
                    System.out.println("Greška pri unosu ->"+e.getMessage());
                    log.error("Korisnik je pokušao staviti prazan password!");
                }
            }


            /* Ne treba mi email za sad
            System.out.println("Email:");
            String email=sc.nextLine();
             */
            System.out.println("User id je dodjeljen automatski -> Vaš id je: "+(i+1)+"\n");
            Integer userId=i+1;

            users.add( new User.Builder(username,password)
                    .userId(userId).
                    build());
            log.info("Uspješno generiran korisnik: {}",username);
        }
        log.trace("Završeno generiranje korisnika.");
        return users;
    }


    /**
     * Generira proizvode (majice, hlače i cipele).
     * <p>Metoda provjerava ispravnost cijene i veličine te u slučaju pogreške traži ponovni unos.</p>
     *
     * @param sc Scanner objekt za unos
     * @throws NumberFormatException ako se unese String umjesto brojčane vrijednosti
     * @throws InvalidInputException ako korisnik unese krivu vrijednost za traženi atribut objekta
     * @throws InvalidNameException ako korisnik pokuša unijeti prazno ime
     * @return polje {@link Item} objekata
     */
    private static List<Item> generateItems(Scanner sc)  {
        log.trace("Započeto generiranje proizvoda.");
        List<Item> items=new ArrayList<>();

        Integer itemId=0;

        System.out.println("Napravite popis proizvoda:");

        System.out.println("Majice:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". majicu:");

            String ime=null;
            while(true) {

                System.out.println("Ime:");
                try{
                    ime=sc.nextLine();
                    if(ime.isEmpty()){
                        throw new InvalidNameException("Naziv proizvoda ne smije biti prazan!");
                    }
                    break;
                }catch(InvalidNameException e){
                    System.out.println("Greška pri unosu ->"+e.getMessage());
                    log.error("Korisnik je ostavio ime proizvoda prazno",e);
                }
            }

            BigDecimal price=null;
            while(true) {
                System.out.println("Cijena:");

                try {
                    price = new BigDecimal(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    InvalidInputException ex= new InvalidInputException("Cijena mora biti brojčana vrijednost!");
                    System.out.println("Greška pri unosu vrijednosti -> "+ex.getMessage());
                    log.error("Neispravan unos cijene", e);
                }
            }

            System.out.println("Boja:");
            String boja=sc.nextLine();
            System.out.println("Veličina:");
            String velicina=sc.nextLine();
            itemId++;

            items.add(new Majica(ime,price,itemId,boja,velicina));

        }

        System.out.println("Hlače:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". hlače:");
            String ime=null;
            while(true) {
                System.out.println("Ime:");
                try{
                    ime=sc.nextLine();
                    if(ime.isEmpty()){
                        throw new InvalidNameException("Naziv proizvoda ne smije biti prazan");
                    }
                    break;
                }catch(InvalidNameException e){
                    System.out.println("Greška pri unosu ->"+e.getMessage());
                    log.error("Korisnik je ostavio ime proizvoda prazno",e);
                }
            }

            BigDecimal price=null;
            while(true) {
                System.out.println("Cijena:");

                try {
                    price = new BigDecimal(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    InvalidInputException ex= new InvalidInputException("Cijena mora biti brojčana vrijednost!");
                    System.out.println("Greška pri unosu vrijednosti -> "+ex.getMessage());
                    log.error("Neispravan unos cijene", e);
                }
            }

            System.out.println("Boja:");
            String boja=sc.nextLine();
            System.out.println("Veličina:");
            String velicina=sc.nextLine();
            System.out.println("Vrsta:");
            String vrsta=sc.nextLine();
            itemId++;

            items.add(new Hlace(ime,price,itemId,boja,velicina,vrsta));

        }

        System.out.println("Cipele:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". cipele:");
            String ime=null;
            while(true) {
                System.out.println("Ime:");
                try{
                    ime=sc.nextLine();
                    if(ime.isEmpty()){
                        throw new InvalidNameException("Naziv proizvoda ne smije biti prazan");
                    }
                    break;
                }catch(InvalidNameException e){
                    System.out.println("Greška pri unosu ->"+e.getMessage());
                    log.error("Korisnik je ostavio ime proizvoda prazno",e);
                }
            }

            BigDecimal price=null;
            while(true) {
                System.out.println("Cijena:");

                try {
                    price = new BigDecimal(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    InvalidInputException ex= new InvalidInputException("Cijena mora biti brojčana vrijednost!");
                    System.out.println("Greška pri unosu vrijednosti -> "+ex.getMessage());
                    log.error("Neispravan unos cijene", e);
                }
            }

            BigDecimal velicina=null;
            while(true) {
                System.out.println("Veličina:");

                try {
                    velicina = new BigDecimal(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    InvalidInputException ex= new InvalidInputException("Veličina mora biti brojčana vrijednost!");
                    System.out.println("Greška pri unosu vrijednosti -> "+ex.getMessage());
                    log.error("Neispravan unos veličine", e);
                }
            }

            itemId++;

            items.add(new Cipele(ime,price,itemId,velicina));

        }
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

    private static List<Booking> generateBookings(Scanner sc,List<User> users,List<Item> items,Set<Record> records, Map<String,List<Booking>> userBookings, List<Object> arhivaProizvoda)  {

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
                        choice = sc.nextInt();

                        //provjera jel odabrao postojeci proizvod
                        try {
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
                if(!orderedItems.isEmpty()) {
                bookings.add( new Booking(selectedUser, orderedItems, orderedQuantity,bookingIndex));

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
    private static void pretrazivanje(Scanner sc,List<Booking> bookings, List<Item> items,Set<Record> records, Map<String, List<Booking>> userBookings,List<Object> arhivaProizvoda) {

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
                    System.out.println("Odabir:");

                    try {
                        odabir = sc.nextInt();
                        sc.nextLine();

                        if (odabir != 1 && odabir != 2 && odabir != 3) {
                            throw new InvalidOdabirException("Neispravan unos! Unesite 1, 2 ili 3.");
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
                    odabirProizvoda(sc, items,arhivaProizvoda);
                }

                if (odabir.equals(2)) {
                    odabirNarudzbe(sc, bookings, records);
                }
                if(odabir.equals(3)) {
                    pretrazivanjeKorisnika(sc,userBookings);
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
                pretrazivanjePoKategoriji(sc, items);
            }

            case 5 -> {
                // Ispis svih PRODANIH proizvoda
                ispisProdanihProizvoda(items);
            }

            case 6 -> {
                // Ispis svih DOSTUPNIH proizvoda
                ispisDostupnihProizvoda(items);
            }

            case 7 ->{
                // Ispis arhive proizvoda
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
            System.out.println("Najskuplja narudžba je narudžba sa indexom: "+expensiveBooking.getBookingId());
            System.out.println("Naručitelj: "+expensiveBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+expensiveBooking.getTotalPrice()+" EUR");
            System.out.print("Status narudžbe: ");
            System.out.println(expensiveBooking.getStatus());

        }

        if(odabir.equals(2)){
            System.out.println("Najjeftinija narudžba je narudžba sa indexom: "+cheapestBooking.getBookingId());
            System.out.println("Naručitelj: "+cheapestBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+cheapestBooking.getTotalPrice()+" EUR");
            System.out.print("Status narudžbe: ");
            System.out.println(cheapestBooking.getStatus());

        }

        if (odabir.equals(3)){

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




}
