package entities;

import java.math.BigDecimal;

public class Item {

    private String name;
    private BigDecimal price;
    private Integer itemId;


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
}

