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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession(); //GET SESSION
        Transaction transaction = session.beginTransaction(); //GET TRANSACTION
        Gson gson = new Gson();
        ResponseDTO resDTO = new ResponseDTO();

        try {
            //GET REQUEST PARAMETERS
            String id = req.getParameter("id");
            String qty = req.getParameter("qty");

            //VALIDATIONS
            if (!Validator.VALIDATE_INTEGER(id)) {
                //PRODUCT NOT FOUND
                resDTO.setContent("Product Not Found");

            } else if (!Validator.VALIDATE_INTEGER(qty)) {
                //INVALID QUANTITY
                resDTO.setContent("Invalid Quantity");
            } else {

                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {
                    //QUANTITY MUST BE GREATER THAN 0

                } else {
                    //QUANTITY IS GREATER THAN 0

                    Product product = (Product) session.load(Product.class, productId);

                    if (product != null) {
                        //PRODUCT FOUND

                        if (req.getSession().getAttribute("user") != null) {
                            //DB CART
                            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");//GET USER

                            //FIND USER IN DB
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));

                            //CAST USER
                            User user = (User) criteria1.uniqueResult();

                            //CHECK IN DB CART
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {
                                //ITEM NOT FOUND IN CART

                                if (productQty <= product.getQty()) {
                                    //ADD PRODUCT INTO CART

                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);

                                    session.save(cart);//SAVE TO RAM
                                    transaction.commit();//SAVE TO DB
                                    
                                    
                                    resDTO.setSuccess(true);
                                    resDTO.setContent("Cart Item Addes");
                                    

                                } else {
                                    //QUNITY NOT AVAILABLE

                                }

                            } else {
                                //ITEM ALREADY FOUND IN CART

                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                //ADD NEW QUANITY TO EXISTING CART ITEM & CHECK TOTATL QUANTIY EXISTS IN PRODUCT
                                if ((cartItem.getQty() + productQty) <= product.getQty()) {
                                    //QUANTITY AVAILABLE TO UPDATE
                                    cartItem.setQty(cartItem.getQty() + productQty);

                                    session.update(cartItem); //SAVE UPDATE IN RAM
                                    transaction.commit(); //UPDATE DB

                                } else {
                                    //CAN'T UPDATE YOUR CART. QUANTITY NOT AVAILABLE 
                                }

                            }

                        } else {
                            //SESSION CART

                            HttpSession httpSession = req.getSession();

                            if (httpSession.getAttribute("sessionCart") != null) {
                                //SESSION CART FOUND

                                ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                                CartDTO foundCartDTO = null;
                                for (CartDTO cartDTO : sessionCart) {
                                    if (cartDTO.getProduct().getId() == product.getId()) {
                                        foundCartDTO = cartDTO;
                                        break;
                                    }
                                }

                                if (foundCartDTO != null) {
                                    //PRODUCT FOUND

                                    if ((foundCartDTO.getQty() + productQty) <= product.getQty()) {
                                        //UPDATE QUANTITY
                                        foundCartDTO.setQty(foundCartDTO.getQty() + productQty);

                                    } else {
                                        //QUANTITY NOT AVAILABLE
                                    }

                                } else {
                                    //PRODUCT NOT FOUND

                                    if (productQty <= product.getQty()) {
                                        //ADD TO SESSION CART
                                        CartDTO cartDTO = new CartDTO();
                                        cartDTO.setProduct(product);
                                        cartDTO.setQty(productQty);
                                        sessionCart.add(cartDTO);

                                    } else {
                                        //QUANTITY NOT AVAILABLE

                                    }

                                }

                            } else {
                                //SESSION CART NOT FOUND
                                if (productQty <= product.getQty()) {
                                    //ADD TO SESSION CART
                                    ArrayList<CartDTO> sessionCart = new ArrayList<>();

                                    CartDTO cartDTO = new CartDTO();
                                    cartDTO.setProduct(null);
                                    cartDTO.setQty(0);

                                    req.getSession().setAttribute("sessionCart", sessionCart);
                                    
                                     resDTO.setSuccess(true);
                                    resDTO.setContent("Cart Item Addes");
                                } else {
                                    //QUANTITY NOT AVALIALE

                                }
                            }

                        }

                    } else {
                        //PRODUCT NOT FOUND
                        resDTO.setContent("Product not found");
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));
        session.close();

    }

}
