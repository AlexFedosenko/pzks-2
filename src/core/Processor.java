package core;

import java.util.ArrayList;

/**
 * User: Alex
 * Date: 14.05.13
 * Time: 23:06
 */
public class Processor {
    public boolean free = true;
//    public boolean sending = false;
    public int freeTime = 0;
    public int number;
    ArrayList<ArrayList<Object>> sendings = new ArrayList<>();
    public int busyTo = 0;
    public int busyFrom = 0;
    public int lastSending = 0;
//    public int sendTo = 0;
//    public int sendFrom = 0;

//    public ArrayList<Task> data = new ArrayList<>();
//    public ArrayList<Processor> sendPath = new ArrayList<>();

    public Processor clone() {
        Processor result = new Processor();
        result.free = free;
        result.freeTime = freeTime;
        result.number = number;
        result.busyFrom = busyFrom;
        result.busyTo = busyTo;
        result.lastSending = lastSending;
        result.sendings = (ArrayList<ArrayList<Object>>) sendings.clone();
//        for (ArrayList<Object> element : sendings) {
//            ArrayList<Object> note = new ArrayList<>();
//            note.add(((Processor)element.get(0)).clone());
//            note.add(element.get(1));
//            note.add(((Processor)element.get(2)).clone());
//            note.add(element.get(3));
//            note.add(((Task)element.get(4)).clone());
//            note.add(((Processor)element.get(5)).clone());
//            note.add(((Processor)element.get(6)).clone());
//            note.add(element.get(7));
//            result.sendings.add(note);
//        }
        return result;

    }
}
