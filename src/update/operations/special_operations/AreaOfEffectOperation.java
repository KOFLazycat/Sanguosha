package update.operations.special_operations;

import java.util.ArrayList;
import java.util.List;

import player.PlayerClientComplete;
import player.PlayerOriginal;
import update.Damage;
import update.Update;
import update.UseOfCards;
import cards.Card;
import cards.equipments.Equipment.EquipmentType;
import core.PlayerInfo;

public abstract class AreaOfEffectOperation extends SpecialOperation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4879416749526010430L;
	protected PlayerInfo source;
	private List<PlayerInfo> visitedPlayers;
	protected PlayerInfo currentTarget;
	protected Card aoe;
	protected boolean sent;
	public AreaOfEffectOperation(PlayerClientComplete player, Card aoe, Update next) 
	{
		super(next, player.getCurrentStage().getSource());
		this.aoe = aoe;
		this.source = player.getPlayerInfo();
		this.currentTarget = player.getNextPlayerAlive();
		visitedPlayers = new ArrayList<PlayerInfo>();
		sent = false;
	}
	@Override
	public PlayerInfo getCurrentTarget()
	{
		return currentTarget;
	}
	@Override
	public void onPlayerSelected(PlayerClientComplete operator,PlayerOriginal player)
	{
		// no target selection
	}

	@Override
	public void onCardSelected(PlayerClientComplete operator, Card card) 
	{
		cardSelectedAsReaction(operator, card);
	}

	@Override
	public void onCancelledBy(PlayerClientComplete player) 
	{
		if(player.matches(source))//cancel operation
		{
			player.setCardOnHandSelected(aoe, false);
			player.setConfirmEnabled(false);
			player.setCancelEnabled(false);
			player.setOperation(null);
		}
		else//target
		{
			if(reactionCard != null)//cancel selection
			{
				player.setCardOnHandSelected(reactionCard, false);
				player.setConfirmEnabled(false);
				reactionCard = null;
				player.setOperation(this);
			}
			else //give up
			{
				setStage(AFTER);
				player.setAllCardsOnHandSelectable(false);
				player.setCancelEnabled(false);
				player.setOperation(null);
				player.sendToMaster(new Damage(aoe,source,currentTarget,this));
			}
		}
	}

	@Override
	public void onConfirmedBy(PlayerClientComplete player) 
	{
		if(!sent)//confirm AOE
		{
			sent = true;
			player.setCardOnHandSelected(aoe, false);
			player.sendToMaster(new UseOfCards(source,aoe,this));
		}
		else//target reacted
		{
			player.setCardOnHandSelected(reactionCard, false);
			setStage(AFTER);
			player.sendToMaster(new UseOfCards(currentTarget,reactionCard,this));
		}
	}
	@Override
	protected void playerOpBefore(PlayerClientComplete player)
	{
		if(player.matches(currentTarget))
		{
			//here for future skills
			if(player.isEquipped(EquipmentType.SHIELD) && !player.getShield().mustReactTo(aoe))
				setStage(AFTER);
			else
				setStage(NEUTRALIZATION);
			player.sendToMaster(this);
		}
	}
	@Override
	protected void playerOpEffect(PlayerClientComplete player) 
	{
		if(player.matches(currentTarget))
		{
			reactionCard = null;
			if(!player.isAlive())//already dead
			{
				playerOpAfter(player);//next player
				return;
			}
			player.setOperation(this);
			AOETargetOperation(player);
		}
		
	}
	/**
	 * what does a target do, when a special card takes effect
	 * @param target : current target of special card
	 */
	protected abstract void AOETargetOperation(PlayerClientComplete target);
	@Override
	protected void playerOpAfter(PlayerClientComplete player)
	{
		if(player.matches(currentTarget))
		{
			visitedPlayers.add(currentTarget);
			currentTarget = player.getNextPlayerAlive();
			setStage(BEFORE);
			
			if(currentTarget.equals(source) || visitedPlayers.contains(currentTarget))//cycle complete
				player.sendToMaster(getNext());
			else
				player.sendToMaster(this);
		}
	}
}
