package server;

import client.Command;
import client.Request;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {

    // Fields
    private Server server;
    private String lobbyName;
    private List<String> list;

    // Methods
	public GameLobby(Server server, String lobbyName) {
        this.server = server;
        this.lobbyName = lobbyName;
        list = new ArrayList<>();
	}

	public void addUser(String userName) {
		list.add(userName);
		debugPrintLobbyContents();
	}

	public void removeUser(String userName) {
		list.remove(userName);
		debugPrintLobbyContents();
	}

	public String getLobbyName() {
		return lobbyName;
	}


	//Can be used to send contents of waiting list over socket
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(lobbyName).append(" ");
		for (String userName : list) {
			str.append(userName);
			str.append(" ");
		}
		return str.toString();
	}

	public void startGame(ServerThread player1, ServerThread player2) {
		System.out.println("CURRENT GAME IS" + lobbyName);
		String p1 = player1.getUserName();
		String p2 = player2.getUserName();
		if (lobbyName.equalsIgnoreCase("Chutes-and-Ladders")) {
			ChutesLogic newGame = new ChutesLogic(server, player1, player2, 10);

			player1.send(new Request(Command.CHUTE));
			player1.setOpponent(player2);
			player1.setGame(newGame);


			player2.send(new Request(Command.CHUTE)); // 'WELCOME username2 O username1 X '
			player2.setOpponent(player1);
			player2.setGame(newGame);

		}
        else if (lobbyName.equalsIgnoreCase("Tic-Tac-Toe")) {
			TicTacToeLogic newGame = new TicTacToeLogic(server, player1, player2, 3);

			player1.send(new Request(Command.WELCOME, p1 + " X " + p2 + " O ")); // 'WELCOME username1 X username2 O '
			player1.setOpponent(player2);
			player1.setGame(newGame);

			player2.send(new Request(Command.WELCOME, p2 + " O " + p1 + " X ")); // 'WELCOME username2 O username1 X '
			player2.setOpponent(player1);
			player2.setGame(newGame);

		}
	}

    private void debugPrintLobbyContents() {
        System.out.println("-----------------------------------------------------------------------------");
        String lobbyContentsMessage = lobbyName + " lobby contents updated: ";
        if (list.size() == 0) {
            lobbyContentsMessage += "<empty>";
        } else {
            for (String user : list) {
                lobbyContentsMessage += user + " ";
            }
        }
        System.out.println(lobbyContentsMessage);
    }

    public void handleRequest(Request request) { // TODO - Make a copy of Request and Command classes and put in server package.
        String[] tokens = request.getTokens();
        Command command = request.getCommand();
        switch(command) {
            case JOIN:
                ServerThread serverThreadPlayer1 = server.getConnection(tokens[1]);
                ServerThread serverThreadPlayer2 = server.getConnection(tokens[2]);
                startGame(serverThreadPlayer1, serverThreadPlayer2);
                server.sendToAll(new Request(Command.LOBBY, toString()));
                break;
        }
    }
}

