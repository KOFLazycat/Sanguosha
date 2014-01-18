package events;

import player.PlayerOriginal;
import player.PlayerOriginalClientComplete;
import update.DisposalOfEquipment;
import update.Update;
import update.UseOfCards;
import core.Card;
import core.Equipment;
import core.Framework;
import core.Operation;
import core.PlayerInfo;

public class EquipOperation implements Operation
{
	private Equipment equipment;
	private PlayerInfo source;
	private Update next;
	
	public EquipOperation(PlayerInfo source, Equipment equipment, Update next)
	{
		this.equipment = equipment;
		this.source = source;
		this.next = next;
	}
	@Override
	public void frameworkOperation(Framework framework) 
	{
		framework.sendToAllClients(this);
	}

	@Override
	public void playerOperation(PlayerOriginalClientComplete player)
	{
		if(player.isEqualTo(source))
		{
			Equipment old = player.equip(equipment);
			if(old != null)
			{
				player.sendToMaster(new DisposalOfEquipment(source,old,next));
			}
			else
				player.sendToMaster(next);
		}
		else
			player.findMatch(source).equip(equipment);
	}

	@Override
	public void onPlayerSelected(PlayerOriginalClientComplete operator,PlayerOriginal player) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCardSelected(PlayerOriginalClientComplete operator, Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelledBy(PlayerOriginalClientComplete player) 
	{
		player.setConfirmEnabled(false);
		player.setCancelEnabled(false);
		player.setCardOnHandSelected(equipment, false);
	}

	@Override
	public void onConfirmedBy(PlayerOriginalClientComplete player)
	{
		player.setOperation(null);
		player.setCardOnHandSelected(equipment, false);
		player.setCancelEnabled(false);
		player.sendToMaster(new UseOfCards(source,equipment,this));
	}

}
