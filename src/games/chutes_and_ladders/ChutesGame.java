package games.chutes_and_ladders;

import games.AbstractGame ;
import org.javatuples.Pair;
import server.ServerThread;
import shared.Command;
import shared.GameName;
import shared.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ChutesGame extends AbstractGame {

    private ServerThread currentPlayer;
    private Map<ServerThread, Integer> playerLocations;
    private Map<Integer, Integer> destinationMap;

    private Random random;

    public ChutesGame(ServerThread player1, ServerThread player2) {
        super(player1, player2);

        currentPlayer = player1;
        random = new Random();

        playerLocations = new HashMap<ServerThread, Integer>();
        playerLocations.put(player1, 0);
        playerLocations.put(player2, 0);

        destinationMap = new HashMap<Integer,Integer>();
        int boardSize = board.getNumCols() * board.getNumRows();
        int boardSizeAdj = boardSize + 5; // +5 if player is on 99 and rolls a 6 = 105;
        for (int i = 1; i <= boardSizeAdj; ++i){
        	destinationMap.put(i, i);
        }

        //chutes
        destinationMap.put(16, 6);
        destinationMap.put(47, 26);
        destinationMap.put(49, 11);
        destinationMap.put(56, 53);
		destinationMap.put(62, 19);
		destinationMap.put(64, 60);
		destinationMap.put(87, 24);
		destinationMap.put(93, 73);
		destinationMap.put(95, 75);
		destinationMap.put(98, 78);

        //ladders
		destinationMap.put(1, 38);
		destinationMap.put(4, 14);
		destinationMap.put(9, 31);
		destinationMap.put(21, 42);
		destinationMap.put(28, 84);
		destinationMap.put(36, 44);
		destinationMap.put(51, 67);
		destinationMap.put(71, 91);
		destinationMap.put(80, 100);

    }

    @Override
    public GameName getGameName(){
    	return GameName.CHUTES_AND_LADDERS;
    }
    
    @Override
    public boolean legalMove(ServerThread player, Request request) {
    	return player == currentPlayer; //just "roll"
    }

    @Override
    public void makeMove(ServerThread player, Request request) {
    	int movement = rollDice();
    	int oldPosition = playerLocations.get(player);

        if (oldPosition != 0) {
            player.send(new Request(Command.REMOVE_FROM, player.getUserName() + " " + locationToCoord(oldPosition).toString()));
            otherPlayer(player).send(new Request(Command.REMOVE_FROM, player.getUserName() + " " + locationToCoord(oldPosition).toString()));
        }

    	int newPosition = oldPosition + movement;
    	int newActualPosition = destinationMap.get(newPosition);

    	if (newActualPosition > 100) { //overshot the end, don't move
    		newActualPosition = oldPosition;
    	}

    	playerLocations.put(player, newActualPosition);
    	Pair<Integer, Integer> newPosAsPair = locationToCoord(newActualPosition);
        System.out.println(player.getUserName() + "  ROLLED: " + movement + "    AND MOVED    FROM: " + oldPosition + " == " + locationToCoord(oldPosition) + "     TO: " + newPosition + " -> " + newActualPosition + " == " + newPosAsPair.toString());

        if (playerLocations.get(player).equals(playerLocations.get(otherPlayer(player)))) {
            // two on same spot
            player.send(new Request(Command.MOVE_BOTH_TO, newPosAsPair.toString()));
            otherPlayer(player).send(new Request(Command.MOVE_BOTH_TO, newPosAsPair.toString()));
        }
        else {
            player.send(new Request(Command.MOVE_TO, player.getUserName() + " " + newPosAsPair.toString()));
            otherPlayer(player).send(new Request(Command.MOVE_TO, player.getUserName() + " " + newPosAsPair.toString()));
        }

    	if (newActualPosition == 100){
    		endGameWinner(player);
    	}

    	changeTurn();
    }

    @Override
    public ServerThread currentPlayer() {
        return currentPlayer;
    }

    private void changeTurn(){
    	currentPlayer = otherPlayer(currentPlayer);
    }

    private Pair<Integer, Integer> locationToCoord(int location){
    	//math to convert 1 - 100 to col, row for Chutes
    	int adj = location - 1;
    	int row = 10 - (adj / 10);

    	if (row % 2 == 0){
    		int col = location % 10;
    		if (col == 0){
    			col = 10;
    		}
    		return Pair.with(col, row);
    	}
    	else {
    		return Pair.with(10 - (adj % 10), row);
    	}
    }

    private int rollDice(){
    	return random.nextInt(6) + 1;
    }
}

