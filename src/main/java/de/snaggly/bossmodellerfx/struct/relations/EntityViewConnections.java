package de.snaggly.bossmodellerfx.struct.relations;

public class EntityViewConnections {
    private int northConnections = 1;
    private int southConnections = 1;
    private int eastConnections = 1;
    private int westConnections = 1;

    private int northConnectionsLeft = 0;
    private int southConnectionsLeft = 0;
    private int eastConnectionsLeft = 0;
    private int westConnectionsLeft = 0;

    public void increaseNorthConnections() {
        northConnections++;
        northConnectionsLeft++;
    }

    public void increaseSouthConnections() {
        southConnections++;
        southConnectionsLeft++;
    }

    public void increaseEastConnections() {
        eastConnections++;
        eastConnectionsLeft++;
    }

    public void increaseWestConnections() {
        westConnections++;
        westConnectionsLeft++;
    }

    public int getNorthConnections() {
        return northConnections;
    }

    public int getSouthConnections() {
        return southConnections;
    }

    public int getEastConnections() {
        return eastConnections;
    }

    public int getWestConnections() {
        return westConnections;
    }

    public int getNorthConnectionsLeft() {
        var result = northConnectionsLeft;
        northConnectionsLeft--;
        return result;
    }

    public int getSouthConnectionsLeft() {
        var result = southConnectionsLeft;
        southConnectionsLeft--;
        return result;
    }

    public int getEastConnectionsLeft() {
        var result = eastConnectionsLeft;
        eastConnectionsLeft--;
        return result;
    }

    public int getWestConnectionsLeft() {
        var result = westConnectionsLeft;
        westConnectionsLeft--;
        return result;
    }
}
