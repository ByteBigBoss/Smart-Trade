package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadCheckout", urlPatterns = {"/LoadCheckout"})
public class LoadCheckout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Gson gson = new Gson();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        HttpSession httpSession = req.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (httpSession.getAttribute("user") != null) {
            
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

            //GET USER FROM DB
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria1.uniqueResult();
            
            //GET USER'S LAST ADDRESS FROM DB
            Criteria criteria2 = session.createCriteria(Address.class);
            criteria2.add(Restrictions.eq("user", user));
            criteria2.addOrder(Order.desc("id"));
            criteria2.setMaxResults(1);
            Address address = (Address) criteria2.list().get(0);
            
            //GET ALL CITIES FROM DB
            Criteria criteria3 = session.createCriteria(City.class);
            criteria3.addOrder(Order.asc("name"));
            List<City> cityList = criteria3.list();
            
            //GET CART ITEMS FROM DB
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList =  criteria4.list();
            
            //PACK ADDRESS IN JSON OBJECT
            address.setUser(null);
            jsonObject.add("address", gson.toJsonTree(address));
            
            //PACK CITIES IN JSON OBJECT
            jsonObject.add("cityLit", gson.toJsonTree(cityList));
            
            //PACK CART ITEMSS IN JSON OBJECT
            for (Cart cart : cartList) {
                cart.setUser(null);
                cart.getProduct().setUser(null);
            }
            jsonObject.add("cartList", gson.toJsonTree(cartList));
            
            jsonObject.addProperty("success", true);
            
        } else {
            //NOT SIGNED IN
            jsonObject.addProperty("message", "Not signed in");
        }
        
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));
        session.close();

    }

}
