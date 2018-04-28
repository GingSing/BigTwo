import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/** Server that receives and outputs information to two separate clients. Can host multiple different threads.
 *
 */
public class MultiThreadServer extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();
    private ArrayList<Socket> serverPlayerSockets = new ArrayList<Socket>();
    private ArrayList<DataInputStream> fromPlayers;
    private ArrayList<DataOutputStream> toPlayers;
    private ArrayList<ObjectInputStream> objectsFromPlayers;
    private ArrayList<ObjectOutputStream> objectsToPlayers;

    public static void main(String[] args) {

        MultiThreadServer frame = new MultiThreadServer(); //new instance
    }

    public MultiThreadServer() {

        this.fromPlayers = new ArrayList<DataInputStream>();
        this.toPlayers = new ArrayList<DataOutputStream>();
        this.objectsFromPlayers = new ArrayList<ObjectInputStream>();
        this.objectsToPlayers = new ArrayList<ObjectOutputStream>();


        // Place text area on the frame
        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("MultiThreadServer");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            ServerSocket talkingSocket = new ServerSocket(5000);
            jta.append("MultiThreadServer started at " + new Date() + '\n');

            // Number a client
            int clientNo = 0;

            while (true) {
                // Listen for a new connection request
                Socket socket = serverSocket.accept();

                serverPlayerSockets.add(socket);
                fromPlayers.add(new DataInputStream(socket.getInputStream()));
                toPlayers.add(new DataOutputStream(socket.getOutputStream()));
                objectsFromPlayers.add(new ObjectInputStream(socket.getInputStream()));
                objectsToPlayers.add(new ObjectOutputStream(socket.getOutputStream()));

                // Display the client number
                jta.append("Starting thread for client " + clientNo +
                        " at " + new Date() + '\n');

                // Find the client's host name, and IP address
                InetAddress inetAddress = socket.getInetAddress();
                jta.append("Client " + clientNo + "'s host name is "
                        + inetAddress.getHostName() + "\n");
                jta.append("Client " + clientNo + "'s IP Address is "
                        + inetAddress.getHostAddress() + "\n");

                // Increment clientNo
                clientNo++;
                if (serverPlayerSockets.size() == 2) {
                    HandleASession task = new HandleASession(serverPlayerSockets, fromPlayers, toPlayers, objectsFromPlayers, objectsToPlayers);
                    new Thread(task).start();
                    serverPlayerSockets = new ArrayList<Socket>();
                    fromPlayers = new ArrayList<DataInputStream>();
                    toPlayers = new ArrayList<DataOutputStream>();
                    objectsFromPlayers = new ArrayList<ObjectInputStream>();
                    objectsToPlayers = new ArrayList<ObjectOutputStream>();
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    // Inner class
    // Define the thread class for handling new connection
    class HandleASession implements Runnable {

        private Deck deck;
        private Played played;
        private ArrayList<Socket> serverPlayerSockets;
        private int turn = 0;
        private ArrayList<DataInputStream> fromPlayers;
        private ArrayList<DataOutputStream> toPlayers;
        private ArrayList<ObjectInputStream> objectsFromPlayers;
        private ArrayList<ObjectOutputStream> objectsToPlayers;
        private int winner = -1;

        private int[] handSizes = {13, 13, 13, 13};
        private String PLAYABLESIZES = "1, 2, 5"; //CHECKS IF CARDS ARE LEGAL TO PLAY

        /**
         * Constructs a thread.
         */
        public HandleASession(ArrayList<Socket> playerSockets, ArrayList<DataInputStream> fromPlayers, ArrayList<DataOutputStream> toPlayers,
                              ArrayList<ObjectInputStream> objectsFromPlayers, ArrayList<ObjectOutputStream> objectsToPlayers) {

            this.serverPlayerSockets = playerSockets;
            this.deck = new Deck();
            this.played = new Played();
            this.fromPlayers = fromPlayers;
            this.toPlayers = toPlayers;
            this.objectsFromPlayers = objectsFromPlayers;
            this.objectsToPlayers = objectsToPlayers;
        }


        /**
         * Runs a thread.
         */
        public void run() {
            try {

                this.deck.shuffleDeck();

                // Deals cards and lets play know their player numbers
                for (int i = 0; i < this.serverPlayerSockets.size(); i++) {
                    int cardStartNum = i * 14;
                    this.objectsToPlayers.get(i).writeObject(new ArrayList<Card>(deck.getAllCards().subList(cardStartNum, cardStartNum + 13)));
                    toPlayers.get(turn).flush();
                    this.toPlayers.get(i).writeInt(i);
                    toPlayers.get(turn).flush();
                }

                //Continuously serve the client
                while (true) {
                    ArrayList<Integer> tempArray = new ArrayList<Integer>();
                    for (int num : handSizes) {
                        tempArray.add(num);
                    }

                    //Writes to all players whether there has been a winner or not
                    for (int i = 0; i < serverPlayerSockets.size(); i++) {
                        if (winner != -1) {
                            toPlayers.get(i).writeBoolean(true);
                            toPlayers.get(turn).flush();
                        } else {
                            toPlayers.get(i).writeBoolean(false);
                            toPlayers.get(turn).flush();
                        }
                    }

                    //Writes to all players whose turn it is
                    for (int i = 0; i < serverPlayerSockets.size(); i++) {

                        if (i == turn) {
                            toPlayers.get(turn).writeInt(0);
                            toPlayers.get(turn).flush();
                        } else {
                            toPlayers.get(i).writeInt(1);
                        }
                        objectsToPlayers.get(i).writeObject(tempArray); // temp array is the list of hand sizes from players (can be a four player game but takes more time to do)
                        objectsToPlayers.get(i).writeObject(this.played.getLastPlayed()); //sends instance of last played hand to show on played component
                    }

                    try {
                        boolean doesntBeatLast = true;
                        while (doesntBeatLast) {
                            int playOrQuit = fromPlayers.get(turn).readInt(); //checks if anyone has quit (anything except 1 is quit)
                            if (playOrQuit == 1) {
                                ArrayList<?> hand = (ArrayList<?>) objectsFromPlayers.get(turn).readObject();
                                if (hand.isEmpty()) {
                                    doesntBeatLast = false;
                                    played.playedCard(hand);
                                    toPlayers.get(turn).writeBoolean(true);
                                    toPlayers.get(turn).flush();
                                    toPlayers.get(turn).writeBoolean(false);
                                    toPlayers.get(turn).flush();
                                } else if (hand.size() != getLastPlayed().size() && !PLAYABLESIZES.contains("" + hand.size()) && getLastPlayed().size() != 0) { // checks to make sure the same size of cards are played
                                    toPlayers.get(turn).writeBoolean(false);
                                    toPlayers.get(turn).flush();
                                } else if (beatsLastHand(hand)) { // checks if beats last
                                    doesntBeatLast = false;
                                    played.playedCard(hand);
                                    toPlayers.get(turn).writeBoolean(true);
                                    toPlayers.get(turn).flush();
                                    handSizes[turn] -= hand.size();
                                    if (handSizes[turn] < 1) {
                                        toPlayers.get(turn).writeBoolean(true); // allows play
                                        toPlayers.get(turn).flush();
                                        winner = turn;
                                    } else {
                                        toPlayers.get(turn).writeBoolean(false); //check win
                                        toPlayers.get(turn).flush();
                                    }
                                } else {
                                    //handle else
                                    toPlayers.get(turn).writeBoolean(false);
                                    toPlayers.get(turn).flush();
                                }
                            } else {
                                for(Socket sockets : serverPlayerSockets){
                                    sockets.close(); //closes sockets when player has quit
                                }
                            }
                        }
                    }catch(SocketException a) {
                        System.out.println("Socket Exception in run().");
                    }catch (Exception e) {
                        System.out.println("Object not found in run().");
                    }

                    if (turn < serverPlayerSockets.size() - 1) {
                        turn++;
                    } else {
                        turn = 0;
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        /** Gets the last played hand.
         *
         * @return arraylist of cards played last
         */
        public ArrayList<Card> getLastPlayed() {
            return played.getLastPlayed();
        }

        /** Checks to see if card beats the last hand.
         *
         * @param uncastedTemp new hand of cards played
         * @return
         */
        public boolean beatsLastHand(ArrayList<?> uncastedTemp) {

            ArrayList<Card> temp = new ArrayList<Card>();
            for(Object card : uncastedTemp){
                temp.add((Card) card);
            }

            int valueTemp = getValue(temp);
            int valueLastPlayed = getValue(getLastPlayed());

            if (valueTemp == -1) {
                return false;
            } else if (valueTemp > valueLastPlayed) {
                return true;
            } else {
                return false;
            }

        }

        /** Returns value of a hand.
         *
         * @param hand Arraylist of cards
         * @return value of cards in int
         */
        public int getValue(ArrayList<Card> hand) {

            if (hand.size() == 1) {
                return scoreSingles(hand.get(0));
            } else if (hand.size() == 2) {
                if (hand.get(0).getCardValue() == hand.get(1).getCardValue()) {
                    return scorePairs(hand);
                }
            } else if (hand.size() == 5) {
                String COMBOS = "fullhouse, straight, flush, 4ofakind";
                String currCombo = checkCombos(hand);

                if (COMBOS.contains(currCombo)) {
                    return (scoreCombos(hand, currCombo));
                } else {
                    return -1;
                }
            }

            return -1;

        }


        /**
         * Returns the value of the combo.
         *
         * @param hand hand to be checked
         * @return value of combo
         */
        private String checkCombos(ArrayList<Card> hand) {

            int firstAmount = 0;
            int secondAmount = 0;
            int currAmount = 1;

            Card lastCard = hand.get(0);

            for (int i = 1; i < hand.size(); i++) {
                if (hand.get(i).getCardValue() == lastCard.getCardValue()) {
                    currAmount++;
                }
                if ((hand.get(i).getCardValue() != lastCard.getCardValue()) || (i == hand.size() - 1)) {
                    if (currAmount > firstAmount) {
                        secondAmount = firstAmount;
                        firstAmount = currAmount;
                        lastCard = hand.get(i);
                    } else if (currAmount >= secondAmount) {
                        secondAmount = currAmount;
                        lastCard = hand.get(i);
                    }
                    currAmount = 1;
                }

            }

            if (((firstAmount == 3) && (secondAmount == 2)) || ((secondAmount == 3) && (firstAmount == 2))) {
                return "fullhouse";
            } else if (((firstAmount == 4) || ((secondAmount == 4)))) {
                return "4ofakind";
            } else if (((firstAmount == 1) && (secondAmount == 1)) && ((Math.abs(hand.get(0).getCardValue() - hand.get(4).getCardValue()) == 4) ||
                    (hand.get(1).getCardValue() == 14 && hand.get(0).getCardValue() == 15 && hand.get(4).getCardValue() == 3 && hand.get(3).getCardValue() == 4 && hand.get(2).getCardValue() == 5) ||
                    (hand.get(0).getCardValue() == 15 && hand.get(4).getCardValue() == 3 && hand.get(3).getCardValue() == 4 && hand.get(2).getCardValue() == 5 && hand.get(1).getCardValue() == 6))) {
                //FIXES FOR EXCEPTIONS

                return "straight";
            } else {
                boolean flag = true;
                int suit = hand.get(1).getSuit().getSuitValue();
                for (Card card : hand) {
                    if (card.getSuit().getSuitValue() != suit) {
                        flag = false;
                    }
                }

                if (flag) {
                    return "flush";
                }
            }
            System.out.println("Error: Not a combination in checkCombos().");
            return "false";

        }

        /**
         * Returns the score of the single card.
         *
         * @param tempCard card to be scored
         * @return value of card
         */
        private int scoreSingles(Card tempCard) {

            return tempCard.getCardValue() * 4 + tempCard.getSuit().getSuitValue();

        }

        /**
         * Returns score of the combo.
         *
         * @param hand  hand to be scored
         * @param combo type of combo
         * @return value of combo
         */
        private int scoreCombos(ArrayList<Card> hand, String combo) {

            int STRAIGHT_SCORE = 300;
            int FLUSH_SCORE = 350;
            int FULLHOUSE_SCORE = 475;
            int FOUROFAKIND_SCORE = 550;

            int score = 0;


            if (combo.equals("flush")) {
                score += FLUSH_SCORE;
                score += (hand.get(0).getSuit().getSuitValue() * 13); //get suit value
                score += hand.get(0).getCardValue(); // get value of highest card
            } else if (combo.equals("fullhouse")) {

                score += FULLHOUSE_SCORE;

                int length = 0;
                Card firstCard = hand.get(0);

                for (Card card : hand) {
                    if (card.getCardValue() == firstCard.getCardValue()) {
                        length++;
                    }
                }

                if (length == 3) {
                    score += hand.get(0).getCardValue();
                } else if (length == 2) {
                    score += hand.get(hand.size() - 1).getCardValue();
                }

            } else if (combo.equals("4ofakind")) {

                score += FOUROFAKIND_SCORE;

                int length = 0;
                Card firstCard = hand.get(0);

                for (Card card : hand) {
                    if (card.getCardValue() == firstCard.getCardValue()) {
                        length++;
                    }
                }

                if (length == 4) {
                    score += hand.get(0).getCardValue();
                } else if (length == 1) {
                    score += hand.get(hand.size() - 1).getCardValue();
                }

            } else if (combo.equals("straight")) {
                score += STRAIGHT_SCORE;
                score += hand.get(0).getCardValue(); // adds value of largest card

                boolean flag = true;
                int suit = hand.get(1).getSuit().getSuitValue();
                for (Card card : hand) {
                    if (card.getSuit().getSuitValue() != suit) {
                        flag = false;
                    }
                }

                if (flag) {
                    score += FLUSH_SCORE; // checks if royal flush
                    score += suit * 13;
                }

            }
            return score;

        }

        /**
         * Returns score of the pair.
         *
         * @param hand hand to be scored
         * @return
         */
        private int scorePairs(ArrayList<Card> hand) {

            int value = 0;

            Card card1 = hand.get(0);
            Card card2 = hand.get(1);

            int valueMultiplier = 16; //makes it so card value is worth more than suit value.

            value += card1.getCardValue() * valueMultiplier;

            value += card1.getSuit().getSuitValue();
            value += card2.getSuit().getSuitValue();

            return value;

        }
    }
}