package pl.edu.pw.elka.opa.lab4;

public class Passenger {

    private int startFloor;
    private int destinationFloor;

    /**
     * The Passenger class represents a simulated person that randomly generates an urge to travel
     * @param startFloor - variable representing the floor, where the passenger starts his journey
     * @param destinationFloor - variable representing the floor, where the passenger ends his journey
     */
    public Passenger(int startFloor, int destinationFloor) {
        this.startFloor = startFloor;
        this.destinationFloor = destinationFloor;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public String toString() {
        return "[" + getStartFloor() + "-" + getDestinationFloor() +"]";
    }
}
