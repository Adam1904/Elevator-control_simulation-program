package pl.edu.pw.elka.opa.lab4;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class PassengerGenerator extends Thread{

    private final long timeBetweenGeneratingPassengers;
    private final double probability;
    private final int numOfFloors;
    private LinkedList<Floor> floorLinkedList;
    private LinkedBlockingQueue<Integer> signalLinkedList;

    /**
     * The PassengerGenerator class is responsible for generating passengers in a unit of time, set by the user
     * @param timeOfGeneratingPassengers - simulation time unit, i.e. the time (e.g. in seconds) needed for elevator to
     * move (and generation of passenger)
     * @param probability - probability of the passenger appearing in t time unit, on a random floor with the
     * intention of traveling to another, random floor
     * @param numOfFloors - number of floors in the building
     * @param floorLinkedList - list of generated floors, which is automatically set based on the numOfFloors variable
     * @param signalLinkedList - a list storing signals in the form of a designated beginning of travel routes
     */
    public PassengerGenerator(long timeOfGeneratingPassengers, double probability, int numOfFloors, LinkedList<Floor> floorLinkedList, LinkedBlockingQueue<Integer> signalLinkedList) {
        this.timeBetweenGeneratingPassengers = timeOfGeneratingPassengers;
        this.probability = probability;
        this.numOfFloors = numOfFloors;
        this.floorLinkedList = floorLinkedList;
        this.signalLinkedList = signalLinkedList;
    }

    /**
     * A function that generates a new passenger randomly, which uses java.util.concurrent package to draw random number
     * @return Passenger - with random travel route
     */
    public Passenger generatePassenger(){

        int randomStartFloor = ThreadLocalRandom.current().nextInt(0,numOfFloors); // Draw random floor from range [0,numOfFloors)
        int randomDestinationFloor  = ThreadLocalRandom.current().nextInt(0,numOfFloors); // Draw random floor from range [0,numOfFloors)
        while(randomDestinationFloor == randomStartFloor)
            randomDestinationFloor  = ThreadLocalRandom.current().nextInt(0,numOfFloors); // draw again, if randomDestinationFloor and randomStartFloor, are the same
        Passenger newlyCreatedPassenger = new Passenger(randomStartFloor,randomDestinationFloor);
        try {
            floorLinkedList.get(randomStartFloor).getSemaphore().acquire();
            floorLinkedList.get(randomStartFloor).addToQue(newlyCreatedPassenger);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            floorLinkedList.get(randomStartFloor).getSemaphore().release();
        }

        try {
            signalLinkedList.put(randomStartFloor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newlyCreatedPassenger;
    }

    /**
     * The method responsible for the continuous generation of new passengers in time unit, which is determined by the
     * timeBetweenGeneratingPassengers variable.
     */
    @Override
    public void run(){

        //noinspection InfiniteLoopStatement
        while(true){

            double drawnProbability = ThreadLocalRandom.current().nextDouble(0,1);
            if(probability > drawnProbability){
                generatePassenger();
            }
            try {
                Thread.yield();
                //noinspection BusyWait
                Thread.sleep(timeBetweenGeneratingPassengers);  // wait for some time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
