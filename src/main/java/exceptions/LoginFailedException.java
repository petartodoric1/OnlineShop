package exceptions;
/**
* Baca se kada korisnik previše puta unese krivi password za prijavu
 */
public class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }
}
