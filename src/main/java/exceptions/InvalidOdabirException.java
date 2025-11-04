package exceptions;

/**
 * Baca se kada korisnik unese neispravnu vrijednost kod odabira (npr. 4, a nude se samo 1 i 2)
 */
public class InvalidOdabirException extends RuntimeException {
    public InvalidOdabirException(String message) {
        super(message);
    }
}
