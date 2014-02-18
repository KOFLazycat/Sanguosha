package cards.equipments.weapons;


public class DoubleSword extends Weapon
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7148832102875606951L;

	public DoubleSword(int num, Suit suit) 
	{
		super(2, num, suit);
	}

	@Override
	public String getName() 
	{
		return "DoubleSword";
	}

}
