package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //RESPONSE OBJECT
        ResponseDTO resDTO = new ResponseDTO();

        //GSON BUILDER
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //MAP REQUEST PARAMETER TO USER_DTO CLASS
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        //VALIDATE REQUEST PARAMETERS
        if (userDTO.getEmail().isEmpty()) {
            //EMAIL IS EMPTY
            resDTO.setContent("Please enter your Email");

        } else if (!Validator.VALIDATE_EMAIL(userDTO.getEmail())) {
            //EMAIL IS NOT VALID
            resDTO.setContent("Please enter a valid Email");

        } else if (userDTO.getPassword().isEmpty()) {
            //PASSWORD IS EMPTY
            resDTO.setContent("Please enter your Password");

        } else if (!Validator.VALIDATE_PASSWORD(userDTO.getPassword())) {
            //PASSWORD NOT VALID
            resDTO.setContent("Password must include at least one uppercase letter, number, "
                    + "special character, and be at least 8 characters long");

        } else {

            //FIND SESSION USER
            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            criteria1.add(Restrictions.eq("password", userDTO.getPassword()));
            
            if(!criteria1.list().isEmpty()){
                
                User user = (User) criteria1.list().get(0);
                
                if(!user.getVerification().equals("Verified")){
                    //NOT VERIFIED
                    req.getSession().setAttribute("email", userDTO.getEmail());
                    resDTO.setContent("Unverified");
                    
                }else{
                    //VERIVIED
                    userDTO.setFirst_name(user.getFirst_name());
                    userDTO.setLast_name(user.getLast_name());
                    userDTO.setPassword(null);
                    req.getSession().setAttribute("user", userDTO);
                    
                    resDTO.setSuccess(true);
                    resDTO.setContent("Sign In Success");
                    
                }
                
            }else{
                resDTO.setContent("Invalid details! Please try again");
            }

        }
        
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

    }

}
