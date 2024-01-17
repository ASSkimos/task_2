package ui;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
//окно для игры

public class CheckersWindow extends JFrame {


	public static final int DEFAULT_WIDTH = 500;

	public static final int DEFAULT_HEIGHT = 600;

	public static final String DEFAULT_TITLE = "Шашки";

	private CheckerBoard board;
	
	private OptionPanel opts;

	
	public CheckersWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}

	
	public CheckersWindow(int width, int height, String title) {
		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);

		JPanel layout = new JPanel(new BorderLayout());
		this.board = new CheckerBoard(this);
		this.opts = new OptionPanel(this);
		layout.add(board, BorderLayout.CENTER);
		layout.add(opts, BorderLayout.SOUTH);
		this.add(layout);
	}

	public void restart() {
		this.board.getGame().restart();
		this.board.update();
	}


}
