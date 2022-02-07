package pl.edu.pw.elka.opa.lab4;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Task {

    /**
     * Elevator control - simulation program
     * The program displays in a concise form the state of the entire system every unit of time t (it's state
     * stop, upward movement, downward movement, current number of passengers and a list of passengers
     * waiting on each floor with the representation of travelling intentions).
     * @param floors - number of floors in the building
     * @param time - simulation time unit, i.e. the time (e.g. in seconds) needed for elevator to move (and generation of passenger)
     * @param probability - probability of the passenger appearing in t time unit, on a random floor with the
     * intention of traveling to another, random floor
     */
    public static void program(int floors, long time, double probability){

        time *= 1000; // Conversion of time unit to seconds

        int randomNumOfElevatorCapacity = ThreadLocalRandom.current().nextInt(5,11); // Draw capacity from range [5,10]
        int randomStartFloor = ThreadLocalRandom.current().nextInt(0,floors-1); // Draw random floor from range [0,floors-1)
        int randomDestinationFloor = ThreadLocalRandom.current().nextInt(0,floors-1); //  Draw random floor from range [0,floors-1)
        while(randomDestinationFloor == randomStartFloor)
            randomDestinationFloor = ThreadLocalRandom.current().nextInt(0,floors-1); // draw again, if randomDestinationFloor and randomStartFloor, are the same

        LinkedList<Floor> floorLinkedList = new LinkedList<>();
        LinkedBlockingQueue<Integer> signalLinkedList = new LinkedBlockingQueue<>();
        Elevator elevator = new Elevator(randomStartFloor, randomDestinationFloor, floors, (randomDestinationFloor - randomStartFloor) <= 0, randomStartFloor, randomNumOfElevatorCapacity, time, floorLinkedList, signalLinkedList);
        PassengerGenerator passengerGenerator = new PassengerGenerator(time,probability,floors,floorLinkedList, signalLinkedList);

        Building building = new Building(floors, time, elevator, floorLinkedList, passengerGenerator);
        building.createBuilding(); // Method preparing the random environment
        building.startElevatorAndGenerator(); // Method that starts threads

    }
}
