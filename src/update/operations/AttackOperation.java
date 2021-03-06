package update.operations;

import player.PlayerClientComplete;
import player.PlayerClientSimple;
import player.PlayerOriginal;
import update.Damage;
import update.Update;
import update.UseOfCards;
import cards.Card;
import cards.basics.Attack;
import cards.basics.Dodge;
import cards.equipments.Equipment.EquipmentType;
import core.Framework;
import core.PlayerInfo;

/**
 * The "Attack" event, multistage and very complicated, as many skills and equipment 
 * effects can be invoked at each stage
 * @author Harry
 *
 */
public class AttackOperation extends Operation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -320291606677077531L;

	public static final int TARGET_SELECTION = 1;
	
	public static final int BEFORE_TARGET_LOCKED = 2;//target operation
	public static final int TARGET_LOCKED = 3;//source & target operation
	public static final int AFTER_TARGET_LOCKED_SKILLS = 4;//source operation
	public static final int AFTER_TARGET_LOCKED_WEAPONS = 5;//source operation
	public static final int DODGE_DECISION = 6;//target operation, for shield effects
	
	public static final int USING_DODGE = 7;//target operation, for target skills/shields
	public static final int AFTER_USING_DODGE = 8;//target operation, for target skills
	public static final int ATTACK_DODGED_SKILLS = 9;//source operation
	public static final int ATTACK_DODGED_WEAPONS = 10;//source operation
	
	public static final int ATTACK_NOT_DODGED_PREVENTION = 11;//only for skill: "fog"
	public static final int ATTACK_NOT_DODGED_ADDITION = 12;//only for skill: "gale"
	
	public static final int BEFORE_DAMAGE = 13;//source operation.last stage, for weapon effects
	public static final int DAMAGE = 14;//target operation, take damage
	public static final int END = 15;
	
	private PlayerInfo target;
	private PlayerInfo source;
	private int stage;
	private boolean dodgeable;//is this attack dodge-able? (some skills make an attack un-dodge-able
	private Attack attack;//in the future, there will be transformed attacks, so it will be changed
	private Card dodge;//the dodge that target uses
	private Damage damage;//the damage that this attack carries
	
	public AttackOperation(PlayerClientComplete source, Attack attack, Update next)
	{
		super(next);
		this.source = source.getPlayerInfo();
		target = null;
		this.attack = attack;
		stage = TARGET_SELECTION;
		dodgeable = true;
		dodge = null;
		enableTargets(source);
	}
	private void enableTargets(PlayerClientComplete source)
	{
		for(PlayerClientSimple p : source.getOtherPlayers())
			if(p.isAlive() && source.isPlayerInRange(p,source.getNumberOfPlayersAlive()))//if p is alive an in attack range
				source.setTargetSelectable(p.getPlayerInfo(), true);//in the future, add skill decisions
	}
	private void endOfTargetOperation(PlayerClientComplete player)
	{
		player.setCancelEnabled(false);
		if(dodge != null)
			player.setCardOnHandSelected(dodge, false);
		player.setAllCardsOnHandSelectable(false);
	}
	@Override
	public void frameworkOperation(Framework framework)
	{
		framework.sendToAllClients(this);
	}
	@Override
	public void playerOperation(PlayerClientComplete player) 
	{
		System.out.println(player.getName()+" AttackEvent "+stage);
		if(player.matches(target))//target operations
		{
			if(stage == BEFORE_TARGET_LOCKED)//target skills to change target
			{
				stage = TARGET_LOCKED;
				//insert skill inquiries here
				player.sendToMaster(this);//for now
			}
			else if(stage == DODGE_DECISION)//target skills/shields to cancel the attack
			{
				if(!player.isEquipped(EquipmentType.SHIELD) || player.getShield().mustReactTo(attack))
					//no equipment or equipment cannot cancel the attack
				{
					stage = USING_DODGE;
					player.sendToMaster(this);
				}
				else // can cancel the attack. (in the future, take note of hero skills)
				{
					stage = END;
					player.sendToMaster(this);
				}
			}
			else if(stage == USING_DODGE)//target chooses whether to dodge the attack
			{
				player.setOperation(this);//push it to operation
				player.setCardSelectableByName(Dodge.DODGE, true);//enable dodges
				player.setCancelEnabled(true);//enable cancel (choose not to dodge)
				player.getGameListener().onSetMessage("You are attacked by "+source.getName()+", do you want to dodge?");
			}
			else if(stage == AFTER_USING_DODGE)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == ATTACK_NOT_DODGED_PREVENTION)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == ATTACK_NOT_DODGED_ADDITION)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == DAMAGE)
			{
				stage++;
				player.sendToMaster(damage);
			}
		}
		else if(player.matches(source))//source operation
		{
			if(stage == TARGET_LOCKED)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == AFTER_TARGET_LOCKED_SKILLS)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == AFTER_TARGET_LOCKED_WEAPONS)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == ATTACK_DODGED_SKILLS)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == ATTACK_DODGED_WEAPONS)
			{
				stage = END;
				player.sendToMaster(this);
			}
			else if(stage == BEFORE_DAMAGE)
			{
				stage++;
				player.sendToMaster(this);
			}
			else if(stage == END)
				player.sendToMaster(getNext());
		}
	}


	@Override
	public void onPlayerSelected(PlayerClientComplete operator, PlayerOriginal player) 
	{
		if(stage == TARGET_SELECTION)//now selecting target
		{
			if(target == null)//set a target
			{
				target = player.getPlayerInfo();
				operator.selectTarget(target);
				operator.setConfirmEnabled(true);//target set, can confirm the attack
				operator.setCancelEnabled(true);//can cancel as well
			}
			else if(player.matches(target))//cancel target
			{
				operator.unselectTarget(target);
				target = null;
				operator.setConfirmEnabled(false);
			}
			else//change target
			{
				operator.unselectTarget(target);
				target = player.getPlayerInfo();
				operator.selectTarget(target);
			}
		}
		else
			System.err.println("AttackEvent: invalid player selection at stage "+stage);
	}
	@Override
	public void onConfirmedBy(PlayerClientComplete player) 
	{
		if(player.matches(source))//attack confirmed by source
		{
			int amount = 1;//in the future, player.getDamageAmount (or similar) to take into account skill effects
			player.useAttack();//set attack used once
			if(player.isWineUsed())//wine increases attack damage by 1
			{
				amount++;
				player.useWine();
			}
			player.unselectTarget(target);
			player.setCardOnHandSelected(attack, false);
			damage = new Damage(amount,attack.getElement(),source,target,this);
			stage = BEFORE_TARGET_LOCKED;
			player.sendToMaster(new UseOfCards(source,attack,this));
		}
		else if(player.matches(target))//target dodged
		{
			stage = AFTER_USING_DODGE;
			endOfTargetOperation(player);
			player.sendToMaster(new UseOfCards(target,dodge,this));
		}
		else
			System.err.println("AttackEvent: Invalid confirmation at stage "+stage);
	}
	@Override
	public void onCancelledBy(PlayerClientComplete player)
	{
		if(stage == TARGET_SELECTION)//not sent yet
		{
			cancelOperation(player,attack);
			player.setCardOnHandSelected(attack, false);
			player.setCancelEnabled(false);
		}
		else if(stage == USING_DODGE)//target operation
		{
			if(dodge != null)//unselect dodge
			{
				player.setCardOnHandSelected(dodge, false);
				player.setConfirmEnabled(false);
				dodge = null;
				player.setOperation(this);//push back to operation active
			}
			else // choose not to dodge
			{
				stage = ATTACK_NOT_DODGED_PREVENTION;
				endOfTargetOperation(player);
				player.sendToMaster(this);
			}
		}
		else
			System.err.println("AttackEvent: invalid cancellation at stage "+stage);
	}
	private void cancelOperation(PlayerClientComplete operator, Card card)
	{
		operator.setConfirmEnabled(false);//unable to confirm
		if(target != null)
			operator.unselectTarget(target);//target not selected
		operator.setAllTargetsSelectableExcludingSelf(false);
	}
	@Override
	public void onCardSelected(PlayerClientComplete operator, Card card) 
	{
		if(operator.matches(target))//target select a card (must be dodge)
		{
			if(dodge != null)//unselect previous dodge
			{
				operator.setCardOnHandSelected(dodge, false);
				if(dodge.equals(card))//unselect dodge
				{
					dodge = null;
					operator.setConfirmEnabled(false);
				}
				else//change
				{
					dodge = card;
					operator.setCardOnHandSelected(card, true);
				}
			}
			else //select a new dodge
			{
				dodge = card;
				operator.setCardOnHandSelected(card, true);
				operator.setConfirmEnabled(true);
			}
		}
		else
			System.err.println("AttackEvent: Bystander selecting cards");
	}
}
