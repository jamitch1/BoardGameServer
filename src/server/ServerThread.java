package server;

import client.Command;
import client.Request;
import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    // Fields
    private BufferedReader in;
    private PrintWriter out;
    private ServerThread opponentServerThread;
    private AbstractGameLogic game;
    private String socketAddress;
    private String username;
    private Server server; //reference to the owning server
    private GameLobby lobby; //lobby that the user is currently in
    private static final String defaultLobby = "Tic-Tac-Toe";

    // Methods
    public ServerThread(Socket socket, Server server) throws IOException {
    	this.server = server;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        lobby = server.getLobby(defaultLobby);
        socketAddress = socket.getRemoteSocketAddress().toString();
    }

    @Override
    public void run() {
        Request request;
        while (true) {
            try {
                request = receive();
                handleRequest(request);
            }
            catch (IOException e) {
                server.debugPrintLostConnectionMessage(username, socketAddress);
                removeFromLobby();
                server.sendToAll("LOBBY " + lobby.toString());//TODO
                server.removeConnection(username);
                break;
            }
        }
    }

    // Helper method to obtain position Pair from received String message
    @SuppressWarnings("Duplicates")
    public Pair<Integer, Integer> extractPosition(String message) {
        int i = message.indexOf('[');
        int j = message.indexOf(',');
        int k = message.indexOf(']');
        int x = Integer.parseInt(message.substring(i+1, j).trim());
        int y = Integer.parseInt(message.substring(j+1, k).trim());
        return Pair.with(x, y);
    }

    // Sends message to Client
    public void send(Request request) { // TODO - get rid of this method
        if (!request.getRequest().equals("")) {
            out.println(request.getRequest());
        }
    }
    // Sends message to Client
    public void send(String request) {
        if (!request.equals("")) {
            out.println(request);
        }
    }

    // Returns the next line of stream from Client
    public Request receive() throws IOException {
        String msg = in.readLine();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Client " + getUserName() + " says:  " + msg);
        return new Request(msg);
    }

    public void opponentMoved(Pair<Integer, Integer> location) {
        send(new Request(Command.OPPONENT_MOVED, location.toString()));
        send(game.hasWinner() ? new Request(Command.DEFEAT) : game.tied() ? new Request(Command.TIE): new Request(Command.NULL));
    }

    public ServerThread getOpponent() {
        return opponentServerThread;
    }

    public void setOpponent(ServerThread opponentServerThread) {
        this.opponentServerThread = opponentServerThread;
    }

    public String getUserName() {
        return username == null ? socketAddress : username;
    }

    public void removeFromLobby(){
        lobby.removeUser(username);
    }

    public void setGame(AbstractGameLogic game){
    	this.game = game;
    }

    public void handleRequest(Request request) { // TODO - Make a copy of Request and Command classes and put in server package.
        String[] tokens = request.getTokens();
        Command command = request.getCommand();
        switch(command) {
            case LOGGING_IN:
                if (server.login(tokens[1], tokens[2])){
                    username = tokens[1];
                    send(new Request(Command.LOGIN_SUCCESS, username));
                    server.addConnection(username, this);
                }
                else {
                    send(new Request(Command.LOGIN_FAIL));
                }
                break;
            case CREATING_ACCOUNT:
            	server.createAccount(tokens[1], tokens[2], tokens[3], tokens[4]);
            	break;
            case CHUTE:
                //
                out.println("LOLOLOLOLOL");
                break;
            case GOTO_LOBBY:
                removeFromLobby();
                server.sendToAll("LOBBY " + lobby.toString()); //TODO
                lobby = server.getLobby(tokens[1]);
                lobby.addUser(username); //add to new lobby
                server.sendToAll("LOBBY " + lobby.toString());//TODO
                break;
            case JOIN: //TODO move to lobby
                String otherUser = tokens[1];
                ServerThread otherConnection = server.getConnection(otherUser);
                lobby.startGame(this, otherConnection);
                server.sendToAll("LOBBY " + lobby.toString());//TODO
                break;
            case MOVE: // TODO move to game/abstractgame/logic
                //if (lobby.getLobbyName().equalsIgnoreCase("Chutes-N-Ladders")) {
                //    if (game.legalMove(this)) {
                //        send(new Request(Command.VALID_MOVE));
                //        send(game.hasWinner() ? new Request(Command.VICTORY) : game.tied() ? new Request(Command.TIE): new Request(Command.NULL));
                //    }
                //}
                if (lobby.getLobbyName().equalsIgnoreCase("Tic-Tac-Toe")) {
                    if (game.legalMove(extractPosition(request.getRequest()), this)) {
                        send(new Request(Command.VALID_MOVE));
                        send(game.hasWinner() ? new Request(Command.VICTORY) : game.tied() ? new Request(Command.TIE): new Request(Command.NULL));
                    }
                }
                //else if (lobby.getLobbyName().equalsIgnoreCase("Checkers")) {
                //    if (game.legalMove(extractPosition(msg), this)) {
                //        send(new Request(Command.VALID_MOVE));
                //        send(game.hasWinner() ? new Request(Command.VICTORY) : game.tied() ? new Request(Command.TIE): new Request(Command.NULL));
                //    }
                //}
                break;
            case LOGOUT:
                removeFromLobby();
                server.sendToAll("LOBBY " + lobby.toString()); //TODO
                server.removeConnection(username);
                break;
        }
    }
}
