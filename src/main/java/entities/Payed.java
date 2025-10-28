package entities;

public sealed interface Payed permits Booking {

    boolean isPayed();
    void markAsPayed();
}
