package logic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class MazeCell { static {random = new Random();}
	private static final Random random;
	
	public boolean topWall, bottomWall, leftWall, rightWall;
	private boolean isVisited = false;
	
	private boolean setDestinationCell = false;
	private boolean setBeginningCell = false;
	
	private Color gColor = Color.PINK;
	
	private static final Color MY_BLUE = new Color(0x2868FF);
	
	
	public int SIZE = 25,
			   LINE_THICKNESS = 3;
	
	public static final int DEFAULT_SIZE = 25, DEFAULT_BORDER_THICKNESS = 3;
	
	public final int x, y;
	
	
	
	public MazeCell(Integer Size, Integer LineThickness, int x, int y, boolean top, boolean bottom, boolean left, boolean right) {
		topWall = true;
		bottomWall = true;
		leftWall = true;
		rightWall = true;
		
		SIZE = Size;
		LINE_THICKNESS = LineThickness;
		
		this.x = x;
		this.y = y;
	}
	
	
	
	public void draw(Graphics g)
	{
		Color c = g.getColor();
		
		g.setColor(Color.WHITE);
		
		
		if(!topWall) {g.setColor(gColor);} g.fillRect(x, y, SIZE, LINE_THICKNESS); if(!topWall) {g.setColor(Color.WHITE);}  // Top 
		if(!bottomWall) {g.setColor(gColor);} g.fillRect(x, y + SIZE - LINE_THICKNESS, SIZE, LINE_THICKNESS); if(!bottomWall) {g.setColor(Color.WHITE);} // Bottom
		if(!leftWall) {g.setColor(gColor);} g.fillRect(x, y, LINE_THICKNESS, SIZE); if(!leftWall) {g.setColor(Color.WHITE);} // Left
		if(!rightWall) {g.setColor(gColor);} g.fillRect(x + SIZE - LINE_THICKNESS, y, LINE_THICKNESS, SIZE); if(!rightWall) {g.setColor(Color.WHITE);} // Right
		
		
		g.fillRect(x, y, LINE_THICKNESS, LINE_THICKNESS);
		g.fillRect(x + SIZE - LINE_THICKNESS, y, LINE_THICKNESS, LINE_THICKNESS);
		g.fillRect(x, y + SIZE - LINE_THICKNESS, LINE_THICKNESS, LINE_THICKNESS);
		g.fillRect(x + SIZE - LINE_THICKNESS, y + SIZE - LINE_THICKNESS, LINE_THICKNESS, LINE_THICKNESS);
		
		
		if(setDestinationCell)
			g.setColor(Color.GREEN);
		else if(setBeginningCell)
			g.setColor(MY_BLUE);
		else
			g.setColor(gColor);
		
		g.fillRect(x + LINE_THICKNESS, y + LINE_THICKNESS, SIZE - LINE_THICKNESS*2, SIZE - LINE_THICKNESS*2);
		
		g.setColor(c);
	}
	
	public void setBeginningCell(boolean setBeginningCell) {
		this.setBeginningCell = setBeginningCell;
	}
	public void setDestinationCell(boolean setDestinationCell) {
		this.setDestinationCell = setDestinationCell;
	}
	
	public void visit()
	{
		isVisited = true;
	}
	
	public boolean isVisited() {
		return isVisited;
	}
	
	public boolean isDestinationCell() {
		return setDestinationCell;
	}
	
	public void setColor(Color color)
	{
		gColor = color;
	}
	
	public static final MazeCell generateRandom(int x, int y)
	{
		return new MazeCell(x, y, DEFAULT_SIZE, DEFAULT_BORDER_THICKNESS, random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
	}
	
	public static final MazeCell generateRandom(int x, int y, int Size, int LineThickness)
	{
		return new MazeCell(Size, LineThickness, x, y, random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
	}
}
