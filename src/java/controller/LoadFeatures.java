package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import entity.Category;
import entity.Color;
import entity.Product_Condition;
import entity.Model;
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
import util.HibernateUtil;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadFeatures", urlPatterns = {"/LoadFeatures"})
public class LoadFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //GSON OBJECT
        Gson gson = new Gson();

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        //====> CATEGORY <====//
        //SELECT ALL CATEGORIES
        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list(); //CATEGORY LIST

        //====> MODEL <====//
        //SELECT ALL MODELS
        Criteria criteria2 = session.createCriteria(Model.class);
        criteria2.addOrder(Order.asc("name"));
        List<Model> modelList = criteria2.list(); //MODEL LIST

        //====> COLOR <====//
        //SELECT ALL COLORS
        Criteria criteria3 = session.createCriteria(Color.class);
        criteria3.addOrder(Order.asc("name"));
        List<Color> colorList = criteria3.list(); //COLOR LIST

        //====> STORAGE <====//
        //SELECT ALL STORAGES
        Criteria criteria4 = session.createCriteria(Storage.class);
        criteria4.addOrder(Order.asc("id"));
        List<Storage> storageList = criteria4.list(); //STORAGE LIST

        //====> CONDITION <====//
        //SELECT ALL CONDITIONS
        Criteria criteria5 = session.createCriteria(Product_Condition.class);
        criteria5.addOrder(Order.asc("name"));
        List<Product_Condition> conditionList = criteria5.list(); //CONDITION LIST
        
        //JSON OBJECT {[],[],[],[],[]}
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("modelList", gson.toJsonTree(modelList));
        jsonObject.add("colorList", gson.toJsonTree(colorList));
        jsonObject.add("storageList", gson.toJsonTree(storageList));
        jsonObject.add("conditionList", gson.toJsonTree(conditionList));
        
        
        //SEND RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(jsonObject));
        
        //SESSION CLOSE
        session.close();
        
    }

}
