package entities;

import java.math.BigDecimal;

/**
 * Predstavlja narudžbu korisnika koja sadrži artikle, količine i status plaćanja.
 * Svaka narudžba ima jedinstveni ID te može biti označena kao plaćena.
 */
public final class Booking implements Payed {

    private User user;
    private Item[] items;
    private Integer[] quantity;
    private Integer bookingId;
    private boolean isPayed=false;

    /**
     *Kreira novu narudžbu sa predanim parametrima
     * @param user
     * @param items
     * @param quantity
     * @param bookingId
     */
    public Booking(User user, Item[] items, Integer[] quantity, Integer bookingId) {
        this.user = user;
        this.items = items;
        this.quantity = quantity;
        this.bookingId = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public Integer[] getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer[] quantity) {
        this.quantity = quantity;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;

        for(Integer i=0; i<items.length; i++) {

            if(items[i]== null || quantity[i] == null) {
                break;
            }
            sum = sum.add(items[i].getPrice().multiply(new BigDecimal(quantity[i])));
        }
        return sum;
    }

    @Override
    public boolean isPayed(){
        return isPayed;
    }

    @Override
    public void markAsPayed() {
        this.isPayed = true;
    }
}
