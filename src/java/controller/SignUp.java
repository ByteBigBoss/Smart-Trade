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
import util.Generate;
import util.HibernateUtil;
import util.Mail;
import util.Validator;

/**
 *
 * @author ByteBigBoss
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //RESPONSE OBJECT
        ResponseDTO resDTO = new ResponseDTO();

        //GSON BUILDER: => EXCLUDE EXPOSE ANNOTATED FIELD'S FROM SERIALIZATION (RESPONSE)
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //MAP REQUEST PARAMETER TO USER_DTO CLASS
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        //VALIDATE PARAMETERS
        if (userDTO.getFirst_name().isEmpty()) {
            //FIRST NAME IS EMPTY
            resDTO.setContent("Please enter your First Name");

        } else if (userDTO.getLast_name().isEmpty()) {
            //LAST NAME IS EMPTY
            resDTO.setContent("Please enter your Last Name");

        } else if (userDTO.getEmail().isEmpty()) {
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

            //OPEN SESSION
            Session session = HibernateUtil.getSessionFactory().openSession();

            //CHECK USER EXIST OR NOT
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));

            if (!criteria1.list().isEmpty()) {
                resDTO.setContent("User with this Email already exists");
            } else {

                //GENERATE VERIFICATION CODE
                int code = Generate.RANDOM_VERIFICATION_CODE();

                //SET USER PROPERTIES
                final User user = new User();
                user.setEmail(userDTO.getEmail());
                user.setFirst_name(userDTO.getFirst_name());
                user.setLast_name(userDTO.getLast_name());
                user.setPassword(userDTO.getPassword());
                user.setVerification(String.valueOf(code));

                //SEND VERIFICATION EMAIL
                Thread sendMailThread = new Thread() {
                    @Override
                    public void run() {
                        Mail.sendMail(user.getEmail(), "Smart Trade Verification",
                                "<h1 style=\"color:#6482AD;\">Your Verificaition Code: " + user.getVerification() + "</h1>"
                        );
                    }

                };
                sendMailThread.start();

                //SAVE NEW USER TO RAM
                session.save(user);

                //ADD USER TO DATABASE
                session.beginTransaction().commit();

                //REGISTRATION COMPLETE
                resDTO.setSuccess(true);
                resDTO.setContent("Registration Complete. Please check your inbox for Verification Code!");

            }

            //END SESSION
            session.close();

        }

        //RETURN RESPONSE
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(resDTO));

    }

}
