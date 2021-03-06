package cards.equipments.shields;

import player.PlayerClientComplete;
import update.Damage;
import update.IncreaseOfHealth;
import update.Update;
import cards.Card;

public class SilverLion extends Shield
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4821532886423359596L;

	public SilverLion(int num, Suit suit) 
	{
		super(num, suit);
	}

	@Override
	public String getName()
	{
		return "Silver Lion";
	}

	@Override
	public void onUnequipped(PlayerClientComplete player, Update update)
	{
		if(player.getHealthCurrent() < player.getHealthLimit())
			update.insertNext(new IncreaseOfHealth(player.getPlayerInfo(), null));
	}	
	@Override
	public boolean mustReactTo(Card card) 
	{
		return true;
	}

	@Override
	public void modifyDamage(Damage damage) {
		if(damage.getAmount() > 1)
			damage.setAmount(1);
	}


}
