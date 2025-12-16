package repository;

import entities.Booking;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BookingRepository {

    private static final Logger log =
            LoggerFactory.getLogger(BookingRepository.class);

    private static final Path BOOKINGS_FILE =
            Paths.get("datoteke/bookings.json");

    /**
     * Zapisuje narudžbe u bookings.json datoteku.
     * @param bookings lista narudžbi
     */

    public void save(List<Booking> bookings) {
        try {
            JsonbConfig config = new JsonbConfig()
                    .withFormatting(true);

            Jsonb jsonb = JsonbBuilder.create(config);
            String json = jsonb.toJson(bookings);

            Files.writeString(BOOKINGS_FILE, json);

            log.debug("Spremljeno {} booking-a.", bookings.size());

        } catch (IOException e) {
            log.error("Greška kod pisanja u datoteku bookings.json!", e);
        }
    }
}
