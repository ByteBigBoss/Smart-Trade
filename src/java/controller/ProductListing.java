package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Category;
import entity.Color;
import entity.Model;
import entity.Product;
import entity.Product_Condition;
import entity.Product_Status;
import entity.Storage;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@MultipartConfig
@WebServlet(name = "ProductListing", urlPatterns = {"/ProductListing"})
public class ProductListing extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //RESPONSE OBJECT
        ResponseDTO resDTO = new ResponseDTO();

        //GSON OBJECT
        Gson gson = new Gson();

        //BASIC DETAILS
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String price = req.getParameter("price");
        String qty = req.getParameter("qty");

        //SELECTED ID'S
        String categoryId = req.getParameter("categoryId");
        String modelId = req.getParameter("modelId");
        String storageId = req.getParameter("storageId");
        String colorId = req.getParameter("colorId");
        String conditionId = req.getParameter("conditionId");

        //PRODUCT IMAGES
        Part img1 = req.getPart("img1");
        Part img2 = req.getPart("img2");
        Part img3 = req.getPart("img3");

        //OPEN SESSION
        Session session = HibernateUtil.getSessionFactory().openSession();

        //VALIDATE FIELDS
        if (!Validator.VALIDATE_INTEGER(categoryId)) {
            resDTO.setContent("Invalid Category");

        } else if (!Validator.VALIDATE_INTEGER(modelId)) {
            resDTO.setContent("Invalid Model");

        } else if (title.isEmpty()) {
            resDTO.setContent("Please fill Title");

        } else if (description.isEmpty()) {
            resDTO.setContent("Please fill Description");

        } else if (!Validator.VALIDATE_INTEGER(storageId)) {
            resDTO.setContent("Invalid Storage");

        } else if (!Validator.VALIDATE_INTEGER(colorId)) {
            resDTO.setContent("Invalid Color");

        } else if (!Validator.VALIDATE_INTEGER(conditionId)) {
            resDTO.setContent("Invalid Condition");

        } else if (price.isEmpty()) {
            resDTO.setContent("Please fill Price");

        } else if (!Validator.VALIDATE_DOUBLE(price)) {
            resDTO.setContent("Invalid price");

        } else if (Double.parseDouble(price) <= 0) {
            resDTO.setContent("Price must be greater than 0");

        } else if (qty.isEmpty()) {
            resDTO.setContent("Please fill Quantity");

        } else if (!Validator.VALIDATE_INTEGER(qty)) {
            resDTO.setContent("Invalid Quantity");

        } else if (Integer.parseInt(qty) <= 0) {
            resDTO.setContent("Quantity must be greater than 0");

        } else if (img1.getSubmittedFileName() == null) {
            resDTO.setContent("Please upload Image 1");

        } else if (img2.getSubmittedFileName() == null) {
            resDTO.setContent("Please upload Image 2");

        } else if (img3.getSubmittedFileName() == null) {
            resDTO.setContent("Please upload Image 3");

        } else {

            //FIND CATEGORY BY REQUESTED CATEGORY ID
            Category category = (Category) session.load(Category.class, Integer.parseInt(categoryId)); //IF session.load() NOT WORKS THEN USE session.get()
            if (category == null) {
                //CATEGORY NOT IN DB
                resDTO.setContent("Please select a valid Category");

            } else {

                //FIND MODEL BY REQUESTED MODEL ID
                Model model = (Model) session.load(Model.class, Integer.parseInt(modelId));
                if (model == null) {
                    //MODEL NOT IN DB
                    resDTO.setContent("Please select a valid Model");

                } else {

                    //COMPARE IS REQUESTED MODEL IN CATEGORY
                    if (model.getCategory().getId() != category.getId()) {
                        //COMPARE FAILD
                        resDTO.setContent("Please select a valid Model");

                    } else {

                        //FIND STORAGE BY REQUESTED STORAGE ID
                        Storage storage = (Storage) session.load(Storage.class, Integer.parseInt(storageId));
                        if (storage == null) {
                            //STORAGE NOT IN DB
                            resDTO.setContent("Please select a valid Storage");

                        } else {

                            //FIND COLOR BY REQUESTED COLOR ID
                            Color color = (Color) session.load(Color.class, Integer.parseInt(colorId));
                            if (color == null) {
                                //COLOR NOT IN DB
                                resDTO.setContent("Please select a valid Color");

                            } else {

                                //FIND PRODUCT CONDITION BY REQUESTED CONDITION ID
                                Product_Condition product_Condition = (Product_Condition) session.load(Product_Condition.class, Integer.parseInt(conditionId));
                                if (product_Condition == null) {
                                    //CONDITION NOT IN DB
                                    resDTO.setContent("Please select a valid Condition");

                                } else {

                                    //====> ALL VALIDATED [0]=> [28::VALIDATES] <====//
                                    Product product = new Product();
                                    //FIELDS
                                    product.setTitle(title);
                                    product.setDescription(description);
                                    product.setPrice(Double.parseDouble(price));
                                    product.setQty(Integer.parseInt(qty));
                                    product.setDate_time(new Date());
                                    //JOINS
                                    product.setProduct_Condition(product_Condition);
                                    product.setModel(model);
                                    product.setStorage(storage);
                                    product.setColor(color);

                                    //APPROVED PRODUCT BY ADMIN == Active != Inactive
                                    //===> GET ACTIVE STATUS <===//
                                    Product_Status product_Status = (Product_Status) session.load(Product_Status.class, 1);
                                    product.setProduct_status(product_Status);

                                    //*******************************************//
                                    //GET SESSION USER
                                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
                                    //SEARCH SESSION USER IN DB
                                    Criteria criteria1 = session.createCriteria(User.class);
                                    criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
                                    //CAST USER FROM SEARCH
                                    User user = (User) criteria1.uniqueResult();
                                    product.setUser(user);
                                    //*******************************************//

                                    //SAVE NEW PRODUCT TO RAM
                                    int pid = (int) session.save(product); // RETURN AUTO INCREMENT ID AS SERIALIZABLE ==> CAST INTO INT

                                    //ADD PRODUCT TO DATABASE
                                    session.beginTransaction().commit();

                                    //APPLICATION PATH
                                    String applicationPath = req.getServletContext().getRealPath("");
                                    String newAplicationPath = applicationPath.replace("build"+File.separator+"web", "web");

                                    //PRODUCT FOLDER
                                    File productFolder = new File(newAplicationPath, "//product-images//" + pid);
                                    productFolder.mkdir();

                                    //FILE 1
                                    File file1 = new File(productFolder, "image1.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream1 = img1.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //FILE 2
                                    File file2 = new File(productFolder, "image2.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream2 = img2.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //FILE 3
                                    File file3 = new File(productFolder, "image3.png");// CREATE NEW FILE (IMAGE)
                                    InputStream inputStream3 = img3.getInputStream(); //IMAGE 1 STREAM
                                    Files.copy(inputStream3, file3.toPath(), StandardCopyOption.REPLACE_EXISTING); // COPY IMAGE

                                    //PRODUCT LISTING COMPLETE
                                    resDTO.setSuccess(true);
                                    resDTO.setContent("New Product Added");
                                }

                            }
                        }
                    }

                }

            }

        }

        //RETURN RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

        //END SESSION
        session.close();

    }

}
