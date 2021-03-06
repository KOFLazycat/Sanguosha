package player;

import java.util.List;

import listener.CardDisposalListener;
import listener.CardOnHandListener;
import listener.EquipmentListener;
import listener.HealthListener;
import cards.Card;
import cards.equipments.Equipment;
import cards.equipments.Equipment.EquipmentType;

/**
 * client side simple player implementation, used to hold information of "other players"
 * @author Harry
 *
 */
public class PlayerClientSimple extends PlayerServerSimple
{
	private HealthListener healthListener;
	private CardOnHandListener cardsOnHandListener;
	private EquipmentListener equipmentListener;
	private CardDisposalListener disposalListener;
	
	public PlayerClientSimple(String name) 
	{
		super(name);
	}
	public PlayerClientSimple(String name, int position) 
	{
		super(name,position);
	}
	/**
	 * register health listener to monitor the change in health
	 * @param listener
	 */
	public void registerHealthListener(HealthListener listener)
	{
		healthListener = listener;
		healthListener.onSetHealthLimit(getHero().getHealthLimit());
		healthListener.onSetHealthCurrent(getHealthCurrent());
	}
	/**
	 * register card-on-hand listener to monitor the change of card-on-hand
	 * @param listener
	 */
	public void registerCardOnHandListener(CardOnHandListener listener)
	{
		cardsOnHandListener = listener;
	}
	/**
	 * register equipment listener to monitor the change of equipments
	 * @param listener
	 */
	public void registerEquipmentListener(EquipmentListener listener)
	{
		equipmentListener = listener;
	}
	/**
	 * register card disposal listener to monitor the use and disposal of cards
	 * @param listener
	 */
	public void registerCardDisposalListener(CardDisposalListener listener)
	{
		disposalListener = listener;
	}
	/**
	 * <li>{@link HealthListener} notified
	 */
	@Override
	public void changeHealthLimitTo(int n)
	{
		super.changeHealthLimitTo(n);
		healthListener.onSetHealthLimit(getHero().getHealthLimit());
	}
	/**
	 * <li>{@link HealthListener} notified
	 */
	@Override
	public void changeHealthLimitBy(int n)
	{
		super.changeHealthLimitBy(n);
		healthListener.onSetHealthLimit(getHero().getHealthLimit());
	}
	/**
	 * <li>{@link HealthListener} notified
	 */
	@Override
	public void changeHealthCurrentTo(int n)
	{
		super.changeHealthCurrentTo(n);
		healthListener.onSetHealthCurrent(n);
	}
	/**
	 * <li>{@link HealthListener} notified
	 * @param n
	 */
	@Override
	public void changeHealthCurrentBy(int n)
	{
		super.changeHealthCurrentBy(n);
		healthListener.onHealthChangedBy(n);
	}
	/**
	 * <li>{@link CardOnHandListener} notified
	 * @param card
	 */
	@Override
	public void addCard(Card card)
	{
		super.addCard(card);
		cardsOnHandListener.onCardAdded(card);
	}
	/**
	 * <li>{@link CardOnHandListener} notified
	 * <li>{@link CardDisposalListener} notified
	 * @param card
	 */
	@Override
	public void useCard(Card card)
	{
		super.useCard(card);
		cardsOnHandListener.onCardRemoved(card);
		disposalListener.onCardUsed(card);
	}
	/**
	 * <li>{@link CardDisposalListener} notified
	 * @param card
	 */
	@Override
	public void discardCard(Card card)
	{
		super.discardCard(card);
		cardsOnHandListener.onCardRemoved(card);
		disposalListener.onCardDisposed(card);
	}
	public void showCard(Card card)
	{
		disposalListener.onCardDisposed(card);
	}
	public void showCards(List<? extends Card> equipments)
	{
		for(Card card : equipments)
			showCard(card);
	}
	@Override
	public void removeCardFromHand(Card card)
	{
		super.removeCardFromHand(card);
		cardsOnHandListener.onCardRemoved(card);
	}
	/**
	 * discard an equipment
	 * <li>{@link EquipmentListener} notified
	 * @param type
	 * @return the equipment discarded
	 */
	@Override
	public Equipment unequip(EquipmentType type)
	{
		Equipment e = super.unequip(type);
		equipmentListener.onUnequipped(type);
		return e;
	}
	/**
	 * equip new equipment, return the old one. Return null if nothing is replaced
	 * <li>{@link EquipmentListener} notified
	 * @param equipment : new equipment
	 * @return old equipment, null if no old equipment
	 */
	@Override
	public Equipment equip(Equipment equipment)
	{
		Equipment e = super.equip(equipment);
		equipmentListener.onEquipped(equipment);
		return e;
	}

	/**
	 * {@link HealthListener} notified
	 */
	@Override
	public void kill()
	{
		super.kill();
		healthListener.onDeath();
		equipmentListener.onUnequipped(EquipmentType.WEAPON);
		equipmentListener.onUnequipped(EquipmentType.SHIELD);
		equipmentListener.onUnequipped(EquipmentType.HORSEPLUS);
		equipmentListener.onUnequipped(EquipmentType.HORSEMINUS);
	}
	public void clearDisposalArea()
	{
		disposalListener.refresh();
	}
	//*************** method related to stages *****************



}
