import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureGenerator {
    public static void main(String[] args) throws Exception {

        String mac = "28:56:2F:4A:87:6C";
        long ts = System.currentTimeMillis();      
        String secret = "f09a641e6ecc4539cd6dd2d255801de5de5e7994e7e0a8c131aa9afd5ef21749";
        

        String payload = mac + ":" + ts;
        
   
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);


        String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(payload.getBytes()));
        
 
        System.out.println("--------------------------------------");
        System.out.println("Copy these to your Postman Headers:");     
        System.out.println("--------------------------------------");   
        System.out.println("X-Device-Mac: " + mac);
        System.out.println("X-Device-Timestamp: " + ts);
        System.out.println("X-Device-Signature: " + hash);
        System.out.println("--------------------------------------");
        System.out.println("!!! Remember: Send request within 30 seconds !!!");
    }
}