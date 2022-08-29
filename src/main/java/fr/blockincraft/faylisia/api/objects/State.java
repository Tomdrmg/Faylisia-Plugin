package fr.blockincraft.faylisia.api.objects;

public class State {
    private final int players;
    private final int maxPlayers;
    private final boolean inDevelopment;

    public State(int players, int maxPlayers, boolean inDevelopment) {
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.inDevelopment = inDevelopment;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isInDevelopment() {
        return inDevelopment;
    }
}
