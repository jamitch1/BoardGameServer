import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GUI {

    // Fields
    private JFrame jFrame;
    private JPanel boardPanel;
    private Board board;
    private HashMap<Pair<Integer, Integer>, JButton> guiBoardMap;

    // Methods
    public GUI(Board board) 
    {
        this.board = board;
        this.jFrame = new JFrame("GUI");
        boardPanel = new JPanel();
        boardPanel.setBackground(Color.yellow);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        jFrame.add(boardPanel, "Center");
        createBoard();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(450, 300);
        jFrame.setVisible(true);
        jFrame.setResizable(false);
    }
    
    /*
     * This method will create a hashmap with keys being a button's coordinates,
     * and the values are Button objects. Buttons are also added to the panel
     * at the end of this method.
     */
    public void createBoard()
    {
        guiBoardMap = new HashMap<>();
        for (int xCol=1; xCol<=board.getSize(); ++xCol) 
        {
            for (int yRow=1; yRow<=board.getSize(); ++yRow) 
            {
                Pair<Integer, Integer> pair = Pair.with(xCol, yRow);
                BoardButtonAction action = new BoardButtonAction(board);
                ImageIcon icon = action.chooseIcon(board.getValue(pair));
                BoardButton button = new BoardButton(pair,icon);
                button.addActionListener(action);
                guiBoardMap.put(pair, button);
                boardPanel.add(button);
            }
        }
    }
    

    
}
