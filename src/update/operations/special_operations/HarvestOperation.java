package update.operations.special_operations;

import gui.CardGui;
import gui.PanelGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import player.PlayerClientComplete;
import update.ReceiveCards;
import update.RecycleCards;
import update.Update;
import cards.Card;
import core.Framework;

public class HarvestOperation extends AreaOfEffectOperation
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5403926437292944739L;
	private boolean initialized;
	private List<Card> remainingCards;
	private List<Card> allCards;
	private byte playerCount;
	private int deckSize;
	
	public HarvestOperation(PlayerClientComplete player, Card aoe, Update next) 
	{
		super(player, aoe, next);
		this.currentTarget = this.source;
		initialized = false;
		remainingCards = new ArrayList<Card>();
		allCards = new ArrayList<Card>();
		playerCount = player.getNumberOfPlayersAlive();
	}

	@Override
	public void frameworkOperation(Framework framework)
	{
		if(!initialized)
		{
			allCards.addAll(framework.getDeck().drawMany(playerCount));
			remainingCards.addAll(allCards);
			deckSize = framework.getDeck().getDeckSize();
		}
		framework.sendToAllClients(this);
	}
	@Override
	protected void AOETargetOperation(PlayerClientComplete target) 
	{
		target.getGameListener().onDisplayCustomizedSelectionPaneAtCenter(createPanel(target.getGameListener()));
	}

	@Override
	public void onCardSelected(PlayerClientComplete operator, Card card) 
	{
		remainingCards.remove(card);
		setStage(AFTER);
		operator.sendToMaster(new ReceiveCards(this,operator.getPlayerInfo(),card));
	}

	private SelectionPanel createPanel(ActionListener listener)
	{
		return new SelectionPanel(allCards, remainingCards, listener);
	}
	@Override
	protected void playerOpBefore(PlayerClientComplete player)
	{
		if(!initialized)
		{
			initialized = true;
			player.getGameListener().onDeckSizeUpdated(deckSize); //update deck size
		}
		player.setOperation(this);
		player.getGameListener().onDisplayCustomizedSelectionPaneAtCenter(createPanel(null)); //display selection pane
		super.playerOpBefore(player);
	}
	
	@Override
	public String getName() 
	{
		return "Harvest";
	}
	private class SelectionPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3681521647598800928L;
		private static final int LABEL_HEIGHT = 40;
		
		SelectionPanel(List<Card> all, List<Card> remaining, ActionListener listener)
		{
			setBackground(new Color(222,184,135));
			int size = all.size();
			int width = CardGui.WIDTH * (size > 4 ? 4 : size) + PanelGui.WIDTH/4;
			int height = LABEL_HEIGHT + CardGui.HEIGHT * ((size-1) / 4 + 1);
			setSize(width, height);
			setLayout(null);
			JLabel label = new JLabel("Harvest: Waiting for "+currentTarget.getName()+" to select a card.");
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(new Font(Font.MONOSPACED,Font.BOLD,20));
			label.setSize(width, LABEL_HEIGHT);
			add(label);
			
			JPanel cardPanel = new JPanel();
			cardPanel.setLayout(null);
			cardPanel.setSize(width - PanelGui.WIDTH/4,height-LABEL_HEIGHT);
			cardPanel.setLocation(PanelGui.WIDTH/8,LABEL_HEIGHT);
			int x = 0;
			int y = 0;
			for(Card card : all)
			{
				CardGui gui = new CardGui(card);
				gui.setLocation(x,y);
				x += CardGui.WIDTH;
				if(x == 4 * CardGui.WIDTH)
				{
					x = 0;
					y += CardGui.HEIGHT;
				}
				if(!remaining.contains(card))
					gui.setEnabled(false);
				if(listener != null)
					gui.addActionListener(listener);
				cardPanel.add(gui);
			}
			add(cardPanel);
		}
	}
	/**
	 * Discard the remaining cards
	 */
	@Override
	public Update getNext()
	{
		if(remainingCards.size() != 0)
			insertNext(new RecycleCards(null,remainingCards, this.source));
		return super.getNext();
	}
}
