package pl.edu.pw.elka.opa.lab4;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Building {

    private final int numOfFloors;
    private final long time;
    private final Elevator elevator;
    private LinkedList<Floor> floorLinkedList;
    private final PassengerGenerator passengerGenerator;


    /**
     * Building class responsible for preparing the environment to simulate the elevator program
     * @param numOfFloors - number of floors in the building
     * @param time - simulation time unit, i.e. the time (e.g. in seconds) needed for elevator to move (and generation of passenger)
     * @param elevator - parameter responsible for simulating the operation of the elevator
     * @param floorLinkedList - list of generated floors, which is automatically set based on the numOfFloors variable
     * @param passengerGenerator - generator which in t time unit, generates with some probability new passenger
     */
    public Building(int numOfFloors, long time, Elevator elevator, LinkedList<Floor> floorLinkedList, PassengerGenerator passengerGenerator) {
        this.numOfFloors = numOfFloors;
        this.time = time;
        this.elevator = elevator;
        this.floorLinkedList = floorLinkedList;
        this.passengerGenerator = passengerGenerator;
    }


    /**
     * Prepares the floors of the building based on the data provided by the user
     * @return LinkedList<Floor> - represent floors with queue of waiting passengers
     */
    public LinkedList<Floor> createFloors(){
        if(numOfFloors == 0)
            return null;

        for(var i = 0;i<numOfFloors;i++)
            floorLinkedList.add(new Floor(i, new LinkedList<>(), new Semaphore(1,true)));

        return floorLinkedList;
    }

    /**
     * Creates and sets the floors
     */
    public void createBuilding(){
        floorLinkedList = createFloors();
        elevator.setFloors(floorLinkedList);
    }

    /**
     * The method responsible for turning on the elevator
     */
    public void startElevatorAndGenerator(){
        passengerGenerator.start();
        elevator.start();
    }

    @Override
    public String toString() {
        return "Building contains: 1 elevator, " + numOfFloors + " floors, and the time of elevator moving up and down is " + time + "s.";
    }
}
