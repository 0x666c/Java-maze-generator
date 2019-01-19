package logic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class MazeGenerator {
	
	private BufferStrategy bufferStrategy;
	private Canvas c;
	
	private MazeCell[][] cells;
	
	private volatile boolean alive = true;
	
	private volatile int cellSize = MazeCell.DEFAULT_SIZE;
	private volatile int borderTh = MazeCell.DEFAULT_BORDER_THICKNESS;
	
	private volatile boolean blockChanging = false;
	private volatile boolean visualGeneration = true;
	
	int offsetX;
	int offsetY;
	
	private volatile boolean enableEx = true;
	
	volatile int startindCellX = 0, startindCellY = 0; 
	
	volatile MazeGenSpeed speed = MazeGenSpeed.LIGHTNING;
	
	private int cellsX = 0; // Zero means calculate in process, other value means explicit definition
	private int cellsY = 0;
	
	private String[] timeConsumedRaw = {"-1", "-1"};
	private long time = -1;
	
	
	public MazeGenerator(Canvas c) {
		if(c.getBufferStrategy() == null)
			c.createBufferStrategy(3);
		bufferStrategy = c.getBufferStrategy();
		
		this.c = c;
		
		repaintThread.start();
	}
	

	private volatile boolean doPaint = false;
	Thread repaintThread = new Thread(new Runnable() {
		public void run() {
			while(alive) {
				while(!doPaint) {}
				try {Thread.sleep(10);} catch (InterruptedException e) {}
				repaint();
				Thread.yield();
				
			}
		}
	});
	
	/*try {
		tr.setPriority(
				Thread.currentThread().getPriority() - 1 < 0 ? 0 : Thread.currentThread().getPriority() - 1
				);
		} catch (SecurityException se)
		{
			// Nothing
		}*/
	public synchronized long generate()
	{
		reset();
		
		blockChanging = true;
		
		time = -1;
		
		long stamp = System.nanoTime();
		
		if(cellsX < 1)
			cellsX = c.getWidth() / cellSize < 1 ? 1 : c.getWidth() / cellSize;
		if(cellsY < 1)
			cellsY = c.getHeight() / cellSize < 1 ? 1 : c.getHeight() / cellSize;
		
		cells = new MazeCell[cellsX][cellsY];
		
		offsetX = c.getWidth() - (cellsX)*cellSize;
		offsetY = c.getHeight() - (cellsY)*cellSize;
		if(offsetX < 0) offsetX = 0;
		if(offsetY < 0) offsetY = 0;
		
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				cells[i][j] = MazeCell.generateRandom(i * cellSize + offsetX / 2, j * cellSize + offsetY / 2, cellSize, borderTh);
			}
		}
		
		
		doPaint = visualGeneration;
		
		
		new CellWalker(this);
		repaint();
		
		Graphics g = c.getBufferStrategy().getDrawGraphics();
		g.setColor(Color.BLACK);
		g.setFont(new Font("Consolas", Font.PLAIN, 22));
		g.drawString("Done!", 4 + offsetX / 2, 18 + offsetY / 2);
		
		
		g.dispose();
		c.getBufferStrategy().show();
		
		blockChanging = false;
		
		time = System.nanoTime() - stamp;
		
		return time;
	}
	
	/*public synchronized void generateThreaded()
	{
		blockChanging = true;
		Thread generationThread = new Thread(new Runnable() {
			public void run() {
				blockChanging = true;
				
				time = -1;
				
				if(cellsX < 1)
					cellsX = c.getWidth() / cellSize < 1 ? 1 : c.getWidth() / cellSize;
				if(cellsY < 1)
					cellsY = c.getHeight() / cellSize < 1 ? 1 : c.getHeight() / cellSize;
				
				cells = new MazeCell[cellsX][cellsY];
				System.out.println("Cell array x = " + cellsX);
				System.out.println("Cell array y = " + cellsY);
				
				offsetX = c.getWidth() - (cellsX)*cellSize;
				offsetY = c.getHeight() - (cellsY)*cellSize;
				if(offsetX < 0) offsetX = 0;
				if(offsetY < 0) offsetY = 0;
				
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[0].length; j++) {
						cells[i][j] = MazeCell.generateRandom(i * cellSize + offsetX / 2, j * cellSize + offsetY / 2, cellSize, borderTh);
					}
				}
				
				Thread tr = new Thread(new Runnable() {
					public void run() {
						while(alive) {
							try {Thread.sleep(10);} catch (InterruptedException e) {}
							repaint();
							Thread.yield();
						}
					}
				});
				try {
				tr.setPriority(
						Thread.currentThread().getPriority() - 1 < 0 ? 0 : Thread.currentThread().getPriority() - 1
						);
				} catch (SecurityException se)
				{
					// Nothing
				}
				if(visualGeneration)
					tr.start();
				
				
				new CellWalker(MazeGenerator.this);
				alive = false;
				repaint();
				
				Graphics g = c.getBufferStrategy().getDrawGraphics();
				g.setColor(Color.BLACK);
				g.setFont(new Font("Consolas", Font.PLAIN, 22));
				g.drawString("Done!", 4 + offsetX / 2, 32 + offsetY / 2);
				g.dispose();
				c.getBufferStrategy().show();
				
				blockChanging = false;
				try {
					tr.join();
				} catch (InterruptedException e) {}
				
				reset();
			}
		});
		generationThread.start();
	}*/
	public void saveAsPng(String path, int w, int h)
	{
		BufferedImage img = new BufferedImage(c.getWidth()-offsetX/2, c.getHeight()-offsetY/2, BufferedImage.TYPE_INT_RGB);
		Graphics2D ig = img.createGraphics();
		ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		repaint(ig);
		ig.dispose();
		BufferedImage img2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img2.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, -offsetX/2, -offsetY/2, w, h, 0, 0, img.getWidth(), img.getHeight(), null);
		System.out.println("Exporting image...");
		try {
			FileOutputStream out = new FileOutputStream(path);
			ImageIO.write(img2, "png", out);
			out.flush();
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Unwanted
	@Deprecated
	public void setCellsX(int amount) // Allows generation outside of viewport
	{
		if(enableEx) {
			if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
			if(amount < 1) {throw new RuntimeException("\"amount\" should be greater than 0");}
		}
		
		cellsX = amount;
	}
	@Unwanted
	@Deprecated
	public void setCellsY(int amount) // Allows generation outside of viewport
	{
		if(enableEx) {
			if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
			if(amount < 1) {throw new RuntimeException("\"amount\" should be greater than 0");}
		}
		
		cellsY = amount;
	}
	public void setCellSize(int size)
	{
		if(enableEx) {
			if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
			if(size < 0) {throw new RuntimeException("\"size\" should not be negative");}
		}
		
		cellSize = size;
	}
	public void setLineThickness(int thiccness)
	{
		if(enableEx) {
			if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
			if(thiccness < 0) {throw new RuntimeException("\"thickness\" should not be negative");}
			if(thiccness > cellSize - 1) {throw new RuntimeException("\"thickness\" should not exceed cell size");}
		}
		
		borderTh = thiccness;
	}
	public void setGenerationSpeed(MazeGenSpeed speed)
	{
		this.speed = speed;
	}
	public void setStartingCell(int x, int y)
	{
		if(x > -1) startindCellX = x;
		if(y > -1) startindCellY = y;
	}
	public void replaceCanvas(Canvas newCanvas)
	{
		if(enableEx) {
			if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
			if(newCanvas == null) {throw new RuntimeException("\"newCanvas\" is null");}
			if(c.equals(newCanvas)) {return;}
		}
		
		if(newCanvas.getBufferStrategy() == null)
			newCanvas.createBufferStrategy(3);
		bufferStrategy = newCanvas.getBufferStrategy();
		
		this.c = newCanvas;
	}
	public void visualizeGenerationEnabled(boolean enabled)
	{
		if(enableEx) if(blockChanging) {throw new RuntimeException("Parameter changing is prohibited while generation is in progress!");}
		visualGeneration = enabled;
	}
	public long getConsumedTimeLong()
	{
		return time;
	}
	public String[] getConsumedTimeRaw()
	{
		return timeConsumedRaw;
	}
	public String getConsumedTime()
	{
		return "Generation took "+timeConsumedRaw[0]+" seconds and "+timeConsumedRaw[1]+" milliseconds";
	}
	@Unwanted
	public synchronized void enableErrCheck(boolean enable)
	{
		enableEx = enable;
	}
	

	
	void repaint()
	{
		Graphics g = bufferStrategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 800);
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j].draw(g);
			}
		}
		g.dispose();
		bufferStrategy.show();
	}
	void repaint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 800);
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j].draw(g);
			}
		}
		g.dispose();
	}
	
	
	private void reset()
	{
		boolean s = doPaint;
		doPaint = false;
		try {repaintThread.join(10);} catch (InterruptedException e) {e.printStackTrace();}
		if(blockChanging) {throw new RuntimeException("Resetting is prohibited while generation is in progress!");}
		alive = true;
		cells = null;
		offsetX = 0;
		offsetY = 0;
		
		timeConsumedRaw[0] = ""+TimeUnit.NANOSECONDS.toSeconds(time);
		timeConsumedRaw[1] = ""+TimeUnit.NANOSECONDS.toMillis(time - TimeUnit.NANOSECONDS.toSeconds(time)*1000000000);
		doPaint = s;
	}
	
	MazeCell[][] getCells() {
		return cells;
	}
}