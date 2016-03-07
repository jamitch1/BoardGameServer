import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tile extends JButton implements ActionListener {

    // Fields
    private Board board;
    private Pair<Integer, Integer> coordinates; //saves button coordinates

    // Methods
    public Tile(Board board, Pair<Integer, Integer> coordinates) {
        this.board = board;
        this.coordinates = coordinates;
        this.addActionListener(this);
    }

    // Constructor helpful for debugging
    public Tile(Board board, Pair<Integer, Integer> coordinates, Color color) {
        this.board = board;
        this.coordinates = coordinates;
        this.addActionListener(this);
        this.setBackground(color);
    }

    /*
     * This will execute when a button object is clicked on the game board.
     * Gameboard will be properly updated to what is clicked as well.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        getMaster().setLastClickedTile(this);
        getMaster().getMaster().getMaster().send("MOVE " + coordinates);
    }

    public Board getMaster() {
        return board;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public void setIcon() {
        char ID = board.getID();
        this.setIcon(board.chooseIcon(ID));
        this.setDisabledIcon(board.chooseIcon(ID));
        this.setEnabled(false);
    }

    public void setOpponentIcon() {
        char ID = board.getOpponentID();
        this.setIcon(board.chooseIcon(ID));
        this.setDisabledIcon(board.chooseIcon(ID));
        this.setEnabled(false);
    }
}
