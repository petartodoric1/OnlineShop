package repository;

import entities.Record;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class RecordRepository {

    private static final Logger log = LoggerFactory.getLogger(RecordRepository.class);

    private static final Path RECORDS_FILE = Paths.get("datoteke/records.json");

    /**
     * Zapisuje plaćene narudžbe u records.json datoteku.
     * @param records lista plaćenih narudžbi
     */

    public void save(Set<Record> records) {
        try {
            JsonbConfig config = new JsonbConfig().withFormatting(true);
            Jsonb jsonb = JsonbBuilder.create(config);

            String json = jsonb.toJson(records);
            Files.writeString(RECORDS_FILE, json);

            log.debug("Spremljeno {} record-a u {}", records.size(), RECORDS_FILE);

        } catch (IOException e) {
            log.error("Greška kod pisanja u datoteku records.json!", e);
        }
    }
}
