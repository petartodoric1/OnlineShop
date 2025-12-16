package repository;

import entities.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {

    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);

    private static final String MAJICE_FILE = "datoteke/majice.json";
    private static final String HLACE_FILE  = "datoteke/hlace.json";
    private static final String CIPELE_FILE = "datoteke/cipele.json";

    /**
     * Učitava sve proizvode i dodaje ih u jednu listu.
     * @return listu {@link Item} objekata
     */

    public List<Item> loadAllItems() {
        log.trace("Započeto učitavanje svih proizvoda.");

        List<Item> items = new ArrayList<>();

        items.addAll(loadMajice());
        items.addAll(loadHlace());
        items.addAll(loadCipele());

        log.trace("Završeno učitavanje proizvoda. Ukupno: {}", items.size());
        return items;
    }

    /**
     * Učitava majice iz datoteke majice.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Majica} objekata
     */

    private List<Majica> loadMajice() {
        return loadFromJson(MAJICE_FILE, new ArrayList<Majica>() {});
    }

    /**
     * Učitava Hlače iz datoteke hlace.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Hlace} objekata
     */
    private List<Hlace> loadHlace() {
        return loadFromJson(HLACE_FILE, new ArrayList<Hlace>() {});
    }

    /**
     * Učitava cipele iz datoteke cipele.json.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu {@link Cipele} objekata
     */
    private List<Cipele> loadCipele() {
        return loadFromJson(CIPELE_FILE, new ArrayList<Cipele>() {});
    }

    /**
     * Učitava proizvode iz datoteke datoteke predane kao argument String file.
     * @throws IOException ako metoda ima problema s učitavanjem datoteke (npr. datoteka ne postoji ili je krivi path do datoteke)
     * @return listu objekata predane kategorije kao argument
     */

    private <T> List<T> loadFromJson(String file, ArrayList<T> kategorija) {
        try {
            Jsonb jsonb = JsonbBuilder.create();
            String json = Files.readString(Paths.get(file));

            return jsonb.fromJson(
                    json,
                    kategorija.getClass().getGenericSuperclass()
            );

        } catch (IOException e) {
            log.error("Greška pri učitavanju datoteke: {}", file, e);
            return new ArrayList<>();
        }
    }
}
