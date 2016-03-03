import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;


public class Board {

    // Fields
    private HashMap<Pair, String> boardMap;

    // Methods
    public Board(int size) {
        boardMap = new HashMap<>();
        for (int xCol=1; xCol<=size; ++xCol) {
            for (int yRow=1; yRow<=size; ++yRow) {
                Pair<Integer, Integer> pair = Pair.with(xCol,yRow);
                boardMap.put(pair, "_");
            }
        }
    }

    public void makeMove(Pair<Integer, Integer> location, String piece) {
        boardMap.put(location, piece);
    }

    public void makeMove(int xCol, int yRow, String piece) {
        boardMap.put(Pair.with(xCol, yRow), piece);
    }

    public void debugPrintBoardContents() {
        for (Map.Entry<Pair, String> entry : boardMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
    }

}