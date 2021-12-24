
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hp
 */
public class RSA {

    static PrivateKey privateKey;
    static PublicKey publicKey;
    static byte[] encodedString;

    public static void test() {

        try {
            try {
                generateKeys();
            } catch (IOException ex) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
            }
            String secretMessage = "Baeldung secret message";

//            encodedString = encMessage(secretMessage, publicKey);
            decMessage(privateKey, encodedString);

            String k = publicKey2string();

            try {
                PublicKey hostpk = hostStrkey2PublicKey(k);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
            }
            File file = new File(".\\demo.txt");

            byte[] encryptedFile;
            try {
                encryptedFile = encFile(file, publicKey);
                System.out.println("encryptedFile " + encryptedFile);

                byte[] decryptedFile = decFile(file, privateKey);
                System.out.println("decryptedFile " + decryptedFile);

            } catch (IOException ex) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generateKeys() throws IOException, NoSuchAlgorithmException {
        //Generate RSA Key Pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        //The generated key will have a size of 2048 bits.
        generator.initialize(2048);  
        KeyPair pair = generator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        //Storing Keys in Files
        try (FileOutputStream fos = new FileOutputStream("public.key")) {
            fos.write(publicKey.getEncoded());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }

        File publicKeyFile = new File("public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        try {
            keyFactory.generatePublic(publicKeySpec);
      
    }   catch (InvalidKeySpecException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String encMessage(String secretMessage, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        System.out.println("encodedMessage: " + encodedMessage);
        return encodedMessage;

    }

    public static String decMessage(PrivateKey privateKey, byte[] encryptedMessageBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String decryptedMessage = null;
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

          //  System.out.println("decryptedMessage: " + decryptedMessage);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return decryptedMessage;
    }

    public static byte[] encFile(File file, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, FileNotFoundException, IOException {

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(encryptedFileBytes);
        }

        return encryptedFileBytes;

    }

    public static byte[] decFile(File file, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        //dec
        byte[] encryptedFileBytesFiles = Files.readAllBytes(file.toPath());
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytesFiles);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(decryptedFileBytes);
        }
        return decryptedFileBytes;

    }

    public static String publicKey2string() {

//converting public key to byte            
        byte[] byte_pubkey = publicKey.getEncoded();
     //   System.out.println("\nBYTE KEY::: " + byte_pubkey);

//converting byte to String 
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);

// String str_key = new String(byte_pubkey,Charset.);
     //   System.out.println("\nSTRING KEY::" + str_key);

        return str_key;

    }

    public static PublicKey hostStrkey2PublicKey(String host_strKey) throws InvalidKeySpecException {
        PublicKey public_key = null;
        try {
            //converting string to Bytes
            byte[] byte_pubkey = Base64.getDecoder().decode(host_strKey);
            //    System.out.println("BYTE KEY::" + byte_pubkey);

            //converting it back to public key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(byte_pubkey);
            public_key = keyFactory.generatePublic(publicKeySpec);
            //   System.out.println("FINAL OUTPUT" + public_key);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        //    System.out.println("public_key: "+ public_key.toString());
        return public_key;
    }
}
