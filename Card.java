public class Card {
    int val;
    public Card (int value){
        val = value;
    }
    public synchronized int getval(){
        return val;
    }
}
