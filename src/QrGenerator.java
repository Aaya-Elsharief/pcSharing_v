/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author hp
 */
public class QrGenerator {

    static void generate(String qrString) throws UnsupportedEncodingException, IOException {

        try {
            String QrCodeData = qrString;
            String filePath = ".\\Qr.png";
            String charset = "UTF-8";
            
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix matrix = new MultiFormatWriter().encode(
                    new String(QrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, 200, 200, hintMap);
            
             MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.')+1),new File(filePath));
        System.out.println("Qr code has been generated at the location "+filePath);
            
        ImageIcon icon = new ImageIcon(".\\Qr.png");
        Frame.jLQRcode.setIcon(icon);
            
            
            
            
            
            
            
            
            
            
            
        } catch (WriterException ex) {
            Logger.getLogger(QrGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
