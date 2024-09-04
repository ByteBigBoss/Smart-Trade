package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name="Verification", urlPatterns={"/Verification"})
public class Verification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        ResponseDTO resDTO = new ResponseDTO();
        
        Gson gson = new Gson();
        JsonObject dto = gson.fromJson(req.getReader(), JsonObject.class);
        String verification = dto.get("verification").getAsString();

        if(req.getSession().getAttribute("email") != null){
            
            String email = req.getSession().getAttribute("email").toString();
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", email));
            criteria1.add(Restrictions.eq("verification", verification));
            
            if(!criteria1.list().isEmpty()){
                
                User user = (User) criteria1.list().get(0);
                user.setVerification("Verified");
                
                session.update(user);
                session.beginTransaction().commit();
                
                UserDTO userDTO = new UserDTO();
                userDTO.setFirst_name(user.getFirst_name());
                userDTO.setLast_name(user.getLast_name());
                userDTO.setEmail(email);
                req.getSession().removeAttribute("email");
                req.getSession().setAttribute("user", userDTO);
                
                
                resDTO.setSuccess(true);
                resDTO.setContent("Verification Success");
               
            }else{
                //INVALID VERIFICATION CODE
                resDTO.setContent("Invalid Verification Code");
                
            }
            
        }else{
            
            resDTO.setContent("Verification unavailable! Please Login");
            
        }
        
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));
   
    }

}
