package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "date_time", nullable = false)
    private Date date_time;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Product_Condition product_condition;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_status_id")
    private Product_Status product_status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public Product_Condition getProduct_Condition() {
        return product_condition;
    }

    public void setProduct_Condition(Product_Condition product_Condition) {
        this.product_condition = product_Condition;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product_Status getProduct_status() {
        return product_status;
    }

    public void setProduct_status(Product_Status product_status) {
        this.product_status = product_status;
    }

}
