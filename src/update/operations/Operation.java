package update.operations;

import cards.Card;
import player.PlayerOriginal;
import player.PlayerClientComplete;
import update.Update;
/**
 * An operation that listens to user actions (confirm, cancel, select cards/targets, etc.)
 * @author Harry
 *
 */
public abstract class Operation extends Update
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5751768653876815254L;
	public Operation(Update next) 
	{
		super(next);
	}

	/**
	 * called when a player is selected as target by operator
	 * @param operator
	 * @param player
	 */
	public abstract void onPlayerSelected(PlayerClientComplete operator,PlayerOriginal player);
	
	/**
	 * called when a card is selected by operator
	 * @param card
	 */
	public abstract void onCardSelected(PlayerClientComplete operator, Card card);
	/**
	 * called when cancel is clicked by player
	 * @param player
	 */
	public abstract void onCancelledBy(PlayerClientComplete player);
	/**
	 * called when confirm is clicked by player
	 * @param player
	 */
	public abstract void onConfirmedBy(PlayerClientComplete player);
}
