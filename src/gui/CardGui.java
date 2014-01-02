package gui;
import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import core.Card;

public class CardGui extends JButton
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8973362684095284243L;
	public static final int WIDTH = 142;
	public static final int HEIGHT = 200;
	private Card c;
	private String number;
	private Color color;
	private Image suit;
	private Image img;
	public CardGui(Card card)
	{
		c = card;	
		setSize(WIDTH,HEIGHT);
		number = numToString(c.getNumber());
		readSuit(card.getSuit());
		readName(c.getName());
	}
	private void readName(String name)
	{
		try
		{
			img = ImageIO.read(getClass().getResource("cards/"+name+".png"));
		} 
		catch (IOException e) 
		{
			System.err.println("File not found");
		}
	}
	private void readSuit(int n)
	{
		try
		{
			switch(n)
			{
			case Card.SPADE:
				suit = ImageIO.read(getClass().getResource("cards/spade.png"));
				color = Color.BLACK;
				break;
			case Card.HEART:
				suit = ImageIO.read(getClass().getResource("cards/heart.png"));
				color = Color.RED;
				break;
			case Card.CLUB:
				suit = ImageIO.read(getClass().getResource("cards/club.png"));
				color = Color.BLACK;
				break;
			case Card.DIAMOND:
				suit = ImageIO.read(getClass().getResource("cards/diamond.png"));
				color = Color.RED;
				break;
			}
		}
		catch(IOException e)
		{
			System.err.println("File not found");
		}
		
	}
	private String numToString(int n)
	{
		if(n == 13)
			return "K";
		else if(n == 12)
			return "Q";
		else if(n == 11)
			return "J";
		else if(n == 1)
			return "A";
		else
			return n+"";
	}
	public Card getCard()
	{
		return c;
	}
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(img,0,0,null);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
		g.setColor(color);
		g.drawImage(suit, 10, 28,null);
		if(number.length() == 1)
			g.drawString(number, 15, 25);
		else
			g.drawString(number, 10, 25);
	}

}
