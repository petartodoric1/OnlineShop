package entities;

/**
 * Prestavlja sučelje Payed koje označava jednu narudžbu kao plaćenu
 */
public sealed interface Payed permits Booking {

    boolean isPayed();
    void markAsPayed();
}
