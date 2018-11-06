package no.ntnu.imt3281.ludo.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ludo {

    protected static final int RED = 0;
    protected static final int BLUE = 1;
    protected static final int YELLOW = 2;
    protected static final int GREEN = 3;

    private int activePlayer = 0;

    private int lastThrow = 0;

    private List<Player> players = new ArrayList<>();

    public Ludo() {

    }

    public Ludo(String player1, String player2, String player3, String player4) throws NotEnoughPlayersException {

        players.add(RED, new Player(player1));
        players.add(BLUE, new Player(player2));
        players.add(YELLOW, new Player(player3));
        players.add(GREEN, new Player(player4));

        if (this.nrOfPlayers() < 2) {
            throw new NotEnoughPlayersException();
        }

    }

    public int nrOfPlayers() {
        int playerCount = 0;
        for (Player player : players) {
            if (player.getName() != null) {
                playerCount++;
            }
        }

        return playerCount;
    }

    public int activePlayers() {
        int playerCount = 0;
        for (Player player : players) {
            if (player.getName() != null && player.getState()) {
                playerCount++;
            }
        }

        return playerCount;
    }

    public int activePlayer() {
        return this.activePlayer;
    }

    public String getPlayerName(int playerColor) {
        if (nrOfPlayers() - 1 >= playerColor) {
            if (players.get(playerColor).getState()) {
                return players.get(playerColor).getName();
            } else {
                return "Inactive: " + players.get(playerColor).getName();
            }
        }
        return null;
    }

    public void addPlayer(String name) {
        if (nrOfPlayers() < 4) {
            players.add(new Player(name));
        } else {
            throw new NoRoomForMorePlayersException();
        }
    }

    public void removePlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.setState(false);
            }
        }
    }

    public int getPosition(int player, int piece) {
        return players.get(player)
                .getPieces()
                .get(piece)
                .getPosition();
    }

    public void throwDice(int number) {
        this.lastThrow = number;
        Player player = players.get(activePlayer);
        if (player.inStartingPosition()) {
            player.setThrowAttempts(player.getThrowAttempts() + 1);
            if (number != 6 && player.getThrowAttempts() == 3) {
                this.setNextActivePlayer();
                player.setThrowAttempts(0);
            }
        }
        //TODO need to know the chosen piece for this function call, there should be no need for a for-loop
        for (Piece piece : player.getPieces()) {
            if (towersBlocksOpponents(player, piece.position, number)) {
                this.setNextActivePlayer();
            }
        }
    }

    public int throwDice() {
        //create new random number between 1 and 6
        int nr = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        //throw the number
        throwDice(nr);
        //return number
        return nr;
    }

    public void setNextActivePlayer() {
        this.activePlayer = getNextActivePlayer();
    }

    public int getNextActivePlayer() {
        if (this.activePlayer == nrOfPlayers() - 1) {
            for (int i = 0; i < nrOfPlayers(); i++) {
                if (players.get(i).getState()) {
                    return i;
                }
            }
        } else {
            for (int i = activePlayer + 1; i < nrOfPlayers(); i++) {
                if (players.get(i).getState()) {
                    return i;
                }
            }
        }

        return 69; //should never be returned
    }


    public boolean movePiece(int player, int from, int to) {

        int piece = players.get(player).getPiece(from);

        if (piece != 69) {
            //last throw was a 6, but player is in starting position
            //end of turn
            if (this.lastThrow == 6 && players.get(player).inStartingPosition()) {
                this.setNextActivePlayer();
            }
            //player didn't throw a 6
            //end of turn
            if (this.lastThrow != 6) {
                this.setNextActivePlayer();
            }

            if (players.get(player).getPieces().get(piece).towerPos != -1) {
                players.get(player)
                        .getPieces()
                        .get(piece)
                        .setTower(-1);

                for (Piece piece2 : players.get(player).pieces) {
                    if (piece2.position == from) {
                        piece2.setTower(-1);
                    }
                }
            }

            for (Piece piece2 : players.get(player).pieces) {
                if (piece2.position == to) {
                    //Both pieces become a tower
                    piece2.setTower(piece2.position);
                    players.get(player)
                            .getPieces()
                            .get(piece)
                            .setTower(to);
                }
            }

            //move piece
            players.get(player)
                    .getPieces()
                    .get(piece)
                    .setPosition(to);

            return true;
        } else {
            //doesn't work
            return false;
        }

    }


    public boolean towersBlocksOpponents (Player playr, int from, int number){

        int tPpos;
        int pos;
        for (Player player : players) {
            if (player != playr && player.getName() != null) {
                pos = userGridToLudoBoardGrid(player.getColour(), from);
                for (Piece piece : player.pieces) {
                    tPpos = userGridToLudoBoardGrid(player.getColour(), piece.position);
                    for (int i = from; i <= pos + number; i++) {
                        if (tPpos == i) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
