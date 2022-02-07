package pl.edu.pw.elka.opa.lab4;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Floor {

    private final int numOfFloor;
    private LinkedList<Passenger> queueOfPassengersWaiting;
    private Semaphore semaphore;

    public Floor(int numOfFloor, LinkedList<Passenger> queueOfPassengersWaiting, Semaphore semaphore) {
        this.numOfFloor = numOfFloor;
        this.queueOfPassengersWaiting = queueOfPassengersWaiting;
        this.semaphore = semaphore;
    }

    public synchronized Semaphore getSemaphore() {
        return semaphore;
    }

    public synchronized LinkedList<Passenger> getQueueOfPassengersWaiting() {
        return queueOfPassengersWaiting;
    }

    public synchronized void addToQue(Passenger passenger){
        this.queueOfPassengersWaiting.addLast(passenger);
    }

    public boolean isEmpty(){
        return queueOfPassengersWaiting.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("On the {").append(numOfFloor).append("} floor, there are {").append(queueOfPassengersWaiting.size()).append("} passengers, with destinations {");
        if(queueOfPassengersWaiting.isEmpty())
            text.append("}");
        else {
            for (var i = 0; i < queueOfPassengersWaiting.size(); i++) {
                text.append(queueOfPassengersWaiting.get(i).toString());
                if (i == queueOfPassengersWaiting.size() - 1)
                    text.append("}.");
                else
                    text.append("}, ");
            }
        }
        return text.toString();
    }
}
