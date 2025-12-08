package entities;

import java.math.BigDecimal;

/**
 * Predstavlja proizvod Hlače
 */
public class Hlace extends Item {

    private String color;
    private String size;
    private String type;

    public Hlace(){}

    /**
     * Kreira proizvod Hlače sa predanim parametrima
     * @param name
     * @param price
     * @param itemId
     * @param color
     * @param size
     * @param type
     */
    public Hlace(String name, BigDecimal price, Integer itemId, String color, String size, String type){
        super(name, price, itemId);
        this.color = color;
        this.size = size;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCategory() {
        return "Hlače";
    }


}
