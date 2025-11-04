package entities;

/**
 * Označava proizvod kao prodan ukoliko ga korisnik doda u svoju narudžbu
 */
public interface Sold{

    boolean isSold();
    void markAsSold();
}
