
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hp
 */
public class Frame extends javax.swing.JFrame {

    static Socket socket;
    static ServerSocket serverSocket;
    static InputStream stream;
    static InputStreamReader inputStreamReader;
    static DataInputStream dataInputStream;
    static BufferedReader bufferedReader;
    static String message;
    final File[] fileToSend = new File[1];   // static File file;
    static ArrayList<MyFile> myFiles = new ArrayList<>(); // Array list to hold information about the files received.
    static int fileId = 0;
    static JList list = new JList();
    static ArrayList<DeviceInfo> deviceInfoList = new ArrayList<>();
    static int originalSize = deviceInfoList.size();
    static String deviceIP;
    static int devicePort;
    static PublicKey devicePublicKey;
    static String myPort;
    static PublicKey myPublicKey;
    static String myPublicKeyStr;

    static RSA rsa = new RSA();


    /*   public static void SQLiteDB() {
        Connection connection = null;

        try {
            // Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/D:\\sqlite\\sqlite-tools-win32-x86-3370000\\sqliteDB");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }*/
    public static MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the source of the click which is the JPanel.
                JPanel jPanel = (JPanel) e.getSource();
                // Get the ID of the file.
                int fileId = Integer.parseInt(jPanel.getName());
                ///////////////  System.out.println("fileId = " + fileId);
                // Loop through the file storage and see which file is the selected one.
                /////////////// System.out.println(myFiles);
                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {

                        JFrame jFrame = new JFrame();
                        int result = JOptionPane.showConfirmDialog(jFrame, "Are you sure you want to download:  " + myFile.getName() + "?");

                        if (result == 1) {
                            System.out.println("You pressed NO");

                        } else if (result != 0) {
                            System.out.println("You pressed Cancel");

                        } else {
                            System.out.println("You pressed Yes");
                            System.out.println(myFile.getData());
                            downloadRecivedFile(myFile.getName(), myFile.getData());
                        }

                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        };
    }

    private static String getFileExtension(String fileName) {
        // Get the file type by using the last occurence of . (for example aboutMe.txt returns txt).
        // Will have issues with files like myFile.tar.gz.
        int i = fileName.lastIndexOf('.');
        // System.out.println("indexxxxxxxx "+ i);
        // If there is an extension.
        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }

    }

    /*
    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
        // Frame to hold everything.
        JFrame jFrame = new JFrame("File Downloader");

        ImageIcon img = new ImageIcon("..\\DataTransfare\\src\\DataTransfareImages\\line0.png");
        jFrame.setIconImage(img.getImage());

        // Set the size of the frame.
        jFrame.setSize(600, 600);
        jFrame.setLocationRelativeTo(null);  // *** this will center your app ***

        // Panel to hold everything.
        JPanel jPanel = new JPanel();
        jPanel.setBackground(new java.awt.Color(126, 52, 126));

        // Make the layout a box layout with child elements stacked on top of each other.
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Title above panel.
        JLabel jlTitle = new JLabel("LINE File Downloader");
        jlTitle.setForeground(new java.awt.Color(204, 204, 204));
        // Center the label title horizontally.
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Change the font family, size, and style.
        jlTitle.setFont(new Font("Serif", Font.BOLD, 25));
        // Add spacing on the top and bottom of the element.
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Label to prompt the user if they are sure they want to download the file.
        JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
        // Change the font style, size, and family of the label.
        jlPrompt.setFont(new Font("Serif", Font.BOLD, 20));
        // Add spacing on the top and bottom of the label.
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Center the label horizontally.
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        //  jlPrompt.setForeground(new java.awt.Color(255, 255, 255));
        jlPrompt.setForeground(new java.awt.Color(51, 0, 51));
        // Create the yes for accepting the download.
        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbYes.setFont(new Font("Serif", Font.BOLD, 20));
        jbYes.setForeground(new java.awt.Color(51, 0, 51));

        // No button for rejecting the download.
        JButton jbNo = new JButton("No");
        // Change the size of the button must be preferred because if not the layout will ignore it.
        jbNo.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbNo.setFont(new Font("Serif", Font.BOLD, 20));
        jbYes.setForeground(new java.awt.Color(51, 0, 51));

        // Label to hold the content of the file whether it be text of images.
        JLabel jlFileContent = new JLabel();
        // Align the label horizontally.
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel to hold the yes and no buttons and make the next to each other left and right.
        JPanel jpButtons = new JPanel();
        jpButtons.setBackground(new java.awt.Color(255, 255, 255));
        // Add spacing around the panel.
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Add the yes and no buttons.
        jpButtons.add(jlPrompt);
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        // Yes so download file.
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create the file with its name.
                File f = new File(".\\Inbox");
                if (!(f.exists() && !f.isDirectory())) {
                    new File(".\\Inbox").mkdirs();
                }
                File fileToDownload = new File(".\\Inbox\\" + fileName);
                ////////////////////  System.out.println("PAAATH" + fileToDownload.getAbsolutePath());
                //     Desktop.getDesktop().open(new File("..\\DataTransfare\\Inbox"));
                try {
                    // Create a stream to write data to the file.
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    // Write the actual file data to the file.
                    fileOutputStream.write(fileData);

                    //  fileToDownload.
                    // Close the stream.
                    fileOutputStream.close();
                    // Get rid of the jFrame. after the user clicked yes.
                    jFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // No so close window.
        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // User clicked no so don't download the file but close the jframe.
                jFrame.dispose();
            }
        });

        // Add everything to the panel before adding to the frame.
        jPanel.add(jlTitle);
        //  jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        // Add panel to the frame.
        jFrame.add(jPanel);

        // Return the jFrame so it can be passed the right data and then shown.
        return jFrame;

    }

     */ private static void readReceivedFile(DataInputStream dataInputStream) {

        try {

            // Read the size of the file name so know when to stop reading.
            int fileNameLength = dataInputStream.readInt();

            // If the file exists
            if (fileNameLength > 0) {
                //    System.out.println("fileNameLength" +fileNameLength);
                // Byte array to hold name of file.
                byte[] fileNameBytes = new byte[fileNameLength];
                // Read from the input stream into the byte array.
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                // Create the file name from the byte array.
                String fileName = new String(fileNameBytes);
                //    System.out.println("fileName " + fileName);
                // Read how much data to expect for the actual content of the file.
                int fileContentLength = dataInputStream.readInt();
                //    System.out.println("fileContentLength "+fileContentLength);
                // If the file exists.
                if (fileContentLength >= 0) {
                    // Array to hold the file data.
                    byte[] fileContentBytes = new byte[fileContentLength];
                    //      System.out.println("fileContentBytes " +fileContentBytes);
                    // Read from the input stream into the fileContentBytes array.
                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                    // Make it scrollable when the data gets in jpaneln.

                    // Panel to hold the picture and file name.
                    JPanel jPanelFileRow = new JPanel();
                    jPanelFileRow.setLayout(new BoxLayout(jPanelFileRow, BoxLayout.X_AXIS));
                    jPanelFileRow.setBackground(new java.awt.Color(255, 255, 255));

                    // Set the file name.
                    JLabel jlFileName = new JLabel(fileName);
                    jlFileName.setFont(new Font("Seirf", Font.BOLD, 20));
                    jlFileName.setForeground(new java.awt.Color(51, 0, 51));
                    jlFileName.setBackground(new java.awt.Color(255, 255, 255));
                    jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));

                    if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                        // Set the name to be the fileId so you can get the correct file from the panel.
                        jPanelFileRow.setName((String.valueOf(fileId)));
                        jPanelFileRow.addMouseListener(getMyMouseListener());
                        // Add everything.
                        jPanelFileRow.add(jlFileName);
                        jPaneln.add(jPanelFileRow);
                        jPaneln.validate();
                    } else {
                        // Set the name to be the fileId so you can get the correct file from the panel.
                        jPanelFileRow.setName((String.valueOf(fileId)));
                        // Add a mouse listener so when it is clicked the popup appears.
                        jPanelFileRow.addMouseListener(getMyMouseListener());
                        // Add the file name and pic type to the panel and then add panel to parent panel.
                        jPanelFileRow.add(jlFileName);
                        jPaneln.add(jPanelFileRow);
                        // Perform a relayout.
                        jPaneln.validate();
                    }

                    // Add the new file to the array list which holds all our data.
                    myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                    // Increment the fileId for the next file to be received.
                    fileId++;
                    /////////////////////////  System.out.println("Hello fileid " + fileId);
                    jPaneln.validate();

                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readReceivedMsg(InputStreamReader inputStreamReader, String sendername) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            bufferedReader = new BufferedReader(inputStreamReader);
            String encMessage = bufferedReader.readLine();
         //   byte[] mes = encMessage.getBytes();

         //  message = rsa.decMessage(rsa.privateKey, mes);
            System.out.println(encMessage);
            if (jTextArea1.getText().equals("")) {
                jTextArea1.setText("Sender: " + encMessage);
            } else {
                jTextArea1.setText(jTextArea1.getText() + "\n Sender (" + sendername + "): " + encMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static String checkRecived(char[] action) {

        char[] chat = "chat".toCharArray();
        char[] file = "file".toCharArray();

        for (int i = 0; i < 4; i++) {
            if (action[i] == chat[i]) {
                ///////////      System.out.println("msg");
                return "Msg";
            } else if ((action[i] == file[i])) {
                ///////////////     System.out.println("file");
                return "file";
            }
        }
        return null;
    }

    /**
     * Creates new form Frame
     */
    public Frame() {
        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jIntroPanel = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jContentPane = new javax.swing.JLayeredPane();
        jPanel2 = new javax.swing.JPanel();
        jLPageName = new javax.swing.JLabel();
        jSidePanel = new javax.swing.JPanel();
        jSideHomePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSideReceivePanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jsideChatPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSideSendFilePanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jHomePanel = new javax.swing.JPanel();
        jpFileRow = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtfDeviceName = new javax.swing.JTextField();
        jBtnConnect = new javax.swing.JButton();
        jStartDiscoveryButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanelHomeList = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        javax.swing.JPanel jChatPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        btnSendMsg = new javax.swing.JButton();
        javax.swing.JPanel jSendFilePanel = new javax.swing.JPanel();
        jlFileName1 = new javax.swing.JLabel();
        btnSelectFile = new javax.swing.JButton();
        btnSendFile = new javax.swing.JButton();
        jReceivedFilesPanel = new javax.swing.JPanel();
        jLRecived1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPaneln = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Line for sharing");
        setBackground(new java.awt.Color(255, 255, 255));
        setIconImages(null);

        jIntroPanel.setBackground(new java.awt.Color(255, 255, 255));
        jIntroPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jIntroPanelMouseClicked(evt);
            }
        });
        jIntroPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/line-l1.png"))); // NOI18N
        jIntroPanel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 180, 203, 208));

        jContentPane.setBackground(new java.awt.Color(255, 255, 255));
        jContentPane.setForeground(new java.awt.Color(255, 255, 255));
        jContentPane.setDoubleBuffered(true);

        jPanel2.setBackground(new java.awt.Color(126, 52, 126));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 87));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLPageName.setFont(new java.awt.Font("Serif", 1, 48)); // NOI18N
        jLPageName.setForeground(new java.awt.Color(204, 204, 204));
        jLPageName.setText("Home");
        jPanel2.add(jLPageName, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 0, 320, 120));

        jSidePanel.setBackground(new java.awt.Color(51, 0, 51));
        jSidePanel.setDoubleBuffered(false);
        jSidePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSideHomePanel.setBackground(new java.awt.Color(51, 0, 51));
        jSideHomePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSideHomePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSideHomePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSideHomePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSideHomePanelMouseExited(evt);
            }
        });
        jSideHomePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_home_page_20px.png"))); // NOI18N
        jSideHomePanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 15, -1, -1));

        jLabel2.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Home");
        jSideHomePanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 11, 116, -1));

        jSidePanel.add(jSideHomePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 135, 250, 50));
        jSideHomePanel.getAccessibleContext().setAccessibleName("");

        jLabel13.setFont(new java.awt.Font("Serif", 1, 48)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(204, 204, 204));
        jLabel13.setText("Line");
        jSidePanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 35, 177, 48));

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));
        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSidePanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 94, 206, 10));

        jSideReceivePanel.setBackground(new java.awt.Color(51, 0, 51));
        jSideReceivePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSideReceivePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSideReceivePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSideReceivePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSideReceivePanelMouseExited(evt);
            }
        });
        jSideReceivePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_download_20px.png"))); // NOI18N
        jSideReceivePanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 15, -1, -1));

        jLabel12.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("Receive File");
        jSideReceivePanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 11, 116, -1));

        jSidePanel.add(jSideReceivePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 293, 250, 50));

        jsideChatPanel.setBackground(new java.awt.Color(51, 0, 51));
        jsideChatPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jsideChatPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jsideChatPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jsideChatPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jsideChatPanelMouseExited(evt);
            }
        });
        jsideChatPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_rocketchat_20px.png"))); // NOI18N
        jsideChatPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 15, -1, -1));

        jLabel8.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Chat");
        jsideChatPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 11, 116, -1));

        jSidePanel.add(jsideChatPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 184, 250, 50));

        jSideSendFilePanel.setBackground(new java.awt.Color(51, 0, 51));
        jSideSendFilePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSideSendFilePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSideSendFilePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSideSendFilePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSideSendFilePanelMouseExited(evt);
            }
        });
        jSideSendFilePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_paper_plane_20px.png"))); // NOI18N
        jSideSendFilePanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 15, -1, -1));

        jLabel10.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("Send File");
        jSideSendFilePanel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 11, 80, -1));

        jSidePanel.add(jSideSendFilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 241, 250, 50));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jHomePanel.setBackground(new java.awt.Color(255, 255, 255));
        jHomePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jHomePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jpFileRowLayout = new javax.swing.GroupLayout(jpFileRow);
        jpFileRow.setLayout(jpFileRowLayout);
        jpFileRowLayout.setHorizontalGroup(
            jpFileRowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpFileRowLayout.setVerticalGroup(
            jpFileRowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jHomePanel.add(jpFileRow, new org.netbeans.lib.awtextra.AbsoluteConstraints(338, 301, -1, -1));

        jLabel4.setBackground(new java.awt.Color(51, 0, 51));
        jLabel4.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 0, 51));
        jLabel4.setText("Welecome Back to.. ");
        jHomePanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 440, 40));

        jLabel5.setFont(new java.awt.Font("Serif", 1, 48)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 0, 51));
        jLabel5.setText("Line");
        jHomePanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 220, 50));

        jLabel6.setBackground(new java.awt.Color(51, 0, 51));
        jLabel6.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 0, 51));
        jLabel6.setText("To get Started with sharing, Enter Your device name. ");
        jHomePanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 340, 50));

        jLabel14.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 153));
        jLabel14.setText("Note: This name will be shown to other users.");
        jHomePanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 340, 30));

        jtfDeviceName.setToolTipText("Hello");
        jtfDeviceName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 213, 229)));
        jHomePanel.add(jtfDeviceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 1050, 40));

        jBtnConnect.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jBtnConnect.setText("Connect");
        jBtnConnect.setActionCommand("");
        jBtnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnConnectActionPerformed(evt);
            }
        });
        jHomePanel.add(jBtnConnect, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 1040, 40));

        jStartDiscoveryButton.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jStartDiscoveryButton.setForeground(new java.awt.Color(51, 0, 51));
        jStartDiscoveryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_search_more_20px.png"))); // NOI18N
        jStartDiscoveryButton.setText("Start Discovery");
        jStartDiscoveryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartDiscoveryButtonActionPerformed(evt);
            }
        });
        jHomePanel.add(jStartDiscoveryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 1050, 40));

        jSeparator2.setBackground(new java.awt.Color(51, 0, 51));
        jSeparator2.setForeground(new java.awt.Color(102, 0, 102));
        jHomePanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 1150, 10));

        jScrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 213, 229)));

        jPanelHomeList.setBackground(new java.awt.Color(255, 255, 255));
        jPanelHomeList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 213, 229)));
        jPanelHomeList.setForeground(new java.awt.Color(102, 0, 102));
        jPanelHomeList.setFont(new java.awt.Font("Serif", 2, 18)); // NOI18N
        jPanelHomeList.setLayout(new java.awt.CardLayout());
        jScrollPane4.setViewportView(jPanelHomeList);

        jHomePanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 440, 1040, 150));

        jLabel3.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 0, 102));
        jHomePanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, 1050, 40));

        jTabbedPane1.addTab("tab1", jHomePanel);

        jChatPanel.setBackground(new java.awt.Color(255, 255, 255));
        jChatPanel.setPreferredSize(new java.awt.Dimension(1370, 350));
        jChatPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 213, 229)));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jChatPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 1010, 460));

        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 213, 229)));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jChatPanel.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 950, 30));

        btnSendMsg.setBackground(new java.awt.Color(204, 204, 204));
        btnSendMsg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_email_send_20px.png"))); // NOI18N
        btnSendMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendMsgActionPerformed(evt);
            }
        });
        jChatPanel.add(btnSendMsg, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 600, 60, 30));

        jTabbedPane1.addTab("tab2", jChatPanel);

        jSendFilePanel.setBackground(new java.awt.Color(255, 255, 255));

        jlFileName1.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jlFileName1.setForeground(new java.awt.Color(51, 0, 51));
        jlFileName1.setText("Choose a file to send.");

        btnSelectFile.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        btnSelectFile.setForeground(new java.awt.Color(51, 0, 51));
        btnSelectFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_browse_folder_20px.png"))); // NOI18N
        btnSelectFile.setText("select file");
        btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFileActionPerformed(evt);
            }
        });

        btnSendFile.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        btnSendFile.setForeground(new java.awt.Color(51, 0, 51));
        btnSendFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DataTransfareImages/icons8_send_file_20px.png"))); // NOI18N
        btnSendFile.setText("send file");
        btnSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jSendFilePanelLayout = new javax.swing.GroupLayout(jSendFilePanel);
        jSendFilePanel.setLayout(jSendFilePanelLayout);
        jSendFilePanelLayout.setHorizontalGroup(
            jSendFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSendFilePanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jSendFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlFileName1, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectFile, javax.swing.GroupLayout.DEFAULT_SIZE, 1042, Short.MAX_VALUE)
                    .addComponent(btnSendFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(649, Short.MAX_VALUE))
        );
        jSendFilePanelLayout.setVerticalGroup(
            jSendFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSendFilePanelLayout.createSequentialGroup()
                .addGap(174, 174, 174)
                .addComponent(jlFileName1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(btnSelectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(764, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab3", jSendFilePanel);

        jReceivedFilesPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLRecived1.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLRecived1.setForeground(new java.awt.Color(51, 0, 51));
        jLRecived1.setText("Recived Files:");

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPaneln.setBackground(new java.awt.Color(255, 255, 255));
        jPaneln.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPaneln.setForeground(new java.awt.Color(51, 0, 51));
        jPaneln.setFont(new java.awt.Font("Segoe UI Semilight", 1, 14)); // NOI18N
        jPaneln.setLayout(new javax.swing.BoxLayout(jPaneln, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(jPaneln);

        jButton1.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(51, 0, 51));
        jButton1.setText("View all downloaded files");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel15.setText("jLabel15");

        javax.swing.GroupLayout jReceivedFilesPanelLayout = new javax.swing.GroupLayout(jReceivedFilesPanel);
        jReceivedFilesPanel.setLayout(jReceivedFilesPanelLayout);
        jReceivedFilesPanelLayout.setHorizontalGroup(
            jReceivedFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jReceivedFilesPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jReceivedFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLRecived1)
                    .addComponent(jScrollPane2)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 1025, Short.MAX_VALUE))
                .addContainerGap(665, Short.MAX_VALUE))
        );
        jReceivedFilesPanelLayout.setVerticalGroup(
            jReceivedFilesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jReceivedFilesPanelLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLRecived1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(524, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", jReceivedFilesPanel);

        jContentPane.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jContentPane.setLayer(jSidePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jContentPane.setLayer(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jContentPaneLayout = new javax.swing.GroupLayout(jContentPane);
        jContentPane.setLayout(jContentPaneLayout);
        jContentPaneLayout.setHorizontalGroup(
            jContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jContentPaneLayout.createSequentialGroup()
                .addComponent(jSidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jContentPaneLayout.setVerticalGroup(
            jContentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jSidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1166, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jIntroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1947, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jIntroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectFileActionPerformed
        // TODO add your handling code here:
        if (evt.getSource() == btnSelectFile) {
            // Create a file choo8ser to open the dialog to choose a file.
            JFileChooser jFileChooser = new JFileChooser();
            // Set the title of the dialog.
            jFileChooser.setDialogTitle("Choose a file to send.");
            // Show the dialog and if a file is chosen from the file chooser execute the following statements.
            int response = jFileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
//                 Get the selected file.
                fileToSend[0] = jFileChooser.getSelectedFile();

                ///////////////////////// System.out.println(fileToSend[0] + " gggggggggg");
                // Change the text of the java swing label to have the file name.
                jlFileName1.setText("The file you want to send is: " + fileToSend[0].getName());
            }
    }//GEN-LAST:event_btnSelectFileActionPerformed
    }


    private void btnSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendFileActionPerformed
        // TODO add your handling code here:
        if (evt.getSource() == btnSendFile) {
            // If a file has not yet been selected then display this message.
            if (fileToSend[0] == null) {
                jlFileName1.setText("Please choose a file to send first!");
                // If a file has been selected then do the following.
            } else {
                try {

                    // Create an input stream into the file you want to send.
                    FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                    // Create a socket connection to connect with the server.
                    Socket socket = new Socket(deviceIP, devicePort);

                    // Create an output stream to  write to the server over the socket connection.
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    // Get the name of the file you want to send and store it in filename.
                    String fileName = fileToSend[0].getName();
                    // Convert the name of the file into an array of bytes to be sent to the server.
                    byte[] fileNameBytes = fileName.getBytes();
                    // Create a byte array the size of the file so don't send too little or too much data to the server.
                    byte[] fileBytes = new byte[(int) fileToSend[0].length()];
                    // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                    fileInputStream.read(fileBytes);
                    dataOutputStream.writeBytes("file");
                    // Send the length of the name of the file so server knows when to stop reading.
                    dataOutputStream.writeInt(fileNameBytes.length);
                    // Send the file name.
                    dataOutputStream.write(fileNameBytes);
                    // Send the length of the byte array so the server knows when to stop reading.
                    dataOutputStream.writeInt(fileBytes.length);
                    // Send the actual file.
                    dataOutputStream.write(fileBytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }


    }//GEN-LAST:event_btnSendFileActionPerformed

    private void jSideHomePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideHomePanelMouseEntered
        // TODO add your handling code here:
        Color color = new Color(55, 20, 55);
        jSideHomePanel.setBackground(color);
    }//GEN-LAST:event_jSideHomePanelMouseEntered

    private void jsideChatPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsideChatPanelMouseEntered
        // TODO add your handling code here:
        Color color = new Color(55, 20, 55);
        jsideChatPanel.setBackground(color);

    }//GEN-LAST:event_jsideChatPanelMouseEntered

    private void jSideReceivePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideReceivePanelMouseEntered
        // TODO add your handling code here:
        Color color = new Color(55, 20, 55);
        jSideReceivePanel.setBackground(color);
    }//GEN-LAST:event_jSideReceivePanelMouseEntered

    private void jSideReceivePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideReceivePanelMouseExited
        // TODO add your handling code here:
        Color color = new Color(51, 0, 51);
        jSideReceivePanel.setBackground(color);
    }//GEN-LAST:event_jSideReceivePanelMouseExited

    private void jSideHomePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideHomePanelMouseClicked
        // TODO add your handling code here:
        Color color = new Color(100, 61, 100);
        jSideHomePanel.setBackground(color);
        jTabbedPane1.setSelectedIndex(0);
        jLPageName.setText("Home");

//        if (deviceInfoList.size() != originalSize) {
//            showDevicesList();
//        }

    }//GEN-LAST:event_jSideHomePanelMouseClicked

    private void jsideChatPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsideChatPanelMouseExited
        // TODO add your handling code here:
        Color color = new Color(51, 0, 51);
        jsideChatPanel.setBackground(color);
    }//GEN-LAST:event_jsideChatPanelMouseExited

    private void jSideSendFilePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideSendFilePanelMouseEntered
        // TODO add your handling code here:
        Color color = new Color(55, 20, 55);
        jSideSendFilePanel.setBackground(color);
    }//GEN-LAST:event_jSideSendFilePanelMouseEntered

    private void jSideSendFilePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideSendFilePanelMouseExited
        // TODO add your handling code here:
        Color color = new Color(51, 0, 51);
        jSideSendFilePanel.setBackground(color);
    }//GEN-LAST:event_jSideSendFilePanelMouseExited

    private void jSideHomePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideHomePanelMouseExited
        // TODO add your handling code here:
        Color color = new Color(51, 0, 51);
        jSideHomePanel.setBackground(color);
    }//GEN-LAST:event_jSideHomePanelMouseExited

    private void jsideChatPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsideChatPanelMouseClicked
        // TODO add your handling code here:
        Color color = new Color(100, 61, 100);
        jsideChatPanel.setBackground(color);
        jTabbedPane1.setSelectedIndex(1);
        jLPageName.setText("Chat");

    }//GEN-LAST:event_jsideChatPanelMouseClicked

    private void jSideSendFilePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideSendFilePanelMouseClicked
        // TODO add your handling code here:
        Color color = new Color(100, 61, 100);
        jSideSendFilePanel.setBackground(color);
        jTabbedPane1.setSelectedIndex(2);
        jLPageName.setText("Send File");

    }//GEN-LAST:event_jSideSendFilePanelMouseClicked

    private void jSideReceivePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSideReceivePanelMouseClicked
        // TODO add your handling code here:
        Color color = new Color(100, 61, 100);
        jSideReceivePanel.setBackground(color);
        jTabbedPane1.setSelectedIndex(3);
        jLPageName.setText("Receive File");
    }//GEN-LAST:event_jSideReceivePanelMouseClicked

    private void jIntroPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jIntroPanelMouseClicked
        // TODO add your handling code here:
        jIntroPanel.setVisible(false);
        jContentPane.setVisible(true);
    }//GEN-LAST:event_jIntroPanelMouseClicked

    private void btnSendMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendMsgActionPerformed
        try {
            // TODO add your handling code here:
            Socket socket = new Socket(deviceIP, devicePort);
            String message = jTextField1.getText();
            String encodedMessage = rsa.encMessage(message, devicePublicKey);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write("chat" + encodedMessage);

            System.out.println("chat !!♠" + jTextField1.getText());
            System.out.println("\"chat\" + jTextField1.getText()  " + "chat" + jTextField1.getText());
            jTextArea1.setText(jTextArea1.getText() + "\n Me: " + jTextField1.getText());
            printWriter.flush();
            printWriter.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSendMsgActionPerformed

    private void jStartDiscoveryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartDiscoveryButtonActionPerformed
        // TODO add your handling code here:
        Discovery discovery = new Discovery();
//        System.out.println( deviceInfoList.get(0));
        discovery.start();
        showDevicesList();
    }//GEN-LAST:event_jStartDiscoveryButtonActionPerformed

    private void jBtnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnConnectActionPerformed
        // TODO add your handling code here:
        int indexList;
        indexList = list.getSelectedIndex();
        if (indexList == -1) {
            jLabel3.setText("Please select device to connect with..");
        } else {
            //////////////////  System.out.println(indexList);
            ////////////////////////    System.out.println("deviceInfoList.get ☻ " + deviceInfoList.get(indexList).getDeviceName());
            jLabel3.setText("connected with: " + deviceInfoList.get(indexList).getDeviceName());
            deviceIP = deviceInfoList.get(indexList).getIp();
            devicePort = deviceInfoList.get(indexList).getPort();
            devicePublicKey = deviceInfoList.get(indexList).getPublicKey();

            /////////////////////  System.out.println(deviceIP + " aaa " + devicePort);
        }


    }//GEN-LAST:event_jBtnConnectActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            File f = new File(".\\Inbox");
            if (!(f.exists() && !f.isDirectory())) {
                new File(".\\Inbox").mkdirs();
            }

            Desktop.getDesktop().open(new File(".\\Inbox"));

        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    public static void main(String args[]) throws NoSuchAlgorithmException {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                Frame frame = new Frame();
                ImageIcon img = new ImageIcon("..\\DataTransfare\\src\\DataTransfareImages\\line0.png");
                frame.setIconImage(img.getImage());
                jContentPane.setVisible(false);
                frame.setVisible(true);
            }
        });

        //     SQLiteDB();
        try {
            //  rsa.test();
            rsa.generateKeys();

            myPublicKey = rsa.publicKey;

            myPublicKeyStr = rsa.publicKey2string();
            ///     System.out.println("pppp " + p.toString());
            System.out.println("mypأ♠♠kstr " + myPublicKey);
            // Create a server socket that the server will be listening with.
            serverSocket = new ServerSocket(0);
            myPort = "" + Frame.serverSocket.getLocalPort();
            System.out.println("listening on port: " + serverSocket.getLocalPort());

        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
        // This while loop will run forever so the server will never stop unless the application is closed.
        while (true) {

            try {
                // Wait for a client to connect and when they do create a socket to communicate with them.
                socket = serverSocket.accept(); //accpt TCP connection
                //  InetAddress clientAddress = socket.getInetAddress();
                //System.out.println("clientAddress " + clientAddress);
//                String clientAdressString = clientAddress.getHostAddress();
//                System.out.println("clientAdressString nam " + clientAdressString);
//                String hostname = "Unknown Device";
//                for (DeviceInfo dev : Frame.deviceInfoList) {
//                    if (clientAdressString.equals(dev.getIp())) {
//                        int index = Frame.deviceInfoList.indexOf(dev);
//                        DeviceInfo redev = Frame.deviceInfoList.remove(0);
//                        System.out.println("device ip " + dev.getIp());
//                        System.out.println("device name " + dev.getDeviceName());
//
//                        hostname = dev.getDeviceName();
//                        break;
//                    }
//                }

//                JFrame jFrame = new JFrame();
//                int result = JOptionPane.showConfirmDialog(jFrame, hostname + ": Want to send you message\\file.");
//
//                if (result == 1) {
//                    System.out.println("You pressed NO");
//
//                } else if (result != 0) {
//                    System.out.println("You pressed Cancel");
//
//                } else {
//                    System.out.println("You pressed Yes");
                // Stream to receive data from the client through the socket.
                stream = socket.getInputStream();

                inputStreamReader = new InputStreamReader(stream);
                dataInputStream = new DataInputStream(stream);
                System.out.println("Stream " + stream.toString());
                // Check if receive message or file
                char[] action = new char[4];
                for (int i = 0; i < 4; i++) {
                    action[i] = (char) stream.read();
                }
                String recivedType = checkRecived(action);
                if ("Msg".equals(recivedType)) {
                    try {
                        //   System.out.println("recivedType = " + recivedType);
                        readReceivedMsg(inputStreamReader, "host");
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if ("file".equals(recivedType)) {
                    //  System.out.println("recivedType = " + recivedType);
                    readReceivedFile(dataInputStream);
                }
//                }
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelectFile;
    private javax.swing.JButton btnSendFile;
    private javax.swing.JButton btnSendMsg;
    private javax.swing.JButton jBtnConnect;
    private javax.swing.JButton jButton1;
    private static javax.swing.JLayeredPane jContentPane;
    public javax.swing.JPanel jHomePanel;
    private javax.swing.JPanel jIntroPanel;
    private javax.swing.JLabel jLPageName;
    private javax.swing.JLabel jLRecived1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    static javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private static javax.swing.JPanel jPanelHomeList;
    private static javax.swing.JPanel jPaneln;
    private javax.swing.JPanel jReceivedFilesPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel jSideHomePanel;
    private javax.swing.JPanel jSidePanel;
    private javax.swing.JPanel jSideReceivePanel;
    private javax.swing.JPanel jSideSendFilePanel;
    private javax.swing.JButton jStartDiscoveryButton;
    private javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jlFileName1;
    private static javax.swing.JPanel jpFileRow;
    private javax.swing.JPanel jsideChatPanel;
    static javax.swing.JTextField jtfDeviceName;
    // End of variables declaration//GEN-END:variables

    public static void showDevicesList() {
        DefaultListModel DLM = new DefaultListModel();
        for (DeviceInfo dev : deviceInfoList) {
            DLM.addElement(dev.getDeviceName());
        }
        list.setModel(DLM);
        list.setBackground(new java.awt.Color(255, 255, 255));
        list.setForeground(new java.awt.Color(102, 0, 102));
        list.setFont(new java.awt.Font("Serif", 2, 18));

        jPanelHomeList.add(list);
    }

    public static void downloadRecivedFile(String fileName, byte[] fileData) {
        // Create the file with its name.
        File f = new File(".\\Inbox");
        if (!(f.exists() && !f.isDirectory())) {
            new File(".\\Inbox").mkdirs();
        }
        File fileToDownload = new File(".\\Inbox\\" + fileName);
        ////////////////////  System.out.println("PAAATH" + fileToDownload.getAbsolutePath());
        //     Desktop.getDesktop().open(new File("..\\DataTransfare\\Inbox"));
        try {
            // Create a stream to write data to the file.
            FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
            // Write the actual file data to the file.
            fileOutputStream.write(fileData);

            //  fileToDownload.
            // Close the stream.
            fileOutputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
