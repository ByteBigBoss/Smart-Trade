package util;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class Validator {

    public static boolean VALIDATE_EMAIL(String email){
     return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }
    
    public static boolean VALIDATE_PASSWORD (String password){
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }
    
}
