package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.City;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Gson gson = new Gson();

        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //HTTP SESSION
        HttpSession httpSession = req.getSession();
        Transaction transaction = session.beginTransaction();

        //SESSION
        //GET REQUEST PARAMETERS FROM JSON OBJECT
        boolean isCurrentAddress = requestJsonObject.get("isCurrentAddress").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (httpSession.getAttribute("user") != null) {
            //USER SIGNED IN

            //GET USER FROM DB
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isCurrentAddress) {
                //GET CURRENT ADDRESS
                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {
                    //CURRENT ADDRESS NOT FOUND | PLEASE CREATE A NEW ADDRESS
                    responseJsonObject.addProperty("message", "Current address not found. Please create a new address");
                } else {
                    //CURRENT ADDRESS FOUND
                    Address address = (Address) criteria2.list().get(0);

                    //*** COMPLETE THE CHECKOUT PROCESS ***//
                }

            } else {
                //CREATE NEW ADDRESS

                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill First Name");

                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill Last Name");

                } else if (!Validator.VALIDATE_INTEGER(city_id)) {
                    responseJsonObject.addProperty("message", "Invalid City");

                } else {

                    //CHEC CITY IN DB
                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Invalid City Selected");

                    } else {
                        //CITY FOUND
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address Line 1");

                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address Line 2");

                        } else if (postal_code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Postal Code");

                        } else if (postal_code.length() > 5) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");

                        } else if (!Validator.VALIDATE_INTEGER(postal_code)) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");

                        }else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Mobile");

                        }else if (!Validator.VALIDATE_MOBILE(mobile)) {
                            responseJsonObject.addProperty("message", "Invalid Mobile Number");

                        } else {
                            
                            //CREATE NEW ADDRESS
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setPostal_code(postal_code);
                            address.setMobile(mobile);
                            address.setUser(user);
                            
                            session.save(address);
                            //** COMPLETE THE CHECKOUT PROCESS ** //

                        }

                    }

                }

            }

        } else {
            //USER NOT SIGNED IN
            responseJsonObject.addProperty("message", "User not Signed In");

        }
        
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(responseJsonObject));

    }
    
    private void saveOrders(){
        
    }

}
