package exceptions;

/**
 * Baca se kada korisnik unese neispravnu vrijednost (npr. string umjesto brojčane vrijednosti)
 */

public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
