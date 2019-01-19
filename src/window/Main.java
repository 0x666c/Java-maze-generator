package window;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import logic.MazeGenSpeed;
import logic.MazeGenerator;

public class Main {
	public static volatile boolean keyPressed = false;

	public static void main(String[] args) {
		JFrame f = new JFrame("Maze");
		f.setSize(800, 800);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Canvas canvas = new Canvas();
		f.add(canvas);
		canvas.setFocusable(false);
		f.setVisible(true);
		canvas.createBufferStrategy(2);

		f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				keyPressed = true;
			}
		});
		
		MazeGenerator generator = new MazeGenerator(canvas);
		generator.setCellSize(20);
		generator.setLineThickness(3);
		generator.setGenerationSpeed(MazeGenSpeed.MAX);
		generator.setStartingCell(3, 0);
		generator.generate();
		generator.saveAsPng("c:/users/user/desktop/crap.png", 800, 800);
	}
}
