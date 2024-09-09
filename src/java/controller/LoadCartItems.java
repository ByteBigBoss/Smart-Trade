package controller;

import com.google.gson.Gson;
import dto.CartDTO;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        HttpSession httpSession = req.getSession();

        Gson gson = new Gson();

        ArrayList<CartDTO> Cart_DTO_List = new ArrayList<>();

        try {

            if (httpSession.getAttribute("user") != null) {
                //USER FOUND => DB CART
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user"); //CAST ATTRIBUTE TO USER DTO 

                //FIND USER IN DB
                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", userDTO.getEmail()));

                //CAST USER
                User user = (User) criteria1.uniqueResult();

                //DB SEARCH
                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));

                //ITEM LIST
                List<Cart> cartList = criteria2.list();

                for (Cart cart : cartList) {

                    CartDTO cartDTO = new CartDTO();

                    Product product = cart.getProduct();
                    product.setUser(null);
                    cartDTO.setProduct(product);

                    cartDTO.setQty(cart.getQty());

                    Cart_DTO_List.add(cartDTO);

                }

            } else {
                //USER NOT FOUND => SESSION CART

                if (httpSession.getAttribute("sessionCart") != null) {
                    //SESSION CART FOUND

                    Cart_DTO_List = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");//GET SESSION CART & CAST TO CART DTO

                    //REMOVE USER OBJ
                    for (CartDTO cartDTO : Cart_DTO_List) {
                        cartDTO.getProduct().setUser(null);
                    }

                } else {
                    //CART IS EMPTY

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        session.close();
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(Cart_DTO_List));

    }

}
