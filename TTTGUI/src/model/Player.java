package model;

public class Player {
    private String name;
    private boolean isComputer;
    private boolean isSmartComputer;
    private boolean isX;
    private boolean starts;
    private int numWins;
    private int numDraws;
    private int numLosses;

    public Player(String name, boolean isComputer, boolean isSmartComputer, boolean isX, boolean starts) {
        this.name = name;
        this.isComputer = isComputer;
        this.isSmartComputer = isSmartComputer;
        this.isX = isX;
        this.starts = starts;
        this.numWins = 0;
        this.numDraws = 0;
        this.numLosses = 0;
    }

    public boolean starts() {
        return starts;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String result = name + ": " + numWins + "-" + numLosses + "-" + numDraws;
        return result;
    }

    public int getNumWins() {
        return numWins;
    }

    public int getNumDraws() {
        return numDraws;
    }

    public int getNumLosses() {
        return numLosses;
    }

    public void setNumWins(int numWins) {
        this.numWins = numWins;
    }

    public void setNumLosses(int numLosses) {
        this.numLosses = numLosses;
    }

    public void setNumDraws(int numDraws) {
        this.numDraws = numDraws;
    }

    public boolean isX() {
        return isX;
    }

    public void resetStats() {
        this.numWins = 0;
        this.numDraws = 0;
        this.numLosses = 0;
    }

    public boolean isSmartComputer() {
        return isSmartComputer;
    }
}
