package entities;

import java.math.BigDecimal;
import java.util.List;

/**
 * Predstavlja narudžbu korisnika koja sadrži artikle, količine i status plaćanja.
 * Svaka narudžba ima jedinstveni ID te može biti označena kao plaćena.
 */
public final class Booking implements Payed {

    private User user;
    private List<Item> items;
    private List<Integer> quantity;
    private Integer bookingId;
    private boolean isPayed=false;

    /**
     *Kreira novu narudžbu sa predanim parametrima
     * @param user
     * @param items
     * @param quantity
     * @param bookingId
     */
    public Booking(User user, List<Item> items, List<Integer> quantity, Integer bookingId) {
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }


    /**
     * Računa ukupnu cijenu narudžbe
     * @return Vraća sveukupnu cijenu te narudžbe
     */

    public BigDecimal getTotalPrice() {
        BigDecimal sum = BigDecimal.ZERO;

        for(Integer i=0; i<items.size(); i++) {

            if(items.isEmpty() || quantity.isEmpty()) {
                break;
            }
            Item item = items.get(i);
            Integer qty = quantity.get(i);
            sum = sum.add(item.getPrice().multiply(BigDecimal.valueOf(qty)));
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
