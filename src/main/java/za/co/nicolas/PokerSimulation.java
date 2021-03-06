package za.co.nicolas;

import za.co.nicolas.pocker.game.Card;
import za.co.nicolas.pocker.game.Deck;
import za.co.nicolas.pocker.game.Hand;
import za.co.nicolas.pocker.game.HandType;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Nicolas
 * @date 2020/05/13
 */
public class PokerSimulation {

	// statically initialize all the expected (theoretical) probabilities for each type of hand
	private static Map<HandType, Float> expectedProbabilities = new HashMap<>();
	static {
		// for reference see:  http://en.wikipedia.org/wiki/List_of_poker_hands
		long numberOfPossibleFiveCardHands = (52 * 51 * 50 * 49 * 48) / (5 * 4 * 3 * 2 * 1);
		expectedProbabilities.put(HandType.STRAIGHT_FLUSH,    40F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.FLUSH,           5108F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.STRAIGHT,       10200F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.FOUR_OF_A_KIND,   624F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.FULL_HOUSE,      3744F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.THREE_OF_A_KIND,54912F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.TWO_PAIR,      123552F/numberOfPossibleFiveCardHands);
		expectedProbabilities.put(HandType.ONE_PAIR,     1098240F/numberOfPossibleFiveCardHands);
	}

	private static final int HANDS = 100000;

	public static void main(String[] args) {

		// some examples of creating, shuffling and drawing a 5 card hand from a deck of cards
		Deck deck = new Deck();
		System.out.println("A newly opened deck:\n" + deck);
		System.out.println("\n");

		deck.shuffle();
		System.out.println("shuffling... shuffling... shuffling");

		Hand hand = new Hand(deck.dealHand(5));
		System.out.println("Your hand:\n" + hand);
		System.out.println("\n");
		System.out.println("You have: " + hand.has());

		System.out.println("\n");
		System.out.println("What remains in deck:\n" + deck);
		System.out.println("\n");

		System.out.println("Number of cards left in deck:\n" + deck.getCards().size());
		System.out.println("\n");


		// Begin the poker simulation and compute the results ...
		System.out.println("Start simulating "+HANDS+" random poker hands:");


		Map<HandType, Integer> handTypeCountMap = new HashMap<>();
		for (int i = 0; i < HANDS; i++) {

			deck = new Deck();
			deck.shuffle();
			hand = new Hand(deck.dealHand(5));  // deal a 5 card hand from the top of the deck
			HandType handType = HandType.NOTHING;

			if (i>100000 && i%100000 == 0) System.out.println("simulated "+i+" random poker hands ...");
			if (i>0 && i<100000 && i%10000 == 0) System.out.println("simulated "+i+" random poker hands ...");

			if (hand.isFourOfAKind()) {
				handType = HandType.FOUR_OF_A_KIND;
			} else if (hand.isFullHouse()) {
				handType = HandType.FULL_HOUSE;
			} else if (hand.isThreeOfAKind()) {
				handType = HandType.THREE_OF_A_KIND;
			} else if (hand.isTwoPair()) {
				handType = HandType.TWO_PAIR;
			} else if (hand.isOnePair()) {
				handType = HandType.ONE_PAIR;
			} else {

				//  First we want to see if Aces can play low in straights and straight-flushes
				if (hand.isStraightFlush()) {
					handType = HandType.STRAIGHT_FLUSH;
				} else if (hand.isFlush()) {
					handType = HandType.FLUSH;
				} else if (hand.isStraight()) {
					handType = HandType.STRAIGHT;
				}

				//  Now we want to see if Aces can play high in straights and straight-flushes
				if (hand.hasAces()) {
					for (Card card: hand.getCards()) {
						if (card.isAce()) {
							card.swapTheRankOfHowAceIsPlayed();
						}
					}

					hand.order(); // reorder using the new rank of ace

					if (hand.isStraightFlush()) {
						handType = HandType.STRAIGHT_FLUSH;
					} else if (hand.isStraight()) {
						handType = HandType.STRAIGHT;
					}
				}
			}


			incrementHandTypeCount(handType, handTypeCountMap);
		}

		System.out.println("simulated "+HANDS+" random poker hands");
		System.out.println("Done\n");

	}

	private static void incrementHandTypeCount(HandType handType, Map<HandType, Integer> handTypeCountMap) {
		Integer count = handTypeCountMap.get(handType);
		if (count == null) {
			handTypeCountMap.put(handType, 1);
		} else {
			handTypeCountMap.put(handType, ++count);
		}
	}

}
