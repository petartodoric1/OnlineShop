package app;

import entities.Booking;
import entities.Item;
import entities.User;
import entities.Record;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    private static final Integer NUMBER_OF_ALL = 5 ;
    static void main() {
        Scanner sc= new Scanner(System.in);

        User[] users= new User[NUMBER_OF_ALL];
        Item[] items=new Item[NUMBER_OF_ALL];
        Booking[] bookings= new Booking[NUMBER_OF_ALL];
        Record[] records= new Record[NUMBER_OF_ALL];
        String answer="Da";

        System.out.println("Generirajte korisnike!");
        for(Integer i=0;i< users.length;i++){
            System.out.println("Unesite "+(i+1)+". korisnika:");
            System.out.println("Username:");
            String username=sc.nextLine();
            System.out.println("Password:");
            String password=sc.nextLine();
            System.out.println("Email:");
            String email=sc.nextLine();
            System.out.println("User id je dodjeljen automatski -> Vaš id je: "+(i+1));
            Integer userId=i+1;


            users[i]= new User(username,password,email,userId);

        }

        System.out.println("Napravite popis proizvoda:");

        for(Integer i=0;i< items.length;i++){
            System.out.println("Unesite "+(i+1)+". proizvod:");
            System.out.println("Ime:");
            String ime=sc.nextLine();
            System.out.println("Cijenu:");
            BigDecimal price=new BigDecimal(sc.nextLine());
            Integer itemId=i+1;


            items[i]=new Item(ime,price,itemId);


        }


        for(Integer bookingIndex=0;bookingIndex< bookings.length;bookingIndex++) {

            System.out.println("Dobar dan, zelite li nešto kupiti?");
            String confirmation = sc.nextLine();

            if (confirmation.equals("Da")) {

                Integer ordinal = 1;
                Integer orderedIndex = 0;
                Item[] orderedItems = new Item[NUMBER_OF_ALL];
                Integer[] orderedQuantity = new Integer[NUMBER_OF_ALL];


                System.out.println("Unesite vaš username:");
                Integer correctUserName = 0;
                int userIndex = 0;
                String username = sc.nextLine();


                while (correctUserName != 1) {

                    for (Integer i = 0; i < users.length; i++) {
                        if (users[i].getUsername().equals(username)) {
                            correctUserName = 1;
                            userIndex = i;
                        }

                    }
                    if (correctUserName != 1) {
                        System.out.println("Krivi username! Probajte ponovo:");
                        username = sc.nextLine();
                    }

                }

                User selectedUser = users[userIndex];

                System.out.println("Uspiješna prijava, nastavite s kupnjom!");

                do {
                    for (Item i : items) {
                        System.out.println(ordinal + "." + i.getName() + " - " + i.getPrice() + " EUR");
                        ordinal++;
                    }
                    ordinal = 1;

                    System.out.println("Odaberite proizvod:");
                    Integer choice = sc.nextInt();

                    Item selectedItem = items[choice - 1];
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

            } else {
                System.out.println("U redu, doviđenja!");
                bookingIndex--;
            }
        }

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
        System.out.println("Vaš odabir: ");

        Integer odabir=sc.nextInt();

        if(odabir==1){
            System.out.println("Najskuplja narudžba je narudžba sa indexom: "+expensiveBooking.getBookingId());
            System.out.println("Naručitelj: "+expensiveBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+expensiveBooking.getTotalPrice()+" EUR");

        }
        else if(odabir==2){
            System.out.println("Najjeftinija narudžba je narudžba sa indexom: "+cheapestBooking.getBookingId());
            System.out.println("Naručitelj: "+cheapestBooking.getUser().getUsername());
            System.out.println("Ukupna cijena je: "+cheapestBooking.getTotalPrice()+" EUR");

        }
        else{
            System.out.println("Krivi odabir!");
        }


    }

}
