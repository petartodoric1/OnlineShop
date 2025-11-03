package app;

import entities.Booking;
import entities.User;
import entities.Item;
import entities.Majica;
import entities.Hlace;
import entities.Cipele;
import entities.Record;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    private static final Integer NUMBER_OF_ALL = 2 ;
    private static final Integer NUMBER_OF_ITEMS = 3;

    static void main() {
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
        }
        return users;
    }

    private static Item[] generateItems(Scanner sc) {
        Item[] items=new Item[NUMBER_OF_ALL*NUMBER_OF_ITEMS];

        Integer itemId=0;
        Integer itemIndex=0;
        System.out.println("Napravite popis proizvoda:");

        System.out.println("Majice:");

        for(Integer i=0;i< NUMBER_OF_ALL;i++){

            System.out.println("Unesite "+(i+1)+". majicu:");
            System.out.println("Ime:");
            String ime=sc.nextLine();
            System.out.println("Cijena:");
            BigDecimal price=new BigDecimal(sc.nextLine());
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
            System.out.println("Cijena:");
            BigDecimal price=new BigDecimal(sc.nextLine());
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
            System.out.println("Cijena:");
            BigDecimal price=new BigDecimal(sc.nextLine());
            System.out.println("Veličina (npr. 42,42.5,43...):");
            BigDecimal velicina= new BigDecimal(sc.nextLine());
            itemId++;

            items[itemIndex]=new Cipele(ime,price,itemId,velicina);
            itemIndex++;
        }

        return items;
    }

    private static Booking[] generateBookings(Scanner sc,User[] users,Item[] items,Record[] records) {
        Booking[] bookings=new Booking[NUMBER_OF_ALL];
        Integer recordId=0;
        for(Integer bookingIndex=0;bookingIndex< bookings.length;bookingIndex++) {

            System.out.println("Dobar dan, zelite li nešto kupiti?");
            String confirmation = sc.nextLine();

            if (confirmation.equals("Da")) {

                Integer ordinal = 1;
                Integer orderedIndex = 0;
                Item[] orderedItems = new Item[NUMBER_OF_ALL*NUMBER_OF_ITEMS];
                Integer[] orderedQuantity = new Integer[NUMBER_OF_ALL*NUMBER_OF_ITEMS];
                String answer;

                User selectedUser=login(sc,users);

                do {
                    for (Item i : items) {
                        if(i.isSold()){
                            System.out.print("(RASPRODANO) -> ");
                        }
                        System.out.println("("+ordinal +")"+ "-> " + i.getName()+" ("+i.getCategory()+")"+ " - " + i.getPrice() + " EUR");
                        ordinal++;
                    }
                    ordinal = 1;

                    System.out.println("Odaberite proizvod:");
                    Integer choice = sc.nextInt();

                    Item selectedItem = items[choice - 1];

                    if(selectedItem.isSold()){
                        System.out.println("Ovaj proizvod je rasprodan!");
                        sc.nextLine();
                        System.out.println("Želite li neki drugi proizvod (Da/Ne):");
                        answer = sc.nextLine();
                        continue;
                    }

                    selectedItem.markAsSold();
                    orderedItems[orderedIndex] = selectedItem;

                    System.out.println("Odaberite količinu:");
                    Integer quantity = sc.nextInt();
                    orderedQuantity[orderedIndex] = quantity;
                    orderedIndex++;
                    sc.nextLine();
                    System.out.println("Želite li još neki proizvod:");
                    answer = sc.nextLine();

                } while ("Da".equals(answer));

                bookings[bookingIndex] = new Booking(selectedUser, orderedItems, orderedQuantity,bookingIndex);
                System.out.println("Ukupna cijena vaše narudžbe je: "+bookings[bookingIndex].getTotalPrice()+" EUR\n");

                System.out.println("Želite li platiti ili rezervirati narudžbu:");
                System.out.println("(1)Platiti");
                System.out.println("(2)Rezervirati");
                System.out.println("Vaš odabir:");
                String odgovor = sc.nextLine();

                if (odgovor.equals("1")) {
                    System.out.println("Vaša narudžba je plaćena i poslana na vašu adresu!");
                    bookings[bookingIndex].markAsPayed();

                    records[recordId]=new Record(selectedUser.getUsername(),
                                                 bookings[bookingIndex].getTotalPrice(),
                                                 bookings[bookingIndex].getBookingId(),
                                                 LocalDateTime.now());
                    recordId++;

                }
                else{
                    System.out.println("Vaša narudžba vas čeka u našoj poslovnici!");
                }

            }
            else {
                System.out.println("U redu, doviđenja!");
                bookingIndex--;
            }
        }
        return bookings;
    }

    private static User login(Scanner sc, User[] users) {

        System.out.println("Unesite vaš username:");
        boolean correctUserName = false;
        int userIndex = 0;
        String username = sc.nextLine();

        while (!correctUserName) {

            for (Integer i = 0; i < users.length; i++) {
                if (users[i].getUsername().equals(username)) {
                    correctUserName = true;
                    userIndex = i;
                    System.out.println("Unesite password:");
                    String password = sc.nextLine();
                    while(!users[i].getPassword().equals(password)) {
                        System.out.println("Netočan password! Probajte ponovo:");
                        password = sc.nextLine();
                    }
                    break;
                }

            }
            if (!correctUserName) {
                System.out.println("Krivi username! Probajte ponovo:");
                username = sc.nextLine();
            }

        }
        User selectedUser = users[userIndex];
        System.out.println("Uspiješna prijava, nastavite s kupnjom!");

        return selectedUser;
    }

    private static void pretrazivanje(Scanner sc,Booking[] bookings, Item[] items,Record[] records) {
        System.out.println("Želite li započeti pretraživanje?");
        String confirmation=sc.nextLine();

        if (confirmation.equals("Da")) {
            String answer="Da";
            do {
                Integer odabir;
                System.out.println("Pretraži:");
                System.out.println("(1) Proizvode");
                System.out.println("(2) Narudžbe");
                System.out.println("Odabir:");
                odabir = sc.nextInt();

                if (odabir.equals(1)) {
                    odabirProizvoda(sc, items);
                } else if (odabir.equals(2)) {
                    odabirNarudzbe(sc, bookings,records);
                } else {
                    System.out.println("Krivi odabir!");
                }
                sc.nextLine();
                System.out.println("Želite li nastaviti pretraživanje?");
                answer = sc.nextLine();
            } while ("Da".equals(answer));

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

        System.out.println("Koji proizvod želite odabrati:");
        System.out.println("(1) Najskuplji proizvod");
        System.out.println("(2) Najjeftiniji proizvod");
        System.out.println("Vaš odabir: ");

        Integer odabir=sc.nextInt();

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
        else if(odabir.equals(2)){
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
        else{
            System.out.println("Krivi odabir!");
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

        System.out.println("Koju narudžbu želite odabrati:");
        System.out.println("(1) Najskuplja narudžba");
        System.out.println("(2) Najjeftinija narudžba");
        System.out.println("(3) Plaćene narudžbe");
        System.out.println("Vaš odabir: ");

        Integer odabir=sc.nextInt();

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
        else if(odabir.equals(2)){
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
        else if (odabir.equals(3)){

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
        else{
            System.out.println("Krivi odabir!");
        }



    }

}
