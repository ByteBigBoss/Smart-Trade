package dto;

import entity.Product;
import java.io.Serializable;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class CartDTO implements Serializable{
       
    
    private Product product;
    private int qty;

    public CartDTO() {
    }
    

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

}
