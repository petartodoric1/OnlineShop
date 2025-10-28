package entities;

import java.math.BigDecimal;

public class Majica extends Item {

    private String color;
    private String size;

    public Majica(String name, BigDecimal price, Integer itemId, String color, String size) {
        super(name,price,itemId);
        this.color = color;
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getCategory() {
        return "Majica";
    }

}
