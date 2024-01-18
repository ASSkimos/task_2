package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import gameLogic.MoveCreator;
import simpleGameModel.Board;
import simpleGameModel.Game;
// графическое представление доски и шашек

public class CheckerBoard extends JButton {

	private static final int PADDING = 16;

	private Game game;


	private Point selected;

	private boolean selectionValid;

	private Color lightTile;

	private Color darkTile;

	private boolean isGameOver;

	
	public CheckerBoard(CheckersWindow window) {
		this(window, new Game());
	}
	
	public CheckerBoard(CheckersWindow window, Game game) {

		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
		super.setBackground(Color.LIGHT_GRAY);
		this.addActionListener(new ClickListener());
		
		// начало игры
		this.game = (game == null)? new Game() : game;
		this.lightTile = Color.WHITE;
		this.darkTile = new Color(115,66,34);
	}
	
	//перерисовка игры
	public void update() {
		this.isGameOver = game.isGameOver();
		repaint();
	}


	
	public synchronized boolean setGameState(boolean testValue,//выполняется только 1 потоком
			String newState, String expected) {

		if (testValue && !game.getGameState().equals(expected)) {
			return false;
		}
		
		// обновить состояние игры
		this.game.setGameState(newState);
		repaint();
		
		return true;
	}
	

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Game game = this.game.copy();
		
		// Отображение доски
		final int box_padding = 4;
		final int w = getWidth(), h = getHeight();
		final int dim = w < h? w : h, box_size = (dim - 2 * PADDING) / 8;
		final int offset_x = (w - box_size * 8) / 2;
		final int offset_y = (h - box_size * 8) / 2;
		final int checker_size = Math.max(0, box_size - 2 * box_padding);

		g.setColor(Color.BLACK);
		g.drawRect(offset_x - 1, offset_y - 1, box_size * 8 + 1, box_size * 8 + 1);
		g.setColor(lightTile);
		g.fillRect(offset_x, offset_y, box_size * 8, box_size * 8);
		g.setColor(darkTile);
		for (int y = 0; y < 8; y ++) {
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				g.fillRect(offset_x + x * box_size, offset_y + y * box_size,
						box_size, box_size);
			}
		}

		if (Board.isValidPoint(selected)) {
			g.setColor(selectionValid? Color.GREEN : Color.RED);
			g.fillRect(offset_x + selected.x * box_size,
					offset_y + selected.y * box_size,
					box_size, box_size);
		}
		
		// рисуем шашки
		Board b = game.getBoard();
		for (int y = 0; y < 8; y ++) {
			int cy = offset_y + y * box_size + box_padding;
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				int id = b.getID(x, y);
				
				// пустая плитка(пропуск)
				if (id == Board.EMPTY) {
					continue;
				}
				
				int cx = offset_x + x * box_size + box_padding;
				
				// Отрисовка черной шашки
				if (id == Board.BLACK_CHECKER) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.BLACK);
					g.fillOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, checker_size, checker_size);
				}
				
				// Отрисовка черной дамки
				else if (id == Board.BLACK_KING) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.BLACK);
					g.fillOval(cx - 1, cy - 2, checker_size, checker_size);
				}
				
				// Отрисовка белой шашки
				else if (id == Board.WHITE_CHECKER) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.WHITE);
					g.fillOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, checker_size, checker_size);
				}
				
				// Отрисовка белой дамки
				else if (id == Board.WHITE_KING) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, checker_size, checker_size);
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, checker_size, checker_size);
					g.setColor(Color.WHITE);
					g.fillOval(cx - 1, cy - 2, checker_size, checker_size);
				}
				
				// Дополнительная отрисовка для дамки
				if (Board.isKingChecker(id)) {
					g.setColor(new Color(255, 240, 0));
					g.drawOval(cx - 1, cy - 2, checker_size, checker_size);
					g.drawOval(cx + 1, cy, checker_size - 4, checker_size - 4);
				}
			}
		}
		
		// Отрисовка указателя хода
		String msg = game.isP1Turn()? "Ход 1 игрока" : "Ход 2 игрока";
		int width = g.getFontMetrics().stringWidth(msg);
		Color back = game.isP1Turn()? Color.BLACK : Color.WHITE;
		Color front = game.isP1Turn()? Color.WHITE : Color.BLACK;
		g.setColor(back);
		g.fillRect(w / 2 - width / 2 - 5, offset_y + 8 * box_size + 2,
				width + 10, 15);
		g.setColor(front);
		g.drawString(msg, w / 2 - width / 2, offset_y + 8 * box_size + 2 + 12);

		if (isGameOver) {
			g.setFont(new Font("Arial", Font.BOLD, 20));
			msg = "Игра Окончена!";
			width = g.getFontMetrics().stringWidth(msg);
			g.setColor(new Color(240, 240, 255));
			g.fillRoundRect(w / 2 - width / 2 - 5,
					offset_y + box_size * 4 - 16,
					width + 10, 30, 10, 10);
			g.setColor(Color.RED);
			g.drawString(msg, w / 2 - width / 2, offset_y + box_size * 4 + 7);
		}
	}
	
	public Game getGame() {
		return game;
	}

	private void handleClick(int x, int y) {

		if (isGameOver) {
			return;
		}
		
		Game copy = game.copy();
		
		// определяем какой квадратик выбран
		final int w = getWidth(), h = getHeight();
		final int dim = w < h? w : h, box_size = (dim - 2 * PADDING) / 8;
		final int offset_x = (w - box_size * 8) / 2;
		final int offset_y = (h - box_size * 8) / 2;
		x = (x - offset_x) / box_size;
		y = (y - offset_y) / box_size;
		Point sel = new Point(x, y);
		
		// определяем возможно ли с него пойти
		if (Board.isValidPoint(sel) && Board.isValidPoint(selected)) {
			boolean change = copy.isP1Turn();
			String expected = copy.getGameState();
			boolean move = copy.move(selected, sel);
			boolean updated = (move? setGameState(true, copy.getGameState(), expected) : false);
			change = (copy.isP1Turn() != change);
			this.selected = change? null : sel;
		} else {
			this.selected = sel;
		}

		// возможно ли пойти с выбранного квадратика
		this.selectionValid = isValidSelection(
				copy.getBoard(), copy.isP1Turn(), selected);
		
		update();
	}

	private boolean isValidSelection(Board b, boolean isP1Turn, Point selected) {

		// Trivial cases
		int i = Board.toIndex(selected), id = b.getID(i);
		if (id == Board.EMPTY || id == Board.INVALID) { // нет шашки
			return false;
		} else if(isP1Turn ^ Board.isBlackChecker(id)) { // не та шашка
			return false;
		} else if (!MoveCreator.getSkips(b, i).isEmpty()) { // требуется бить шашку
			return true;
		} else if (MoveCreator.getMoves(b, i).isEmpty()) { // нет ходов
			return false;
		}

		List<Point> points = b.find(
				isP1Turn? Board.BLACK_CHECKER : Board.WHITE_CHECKER);
		points.addAll(b.find(
				isP1Turn? Board.BLACK_KING : Board.WHITE_KING));
		for (Point p : points) {
			int checker = Board.toIndex(p);
			if (checker == i) {
				continue;
			}
			if (!MoveCreator.getSkips(b, checker).isEmpty()) {
				return false;
			}
		}

		return true;
	}


	private class ClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// получаем координаты мыши при нажатии
			Point m = CheckerBoard.this.getMousePosition();
			if (m != null) {
				handleClick(m.x, m.y);
			}
		}
	}
}
