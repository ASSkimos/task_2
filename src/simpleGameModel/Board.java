package simpleGameModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

//реализация доски 8 на 8(шашки только на черных клетках). Черных клеток ровно половину (32)с индексами
//  от 0 да 31
public class Board {

    public static final int INVALID = -1;// id точки, находящейся не на доске

    public static final int EMPTY = 0;// id пустой клетки

    public static final int BLACK_CHECKER = 4 * 1 + 2 * 1 + 1 * 0;

    public static final int WHITE_CHECKER = 4 * 1 + 2 * 0 + 1 * 0;

    public static final int BLACK_KING = 4 * 1 + 2 * 1 + 1 * 1;

    public static final int WHITE_KING = 4 * 1 + 2 * 0 + 1 * 1;

    private int[] state;

    //Создание новой шахматной доски
    public Board() {
        reset();
    }


    public Board copy() {
        Board copy = new Board();
        copy.state = state.clone();
        return copy;
    }


    public void reset() {

        // Восстановления состояния доски(изначальное положение шашек)
        this.state = new int[3];
        for (int i = 0; i < 12; i++) {
            set(i, BLACK_CHECKER);
            set(31 - i, WHITE_CHECKER);
        }
    }


    public List<Point> find(int id) {
        // нахождение всех черных плиток с соответствующим id(поиск черных шашек, белых, дамок и тд)
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            if (getID(i) == id) {
                points.add(toPoint(i));
            }
        }
        return points;
    }


    public void set(int index, int id) {

        // индекс за границами
        if (!isValidIndex(index)) {
            return;
        }

        // Если недопустимый id, то Empty
        if (id < 0) {
            id = EMPTY;
        }

        // состояние игры в битах
        for (int i = 0; i < state.length; i++) {
            boolean set = ((1 << (state.length - i - 1)) & id) != 0;
            this.state[i] = setBit(state[i], index, set);
        }
    }

    public static int setBit(int target, int bit, boolean set) {

        if (bit < 0 || bit > 31) {
            return target;
        }

        // Указать бит
        if (set) {
            target |= (1 << bit);
        }

        // Очистить бит
        else {
            target &= (~(1 << bit));
        }

        return target;
    }


    public static int getBit(int target, int bit) {

        // вышли за границы
        if (bit < 0 || bit > 31) {
            return 0;
        }

        return (target & (1 << bit)) != 0 ? 1 : 0;
    }

    public int getID(int x, int y) {
        return getID(toIndex(x, y));
    }

//получить состояние клетки с помощью индекса
    public int getID(int index) {
        if (!isValidIndex(index)) {
            return INVALID;
        }
        return getBit(state[0], index) * 4 + getBit(state[1], index) * 2
                + getBit(state[2], index);
    }

    //метод преобразования из индекса в точку(index=0, point(1,0), index=31,  point(7,7))
    public static Point toPoint(int index) {
        int y = index / 4;
        int x = 2 * (index % 4) + (y + 1) % 2;
        return !isValidIndex(index) ? new Point(-1, -1) : new Point(x, y);
    }


    public static int toIndex(int x, int y) {

        // Недопустимые x и y (за границами доски)
        if (!isValidPoint(new Point(x, y))) {
            return -1;
        }

        return y * 4 + x / 2;
    }


    public static int toIndex(Point p) {
        return (p == null) ? -1 : toIndex(p.x, p.y);
    }


    public static Point middle(Point p1, Point p2) {

        // Точка не инициализированана
        if (p1 == null || p2 == null) {
            return new Point(-1, -1);
        }

        return middle(p1.x, p1.y, p2.x, p2.y);
    }


    public static Point middle(int index1, int index2) {
        return middle(toPoint(index1), toPoint(index2));
    }


    public static Point middle(int x1, int y1, int x2, int y2) {

        int dx = x2 - x1, dy = y2 - y1;
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || // За доской
                x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
            return new Point(-1, -1);
        } else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) { // Белая клетка(нан ней не может быть шашки)
            return new Point(-1, -1);
        } else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
            return new Point(-1, -1);
        }

        return new Point(x1 + dx / 2, y1 + dy / 2);
    }

    public static boolean isValidIndex(int testIndex) {
        return testIndex >= 0 && testIndex < 32;
    }

    public static boolean isValidPoint(Point testPoint) {

        if (testPoint == null) {
            return false;
        }

        final int x = testPoint.x, y = testPoint.y;//точка на доске
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return false;
        }

        if (x % 2 == y % 2) {//точка на белой клетке
            return false;
        }

        return true;
    }


    public static boolean isBlackChecker(int id) {
        return id == Board.BLACK_CHECKER || id == Board.BLACK_KING;
    }


    public static boolean isWhiteChecker(int id) {
        return id == Board.WHITE_CHECKER || id == Board.WHITE_KING;
    }


    public static boolean isKingChecker(int id) {
        return id == Board.BLACK_KING || id == Board.WHITE_KING;
    }
}
