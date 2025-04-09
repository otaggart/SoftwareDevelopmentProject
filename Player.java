import java.util.ArrayList;
public class Player {
    public int index; //Stores the index value of the player for use in reference and card preference
    public ArrayList<Card> hand = new ArrayList<Card>(); //Stores the cards in hand as an arraylist
    public boolean loser = false; //Flag to denote if player lost the game
    private int winNumber; //Variable to store the index of the player who won the game
    int i = 0; //Counter variable
    public Player(Integer i){ //Constructor
        index = i;
    }
    public Card discardDecide(){ //Decides which card to discard
        while (i < 5){ //Cycles through whole hand
            if (hand.get(i).getval() == index){ //Checks if current card has the same value as the player index, and is therefore preferred
                i++;
            }
            else{
                Card store = hand.get(i); //Temporarily stores the card to discard
                hand.remove(i); //Removes the discarded card from players hand
                return store; //Returns the card to discard to main program
            }
        }
        return null; //Returns null if every card in players hand is preffered value
    }
    public void addToHand(Card card){ // Adds a card to players hand
        hand.add(card);
    }
    public int lossCheck(){
        if (loser){ //Checks if player lost the game
            return winNumber; //Returns the index of the player who won
        }
        else {
            return -1; //Returns a negative value
        }
    }
    public void setWinNumber(int number){ //Receives the index of the player who won
        winNumber = number; //Assigns variable to received value
        loser = true; //Flags the player as having lost the game
    }
    public boolean hasWon(){ //Method for checking if the player has a winning hand
        int value = hand.getFirst().getval(); //Gets the first value in their hand to compare the rest of the hand against
        int count = 0; //Count to store how many cards in hand had the same value
        int k = 0; //Incrementer
        while (k < 4) { //Cycles through whole hand
            if (hand.get(k).getval() == value) { //Checks if current card has the same value as the stored card value
                count++;
                k++;
            }
            else {
                return false; //Returns false to denote player does not have a winning hand
            }
        }
        return count >= 4; //Returns true if count incremented to 4 or above, denoting that the player has a winning hand
    }
}
