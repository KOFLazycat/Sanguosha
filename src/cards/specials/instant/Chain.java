package cards.specials.instant;

import player.PlayerClientComplete;
import update.Update;
import update.operations.Operation;

public class Chain extends Instant
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4842163599907076818L;

	public Chain(int num, Suit suit) 
	{
		super(num, suit);
	}

	@Override
	public String getName() 
	{
		return "Chain";
	}

	@Override
	public Operation onActivatedBy(PlayerClientComplete player,
			Update next) {
		// TODO Auto-generated method stub
		return null;
	}
}
