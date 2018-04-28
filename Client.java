import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Client class containing the user GUI.
 */
public class Client implements Runnable{


    private JFrame frame;
    private DataOutputStream toServer;
    private DataInputStream fromServer;
    private ObjectOutputStream objectsToServer;
    private ObjectInputStream objectsFromServer;
    private JButton play;
    private JButton quit;
    private JButton reset;
    private PlayedComponent pc = new PlayedComponent();
    private CardGame cg = new CardGame();
    private MyMousePressListener mpl;
    private JLabel handSize;
    private JLabel playerNum;
    private JLabel playerHasQuitLabel;

    private int playerNumber;
    private String host = "localhost";

    private boolean waiting = true;
    private boolean continueToPlay = true;
    private boolean myTurn = false;
    private boolean winner = false;
    private boolean quitting = false;

    public Client(String hostAddr){

        if(!hostAddr.equals("")){
            this.host = hostAddr;
        }

        frame = new JFrame("Big Two Application");
        frame.setSize(1190, 600);

        JPanel shared = new JPanel(new GridLayout(1, 1)); // played component screen which shows last played hand
        shared.setSize(500, 300);
        shared.setLocation(355, 30);
        shared.add(pc);
        shared.setBackground(Color.white);

        JPanel playPanel = new JPanel(new GridLayout(1, 1)); // play button holder
        playPanel.setSize(100, 50);
        playPanel.setLocation(560, 350);

        JPanel quitPanel = new JPanel(new GridLayout(1,1)); // quit button holder
        quitPanel.setLocation(1000, 350);
        quitPanel.setSize(100, 50);

        JPanel hSizes = new JPanel(new GridLayout(1, 1)); // opponent's hand size holder
        hSizes.setSize(150, 50);
        hSizes.setLocation(100, 150);

        JPanel player = new JPanel(new GridLayout(1, 1)); // player number holder
        player.setSize(75, 50);
        player.setLocation(10, 10);

        reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        JPanel resetGame = new JPanel(new GridLayout(1,1)); // reset button holder
        resetGame.setSize(100, 50);
        resetGame.setLocation(1000, 100);

        resetGame.add(reset);

        JPanel playerHasQuitPanel = new JPanel(new GridLayout(1, 1)); // player has quit label holder
        playerHasQuitPanel.setSize(150, 50);
        playerHasQuitPanel.setLocation(100,350);

        playerHasQuitLabel = new JLabel("", SwingConstants.CENTER); // centers the text
        playerHasQuitPanel.add(playerHasQuitLabel);

        playerNum = new JLabel("", SwingConstants.CENTER);
        player.add(playerNum);

        play = new JButton("Play Cards");

        play.setFocusPainted(false);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    toServer.writeInt(1); // tells server that it is not a quit command
                    toServer.flush();
                    playCards();
                }catch(Exception a){
                    a.printStackTrace();
                }
            }
        });

        quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    toServer.writeInt(2); // tells server that it is a quit command
                    toServer.flush();
                    playerHasQuitLabel.setText("You Have Quit.");
                    quitting = true; //shows a player is quitting
                    continueToPlay = false; // stop playing
                    waiting = false; // stops wait
                }catch(Exception a){
                    System.out.println("Quit Button Error in quitButton.");
                }
            }
        });

        quitPanel.add(quit);

        playPanel.add(play);

        handSize = new JLabel("", SwingConstants.CENTER);
        hSizes.add(handSize);


        mpl = new MyMousePressListener();
        frame.addMouseListener(mpl);
        play.setEnabled(myTurn);
        play.setBackground(Color.white);
        quit.setBackground(Color.white);
        reset.setBackground(Color.white);

        frame.getContentPane().setBackground(new Color(135, 206, 235));
        hSizes.setBackground(new Color(135, 206, 235));
        player.setBackground(new Color(135, 206, 235));
        playerHasQuitPanel.setBackground(new Color(135, 206, 235)); // blue color

        frame.add(quitPanel);
        frame.add(playPanel);

        frame.add(player);
        frame.add(shared);
        frame.add(hSizes);
        frame.add(resetGame);
        frame.add(playerHasQuitPanel);
        frame.add(cg);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        connectToServer();
    }

    /**
     * Connect to server.
     */
    public void connectToServer(){

        Socket socket;
        int PORT = 8000;

        try{
            socket = new Socket(host, PORT);

            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            objectsToServer = new ObjectOutputStream(socket.getOutputStream());
            objectsFromServer = new ObjectInputStream(socket.getInputStream());
            cg.updateHand((ArrayList<?>) objectsFromServer.readObject());
            playerNumber = fromServer.readInt();
            playerNum.setText("Player: " + this.playerNumber);
        }catch(Exception e){
            System.err.println(e);
        }

        Thread thread = new Thread(this);
        thread.start();
    }

    /** Resets the game's gamestate and tries to find another game.
     *
     */
    public void reset(){
        pc.reset();
        handSize.setText("");
        playerNum.setText("");
        playerHasQuitLabel.setText("");

        waiting = true;
        continueToPlay = true;
        myTurn = false;
        winner = false;
        quitting = false;

        connectToServer();
    }


    /**
     * Get opponents hands array list.
     *
     * @return the array list
     */
    public ArrayList<Integer> getOpponentsHands(){
        try {
            ArrayList<?> temp = (ArrayList<?>) objectsFromServer.readObject();
            ArrayList<Integer> handSizes = new ArrayList<Integer>();
            for(Object object : temp){
                handSizes.add((Integer) object);
            }
            return handSizes;

        }catch(Exception e){
            System.out.println("ClassNotFound Error in getOpponentsHands().");
            turnOffLoops();
        }
        return null;
    }

    /** turns the loops off
     *
     */
    public void turnOffLoops(){
        continueToPlay = false;
        myTurn = true;
    }

    /**
     * Runs the program which listens and replies to server outputs.
     */
    public void run(){

        if(toServer != null && fromServer != null && objectsToServer != null && objectsFromServer != null) {
            while (continueToPlay) {
                while (!myTurn) {
                    try {
                        winner = fromServer.readBoolean(); // determines if player has won
                        int input = fromServer.readInt(); //determines turn
                        if (!winner) {
                            if (playerNumber == 0)
                                handSize.setText("Opponent's Hand Size: " + getOpponentsHands().get(1)); // can get more than two hands ( was thinking of implementing 4 plays but would take more time)
                            else if (playerNumber == 1)
                                handSize.setText(("Opponent's Hand Size: " + getOpponentsHands().get(0)));
                            if (input == 0) {
                                pc.updateLastPlayed((ArrayList<?>) objectsFromServer.readObject());
                                myTurn = true;
                                play();
                            } else if (input == 1) {
                                pc.updateLastPlayed((ArrayList<?>) objectsFromServer.readObject());
                            }
                        } else {
                            ArrayList<?> container = (ArrayList<?>) objectsFromServer.readObject(); //containers hold server output when it shouldn't be used
                            ArrayList<?> container2 = (ArrayList<?>) objectsFromServer.readObject();
                            turnOffLoops();

                        }
                    }catch(EOFException f){

                        playerHasQuitLabel.setText("Your Opponent Has Quit.");
                        freeze();

                    }catch (Exception e) {
                        System.out.println("run() Exception.");
                        e.printStackTrace();
                        turnOffLoops();
                    }
                }
                try {
                    if(!winner) {
                        waitForPlayerAction();

                        myTurn = false;
                        play.setEnabled(false);
                        quit.setEnabled(false);
                        play.setBackground(Color.white);

                    }
                }catch (Exception e) {
                    System.out.println("run () Interrupted.");
                    e.printStackTrace();
                    turnOffLoops();
                }

                if(winner) {
                    waiting = true;
                }

            }

            if (winner) {
                if(cg.getHandSize() != 0) {
                    handSize.setText("Opponent's Hand Size : 0");
                }
                pc.triggerWinner(false);
            }
            freeze();
        }

    }

    /**
     * Sets all use for play buttons to true.
     */
    public void play(){
        play.setEnabled(true);
        quit.setEnabled(true);
        play.setBackground(new Color(152,251,152));
    }

    /**
     * attempts to play the hand unless the combination is not allowed or the value of the combination is not high enough.
     */
    public void playCards(){

        cg.resetMyLastPlayed();

        ArrayList<Card> temp = new ArrayList<Card>();

        //changes selectedCards to Array of Cards
        for (int num : cg.getSelectedCards()) {
            temp.add(new Card(cg.getHand().get(num)));
        }

        if(beatsLastHand(temp) || cg.getSelectedCards().isEmpty()) {
            for (int num : cg.getSelectedCards()) {
                cg.getMyLastPlayed().add(new Card(cg.getHand().get(num)));
                cg.getHand().remove(num);
            }
            cg.getSelectedCards().clear();
            frame.repaint();
            waiting = false;
            if (checkWin()) {
                pc.triggerWinner(true);
                freeze();
            }
        }else{
            cg.getSelectedCards().clear();
            frame.repaint();
        }
    }

    /**
     * Wait for player action.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void waitForPlayerAction() throws InterruptedException{
        while(waiting){
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * Check win boolean.
     *
     * @return the boolean
     */
    public boolean checkWin(){
        boolean win = false;
        try {
            win = fromServer.readBoolean();
        }catch(IOException e){
            System.out.println("IOException in checkWin().");
            turnOffLoops();
        }

        return win;
    }

    /**
     * Beats last hand boolean.
     *
     * @param hand the hand
     * @return the boolean
     */
    public boolean beatsLastHand(ArrayList<Card> hand){

        boolean beatsLast = false;

        try {
            objectsToServer.writeObject(hand);
            objectsToServer.flush();

            beatsLast = fromServer.readBoolean();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("ClassNotFound Exception or IOException in beatsLastHand()");
            turnOffLoops();
        }
        return beatsLast;
    }

    /**
     * Freeze buttons.
     */
    public void freeze(){
        play.setEnabled(false);
        quit.setEnabled(false);
    }


    /**
     * Listens to mouse presses against Jframe.
     */
    class MyMousePressListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e){

            int tempEX = e.getX() - 8;
            int tempEYM = 458;
            int tempEYm = 455;
            int padding = 5;

            try {
                if (tempEX > cg.getXPos(0) - padding && tempEX < cg.getXPos(0) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(0);
                } else if (cg.getHandSize() > 0 && tempEX > cg.getXPos(1) - padding && tempEX < cg.getXPos(1) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(1);
                } else if (cg.getHandSize() > 1 && tempEX > cg.getXPos(2) - padding && tempEX < cg.getXPos(2) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(2);
                } else if (cg.getHandSize() > 2 && tempEX > cg.getXPos(3) - padding && tempEX < cg.getXPos(3) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(3);
                } else if (cg.getHandSize() > 3 && tempEX > cg.getXPos(4) - padding && tempEX < cg.getXPos(4) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(4);
                } else if (cg.getHandSize() > 4 && tempEX > cg.getXPos(5) - padding && tempEX < cg.getXPos(5) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(5);
                } else if (cg.getHandSize() > 5 && tempEX > cg.getXPos(6) - padding && tempEX < cg.getXPos(6) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(6);
                } else if (cg.getHandSize() > 6 && tempEX > cg.getXPos(7) - padding && tempEX < cg.getXPos(7) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(7);
                } else if (cg.getHandSize() > 7 && tempEX > cg.getXPos(8) - padding && tempEX < cg.getXPos(8) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(8);
                } else if (cg.getHandSize() > 8 && tempEX > cg.getXPos(9) - padding && tempEX < cg.getXPos(9) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(9);
                } else if (cg.getHandSize() > 9 && tempEX > cg.getXPos(10) - padding && tempEX < cg.getXPos(10) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(10);
                } else if (cg.getHandSize() > 10 && tempEX > cg.getXPos(11) - padding && tempEX < cg.getXPos(11) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(11);
                } else if (cg.getHandSize() > 11 && tempEX > cg.getXPos(12) - padding && tempEX < cg.getXPos(12) + CardDrawer.CARD_WIDTH + padding && e.getY() > tempEYm - padding && e.getY() < tempEYM + CardDrawer.CARD_HEIGHT + padding) {
                    cg.selectCard(12);
                }
            }catch(Exception a){

            }

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
    }

    /**
     * Calls client class.
     *
     * @param args the args
     */
    public static void main(String[] args){

        if(args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-host") && !args[i + 1].isEmpty()) {
                    new Client(args[i + 1]);
                }
                else if (args[i]. equals("-help")){
                    System.out.println("Command Line Options: ");
                    System.out.println("    -host [hostAddr]");

                    System.out.println("\nTo play the game: Try to reduce your hand size to 0 before your opponent. " +
                            "\nTo do so, you can play cards that are singles, pairs or combos. Singles are any single card." +
                            "\nPairs are any 2 cards with the same numerical value. Combos include 4 of a kind, flush, straights and fullhouses." +
                            "\nIf combos are unclear please refer to google.ca. You can play any card or cards in the beginning of the game. After the first turn," +
                            "\nthe next player must match the cards played by the opposing player. If a player cannot beat a combination of cards with their own cards," +
                            "\nhe may pass the turn.");
                }
            }
        }else{
            new Client("");
        }

    }
}
