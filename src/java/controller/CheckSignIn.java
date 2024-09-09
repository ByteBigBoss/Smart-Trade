package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Product;
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
import util.HibernateUtil;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name="CheckSignIn", urlPatterns={"/CheckSignIn"})
public class CheckSignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        JsonObject jsonObject = new JsonObject();
        
        ResponseDTO resDTO = new ResponseDTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        if(req.getSession().getAttribute("user")!=null){
            //ALREADY SIGNED IN
            
            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
            resDTO.setSuccess(true);
            resDTO.setContent(userDTO);
            
             
        }else{
         //NOT SIGNED IN
            resDTO.setContent("Not Signed In");
        }
        
        jsonObject.add("response_dto", gson.toJsonTree(resDTO));
        
        //GET LAST 3 PRODUCTS
        Criteria criteria1 = session.createCriteria(Product.class);
        criteria1.addOrder(Order.desc("id"));
        criteria1.setMaxResults(3);
        List<Product> productList = criteria1.list();
        
        for (Product product : productList) {
            product.setUser(null);
        }
        
        Gson gson1 = new Gson();
        jsonObject.add("products", gson1.toJsonTree(productList));
        
        res.setContentType("application/json");
        res.getWriter().write(gson1.toJson(jsonObject));
        
    }

}
