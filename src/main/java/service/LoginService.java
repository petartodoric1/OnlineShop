package service;

import entities.User;
import exceptions.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public User login(Scanner sc, List<User> users) throws LoginFailedException {

        System.out.println("Unesite vaš username:");
        String username = sc.nextLine();

        Optional<User> selectedUser = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (selectedUser.isEmpty()) {
            System.out.println("Krivi username! Probajte ponovo.");
            return login(sc, users); // isto kao kod tebe
        }

        User user = selectedUser.get();

        int attempts = 0;

        while (true) {
            System.out.println("Unesite password:");
            String password = sc.nextLine();

            if (password.equals(user.getPassword())) {
                log.info("Korisnik {} uspješno prijavljen.", user.getUsername());
                System.out.println("Uspiješna prijava! Nastavite s kupnjom!");
                return user;
            } else {
                attempts++;

                log.warn("Neuspješna prijava korisnika: {} (pokušaj {}/3)", user.getUsername(), attempts);

                if (attempts >= 3) {
                    throw new LoginFailedException("Previše neuspjelih pokušaja unosa passworda!");
                }

                System.out.println("Krivi password! Pokušajte ponovo:");
            }
        }
    }
}
