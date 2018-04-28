import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Chatroom implements Runnable{

    private JFrame frame;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private JTextArea jta;
    private JTextField jtf;
    private JScrollPane pane;
    private boolean writing = false;

    private String host = "localhost";

    public Chatroom(String hostAddr){

        frame = new JFrame("Chatroom");
        frame.setSize(500, 500);
        frame.getContentPane().setLayout(new FlowLayout());

        jta = new JTextArea();
        jta.setPreferredSize(new Dimension(450, 350));
        jtf = new JTextField();
        jtf.setPreferredSize(new Dimension(450, 100));
        jtf.setBackground(Color.red);
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    writing = true;
                    toServer.writeInt(2);
                    toServer.writeInt(jtf.getText().length());
                    for(int i = 0; i < jtf.getText().length(); i++){
                        toServer.writeChar(jtf.getText().charAt(i));
                    }
                    jta.append(jtf.getText());
                    writing = false;
                }catch(Exception a){
                    System.out.println("Exception Error in JavaTextField.");
                }
            }
        });

//        JPanel textBoard = new JPanel();
//        textBoard.add(pane);
//
//        textBoard.setSize(500, 300);
//        textBoard.setBackground(Color.red);
//        textBoard.setLocation(0, 0);
//
//        JPanel inputBox = new JPanel();
//        inputBox.setLocation(0, 400);
//        inputBox.add(jtf);
//        inputBox.setBackground(Color.yellow);

//        frame.add(textBoard);
//        frame.add(inputBox);
        frame.getContentPane().add(jta);
        frame.getContentPane().add(jtf);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        connectToServer();

    }

    public void connectToServer(){
        Socket socket;
        int PORT = 5000;

        try{
            socket = new Socket(host, PORT);

            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
        }catch(Exception e){
            System.err.println(e);
        }

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        if (toServer != null && fromServer != null) {
            while (true) {
                try {
                    System.out.println(writing);
                    if (!writing) {
                        toServer.writeInt(1);
                        int words = fromServer.readInt();
                        System.out.println(words);
                        if (words > 0) {
                            String message = "";
                            for (int i = 0; i < words; i++) {
                                message += fromServer.readChar();
                            }
                            jta.append(message);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("run() Exception.");
                }
            }

        }
    }

    public static void main(String[] args){

        new Chatroom("");

    }
}
