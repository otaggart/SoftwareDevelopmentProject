import java.util.ArrayList;
public class Deck {//this is the deck class, It is used to instantiate each deck object
    private ArrayList <Card> cards = new ArrayList<>();//contains all cards in deck
    int deckNumber;
    public Deck(int dNo){//CONSTRUCTOR
        deckNumber = dNo;
    }

    public void discardCard(Card n){
        cards.add(n);
    }
    public Card drawCard(){
        Card x = cards.getFirst();
        deleteCard();
        return x;

    }
    private void deleteCard(){
        cards.removeFirst();
    }
    public Boolean hasCards(){
        return cards.size() > 1;
    }
    public ArrayList<Integer> returnCards( ){
        ArrayList<Integer> tempvals = new ArrayList<>();
        for(int p = 0;p<cards.size();p++) {//convert list of cards to corresponding list of integers
            tempvals.add(cards.get(p).getval());
        }
        return tempvals;
    }
}
