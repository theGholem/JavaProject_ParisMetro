package fr.TPIII_INF4063;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class metroParis {
    private Map<String, Station> stationsMap;
    private List<Station> stationsList;
    private List<Connection> connections;

    public metroParis() {
        stationsMap = new HashMap<>();
        stationsList = new ArrayList<>();
        connections = new ArrayList<>();
    }

    public static void main(String[] args) {
        metroParis metro = new metroParis();
        metro.readFile();

        // Example
        String startStationName = "Anvers";
        String endStationName = "Argentine";

        Station startStation = metro.getStationByName(startStationName);
        Station endStation = metro.getStationByName(endStationName);

        if (startStation != null && endStation != null) {
            Trajet dijkstraTrajet = metro.trajetLePlusRapideDijkstra(startStation, endStation);
            Trajet bellmanFordTrajet = metro.trajetLePlusRapideBellmanFord(startStation, endStation);

            System.out.println("Dijkstra: " + dijkstraTrajet.getListeDesStationsDuTrajet());
            System.out.println("Bellman-Ford: " + bellmanFordTrajet.getListeDesStationsDuTrajet());
            /*
             * System.out.println("Dijkstra: " + dijkstraTrajet);
             * System.out.println("Bellman-Ford: " + bellmanFordTrajet);
             */
        } else {
            System.out.println("Invalid station names.");
        }
    }

    public void readFile() {
        try {
            File myObj = new File("Metro.txt");
            Scanner myReader = new Scanner(myObj);

            // Skip the first line
            myReader.nextLine();

            // Read station names
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("$")) {
                    break;
                }
                String[] line = data.split(" ");
                int stationNumber = Integer.parseInt(line[0]);
                String stationName = line[1];

                Station station = new Station(stationName, stationNumber);
                stationsMap.put(stationName, station);
                stationsList.add(station);
            }

            // Skip the second section delimiter
            myReader.nextLine();

            // Read station positions
            for (Station station : stationsList) {
                String data = myReader.nextLine();
                if (data.contains("$")) {
                    break;
                }
                String[] line = data.split(" ");
                int x = Integer.parseInt(line[1]);
                int y = Integer.parseInt(line[2]);
                station.setPosition(x, y);
            }

            // Skip the third section delimiter
            myReader.nextLine();

            // Read connections between stations
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("$")) {
                    break;
                }
                String[] line = data.split(" ");
                int startStationNumber = Integer.parseInt(line[0]);
                int endStationNumber = Integer.parseInt(line[1]);
                int travelTime = Integer.parseInt(line[2]);

                Connection connection = new Connection(startStationNumber, endStationNumber, travelTime);
                connections.add(connection);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier ne semble pas exister à l'emplacement donné.");
            e.printStackTrace();
        }
    }

    public Trajet trajetLePlusRapideDijkstra(Station startStation, Station endStation) {
        Map<Station, Integer> distances = new HashMap<>();
        Map<Station, Station> previousStations = new HashMap<>();
        List<Station> unvisitedStations = new ArrayList<>();

        for (Station station : stationsList) {
            distances.put(station, Integer.MAX_VALUE);
            previousStations.put(station, null);
            unvisitedStations.add(station);
        }

        distances.put(startStation, 0);

        while (!unvisitedStations.isEmpty()) {
            Station currentStation = getClosestStation(unvisitedStations, distances);
            unvisitedStations.remove(currentStation);

            if (currentStation == endStation) {
                break;
            }

            for (Station neighbor : getNeighbors(currentStation)) {
                int travelTime = getTravelTime(currentStation, neighbor);
                int newDistance = distances.get(currentStation) + travelTime;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousStations.put(neighbor, currentStation);
                }
            }
        }

        return buildTrajet(startStation, endStation, distances, previousStations);
    }

    public Trajet trajetLePlusRapideBellmanFord(Station startStation, Station endStation) {
        Map<Station, Integer> distances = new HashMap<>();
        Map<Station, Station> previousStations = new HashMap<>();

        for (Station station : stationsList) {
            distances.put(station, Integer.MAX_VALUE);
            previousStations.put(station, null);
        }

        distances.put(startStation, 0);

        for (int i = 0; i < stationsList.size() - 1; i++) {
            for (Connection connection : connections) {
                Station start = getStationByNumber(connection.getStartStationNumber());
                Station end = getStationByNumber(connection.getEndStationNumber());
                int travelTime = connection.getTravelTime();

                if (distances.get(start) != Integer.MAX_VALUE
                        && distances.get(start) + travelTime < distances.get(end)) {
                    distances.put(end, distances.get(start) + travelTime);
                    previousStations.put(end, start);
                }
            }
        }

        return buildTrajet(startStation, endStation, distances, previousStations);
    }

    public ArrayList<Station> stationsCritiques() {
        ArrayList<Station> criticalStations = new ArrayList<>();

        for (Station station : stationsList) {
            station.setInService(false);
            if (!isGraphConnected()) {
                criticalStations.add(station);
            }
            station.setInService(true);
        }

        return criticalStations;
    }

    private Station getStationByName(String name) {
        return stationsMap.get(name);
    }

    private Station getStationByNumber(int number) {
        for (Station station : stationsList) {
            if (station.getNumber() == number) {
                return station;
            }
        }
        return null;
    }

    private List<Station> getNeighbors(Station station) {
        List<Station> neighbors = new ArrayList<>();
        for (Connection connection : connections) {
            if (connection.getStartStationNumber() == station.getNumber()) {
                Station neighbor = getStationByNumber(connection.getEndStationNumber());
                if (neighbor != null) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private int getTravelTime(Station startStation, Station endStation) {
        for (Connection connection : connections) {
            if (connection.getStartStationNumber() == startStation.getNumber() &&
                    connection.getEndStationNumber() == endStation.getNumber()) {
                return connection.getTravelTime();
            }
        }
        return Integer.MAX_VALUE; // No direct connection
    }

    private Station getClosestStation(List<Station> unvisitedStations, Map<Station, Integer> distances) {
        Station closestStation = null;
        int minDistance = Integer.MAX_VALUE;

        for (Station station : unvisitedStations) {
            int distance = distances.get(station);
            if (distance < minDistance) {
                closestStation = station;
                minDistance = distance;
            }
        }

        return closestStation;
    }

    private Trajet buildTrajet(Station startStation, Station endStation, Map<Station, Integer> distances,
            Map<Station, Station> previousStations) {
        Trajet trajet = new Trajet();

        if (previousStations.get(endStation) == null) {
            return trajet; // No path found
        }

        List<Station> path = new ArrayList<>();
        Station currentStation = endStation;

        while (currentStation != null) {
            path.add(0, currentStation);
            currentStation = previousStations.get(currentStation);
        }

        trajet.setListeDesStationsDuTrajet(path);
        trajet.setTempsRequis(distances.get(endStation));

        return trajet;
    }

    private boolean isGraphConnected() {
        Station startStation = stationsList.get(0);
        List<Station> reachableStations = new ArrayList<>();
        dfs(startStation, reachableStations);

        return reachableStations.size() == stationsList.size();
    }

    private void dfs(Station station, List<Station> reachableStations) {
        reachableStations.add(station);

        for (Station neighbor : getNeighbors(station)) {
            if (!reachableStations.contains(neighbor) && neighbor.isInService()) {
                dfs(neighbor, reachableStations);
            }
        }
    }
}
