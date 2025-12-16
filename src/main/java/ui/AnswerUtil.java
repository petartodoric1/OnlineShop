package ui;

import exceptions.InvalidDaNeException;
import exceptions.InvalidOdabirException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.Scanner;

public final class AnswerUtil {

    private static final Logger log = LoggerFactory.getLogger(AnswerUtil.class);

    private AnswerUtil() {}

    /**
     * Pita korisnika Da/Ne i vraća true za "Da", false za "Ne".
     */
    public static boolean DaNeOdgovor(Scanner sc, String prompt) {
        while (true) {
            System.out.println(prompt);
            String answer = sc.nextLine().trim();

            try {
                if (!answer.equalsIgnoreCase("Da") && !answer.equalsIgnoreCase("Ne")) {
                    throw new InvalidDaNeException("Unos mora biti Da ili Ne!");
                }
                return answer.equalsIgnoreCase("Da");
            } catch (InvalidDaNeException e) {
                System.out.println("Greška pri unosu -> " + e.getMessage());
                log.warn("Pogrešan unos 'Da/Ne': {}", answer);
            }
        }
    }

    /**
     * Čita int u rasponu [min, max].
     */
    public static int odabirOdgovor(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.println(prompt);
            try {
                int val = sc.nextInt();
                sc.nextLine(); // pojedi \n

                if (val < min || val > max) {
                    throw new InvalidOdabirException(
                            "Neispravan unos! Raspon je " + min + " - " + max + "."
                    );
                }
                return val;

            } catch (InvalidOdabirException e) {
                System.out.println("Greška pri unosu -> " + e.getMessage());
                log.warn("Neispravan odabir: {}", e.getMessage());
                sc.nextLine();

            } catch (InputMismatchException e) {
                System.out.println("Greška: Morate unijeti broj!");
                log.warn("Unesen string umjesto broja.");
                sc.nextLine(); // očisti buffer
            }
        }
    }


}
