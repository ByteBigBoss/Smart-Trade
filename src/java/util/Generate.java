package util;

import java.util.UUID;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class Generate {
    
    public static int RANDOM_VERIFICATION_CODE(){
//        return UUID.randomUUID().toString();
        return (int) (Math.random() * 1000000);
    }

}
