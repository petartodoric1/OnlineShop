package service;

import entities.Backup;
import entities.Item;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final String BACKUP_FILE = "datoteke/backup.bin";

    public void saveBackup(List<User> users, List<Item> items) {

        Backup backup = new Backup(users, items);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(BACKUP_FILE))) {

            oos.writeObject(backup);
            System.out.println("Backup učitanih podataka je spremljen u backup.bin!");

        } catch (IOException e) {
            log.error("Greška pri serijalizaciji datoteke backup.bin", e);
            System.out.println("Greška pri backupiranju podataka -> " + e.getMessage());
        }
    }

    public Backup loadBackup() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(BACKUP_FILE))) {

            Object o = ois.readObject();
            return (Backup) o;

        } catch (FileNotFoundException e) {
            log.error("Backup datoteka ne postoji!", e);
            System.out.println("Backup datoteka ne postoji -> " + e.getMessage());
            return null;

        } catch (IOException | ClassNotFoundException e) {
            log.error("Greška pri otvaranju backup datoteke", e);
            System.out.println("Greška pri učitavanju backupa! -> " + e.getMessage());
            return null;
        }
    }

    public void pregaziBackup(Backup backup, List<User> users, List<Item> items) {

        users.clear();
        items.clear();

        users.addAll(backup.getUsers());
        items.addAll(backup.getItems());

        System.out.println("Podatci iz backupa su uspješno zamjenjeni novim podatcima.");
    }
}
