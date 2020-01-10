import java.awt.EventQueue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.json.simple.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.Timer;
import java.util.TimerTask;


public class JChat
{
    private JFrame frame;
    private  JTextField userName;
    private JTextArea chats;
    private JScrollPane pane;
    private  JTextField message;
    private String dayte;
    
   


    public static void main(String []args)
    {
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                try {
                    JChat window = new JChat();
                    window.frame.setVisible(true);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }//end
    public JChat(){
        initialize();
    }
    private void initialize(){
        Date datetime = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.dayte = DATE_FORMAT.format(datetime);
        System.out.println(dayte);

        frame = new JFrame();
        frame.setBounds(100,200,730,489);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(null);

        JLabel lblName = new JLabel ("User name: ");
        lblName.setBounds (65,30,70,14);
        frame.getContentPane().add(lblName);

        userName = new JTextField();
        userName.setBounds (135,30,150,20);
        frame.getContentPane().add(userName);
        userName.setColumns(10);//experiment
        JLabel lblChats = new JLabel("Chats:");
        lblChats.setBounds (65,55,65,14);
        frame.getContentPane().add(lblChats);
        
        chats = new JTextArea();
        chats.setBounds(135,55,400,200);
        chats.setLineWrap(true);
        chats.setEditable(false);
        pane = new JScrollPane(chats, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setBounds(135,55,400,200);
         frame.getContentPane().add(pane);


         JLabel lblMessage = new JLabel("Message:");
         lblMessage.setBounds(65,260,65,14);
         frame.getContentPane().add(lblMessage);


         message = new JTextField();
         message.setBounds(135,260,340,20);
         frame.getContentPane().add(message);
         message.setColumns(10);

        JButton btnSubmit = new JButton("send");
        btnSubmit.setBackground(Color.BLUE);
        btnSubmit.setForeground(Color.YELLOW);
        btnSubmit.setBounds(470,260,70,19);

        frame.getContentPane().add(btnSubmit);
        SendChatListener sendChatListener = new SendChatListener();
        btnSubmit.addActionListener(sendChatListener);

         message.addActionListener(sendChatListener);

         Timer timer = new Timer ();
         TimerTask task = new TimerTask(){
             @Override 
             public void run(){
                 getChats();
             }
         };
         timer.schedule(task,2000,2000);




    }//initialize
    private class SendChatListener implements ActionListener
     {
         public void actionPerformed(ActionEvent arg0){
             
             if (userName.getText().isEmpty()){
                 JOptionPane.showMessageDialog(null,"Username required");
                 userName.requestFocus();
                 return;
             }
            if (message.getText().isEmpty()){
                 message.requestFocus();
                 return;
            }
            BufferedReader sendChatResponse;
            try {
                String msg = URLEncoder.encode(message.getText(),"UTF-8");//allow to have space in between when typing
                String usr = URLEncoder.encode(userName.getText(),"UTF-8");
             
                URL sendMessageURL = new URL ("URL"+ usr+ "&message="+ msg);
                URLConnection chatConnection = sendMessageURL.openConnection();

                sendChatResponse = new BufferedReader (new InputStreamReader (chatConnection.getInputStream()));
                sendChatResponse.close();

            }catch(IOException ioex) {
                System.out.println("IOException: error sending chat message...");
                System.out.println(ioex.getMessage());

            }catch (Exception e){
                System.out.println("Exception: error sending chat message...");
                System.out.println(e.getMessage());
            }
            getChats();
         }//end actionperformed
    }//end of sendChatListener class.
private void getChats(){
    try {
        URL getChatMessage = new URL("URL" +URLEncoder.encode(this.dayte,"UTF-8"));
        URLConnection messageConnection = getChatMessage.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(messageConnection.getInputStream()));
        
        String inputLine;
        inputLine = in.readLine();
        JSONParser parser = new JSONParser();
        Object allChats = parser.parse(inputLine);
        JSONObject chaatsObjects = (JSONObject)allChats;
        JSONArray chatsArray = (JSONArray) chaatsObjects.get("chats");
        
        String chatsString = "";
        for (Object chat: chatsArray){
            JSONObject achat = (JSONObject)chat;
            chatsString += "("+achat.get("dayte")+")"+achat.get("user")+ ": "+achat.get("message")+"\n";
            //System.out.println(ChatsString);
        }
        chats.setText(chatsString);
        in.close();


    }catch(IOException ioex){
        System.out.println("error receiving chat message....");
    }catch(ParseException pe){
        System.out.println ("error parsing position: "+pe.getPosition());
        System.out.println(pe);
    }
}

}