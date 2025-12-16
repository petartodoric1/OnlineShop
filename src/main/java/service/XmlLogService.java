package service;

import entities.LogEntries;
import entities.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlLogService {

    private static final Logger log = LoggerFactory.getLogger(XmlLogService.class);
    private static final String XML_LOG_FILE = "datoteke/log.xml";

    private final List<LogEntry> logEntries = new ArrayList<>();

    /**
     * Dodaje novi zapis u XML log i odmah ga sprema u datoteku
     */
    public void addLogEntry(String action, String details) {
        LogEntry entry = new LogEntry(action, details);
        logEntries.add(entry);
        saveLogToXml();
    }

    /**
     * Sprema sve log zapise u XML datoteku
     */
    private void saveLogToXml() {
        try {
            JAXBContext context = JAXBContext.newInstance(LogEntries.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            LogEntries wrapper = new LogEntries(logEntries);
            marshaller.marshal(wrapper, new File(XML_LOG_FILE));

        } catch (JAXBException e) {
            log.error("Greška pri zapisivanju XML loga!", e);
        }
    }

    /**
     * Ispisuje XML log bez XML tagova (samo vrijednosti)
     */
    public void printLogFromXml() {
        File file = new File(XML_LOG_FILE);

        if (!file.exists()) {
            System.out.println("Nema spremljenog XML loga.");
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(LogEntries.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            LogEntries wrapper = (LogEntries) unmarshaller.unmarshal(file);

            System.out.println("Zapisane korisničke akcije:");

            for (LogEntry e : wrapper.getEntries()) {
                System.out.println(
                        e.getTime() + " | " +
                                e.getAction() + " | " +
                                e.getDetails()
                );
            }

        } catch (JAXBException e) {
            System.out.println("Greška pri čitanju XML loga.");
            log.error("Greška pri čitanju XML loga!", e);
        }
    }
}
