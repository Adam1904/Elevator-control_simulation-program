package pl.edu.pw.elka.opa.lab4;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Elevator extends Thread{

    static int currentNumOfPassengersInside = 0;

    private final int startFloor;
    private int destinationFloor;
    private final int numOfFloors;
    private boolean direction; // true - up, false - down
    private int currentFloor;
    private final int capacity;
    private final long time;

    private LinkedList<Floor> floors;
    private LinkedBlockingQueue<Integer> signalLinkedList;

    /**
     * Elevator class is the most complicated class, where most possible elevator driving scenarios are controlled.
     * @param startFloor - variable representing the floor where the elevator starts simulation (random value)
     * @param destinationFloor - a variable representing the floor where the elevator is going
     * @param numOfFloors - number of floors in the building
     * @param direction - a boolean value representing the direction of the elevator
     * @param currentFloor - variable that represents the current floor of the elevator
     * @param capacity - a randomly generated lift capacity that must not be overloaded under any circumstances
     * @param time - simulation time unit, i.e. the time (e.g. in seconds) needed for elevator to move (and generation of passenger)
     * @param floors - number of floors in the building
     * @param signalLinkedList - a list storing signals in the form of a designated beginning of travel routes
     */
    public Elevator(int startFloor, int destinationFloor, int numOfFloors, boolean direction, int currentFloor, int capacity, long time, LinkedList<Floor> floors, LinkedBlockingQueue<Integer> signalLinkedList) {
        this.startFloor = startFloor;
        this.destinationFloor = destinationFloor;
        this.numOfFloors = numOfFloors;
        this.direction = direction;
        this.currentFloor = currentFloor;
        this.capacity = capacity;
        this.time = time;
        this.floors = floors;
        this.signalLinkedList = signalLinkedList;
    }

    // Getters and Setters
    public LinkedBlockingQueue<Integer> getSignalLinkedList() {
        return signalLinkedList;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public int getNumOfFloors() {
        return numOfFloors;
    }

    public synchronized boolean isDirection() {
        return direction;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getTime() {
        return time;
    }

    public void setFloors(LinkedList<Floor> floors) {
        this.floors = floors;
    }

    /**
     * The method responsible for the continuous work of the lift, which is based on the while (true) loop. The loop can
     * last forever, the elevator has to decide what to do in the next movements. Cases were considered where there are
     * no passengers and if there are new passengers on the route. After each iteration, appropriate messages are displayed.
     */
    @Override
    public void run(){

        LinkedList<Passenger> currentPassengerList = new LinkedList<>();
        setCurrentFloor(startFloor);

        //noinspection InfiniteLoopStatement
        while(true){ // The elevator starts with random parameters startFloor and destinationFloor
            Floor floor = floors.get(getCurrentFloor()); // iterate through floors
            try {
                floor.getSemaphore().acquire(); // set the lock for current floor
                System.out.println(this);

                if (getSignalLinkedList().isEmpty()) {
                    waitForTheSignal(); // if there are no passengers, wait for new signals
                    try {
                        Thread.yield();
                        //noinspection BusyWait
                        Thread.sleep(getTime()); // wait for a certain amount of time, which was set by the user
                    } catch (InterruptedException e) { // Handling exceptions
                        e.printStackTrace();
                    }
                    continue;
                } else { // if there are passengers

                    for (var i = 0; i < currentPassengerList.size(); i++) { // go through all the passengers inside the elevator
                        if (currentPassengerList.get(i).getDestinationFloor() == getCurrentFloor()) {
                            System.out.println("[---] Passenger stepped out of the elevator on the floor: " + getCurrentFloor() + ", he/she travelled through: " + currentPassengerList.get(i));
                            //noinspection SuspiciousListRemoveInLoop
                            currentPassengerList.remove(i); // remove if passenger is leaving
                            currentNumOfPassengersInside--;
                        }
                    }

                    if (!floor.isEmpty()) {
                        int counter = 0;
                        for (Passenger passenger : floor.getQueueOfPassengersWaiting()) { // go through all the passengers waiting on the current floor
                            if (getCapacity() > currentPassengerList.size()) {
                                currentPassengerList.add(passenger);
                                System.out.println("[+++] A new passenger stepped into the elevator, on the floor: " + getCurrentFloor() + ", with the intention of reaching: " + passenger.getDestinationFloor() + " floor");
                                currentNumOfPassengersInside++;
                                counter++; // a secondary variable
                            } else
                                System.out.println("[!!!] The elevator is full to the brim, unable to accommodate more passengers on the floor: " + getCurrentFloor());
                        }
                        if(counter>0)
                            floor.getQueueOfPassengersWaiting().clear(); // remove from the waiting list of passengers, on the current floor
                    }

                    if (currentNumOfPassengersInside == 0 && signalLinkedList.peek()!=null)
                        setDestinationFloor(signalLinkedList.peek()); // if there are no passengers in the elevator, go towards the first waiting passenger
                    else {
                        setDestinationFloor(currentPassengerList.getFirst().getDestinationFloor()); // If there are passengers, serve the first passenger according to the FIFO queue
                    }

                    direction = (getCurrentFloor() - getDestinationFloor()) <= 0; // determine the direction in which the elevator moves true - up, false - down
                }

            } catch (Exception e){
                e.printStackTrace();
            } finally {
                floors.get(getCurrentFloor()).getSemaphore().release(); // release the lock for current floor
            }

            if(signalLinkedList.size()!=0){ // check whether signals are null (if list is null, then nobody is waiting for the elevator)
                if(isDirection())
                    goUpwards();
                else
                    goDownwards();
            }

            if(!signalLinkedList.isEmpty())
                signalLinkedList.removeIf( predicate -> predicate.equals(currentFloor)); // remove the signals with the number of the current elevator position

            try {
                Thread.yield();
                //noinspection BusyWait
                Thread.sleep(getTime()); // wait for a certain amount of time, which was set by the user
            } catch (InterruptedException e) { // Handling exceptions
                e.printStackTrace();
            }

        }
    }

    /**
     * The method that is responsible for handling the elevator, while the elevator is going upwards
     */
    public void goUpwards() {
        if(getCurrentFloor() == (getNumOfFloors()-1)){ // Check in case the elevator is on the highest floor
            System.out.println("[!!!] The elevator is on the highest floor " + getCurrentFloor() + " (can't go higher)" + ", destination floor: " + getDestinationFloor() + " [number of passengers inside: " + currentNumOfPassengersInside + "/" + getCapacity() + "]");
        } else {
            System.out.println("[^^^] The elevator is going up, current floor: " + getCurrentFloor() + ", destination floor: " + getDestinationFloor() + " [number of passengers inside: " + currentNumOfPassengersInside + "/" + getCapacity() + "]");
            currentFloor = getCurrentFloor();
            setCurrentFloor(++currentFloor);
        }
    }

    /**
     * The method that is responsible for handling the elevator, while the elevator is going downwards
     */
    public void goDownwards() {
        if(currentFloor==0) { // Check in case the elevator is on the lowest floor
            System.out.println("[!!!] The elevator is on the lowest floor " + getCurrentFloor() + " (can't go lower)"  + ", destination floor: " + getDestinationFloor() + " [number of passengers inside: " + currentNumOfPassengersInside + "/" + getCapacity() + "]");
        } else {
            System.out.println("[vvv] The elevator is going down, current floor: " + getCurrentFloor() + ", destination floor: " + getDestinationFloor() + " [number of passengers inside: " + currentNumOfPassengersInside + "/" + getCapacity() + "]");
            currentFloor = getCurrentFloor();
            setCurrentFloor(--currentFloor);
        }
    }

    /**
     * The method that is responsible for handling the elevator, while there are no passengers
     */
    public void waitForTheSignal() {
        System.out.println("[###] The elevator is waiting patiently for passenger to arrive, on the floor: " + getCurrentFloor());
    }

    /**
     * ToString () method, which clearly displays the current state of the elevator
     * @return concatenated String
     */
    @Override
    public String toString() {
        //StringBuilder text = new StringBuilder(String.join("", Collections.nCopies(15, "\n\n\n")));
        StringBuilder text = new StringBuilder(String.join("", Collections.nCopies(15, "----------")));
        text.append("\n[The elevator with [").append(currentNumOfPassengersInside).append("/").append(capacity).append("] passengers is currently on the floor {").append(getCurrentFloor()).append("}, marked below with '[*]']:");
        for(var i=numOfFloors-1; i>=0; i--) {
            if(i==getCurrentFloor())
                text.append("\n[*] ").append(floors.get(i).toString());
            else
                text.append("\n[ ] ").append(floors.get(i).toString());
        }
        text.append("\n");
        text.append(String.join("", Collections.nCopies(15, "-.-.-.-.-.")));
        return text.toString();
    }

}
