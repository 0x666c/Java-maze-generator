package logic;

public enum MazeGenSpeed {
	
	SNAIL(300),
	
	SLOW(55),
	
	MEDIUM(25),
	
	QUICK(12),
	
	LIGHTNING(1),
	
	MAX(0);
	
	int sleepDelay;
	private MazeGenSpeed(int sleepdelay) {
		sleepDelay = sleepdelay;
	}
}
