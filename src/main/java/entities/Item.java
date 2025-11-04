package entities;

import java.math.BigDecimal;

/**
 * Predstavlja jedan proizvod
 * <p>Apstraktna klasa od koje ostatak proizvoda nasljeđuje atribute name,price,ItemId</p>
 */
public abstract class Item implements Sold{

    private String name;
    private BigDecimal price;
    private Integer itemId;
    private boolean sold= false;

    /**
     * Generira jedan item
     * @param name
     * @param price
     * @param itemId
     */
    public Item(String name, BigDecimal price, Integer itemId) {
        this.name = name;
        this.price = price;
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public abstract String getCategory();

    @Override
    public boolean isSold() {
        return sold;
    }
    @Override
    public void markAsSold() {
        this.sold = true;
    }

}

