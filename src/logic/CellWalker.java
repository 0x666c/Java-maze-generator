package logic;

import java.awt.Color;
import java.util.Random;
import java.util.Stack;

public class CellWalker {
	private final Stack<MazeCell> cellStack;
	private final MazeCell[][] cells;
	private final MazeGenerator gen;
	
	private static final Color MY_ORANGE = new Color(0xFF6216);
	
	int xB, yB;
	int xCell = -1, yCell = -1; // Starting position
	
	public CellWalker(MazeGenerator gen) {
		this.gen = gen;
		cellStack = new Stack<>();
		cells = gen.getCells();
		xCell = gen.startindCellX;
		yCell = gen.startindCellY;
		
		xB = xCell;
		yB = yCell;
		
		cells[xCell][yCell].setBeginningCell(true);
		
		MazeCell prevCell = cells[xCell][yCell];
		do {
			boolean validUp = false;
			boolean validDown = false;
			boolean validLeft = false;
			boolean validRight= false;
			
			boolean valid = false;
			
			boolean nonValidLast = false;
			
			int dir = 0;
			while(!valid)
			{
				Random random = new Random();
				
				if(yCell-1 >= 0)
					validUp = !cells[xCell][yCell-1].isVisited();
				if(yCell+1 < cells[0].length)
					validDown = !cells[xCell][yCell+1].isVisited();
				if(xCell-1 >= 0)
					validLeft = !cells[xCell-1][yCell].isVisited();
				if(xCell+1 < cells.length)
					validRight = !cells[xCell+1][yCell].isVisited();
				
				
				if((validUp || validDown || validLeft || validRight) == false)
				{
					nonValidLast = true;
					
					MazeCell pc = cellStack.pop();
					
					prevCell = cells[xCell][yCell];
					prevCell.setColor(MY_ORANGE);
					
					xCell = pc.x / (pc.SIZE);
					yCell = pc.y / (pc.SIZE);
					
					cells[xCell][yCell].setColor(Color.RED);
					
					try {Thread.sleep(gen.speed.sleepDelay/2);} catch (InterruptedException e) {}
					
					continue;
				}
				
				if(nonValidLast)
				{
					prevCell = cells[xCell][yCell];
					prevCell.setColor(MY_ORANGE);
				}
				
				switch (dir = random.nextInt(4)) {
				case 0:
					if(xCell+1 < cells.length && validRight)
					{
						xCell++;
						prevCell.rightWall = false;
						valid = true;
					}
					break;
				case 1:
					if(xCell-1 >= 0 && validLeft)
					{
						xCell--;
						prevCell.leftWall = false;
						valid = true;
					}
					break;
				case 2:
					if(yCell+1 < cells[0].length && validDown)
					{
						yCell++;
						prevCell.bottomWall = false;
						valid = true;
					}
					break;
				case 3:
					if(yCell-1 >= 0 && validUp)
					{
						yCell--;
						prevCell.topWall = false;
						valid = true;
					}
					break;
				}
			}
			cells[xCell][yCell].visit();
			
			cellStack.push(cells[xCell][yCell]);
			
			cells[xCell][yCell].setColor(Color.RED);
			
			prevCell.setColor(MY_ORANGE);
			
			if(dir == 3)cells[xCell][yCell].bottomWall = prevCell.topWall;
			if(dir == 2)cells[xCell][yCell].topWall = prevCell.bottomWall;
			if(dir == 1)cells[xCell][yCell].rightWall = prevCell.leftWall;
			if(dir == 0)cells[xCell][yCell].leftWall = prevCell.rightWall;

			
			prevCell = cells[xCell][yCell];
			
			try {Thread.sleep(gen.speed.sleepDelay);} catch (InterruptedException e) {}
			
		} while(!areAllCellsVisited(cells));
		
		cells[xCell][yCell].setColor(MY_ORANGE);
		
		int rX = xCell;
		int rY = yCell;
		do {
			rX = new Random().nextInt(cells.length);
		} while(rX == xCell && cells[rX][rY].isDestinationCell());
		
		if(!(cells.length == 2 && cells[0].length == 1))
			do {
				rY = new Random().nextInt(cells[0].length);
			} while(rY == yCell &&
					cells[rX][rY].isDestinationCell());
		
		
		if(cells.length == 1) rY = cells[0].length - 1;
		if(cells[0].length == 1) rX = cells.length - 1;
		
		cells[rX][rY].setDestinationCell(true);
		System.out.println("Cell ["+rX+"]["+rY+"] is the exit");
		System.out.println("Cell ["+xB+"]["+yB+"] is the entrance");
		
	}
	
	private boolean areAllCellsVisited(MazeCell[][] cells)
	{
		for (MazeCell[] mazeCells : cells) {
			for (MazeCell mazeCell : mazeCells) {
				if(!mazeCell.isVisited()) return false;
			}
		}
		return true;
	}
}