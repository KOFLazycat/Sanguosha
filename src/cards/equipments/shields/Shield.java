package cards.equipments.shields;

import cards.Card;
import cards.equipments.Equipment;
import update.Damage;

public abstract class Shield extends Equipment
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3973053122566006924L;

	public Shield(int num, Suit suit) 
	{
		super(num, suit, EquipmentType.SHIELD);
	}
	public abstract boolean mustReactTo(Card card);
	
	//public abstract boolean hasEffectOn(Update update);
	
	public abstract void modifyDamage(Damage damage);
}
