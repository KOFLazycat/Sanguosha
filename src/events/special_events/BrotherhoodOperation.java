package events.special_events;

import player.PlayerOriginalClientComplete;
import update.IncreaseOfHealth;
import update.Update;
import core.Card;

public class BrotherhoodOperation extends AreaOfEffectOperation
{

	public BrotherhoodOperation(PlayerOriginalClientComplete player, Card aoe,Update next)
	{
		super(player, aoe, next);
		currentTarget = source;
	}

	@Override
	protected void playerOpBefore(PlayerOriginalClientComplete player)
	{
		if(player.isEqualTo(currentTarget))
		{
			if(player.getHealthCurrent() == player.getHealthLimit())//not hurt
				setStage(AFTER);
			else
				setStage(NEUTRALIZATION);
			player.sendToMaster(this);
		}
	}
	@Override
	protected void targetOp(PlayerOriginalClientComplete target)
	{
		this.setStage(AFTER);
		target.sendToMaster(new IncreaseOfHealth(source,currentTarget,this));
	}

}
