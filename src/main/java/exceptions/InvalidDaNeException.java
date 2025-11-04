package exceptions;

/**
 * Baca se kada korisnik unese krivi odgovor na Da/Ne pitanje
 */
public class InvalidDaNeException extends RuntimeException {
    public InvalidDaNeException(String message) {
        super(message);
    }
}
