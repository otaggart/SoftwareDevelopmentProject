import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CardGameClass {
    public ArrayList<Deck> deckList = new ArrayList<>(); //instantiate a variable in order to track the Decks in play.
    public ArrayList<Player> playerList = new ArrayList<>();//instantitate a variable in order to keep track of and reference player
    public ArrayList<PlayerThread> threadList = new ArrayList<>(); //instantiate a variable in order to keep track of and reference player threads
    public static int n; // number of players
    public ArrayList<Integer> CardValueList; // arraylist to hold the card values
    private File cardFile;
    private String fileLocation;
    public void gamestart(boolean testing) throws FileNotFoundException {
        int r = 0;
        boolean invalidSize = true;
        boolean invalidValues = true;
        //TAKE AND CHECK FILE INPUT, LOOP UNTIL VALID INPUT////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(!testing) { //Checks if its being run as a test and if its not ask the user for their inputs
            while (invalidSize || invalidValues) {
                CardValueList = new ArrayList<>();
                Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Please enter the number of players: "); //Get number of players
                n = myObj.nextInt();  // Read user input
                Scanner fileScan = new Scanner(System.in);
                System.out.println("Please enter location of pack to load:");
                fileLocation = fileScan.nextLine();
                System.out.println(fileLocation);
                cardFile = new File(fileLocation);
                Scanner fileScanner = new Scanner(cardFile);
                while (fileScanner.hasNextLine()) {//checks if each line is a number and adds to CardValueList
                    String line = fileScanner.nextLine().trim();
                    try {
                        CardValueList.add(Integer.valueOf(line));
                    } catch (NumberFormatException e) {
                        System.out.println("pack invalid");
                    }
                }
                if (CardValueList.size() == 8 * n) {//checks to make sure number of cards inputted is correct for the number of players
                    invalidSize = false;
                } else {
                    System.out.println("Pack is invalid size. ");
                }
                try {
                    for (int z = 0; z < 8 * n; z++) {// checks to ensure pack contains only positive integers
                        if (CardValueList.get(z) < 0) {
                            System.out.println("Pack contains invalid value. ");
                            break;
                        } else if (z >= (8 * n) - 1) {
                            invalidValues = false;
                        }
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }
        else{ //Static inputs for testing program functionality
            n = 4;
            CardValueList = new ArrayList<>();
            for (int u = 0; u < 8*n; u++)
                CardValueList.add((int)(Math.random() * 101));
        }

        for (int i = 0; i < n; i++) {//instantiates and builds list of players
            Player player = new Player(i);
            playerList.add(player);
        }
        boolean noStop = true;
        for (int i = 0; i<CardValueList.size() && noStop;i++){//allocating cards to players
            if (playerList.get(r).hand.size() == 4){
                noStop = false;
            }
            else{
                Card Card = new Card(CardValueList.get(i));
                playerList.get(r).addToHand(Card);
                r++;
                if (r >= n) {//ensures even distribution of cards
                    r = 0;
                }
            }
        }
        for (int i = 0; i < n; i++){//instantiates and builds list of decks
            Deck deck = new Deck(i);
            deckList.add(deck);
        }
        r =0;
        for (int i = 4*n; i<CardValueList.size();i++ ){//allocating cards to decks, i = 4*n to start allocating where players left off
            Card card = new Card(CardValueList.get(i));
            deckList.get(r).discardCard(card);
            r++;
            if (r >= n) {
                r = 0;
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class PlayerThread extends Thread{
        //DECLARATIONS/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Player player;
        Deck drawDeck;
        Deck discardDeck;
        int romeo;
        ArrayList<PlayerThread> referenceOfJustice; //reference for all threads
        int i;
        int j;
        Card tempDraw;
        Card tempDiscard;
        File playerFile;
        FileWriter playerWriter;
        ArrayList<Integer> handValues = new ArrayList<>();
        Player previousPlayer;
        File deckFile;
        FileWriter deckWriter;
        //CONSTRUCTOR/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public PlayerThread(Player p, Deck d, Deck di,int z,Player beforeP) throws IOException {//CONSTRUCTOR
            //DECLARATIONS////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            player = p;
            drawDeck = d;
            discardDeck = di;
            previousPlayer = beforeP;
            i = z;
            j = z + 1;
            deckFile = new File("deck"+ j + "_output.txt");//initial text file outputs
            deckWriter = new FileWriter("deck"+ j + "_output.txt");
            playerFile = new File("player"+ j + "_output.txt");
            playerWriter = new FileWriter("player"+ j + "_output.txt");
            handVal(player.hand);
            playerWriter.write("player " + j + " initial hand is " + handValues + "\n");//outputs starting hand
        }
        //CONVERT CARD TO INTEGER//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void handVal(ArrayList<Card> x){
            handValues = new ArrayList<>();
            for(int q=0;q<x.size();q++){
                handValues.add(x.get(q).getval());
            }
        }
        //THE TURN LOOP, HANDLES EACH TURN///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void turnLoop() throws InterruptedException, IOException {
            //CHECKS IF PLAYER HAS LOST/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (player.lossCheck()>0){
                romeo = player.lossCheck();
                previousPlayer.setWinNumber(romeo);//informs the previous player of a winner
                playerWriter.write("player " + romeo + " has informed player " + j + " that player " + romeo +  " has won.\n");
                playerWriter.write("player " + j + " exits\n");
                handVal(player.hand);
                playerWriter.write("player " + j + " hand is " + handValues + "\n");//outputs hand
                playerWriter.flush();
                deckWriter.write("Deck" + i + " contents: " + drawDeck.returnCards() + "\n");
                deckWriter.flush();
                referenceOfJustice.get(i).interrupt();//throws interrupt
            }
            //MAKES SURE DRAWDECK IS NOT EMPTY/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (drawDeck.hasCards()) {
                //CHECKS FOR INTERRUPT
                if (Thread.currentThread().isInterrupted()) {
                    return;  // Exit thread gracefully
                }
                //DRAW AND DISCARD///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(player.hand.size()== 4){
                    tempDraw = drawDeck.drawCard();//TEMPORARILY STORES CARD BEING HELD
                    player.addToHand(tempDraw);//ADDING THE CARD TO HAND
                    //DISCARD
                    tempDiscard = player.discardDecide();//TEMP DISCARD HOLDS THE CARD THE PLAYER HAS DECIDED TO DISCARD
                    discardDeck.discardCard(tempDiscard);//DISCARDS THE CARD
                    //outputs discard card value and draw card value
                    if (tempDiscard != null && tempDraw!= null) {
                        playerWriter.write("player " + j + " draws a " + tempDraw.getval() + " from deck " + j + "\n");//outputs drawn card value
                        playerWriter.write("player " + j + " discards a " + tempDiscard.getval() + " to deck " + (j + 1) + "\n");//outputs discard card value
                        handVal(player.hand);//Updates store for values of cards in players hand
                        playerWriter.write("player " + j + " current hand is " + handValues + "\n");//outputs hand
                    }
                }
                handVal(player.hand);//Updates store for values of cards in players hand
                //CHECKS IF PLAYER HAS WON //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if(player.hasWon()){
                    previousPlayer.setWinNumber(j);
                    playerWriter.write("player " + j + " wins\n");
                    playerWriter.write("player " + j + " exits\n");//outputs hand
                    handVal(player.hand);
                    playerWriter.write("player " + j + " final hand is " + handValues + "\n");//outputs hand
                    System.out.println("Player " + j + " has won");
                    playerWriter.flush();
                    referenceOfJustice.get(i).kill();
                }
            }
            else {//if no cards to draw sleep so other threads can discard to draw pile
                referenceOfJustice.get(i).sleep(1000);
            }
        }
        //GEBEN IS GERMAN FOR "TO GIVE", THIS METHOD ALLOWS THREAD REFERENCES RO BE PASSED INTO THE THREAD///////////////////////////////////////////////////////////////////////////////////
        public void threadGeben(ArrayList<PlayerThread> mike){
            referenceOfJustice = mike;
        }
        //CALLED TO END GAME////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void kill() throws IOException {
            playerWriter.flush();
            previousPlayer.setWinNumber(j);
            deckWriter.write("Deck" + i + " contents: " + drawDeck.returnCards() + "\n");
            deckWriter.flush();
            referenceOfJustice.get(i).interrupt();//INTERRUPTS


        }
        //THREAD RUN, runs when thread starts/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void run(){
            try{
                if(player.hasWon()){ //Checks if the current player has a winning hand
                    try {
                        playerWriter.write("player " + j + " wins\n"); //Writes the appropriate message to the corresponding output file
                        playerWriter.write("player " + j + " exits\n");
                        handVal(player.hand); //Stores the current value of the players hand
                        playerWriter.write("player " + j + " final hand is " + handValues + "\n");//outputs hand
                        System.out.println("Player " + j + " has won");
                    } catch (IOException e) {
                        return;
                    }
                    try {
                        referenceOfJustice.get(i).kill(); //Attempts to call the method to end the threads processing
                    } catch (IOException e) {
                        return;
                    }
                }
                //MAKES ALL THREADS WAIT TO BEGIN RUNNING UNTIL ALL THREADS HAVE BEEN CREATED/////////////////////////////////////////////////////////////////////////////////
                if (referenceOfJustice.get(i) != referenceOfJustice.getLast()) { //Checks if the current thread is not the last
                    try {
                        synchronized (player){ //Makes the thread wait until its notified
                            player.wait();
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                if(referenceOfJustice.get(i) != referenceOfJustice.getFirst()){ //Checks if thread is not the first thread
                    referenceOfJustice.get(i).sleep(1000); //Makes the thread wait for 1000 milliseconds
                    synchronized (previousPlayer){
                        previousPlayer.notify(); //Unpauses the thread previous to this one
                    }

                }

                //RUNS TURN LOOP WHILE THE THREAD HAS NOT BEEN INTERRUPTED///////////////////////////////////////////////////////////////////////////////////////////////////////
                while(!referenceOfJustice.get(i).currentThread().isInterrupted()){ //Checks if the thread has not been interrupted
                    try {
                        referenceOfJustice.get(i).turnLoop(); //Runs the turnLoop method of the current thread
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        return;  // Exit thread gracefully
                    }

                }
            } catch (InterruptedException e){
                return;
            }

        }

    }



    public static void main(String[] args) throws IOException {
        CardGameClass CardGameClass = new CardGameClass(); //Creates an instance of the executable
        CardGameClass.gamestart(false); //Calls the function to perform game setup
        for(int i = 0;i<CardGameClass.n; i++){ //For loop to cycle through an amount of times equal to the number of players
            if (i == n-1){ //Checks if i is equivalent to the final player
                PlayerThread thread = new PlayerThread(CardGameClass.playerList.get(i),CardGameClass.deckList.get(i),CardGameClass.deckList.get(0),i,CardGameClass.playerList.get(i-1)); //Creates a thread for the nth player
                CardGameClass.threadList.add(thread); //Adds thread to the arraylist of threads
            }
            else if(i==0){ //Checks if i is equivalent to the first player
                PlayerThread thread = new PlayerThread(CardGameClass.playerList.get(i),CardGameClass.deckList.get(i),CardGameClass.deckList.get(i+1),i,CardGameClass.playerList.getLast()); //Creates a thread for player 1
                CardGameClass.threadList.add(thread); //Adds thread to the arraylist of threads
            }
            else{ //Runs for all players other than the last and first
                PlayerThread thread = new PlayerThread(CardGameClass.playerList.get(i),CardGameClass.deckList.get(i),CardGameClass.deckList.get(i+1),i,CardGameClass.playerList.get(i-1)); //Creates a thread for all players
                CardGameClass.threadList.add(thread); //Adds thread to the arraylist of threads
            }
        }
        for(int p = 0;p<CardGameClass.n; p++){ //Runs a number of times equal to the number of players
            CardGameClass.threadList.get(p).threadGeben(CardGameClass.threadList); //Calls the threadGeben method on each thread in the threadList
            CardGameClass.threadList.get(p).start(); //Calls the run method for each thread in the threadList
        }
    }
}




