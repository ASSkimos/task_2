package logic;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.Board;

//класс для определения возможности того или иного хода
public class MoveGenerator {


	public static List<Point> getMoves(Board board, int startIndex) {
		

		List<Point> endPoints = new ArrayList<>();
		// Нет доски или вышли за её грани
		if (board == null || !Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		
		// Добавление всех возможных ходов без учета состояния игры
		int id = board.get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 1);
		
		// Удаление невозможных ходов
		for (int i = 0; i < endPoints.size(); i ++) {
			Point end = endPoints.get(i);
			if (board.get(end.x, end.y) != Board.EMPTY) {
				endPoints.remove(i --);
			}
		}
		
		return endPoints;
	}

	public static List<Point> getSkips(Board board, int startIndex) {//для ситауция, когда нужно бить шашку

		List<Point> endPoints = new ArrayList<>();
		// Нет доски или вышли за её грани
		if (board == null || !Board.isValidIndex(startIndex)) {
			return endPoints;
		}

		// Добавление всех возможных ходов без учета состояния игры
		int id = board.get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 2);

		for (int i = 0; i < endPoints.size(); i ++) {
			// Проверка на возможность битья
			Point end = endPoints.get(i);
			if (!isValidSkip(board, startIndex, Board.toIndex(end))) {
				endPoints.remove(i --);
			}
		}

		return endPoints;
	}

	public static boolean isValidSkip(Board board,
			int startIndex, int endIndex) {

		if (board == null) {
			return false;
		}

		if (board.get(endIndex) != Board.EMPTY) {
			return false;
		}

		// Проверка на то, что бьем шашку
		int id = board.get(startIndex);
		int midID = board.get(Board.toIndex(Board.middle(startIndex, endIndex)));
		if (id == Board.INVALID || id == Board.EMPTY) {
			return false;
		} else if (midID == Board.INVALID || midID == Board.EMPTY) {
			return false;
		} else if (Board.isBlackChecker(midID) ^ Board.isWhiteChecker(id)) {//проверка, что не бьем своих
			return false;
		}

		return true;
	}

	public static void addPoints(List<Point> points, Point p, int id, int delta) {
		
		// Для черных(сверху вниз)
		boolean isKing = Board.isKingChecker(id);
		if (isKing || id == Board.BLACK_CHECKER) {
			points.add(new Point(p.x + delta, p.y + delta));
			points.add(new Point(p.x - delta, p.y + delta));
		}
		
		// Для белых(снизу вверх)
		if (isKing || id == Board.WHITE_CHECKER) {
			points.add(new Point(p.x + delta, p.y - delta));
			points.add(new Point(p.x - delta, p.y - delta));
		}
	}
}
