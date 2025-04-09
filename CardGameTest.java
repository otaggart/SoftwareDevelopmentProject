import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CardGameTest {

    CardGameClass game;

    @BeforeEach
    void setUp() {
        game = new CardGameClass();
    }

    @Test
    void testDeckAllocation() throws IOException {
        game.n = 3; // Simulating a 3-player game
        ArrayList<Integer> samplePack = new ArrayList<>();
        for (int i = 1; i <= 24; i++) { // 8 cards per player, 3 players
            samplePack.add(i);
        }
        game.CardValueList = samplePack;

        game.gamestart(true);

        // Check deck size
        for (Deck deck : game.deckList) {
            assertEquals(4, deck.returnCards().size(), "Each deck should have 4 cards.");
        }
    }


    @Test
    void testWinningCondition() {
        Player player = new Player(1);
        player.addToHand(new Card(1));
        player.addToHand(new Card(1));
        player.addToHand(new Card(1));
        player.addToHand(new Card(1));

        assertTrue(player.hasWon(), "Player with identical cards should win.");
    }

    @Test
    void testLosingCondition() {
        Player player = new Player(2);
        player.addToHand(new Card(1));
        player.addToHand(new Card(2));
        player.addToHand(new Card(3));
        player.addToHand(new Card(4));

        assertFalse(player.hasWon(), "Player with different cards should not win.");
    }

    @Test
    void testPlayerHand() {
        Player player = new Player(1);
        player.addToHand(new Card(5));
        player.addToHand(new Card(10));

        assertEquals(2, player.hand.size(), "Player should have 2 cards in hand.");
        assertEquals(5, player.hand.get(0).getval(), "First card value should be 5.");
        assertEquals(10, player.hand.get(1).getval(), "Second card value should be 10.");
    }

    @Test
    void testDeckOperations() {
        Deck deck = new Deck(1);
        deck.discardCard(new Card(7));
        deck.discardCard(new Card(8));

        Card drawnCard = deck.drawCard();
        assertEquals(7, drawnCard.getval(), "First drawn card value should be 7.");
        assertEquals(1, deck.returnCards().size(), "Deck size should decrease after draw.");
    }

    @Test
    void testTurnLoop() throws Exception {
        Player player = new Player(1);
        Deck drawDeck = new Deck(1);
        Deck discardDeck = new Deck(2);

        drawDeck.discardCard(new Card(9)); // Adding card to draw deck
        drawDeck.discardCard(new Card(10)); // Adding another card
        player.addToHand(new Card(1));
        player.addToHand(new Card(2));
        player.addToHand(new Card(3));
        player.addToHand(new Card(4));

        CardGameClass.PlayerThread thread = new CardGameClass.PlayerThread(player, drawDeck, discardDeck, 0, player);
        thread.turnLoop();

        assertEquals(4, player.hand.size(), "Player should still have 4 cards after turn.");
        assertEquals(1, discardDeck.returnCards().size(), "Discard deck should have 1 card after turn.");
    }
}