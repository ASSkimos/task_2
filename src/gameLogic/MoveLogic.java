package gameLogic;
import java.awt.Point;
import java.util.List;
import gameModel.Board;
import gameModel.Game;

//класс для реализации логики шашек
public class MoveLogic {


    public static boolean isValidMove(Game game, int startIndex, int endIndex) {
        return game == null ? false : isValidMove(game.getBoard(),
                game.isP1Turn(), startIndex, endIndex, game.getSkipIndex());
    }

    public static boolean isValidMove(Board board, boolean isP1Turn, int startIndex, int endIndex, int skipIndex) {

        // Нет доски или вышли за её грани
        if (board == null || !Board.isValidIndex(startIndex) || !Board.isValidIndex(endIndex)) {
            return false;
        } else if (startIndex == endIndex) {
            return false;
        } else if (Board.isValidIndex(skipIndex) && skipIndex != startIndex) {
            return false;
        }

        // Неправильно выбранная шашка или дистанция
        if (!validateIDs(board, isP1Turn, startIndex, endIndex)) {
            return false;
        } else if (!validateDistance(board, isP1Turn, startIndex, endIndex)) {
            return false;
        }

        return true;
    }

    private static boolean validateIDs(Board board, boolean isP1Turn,
                                       int startIndex, int endIndex) {

        // проверка на то, что в конечной клетке нет шашки
        if (board.getID(endIndex) != Board.EMPTY) {
            return false;
        }

        //проверка на то, что цвет выбранная шашка соответствует ходу
        int id = board.getID(startIndex);
        if ((isP1Turn && !Board.isBlackChecker(id))
                || (!isP1Turn && !Board.isWhiteChecker(id))) {
            return false;
        }

        // Через своих не ходим
        Point middle = Board.middle(startIndex, endIndex);
        int midID = board.getID(Board.toIndex(middle));
        if (midID != Board.INVALID && ((!isP1Turn &&
                !Board.isBlackChecker(midID)) ||
                (isP1Turn && !Board.isWhiteChecker(midID)))) {
            return false;
        }

        return true;
    }

    private static boolean validateDistance(Board board, boolean isP1Turn,
                                            int startIndex, int endIndex) {

        // Проверка на диагональный шаг на dx,dy=1 по модулю
        Point start = Board.toPoint(startIndex);
        Point end = Board.toPoint(endIndex);
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
            return false;
        }

        // Проверка на то, что белые должны идти вверх, а черные вниз
        int id = board.getID(startIndex);
        if ((id == Board.WHITE_CHECKER && dy > 0) ||
                (id == Board.BLACK_CHECKER && dy < 0)) {
            return false;
        }

        // Проверка на то, что никого не бьем
        Point middle = Board.middle(startIndex, endIndex);
        int midID = board.getID(Board.toIndex(middle));
        if (midID < 0) {
            List<Point> checkers;
            if (isP1Turn) {
                checkers = board.find(Board.BLACK_CHECKER);
                checkers.addAll(board.find(Board.BLACK_KING));
            } else {
                checkers = board.find(Board.WHITE_CHECKER);
                checkers.addAll(board.find(Board.WHITE_KING));
            }
        }

        return true;
    }
}
