package fr.TPIII_INF4063;


public class Connection {
    private int startStationNumber;
    private int endStationNumber;
    private int travelTime;

    public Connection(int startStationNumber, int endStationNumber, int travelTime) {
        this.startStationNumber = startStationNumber;
        this.endStationNumber = endStationNumber;
        this.travelTime = travelTime;
    }

    public int getStartStationNumber() {
        return startStationNumber;
    }

    public int getEndStationNumber() {
        return endStationNumber;
    }

    public int getTravelTime() {
        return travelTime;
    }
}