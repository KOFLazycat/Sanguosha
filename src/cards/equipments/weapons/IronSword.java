package cards.equipments.weapons;

public class IronSword extends Weapon
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4813948682586646901L;

	public IronSword(int num, Suit suit)
	{
		super(2, num, suit);
	}

	@Override
	public String getName() 
	{
		return "IronSword";
	}

}
