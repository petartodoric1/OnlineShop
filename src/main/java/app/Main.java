package app;

import entities.Booking;
import entities.User;
import entities.Item;
import entities.Majica;
import entities.Hlace;
import entities.Cipele;
import entities.Record;
import exceptions.InvalidDaNeException;
import exceptions.InvalidInputException;
import exceptions.InvalidOdabirException;
import exceptions.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;


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
    private static final Integer NUMBER_OF_ITEMS = 3;

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



        users=generateUsers(sc);
        items=generateItems(sc);

        bookings=generateBookings(sc,users,items,records,userBookings);
        pretrazivanje(sc,bookings,items,records,userBookings);

    }

    /**
     * Generira korisnike prema unosu korisnika preko konzole i svakom korisniku dodjeljuje username i password.
     * <p>Korisnicima se automatski dodjeljuje ID</p>
     *
     * @param sc Scanner objekt za unos podataka s konzole
     * @return polje {@link User} objekata
     */
    private static List<User> generateUsers(Scanner sc) {
        log.trace("Započeto generiranje korisnika.");
        List<User> users=new ArrayList<>();
        System.out.println("Generirajte korisnike!");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){
            System.out.println("Unesite "+(i+1)+". korisnika:");
            System.out.println("Username:");
            String username=sc.nextLine();
            System.out.println("Password:");
            String password=sc.nextLine();

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
     * @return polje {@link Item} objekata
     */
    private static List<Item>  generateItems(Scanner sc)  {
        log.trace("Započeto generiranje proizvoda.");
        List<Item> items=new ArrayList<>();

        Integer itemId=0;
        Integer itemIndex=0;
        System.out.println("Napravite popis proizvoda:");

        System.out.println("Majice:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". majicu:");
            System.out.println("Ime:");
            String ime=sc.nextLine();

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
            itemIndex++;
        }

        System.out.println("Hlače:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". hlače:");
            System.out.println("Ime:");
            String ime=sc.nextLine();

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
            itemIndex++;
        }

        System.out.println("Cipele:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". cipele:");
            System.out.println("Ime:");
            String ime=sc.nextLine();

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
            itemIndex++;
        }
        log.trace("Završeno generiranje proizvoda.");
        return items;
    }

    /**
     * Generira narudžbe korisnika na temelju dostupnih artikala.
     * Omogućuje prijavu korisnika, odabir proizvoda, količina i odabir plaćanja ili rezervacije.
     *
     * @param sc Scanner objekt za unos
     * @param users polje postojećih korisnika
     * @param items polje dostupnih artikala
     * @param records polje zapisa o plaćenim narudžbama
     * @throws InvalidDaNeException ako korisnik na Da/Ne pitanje unese nešto treće
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđena
     * @throws LoginFailedException ako korisnik tri puta unese pogrešnu lozinku
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     * @return polje {@link Booking} objekata
     */

    private static List<Booking> generateBookings(Scanner sc,List<User> users,List<Item> items,Set<Record> records, Map<String,List<Booking>> userBookings)  {
        log.trace("Započeto generiranje narudžbi.");
        List<Booking> bookings= new ArrayList<>();
        Integer recordId=0;
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

                Integer ordinal = 1;
                Integer orderedIndex = 0;
                List<Item> orderedItems = new ArrayList<>();
                List<Integer> orderedQuantity = new ArrayList<>();
                String answer=null;

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

                do {
                    for (Item i : items) {
                        if(i.isSold()){
                            System.out.print("(RASPRODANO) -> ");
                        }
                        System.out.println("("+ordinal +")"+ "-> " + i.getName()+" ("+i.getCategory()+")"+ " - " + i.getPrice() + " EUR");
                        ordinal++;
                    }
                    ordinal = 1;

                    Integer choice=null;
                    while(true) {

                        System.out.println("Odaberite proizvod:");
                        choice = sc.nextInt();
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
                    orderedIndex++;
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

                String username=selectedUser.getUsername();

                    if(!userBookings.containsKey(username)) {
                        userBookings.put(username,new ArrayList<>());
                    }
                    userBookings.get(username).add(bookings.get(bookingIndex));

                System.out.println("Ukupna cijena vaše narudžbe je: "+bookings.get(bookingIndex).getTotalPrice()+" EUR\n");



                }

                else{
                    System.out.println("Doviđenja i dođite nam opet!");
                    bookingIndex--;
                    continue;
                }

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





                if (odgovor.equals(1)) {
                    System.out.println("Vaša narudžba je plaćena i poslana na vašu adresu!");
                    bookings.get(bookingIndex).markAsPayed();

                    records.add(new Record(selectedUser.getUsername(),
                            bookings.get(bookingIndex).getTotalPrice(),
                            bookings.get(bookingIndex).getBookingId(),
                                                 LocalDateTime.now()));
                    recordId++;

                }
                if(odgovor.equals(2)) {
                    System.out.println("Vaša narudžba vas čeka u našoj poslovnici!");
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
     * @param users polje korisnika za provjeru prijave
     * @return prijavljeni {@link User}
     * @throws LoginFailedException ako korisnik tri puta unese pogrešnu lozinku
     */

    private static User login(Scanner sc, List<User> users) throws LoginFailedException {

        System.out.println("Unesite vaš username:");

        String username = sc.nextLine();

        User selectedUser=null;
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                selectedUser=user;
                break;
            }
        }
        if (selectedUser == null) {
            System.out.println("Krivi username! Probajte ponovo.");
            return login(sc,users);
        }

        int attempts=0;

        while(true) {
            System.out.println("Unesite password:");
            String password = sc.nextLine();

            if (password.equals(selectedUser.getPassword())) {
                log.info("Korisnik {} uspješno prijavljen.", selectedUser.getUsername());
                System.out.println("Uspiješna prijava! Nastavite s kupnjom!");
                return selectedUser;
            }
            else {
                attempts++;

                if (attempts>=3) {
                    throw new LoginFailedException("Previše neuspjelih pokušaja unosa passworda!");
                }
                System.out.println("Krivi password! Pokušajte ponovo:");
                log.warn("Neuspješna prijava korisnika: {}", selectedUser.getUsername());

            }
        }

    }

    /**
     * Omogućuje pretraživanje proizvoda i narudžbi.
     * <p>Korisnik može odabrati između pregleda proizvoda ili narudžbi.</p>
     *
     * @param sc Scanner objekt za unos
     * @param bookings polje svih narudžbi
     * @param items polje svih artikala
     * @param records polje svih zapisa o plaćenim narudžbama
     * @throws InvalidDaNeException ako korisnik na Da/Ne pitanje unese nešto treće
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void pretrazivanje(Scanner sc,List<Booking> bookings, List<Item> items,Set<Record> records, Map<String, List<Booking>> userBookings) {

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
                    odabirProizvoda(sc, items);
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
     * Prikazuje najskuplji ili najjeftiniji proizvod.
     * <p>Uključuje i informaciju o dostupnosti proizvoda.</p>
     *
     * @param sc Scanner objekt za unos
     * @param items polje artikala za pretragu
     * @throws InvalidOdabirException ako korisnik kod odabira upiše vrijednost koja nije ponuđen
     * @throws InputMismatchException ako korisnik unese string umjesto brojčane vrijednosti kod odabira
     */
    private static void odabirProizvoda(Scanner sc,List<Item> items) {
        Item cheapestItem=items.get(0);
        Item expensiveItem=items.get(0);

        for(Integer i=1;i< items.size();i++){

            Item currentItem=items.get(i);
            BigDecimal currentPrice=currentItem.getPrice();

            if(currentPrice.compareTo(cheapestItem.getPrice())<0){
                cheapestItem=currentItem;
            }

            if(currentPrice.compareTo(expensiveItem.getPrice())>0){
                expensiveItem=currentItem;
            }
        }




        Integer odabir = null;
        while (true) {
            System.out.println("Koji proizvod želite odabrati:");
            System.out.println("(1) Najskuplji proizvod");
            System.out.println("(2) Najjeftiniji proizvod");
            System.out.println("Vaš odabir: ");

            try {
                odabir = sc.nextInt();
                sc.nextLine();

                if (odabir != 1 && odabir != 2) {
                    throw new InvalidOdabirException("Neispravan unos! Unesite 1 ili 2.");
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
            System.out.println("Najskuplji proizvod je: "+expensiveItem.getName()+" "+expensiveItem.getCategory());
            System.out.println("Ukupna cijena je: "+expensiveItem.getPrice()+" EUR");
            System.out.print("Dostupnost proizvoda: ");
            if(expensiveItem.isSold()){
                System.out.println("Rasprodano");
            }
            else {
                System.out.println("Dostupno");
            }

        }
        if(odabir.equals(2)){
            System.out.println("Najjeftiniji proizvod je: "+cheapestItem.getName()+" "+cheapestItem.getCategory());
            System.out.println("Ukupna cijena je: "+cheapestItem.getPrice()+" EUR");
            System.out.print("Dostupnost proizvoda: ");
            if(cheapestItem.isSold()){
                System.out.println("Rasprodano");
            }
            else {
                System.out.println("Dostupno");
            }

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

        Booking cheapestBooking=bookings.get(0);
        Booking expensiveBooking=bookings.get(0);

        for(Booking booking:bookings){


            BigDecimal currentPrice=booking.getTotalPrice();

            if(currentPrice.compareTo(cheapestBooking.getTotalPrice())<0){
                cheapestBooking=booking;
            }

            if(currentPrice.compareTo(expensiveBooking.getTotalPrice())>0){
                expensiveBooking=booking;
            }
        }


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
            if(expensiveBooking.isPayed()){
                System.out.println("Plaćeno");
            }
            else {
                System.out.println("Rezervirano");
            }
        }

        if(odabir.equals(2)){
            System.out.println("Najjeftinija narudžba je narudžba sa indexom: "+cheapestBooking.getBookingId());
            System.out.println("Naručitelj: "+cheapestBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+cheapestBooking.getTotalPrice()+" EUR");
            System.out.print("Status narudžbe: ");
            if(cheapestBooking.isPayed()){
                System.out.println("Plaćeno");
            }
            else {
                System.out.println("Rezervirano");
            }
        }

        if (odabir.equals(3)){

            System.out.println("Plaćene narudžbe su:");
            boolean found=false;
            for(Record record:records){

                    System.out.println("Username: " + record.username() +
                            ", Cijena narudžbe: " + record.price() + " EUR" +
                            ", BookingId: " + record.bookingId() +
                            ", Vrijeme: " + record.time());
                    found=true;
            }

            if(!found){
                System.out.println("Nema plaćenih narudžbi!");
            }

        }
    }


    /**
     *
     * @param sc Scanner objekt za unos
     * @param userBookings mapa koja sadrži povezuje korisnikov username sa njegovim narudžbama
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
        for (Booking b : korisnikoveNarudzbe) {
            System.out.println("Id narudžbe: " + b.getBookingId() +
                    " | Ukupna cijena: " + b.getTotalPrice() + " EUR " +
                    "| Status: " + (b.isPayed() ? "Plaćeno" : "Rezervirano"));
        }



    }

}
