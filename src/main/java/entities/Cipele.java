package entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cipele extends Item {


    private BigDecimal size;

    public Cipele(String name, BigDecimal price, Integer itemId, BigDecimal size) {
        super(name, price, itemId);
        this.size = size;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    @Override
    public String getCategory(){
        return "Cipele";
    }




}
