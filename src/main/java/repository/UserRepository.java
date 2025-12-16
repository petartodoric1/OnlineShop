package repository;

import entities.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final String USERS_FILE = "datoteke/users.json";


    /**
     * Učitava korisnike iz datoteke users.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link User} objekata
     */

    public List<User> loadUsers() {
        log.trace("Započeto učitavanje korisnika iz users.json.");

        try {
            Jsonb jsonb = JsonbBuilder.create();

            String json = Files.readString(Paths.get(USERS_FILE));

            List<User> users = jsonb.fromJson(
                    json,
                    new ArrayList<User>() {}.getClass().getGenericSuperclass()
            );

            log.trace("Završeno učitavanje korisnika. Učitano: {}", users.size());
            return users;

        } catch (IOException e) {
            log.error("Greška pri učitavanju korisnika iz users.json!", e);
            return new ArrayList<>();
        }
    }

}
