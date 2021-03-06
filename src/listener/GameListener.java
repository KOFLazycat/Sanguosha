package listener;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import player.PlayerClientSimple;
import cards.Card;
import core.PlayerInfo;

public interface GameListener extends ActionListener
{
	/**
	 * invoked when a new player is added to game
	 * @param player
	 */
	public void onPlayerAdded(PlayerClientSimple player);
	/**
	 * invoked when a card on hand is selected
	 * @param card
	 */
	public void onCardSelected(Card card);
	/**
	 * invoked when a card on hand is unselected
	 * @param card
	 */
	public void onCardUnselected(Card card);
	/**
	 * invoked when a target is selected
	 * @param player
	 */
	public void onTargetSelected(PlayerInfo player);
	/**
	 * invoked when a target is unselected
	 * @param player
	 */
	public void onTargetUnselected(PlayerInfo player);
	/**
	 * invoked when a card on hand is set selectable/unselectable
	 * @param card
	 * @param selectable
	 */
	public void onCardSetSelectable(Card card, boolean selectable);
	/**
	 * invoked when a target is set selectable/unselectable
	 * @param player
	 * @param selectable
	 */
	public void onTargetSetSelectable(PlayerInfo player, boolean selectable);
	/**
	 * invoked to enable/disable confirm button
	 * @param isEnabled
	 */
	public void onConfirmSetEnabled(boolean isEnabled);
	/**
	 * invoked to enable/disable cancel button
	 * @param isEnabled
	 */
	public void onCancelSetEnabled(boolean isEnabled);
	/**
	 * invoked to enable/disable end button
	 * @param isEnabled
	 */
	public void onEndSetEnabled(boolean isEnabled);
	/**
	 * update the size of game deck
	 * @param size
	 */
	public void onDeckSizeUpdated(int size);
	
	public void onSetMessage(String message);
	
	public void onClearMessage();
	
	/**
	 * Display the card selection pane on screen
	 * @param player : the owner of these cards
	 * @param showHand : whether to display player's cards on hand
	 * @param showEquipments : whether to display player's equipments
	 * @param showDecisions : whether to display player's decision area
	 */
	public void onDisplayCardSelectionPane(PlayerClientSimple player, boolean showHand, boolean showEquipments, boolean showDecisions);
	
	public void onDisplayCustomizedSelectionPaneAtCenter(JPanel panel);
	
	public void onRemoveCustomizedSelectionPane();
}