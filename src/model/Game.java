package model;

import java.awt.Point;
import java.util.List;
import logic.MoveGenerator;
import logic.MoveLogic;


public class Game {

	private Board board;

	private boolean isP1Turn;

	private int skipIndex;//индекс множественности битья шашек
	
	public Game() {

		restart();
	}


	public Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isP1Turn = isP1Turn;
		g.skipIndex = skipIndex;
		return g;
	}
	

	public void restart() {
		this.board = new Board();
		this.isP1Turn = true;
		this.skipIndex = -1;
	}

	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}

	public boolean move(int startIndex, int endIndex) {
		
		// Возможен ли ход
		if (!MoveLogic.isValidMove(this, startIndex, endIndex)) {
			return false;
		}
		
		// Сам ход

		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		
		// Из шашек в дамки
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == 0 && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == 7 && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		
		// Доп.проверка на смену хода
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipIndex = endIndex;
		}
		if (!midValid || MoveGenerator.getSkips(
				board.copy(), endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isP1Turn = !isP1Turn;
			this.skipIndex = -1;
		}
		
		return true;
	}
	

	public Board getBoard() {
		return board.copy();
	}


	public boolean isGameOver() {

		// Проверка на то, победила ли 1 из команд
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));
		if (black.isEmpty()) {
			return true;
		}
		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		if (white.isEmpty()) {
			return true;
		}
		
		// У текущего игрока еще есть ходы
		List<Point> test = isP1Turn? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!MoveGenerator.getMoves(board, i).isEmpty() ||
					!MoveGenerator.getSkips(board, i).isEmpty()) {
				return false;
			}
		}
		
		// Нет шагов
		return true;
	}
	
	public boolean isP1Turn() {
		return isP1Turn;
	}

	
	public int getSkipIndex() {
		return skipIndex;
	}

	public String getGameState() {

		String state = "";
		for (int i = 0; i < 32; i ++) {
			state += "" + board.get(i);
		}
		
		// Add the other info
		state += (isP1Turn? "1" : "0");
		state += skipIndex;
		
		return state;
	}
	

	public void setGameState(String state) {
		
		restart();
		

		if (state == null || state.isEmpty()) {
			return;
		}
		
		// Апдейт доски
		int n = state.length();
		for (int i = 0; i < 32 && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {}
		}
		
		// Апдейт остальной информации
		if (n > 32) {
			this.isP1Turn = (state.charAt(32) == '1');
		}
		if (n > 33) {
			try {
				this.skipIndex = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipIndex = -1;
			}
		}
	}
}
