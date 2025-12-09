package entities;

import java.math.BigDecimal;

/**
 * Predstavlja jedan par cipela
 */
public class Cipele extends Item {

    private static final long serialVersionUID = 1L;


    private BigDecimal size;

    public Cipele(){}

    /**
     * Kreira jedan par cipela sa predanim parametrima
     * @param name
     * @param price
     * @param itemId
     * @param size
     */
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
