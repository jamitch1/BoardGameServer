import org.javatuples.Pair;
import server.AbstractGameLogic;
import server.ServerThread;

public class CheckersLogic extends AbstractGameLogic {


	public CheckersLogic(ServerThread currentPlayer, ServerThread otherPlayer, int length) {
		super(currentPlayer, otherPlayer, length);
	}

	public boolean makeMove() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean legalMove(Pair<Integer, Integer> location, ServerThread player) {
		return false;
	}

	public boolean hasWinner() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean legalMove() {
		// TODO Auto-generated method stub
		return false;
	}

}
