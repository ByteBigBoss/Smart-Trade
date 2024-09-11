package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Color;
import entity.Model;
import entity.Product;
import entity.Product_Condition;
import entity.Storage;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //GET REQUEST JSON
        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //SEARCH ALL PRODUCTS FROM DB
        Criteria criteria1 = session.createCriteria(Product.class);

        //ADD CATEGORY FILTER
        if (requestJsonObject.has("category_name")) {
            //CATEGORY SELECTED
            String category_name = requestJsonObject.get("category_name").getAsString();

            //GET CATEGORY LIST FROM DB
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("name", category_name));
            Category category = (Category) criteria2.uniqueResult();

            //GET MODEL LIST FROM DB
            Criteria criteria3 = session.createCriteria(Model.class);
            criteria3.add(Restrictions.eq("category", category));
            List<Model> modelList = criteria3.list();

            //FILTER PRODUCTS BY MODEL
            criteria1.add(Restrictions.in("model", modelList));
        }

        if (requestJsonObject.has("condition_name")) {
            //CONDITION SELECTED
            String condition_name = requestJsonObject.get("condition_name").getAsString();

            //GET CONDITION FROM DB
            Criteria criteria4 = session.createCriteria(Product_Condition.class);
            criteria4.add(Restrictions.eq("name", condition_name));
            Product_Condition product_Condition = (Product_Condition) criteria4.uniqueResult();

            //FILTER PRODUCTS BY CONDITION FROM DB
            criteria1.add(Restrictions.eq("product_condition", product_Condition));
        }

        if (requestJsonObject.has("color_name")) {
            //COLOR SELECTED
            String color_name = requestJsonObject.get("color_name").getAsString();

            Criteria criteria5 = session.createCriteria(Color.class);
            criteria5.add(Restrictions.eq("name", color_name));
            Color color = (Color) criteria5.uniqueResult();

            //FILTER PRODUCTS BY COLOR FROM DB
            criteria1.add(Restrictions.eq("color", color));
        }

        if (requestJsonObject.has("storage_value")) {
            //COLOR SELECTED
            String storage_value = requestJsonObject.get("storage_value").getAsString();

            Criteria criteria6 = session.createCriteria(Storage.class);
            criteria6.add(Restrictions.eq("value", storage_value));
            Storage storage = (Storage) criteria6.uniqueResult();

            //FILTER PRODUCTS BY STORAGE FROM DB
            criteria1.add(Restrictions.eq("storage", storage));
        }

        //FILTER PRODUCTS BY PRICE FROM DB
        double price_range_start = requestJsonObject.get("price_range_start").getAsDouble();
        double price_range_end = requestJsonObject.get("price_range_end").getAsDouble();

        criteria1.add(Restrictions.ge("price", price_range_start));//GREATER THAN EQUAL
        criteria1.add(Restrictions.le("price", price_range_end));//LESS THAN EQUAL
        
        
        //FILTER PRODUCTS BY SORTING OPTION FROM DB
        String sort_text = requestJsonObject.get("sort_text").getAsString();
        
        if(sort_text.equals("Sort by Latest")){
            criteria1.addOrder(Order.desc("id"));
            
        }else if(sort_text.equals("Sort by Oldest")){
            criteria1.addOrder(Order.asc("id"));
            
        }else if(sort_text.equals("Sort by Name")){
             criteria1.addOrder(Order.asc("title"));
             
        }else if(sort_text.equals("Sort by Price")){
            criteria1.addOrder(Order.asc("price"));
        }
        
        //GET ALL PRODUCT COUNT
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());
        
        //SET PRODUCT RANGE
        criteria1.setFirstResult(0);
        criteria1.setMaxResults(6);
       
        //GET PRODUCT LIST
        List<Product> productList = criteria1.list();
        
        //REMOVE USERS FROM PRODUCT
        for (Product product : productList) {
            product.setUser(null);
        }
        
        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));
        
        //SEND RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(responseJsonObject));

    }

}
