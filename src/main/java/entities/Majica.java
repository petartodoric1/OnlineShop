package entities;

import java.math.BigDecimal;

/**
 * Predstavlja jednu Majicu
 */
public class Majica extends Item {

    private String color;
    private String size;

    public Majica(){}

    /**
     * Kreira jednu majicu sa predanim parametrima
     * @param name
     * @param price
     * @param itemId
     * @param color
     * @param size
     */
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
