/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 * @author hp
 */
public class Sec {

    static PrivateKey privateKey;
    static PublicKey publicKey;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        try {
            // TODO code application logic here

            generateKeys();

            //Working With Strings
            String secretMessage = "Baeldung secret message";

            try {

                byte[] aswd = encMessage(secretMessage, publicKey);
                decMessage(privateKey, aswd);
                //  assertEquals(secretMessage, decryptedMessage);

            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
            }

            //working with file

            File file = new File(".\\demo.txt");
            //  Path tempFile

            byte[] encryptedFile = encFile(file, publicKey);
            System.out.println("encryptedFile " + encryptedFile);
            
            byte[] decryptedFile = decFile(file, privateKey);
            System.out.println("decryptedFile " + decryptedFile);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void decMessage(PrivateKey privateKey, byte[] encryptedMessageBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

            System.out.println("decryptedMessage: " + decryptedMessage);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static byte[] encMessage(String secretMessage, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        System.out.println("encodedMessage: " + encodedMessage);
        return encryptedMessageBytes;

    }

    private static byte[] encFile(File file, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, FileNotFoundException, IOException {

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(encryptedFileBytes);
        }

        return encryptedFileBytes;

    }

    private static byte[] decFile(File file, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
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

    private static void generateKeys() throws IOException, NoSuchAlgorithmException {
        //Generate RSA Key Pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);  //The generated key will have a size of 2048 bits.
        KeyPair pair = generator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        // System.out.println("publicKey " +publicKey.toString());
        //Storing Keys in Files
        try (FileOutputStream fos = new FileOutputStream("public.key")) {
            fos.write(publicKey.getEncoded());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
        }

        File publicKeyFile = new File("public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        try {
            keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Sec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
