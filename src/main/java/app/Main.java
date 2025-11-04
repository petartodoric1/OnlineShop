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
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static Logger log= LoggerFactory.getLogger(Main.class);

    private static final Integer NUMBER_OF_ALL = 2 ;
    private static final Integer NUMBER_OF_ITEMS = 3;

    static void main() {

        log.info("Pokretanje aplikacije ...");
        Scanner sc= new Scanner(System.in);

        User[] users;
        Item[] items;
        Booking[] bookings;
        Record[] records= new  Record[NUMBER_OF_ALL];


        users=generateUsers(sc);
        items=generateItems(sc);


        bookings=generateBookings(sc,users,items,records);
        pretrazivanje(sc,bookings,items,records);

    }


    private static User[] generateUsers(Scanner sc) {
        log.trace("Započeto generiranje korisnika.");
        User[] users=new User[NUMBER_OF_ALL];
        System.out.println("Generirajte korisnike!");

        for(Integer i=0;i< users.length;i++){
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

            users[i]= new User.Builder(username,password).userId(userId).build();
            log.info("Uspješno generiran korisnik: {}",username);
        }
        log.trace("Završeno generiranje korisnika.");
        return users;
    }

    private static Item[] generateItems(Scanner sc)  {
        log.trace("Započeto generiranje proizvoda.");
        Item[] items=new Item[NUMBER_OF_ALL*NUMBER_OF_ITEMS];

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

            items[itemIndex]=new Majica(ime,price,itemId,boja,velicina);
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

            items[itemIndex]=new Hlace(ime,price,itemId,boja,velicina,vrsta);
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

            items[itemIndex]=new Cipele(ime,price,itemId,velicina);
            itemIndex++;
        }
        log.trace("Završeno generiranje proizvoda.");
        return items;
    }

    private static Booking[] generateBookings(Scanner sc,User[] users,Item[] items,Record[] records)  {
        log.trace("Započeto generiranje narudžbi.");
        Booking[] bookings=new Booking[NUMBER_OF_ALL];
        Integer recordId=0;
        for(Integer bookingIndex=0;bookingIndex< bookings.length;bookingIndex++) {
            log.debug("Generiranje {}. narudžbe", bookingIndex);

            String confirmation=null;

            while(true) {
                System.out.println("Dobar dan, zelite li nešto kupiti? (Da/Ne):");
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

                Integer ordinal = 1;
                Integer orderedIndex = 0;
                Item[] orderedItems = new Item[NUMBER_OF_ALL*NUMBER_OF_ITEMS];
                Integer[] orderedQuantity = new Integer[NUMBER_OF_ALL*NUMBER_OF_ITEMS];
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

                            if (choice > items.length || choice < 1) {
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

                        Item selectedItem = items[choice - 1];
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
                        orderedItems[orderedIndex] = selectedItem;


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

                    orderedQuantity[orderedIndex] = quantity;
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
                if(orderedItems[0]!= null){
                bookings[bookingIndex] = new Booking(selectedUser, orderedItems, orderedQuantity,bookingIndex);
                System.out.println("Ukupna cijena vaše narudžbe je: "+bookings[bookingIndex].getTotalPrice()+" EUR\n");
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
                    bookings[bookingIndex].markAsPayed();

                    records[recordId]=new Record(selectedUser.getUsername(),
                                                 bookings[bookingIndex].getTotalPrice(),
                                                 bookings[bookingIndex].getBookingId(),
                                                 LocalDateTime.now());
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

    private static User login(Scanner sc, User[] users) throws LoginFailedException {

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

    private static void pretrazivanje(Scanner sc,Booking[] bookings, Item[] items,Record[] records) {

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
                    System.out.println("Odabir:");

                    try {
                        odabir = sc.nextInt();
                        sc.nextLine();

                        if (odabir != 1 && odabir != 2) {
                            throw new InvalidOdabirException("Neispravan unos! Unesite 1 ili 2.");
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

    private static void odabirProizvoda(Scanner sc,Item[] items) {
        Item cheapestItem=items[0];
        Item expensiveItem=items[0];

        for(Integer i=1;i< items.length;i++){

            Item currentItem=items[i];
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
    private static void odabirNarudzbe(Scanner sc,Booking[] bookings,Record[] records) {

        Booking cheapestBooking=bookings[0];
        Booking expensiveBooking=bookings[0];

        for(Integer i=1;i< bookings.length;i++){

            Booking currentBooking=bookings[i];
            BigDecimal currentPrice=currentBooking.getTotalPrice();

            if(currentPrice.compareTo(cheapestBooking.getTotalPrice())<0){
                cheapestBooking=currentBooking;
            }

            if(currentPrice.compareTo(expensiveBooking.getTotalPrice())>0){
                expensiveBooking=currentBooking;
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
            for(Integer i=0;i<records.length;i++){

                if(records[i]==null) continue;

                    System.out.println("Username: " + records[i].username() +
                            ", Cijena narudžbe: " + records[i].price() + " EUR" +
                            ", BookingId: " + records[i].bookingId() +
                            ", Vrijeme: " + records[i].time());
                    found=true;
            }

            if(!found){
                System.out.println("Nema plaćenih narudžbi!");
            }

        }
    }

}
