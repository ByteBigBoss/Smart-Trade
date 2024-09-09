package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import entity.Model;
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
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            String productId = req.getParameter("id");

            if (Validator.VALIDATE_INTEGER(productId)) {

                Product product = (Product) session.get(Product.class, Integer.parseInt(productId));
                product.getUser().setPassword(null);
                product.getUser().setVerification(null);
                product.getUser().setEmail(null);

                Criteria criteria1 = session.createCriteria(Model.class);
                criteria1.add(Restrictions.eq("category", product.getModel().getCategory()));
                List<Model> modelList = criteria1.list();

                Criteria criteria2 = session.createCriteria(Product.class);
                criteria2.add(Restrictions.in("model", modelList));
                criteria2.add(Restrictions.ne("id", product.getId()));
                criteria2.setMaxResults(6);

                List<Product> productList = criteria2.list();
                for (Product product1 : productList) {
                    product1.getUser().setPassword(null);
                    product1.getUser().setVerification(null);
                    product1.getUser().setEmail(null);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("product", gson.toJsonTree(product));
                jsonObject.add("productList", gson.toJsonTree(productList));

                res.setContentType("application/json");
                res.getWriter().write(gson.toJson(jsonObject));

            } 

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
