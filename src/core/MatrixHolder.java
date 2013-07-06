package core;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import gui.GenerationPanel;
import gui.GraphEditor;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: Alex
 * Date: 27.02.13
 * Time: 0:07
 */
public class MatrixHolder {

    private static Integer[][] taskMatrix = null;
    private static Integer[][] sysMatrix = null;
    private static int[] values = null;
    private static mxGraph graph = null;
    private static Node[] nodes;
    public static int Tg;
    private static int[] Te;
    private static int[] Ne;
    private static int[] Tb;
    private static int[] Nb;
    public static ArrayList<Task> Q2;
    public static String q2;
    public static ArrayList<Task> Q4;
    public static String q4;
    public static ArrayList<Task> Q16;
    public static String q16;
    public static Processor[] processors;
    public static Processor[] backupProcessors;
    public static int time;
    private static ArrayList<Task> queue = new ArrayList<>();
    private static ArrayList<Task> backupQueue = new ArrayList<>();
    private static ArrayList<Task> executedTasks = new ArrayList<>();
    public static LinkedList<Task[]> queues = new LinkedList<>();
    public static String[][] datasetNames;
    private static int bestPathTime;
    private static int[][] distances;
    public static double[][] Ky = new double[5][3];
    public static double[][] Ke = new double[5][3];
    public static double[][] Kea = new double[5][3];


    private static Algorithms a = new Algorithms();

    public static void clean() {
        taskMatrix = new Integer[0][0];
        sysMatrix = new Integer[0][0];
    }

    public static int getSystemSize() {
        return sysMatrix.length;
    }

    private static void calcGraphCritical() {
        Te = new int[nodes.length];
        Ne = new int[nodes.length];
        Tb = new int[nodes.length];
        Nb = new int[nodes.length];
        int max = 0;
        for (int i = 0; i < Te.length; i++) {
            Te[i] = a.criticalToEnd(true, nodes[i]);
            if (Te[i] > max) {
                max = Te[i];
            }
        }
        Tg = max;

        for (int i = 0; i < Tb.length; i++) {
            Tb[i] = a.criticalFromStart(true, nodes[i]);
        }

        for (int i = 0; i < Ne.length; i++) {
            Ne[i] = a.criticalToEnd(false, nodes[i]);
        }

        for (int i = 0; i < Nb.length; i++) {
            Nb[i] = a.criticalFromStart(false, nodes[i]);
        }
    }

    public static void buildQueues() {

        StringBuilder sb = new StringBuilder();
        //#2
        int[] no2 = new int[nodes.length];
        Q2 = new ArrayList<Task>();
        for (int i = 0; i < no2.length; i++) {
            no2[i] = Tg - Te[i] - Tb[i];
        }

        for (int i = 0; i < no2.length; i++) {
            int min = Integer.MAX_VALUE;
            int k = 0;
            for (int j = 0; j < no2.length; j++) {
                if (no2[j] < min) {
                    min = no2[j];
                    k = j;
                }
            }
            Task task = new Task();
            task.setNumber(k);
            task.executed = false;
            task.setWeight(nodes[k].value);
            if (nodes[k].parents.isEmpty()) {
                task.setReady(true);
            } else {
                task.setReady(false);
            }
            Q2.add(task);
            sb.append(k + 1);
            sb.append("(");
            sb.append(min);
            sb.append(") \t");
            no2[k] = Integer.MAX_VALUE;
        }
        q2 = sb.toString();

        //#4
        sb = new StringBuilder();
        Q4 = new ArrayList<Task>();
        for (int i = 0; i < no2.length; i++) {
            int max = 0;
            int links = 0;
            int k = 0;
            for (int j = 0; j < Ne.length; j++) {
                if (Ne[j] > max) {
                    max = Ne[j];
                    links = nodes[j].downLinks.size() + nodes[j].upLinks.size();
                    k = j;
                } else {
                    if (Ne[j] == max) {
                        if (nodes[j].downLinks.size() + nodes[j].upLinks.size() > links) {
                            links = nodes[j].downLinks.size() + nodes[j].upLinks.size();
                            k = j;
                        }
                    }
                }

            }
            Task task = new Task();
            task.setNumber(k);
            task.executed = false;
            task.setWeight(nodes[k].value);
            if (nodes[k].parents.isEmpty()) {
                task.setReady(true);
            } else {
                task.setReady(false);
            }
            Q4.add(task);
            sb.append(k + 1);
            sb.append("(");
            sb.append(max);
            sb.append(", ");
            sb.append(links);
            sb.append(") \t");
            Ne[k] = 0;
        }
        q4 = sb.toString();

        //16
        sb = new StringBuilder();
        Q16 = new ArrayList<Task>();
        for (int i = 0; i < Tb.length; i++) {
            int min = Integer.MAX_VALUE;
            int k = 0;
            for (int j = 0; j < Tb.length; j++) {
                if (Tb[j] < min) {
                    min = Tb[j];
                    k = j;
                }
            }
            Task task = new Task();
            task.setNumber(k);
            task.executed = false;
            task.setWeight(nodes[k].value);
            if (nodes[k].parents.isEmpty()) {
                task.setReady(true);
            } else {
                task.setReady(false);
            }
            Q16.add(task);
            sb.append(k + 1);
            sb.append("(");
            sb.append(min);
            sb.append(") \t");
            Tb[k] = Integer.MAX_VALUE;
        }
        q16 = sb.toString();
    }

    public static String printQueues() {
        if (!checkCircles()) {
//            calcGraphCritical();
            buildQueues();
            StringBuilder sb = new StringBuilder();
            sb.append("Вариант №2:\t");
            sb.append(q2);
            sb.append("\n\n");
            sb.append("Вариант №4:\t");
            sb.append(q4);
            sb.append("\n\n");
            sb.append("Вариант №16:\t");
            sb.append(q16);
            return sb.toString();
        }
        return "";
    }

    public static  void updateMatrix() {
        clean();

        graph = GraphEditor.getSysComponent().getGraph();
        sysMatrix = getMatrix(graph);
        for (int i = 0; i < sysMatrix.length; i++) {
            for (int j = 0; j < sysMatrix[0].length; j++) {
                if (sysMatrix[i][j] != 0) {
                    sysMatrix[i][j] = 1;
                    sysMatrix[j][i] = 1;
                }
            }
        }
        processors = new Processor[sysMatrix.length];
        for (int i = 0; i < processors.length; i++) {
                processors[i] = new Processor();
            processors[i].free = true;
            processors[i].number = i;
            processors[i].freeTime = processors.length - i;
        }


        graph = GraphEditor.getTaskComponent().getGraph();
        taskMatrix = getMatrix(graph);
        if (!checkCircles()) {
            calcGraphCritical();
            buildQueues();
        }

//        System.out.println(q2);
//        System.out.println(q4);
//        System.out.println(q16);


        if (sysMatrix != null) {
//            checkMultigraph();
        }

        if (taskMatrix != null) {
            checkCircles();
        }
    }

    private static Integer[][] getMatrix(mxGraph graph) {
        int columns = 0, rows = 0;
        ArrayList<mxCell> places = new ArrayList<>();
        Object[] childCells = graph.getChildCells(graph.getDefaultParent());
        for (Object object : childCells) {
            if (object instanceof mxCell) {
                mxCell cell = (mxCell) object;
                if (cell.getValue() instanceof CellInfo) {
                    CellInfo info = (CellInfo) cell.getValue();
                    if (info.isVertex()) {
                        columns++;
                        rows++;
                        places.add(cell);
                        //addPlaceName
                    }
                }
            }
        }
        nodes = new Node[rows];
        for (int i = 0; i < rows; i++) {
            nodes[i] = new Node();
        }

        for (mxCell cell : places) {
            nodes[places.indexOf(cell)].value = Integer.parseInt(cell.getValue().toString());
        }

        values = new int[rows];
        Integer[][] result = new Integer[rows][columns];
        for (int i = 0; i < places.size(); i++) {
            values[i] = ((CellInfo)(places.get(i).getValue())).getMark();
        }


        for (int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.size(); j++) {
                result[i][j] = 0;
            }
        }

        for (Object object : childCells) {
            if (object instanceof mxCell) {
                mxCell cell = (mxCell) object;
                if (cell.isEdge()) {
                    mxCell edgeSource = (mxCell) ((mxCell) cell).getSource();
                    mxCell edgeTarget = (mxCell) ((mxCell) cell).getTarget();

                    if (edgeSource != null && edgeTarget != null) {
                        int a = 0, b = 0;
                        boolean found = false;
                        for (int i = 0; i < places.size(); i++) {
                            if (places.get(i).equals(edgeSource)) {
                                b = i;
                                found = true;
                                break;
                            }
                        }
                        for (int i = 0; i < places.size(); i++) {
                            if (places.get(i).equals(edgeTarget)) {
                                a = i;
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            result[b][a] = Integer.parseInt(cell.getValue().toString());
                            nodes[b].children.add(nodes[a]);
                            nodes[b].downLinks.add(result[b][a]);
                            nodes[a].parents.add(nodes[b]);
                            nodes[a].upLinks.add(result[b][a]);
                        }
                    }
                }
            }
        }
        return result;

    }

    public void setTaskMatrix(Integer[][] matrix) {
        taskMatrix = matrix;
    }

    public void setSysMatrix(Integer[][] matrix) {
        sysMatrix = matrix;
    }

    public static int getLastPlaceName() {
        if (taskMatrix == null) {
            return 0;
        }
        return taskMatrix.length;
    }

    public static int getLastSysName() {
        if (sysMatrix == null) {
            return 0;
        }
        return sysMatrix.length;
    }

    public static boolean checkMultigraph() {
        ArrayList<Integer> checked = new ArrayList<>();
        ArrayList<Integer> step = new ArrayList<>();
        boolean isMultigraph = false;
        if (sysMatrix != null);
        step.add(0);

        while (!step.isEmpty()) {
            for (int i = 0; i < sysMatrix.length; i++) {
                if (sysMatrix[step.get(0)][i] != 0 && !step.contains(i)) {
                    step.add(i);
                }
            }
            checked.add(step.get(0));
            step.remove(0);
        }

        isMultigraph = (checked.size() != sysMatrix.length);

        if (isMultigraph) {
            GraphEditor.getSysComponent().setBackground(Color.pink);
        } else {
            GraphEditor.getSysComponent().setBackground(Color.white);
        }
        return isMultigraph;
    }

    public static int getTotalGraphWeight() {
        int sum = 0;
        for (int i : values) {
            sum += i;
        }
        return sum;
    }

    public static boolean checkCircles() {
//        ArrayList<Integer> checked = new ArrayList<>();
//        ArrayList<Integer> step = new ArrayList<>();
//        boolean hasCircles = false;
//
//        for (int i = 0; i < taskMatrix.length; i++) {
//            step.clear();
//            checked.clear();
//            step.add(i);
//            while (!step.isEmpty()) {
//                for (int j = 0; j < taskMatrix.length; j++) {
//                    if (taskMatrix[step.get(0)][j] != 0 && !checked.contains(j)) {
//                        step.add(j);
//                    }
//                    if (taskMatrix[step.get(0)][j] != 0 && checked.contains(j)) {
//                        hasCircles = true;
//                        break;
//                    }
//                }
//                checked.add(step.get(0));
//                step.remove(0);
//            }
//            if (hasCircles)
//                break;
//        }
//        if (hasCircles) {
//            GraphEditor.getTaskComponent().setBackground(Color.pink);
//        } else {
//            GraphEditor.getTaskComponent().setBackground(Color.white);
//        }
//        return hasCircles;
        boolean[] tag = new boolean[taskMatrix.length];
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < tag.length; i++) {
                boolean input = false;
                if (!tag[i]) {
                    for (int j = 0; j < taskMatrix.length; j++) {
                        if (!tag[j] && taskMatrix[j][i] != 0) {
                            input = true;
                        }
                    }
                    if (!input) {
                        tag[i] = true;
                        changed = true;
                    }
                }

            }
        }
        for (boolean checked : tag) {
            if (!checked) {
                GraphEditor.getTaskComponent().setBackground(Color.pink);
                return true;
            }
        }
        GraphEditor.getTaskComponent().setBackground(Color.white);
        return false;
    }

    public static void simulate() {

    }

    public static void run() {
        queue.clear();
        if (GenerationPanel.q2_2.isSelected() || GenerationPanel.q2_5.isSelected()) {
            for (Task task : Q2) {
                queue.add(task.clone());
            }
        }
        if (GenerationPanel.q4_2.isSelected() || GenerationPanel.q4_5.isSelected()) {
            for (Task task : Q4) {
                queue.add(task.clone());
            }
        }
        if (GenerationPanel.q16_2.isSelected() || GenerationPanel.q16_5.isSelected()) {
            for (Task task : Q16) {
                queue.add(task.clone());
            }
        }

        time = 0;
        datasetNames = new String[0][0];
        executedTasks = new ArrayList<>();
        processors = new Processor[sysMatrix.length];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Processor();
            processors[i].free = true;
            processors[i].number = i;
            processors[i].freeTime = processors.length - i;
        }

        boolean end = true;

        while (true) {
            end = true;
            planning();
            time++;
            for (Task task : queue) {
                if (!task.executed) {
                    end = false;
                    break;
                }
            }
            if (end) {
                break;
            }
        }
        Gantt gantt = new Gantt();
    }

    public static void planning() {
        distances = findDistances(sysMatrix);
        if (!queue.isEmpty()) {
            //освобождение процессоров
            for (Processor proc : processors) {
                if (!proc.free && proc.busyTo == time) {
                    proc.free = true;
                    proc.freeTime = 0;
                }
            }
            for (Processor proc : processors) {
                if (proc.free) {
                    proc.freeTime++;
                }
                if (!proc.sendings.isEmpty()) {
                    //подсчет времени, что прошло с последней пересылки. Только если оно положительное можно использовать процессор для передачи инфы
                    proc.lastSending = time - (int)proc.sendings.get(proc.sendings.size() - 1).get(3);
                }
            }
            for (Task task : queue) {
                //окончание работы задачи
                if (task.isReady() && !task.executed && task.startTime != task.endTime && task.endTime == time) {
                    task.executed = true;
                    task.processing = false;
                    task.processor.free = true;
                    task.processor.freeTime = 0;
                    executedTasks.add(task);
//                    task.processor.busyFrom = 0;
//                    task.processor.busyTo = 0;
                    boolean ready = true;
                    for (Node node : nodes[task.getNumber()].children) {
                        ready = true;
                        for (Node n : node.parents) {
                            if (!getTask(n).executed) {
                                ready = false;
                            }
                        }
                        getTask(node).setReady(ready);
                    }
                }
            }
            for (Task task : queue) {

                if (task.isReady() && !task.processing && !task.executed) {
                    //назначение задачи на процессор
                    if (getFreeProc(task) != null) {
                        if (isAvailableData(getFreeProc(task), task)) {
                            //задача готова к погружению на процессор
                            task.processor = getFreeProc(task);
                            task.startTime = time;
                            task.endTime = task.startTime + task.getWeight();
                            task.processor.free = false;
                            task.processing = true;
                            task.processor.freeTime = 0;
                            task.processor.busyFrom = task.startTime;
                            task.processor.busyTo = task.endTime;
                        } else {
                            //задача не готова к погружению, нужна пересылка данных
                            for (Task parent : executedTasks) {
                                if (nodes[task.getNumber()].parents.contains(nodes[parent.getNumber()]) && !parent.processor.equals(getFreeProc(task))) {
                                    if (parent.executed) {
                                        boolean isPath = false;
                                        for (ArrayList<Object> sending : getFreeProc(task).sendings) {
                                            if (sending.get(4).equals(task) && sending.get(5).equals(parent.processor)) {
                                                isPath = true;
                                                break;
                                            }
                                        }
                                        //расчитать путь, если пути нет
                                        if (!isPath) {
                                            getBestPath(parent.processor, getFreeProc(task), task, nodes[parent.getNumber()].downLinks.get(nodes[parent.getNumber()].children.indexOf(nodes[task.getNumber()])));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//
    }

    private static ArrayList<Processor> getBestPath(Processor p1, Processor p2, Task task, int weight) {

        ArrayList<ArrayList<Processor>> pathes = getPathes(p1);

        int sendingTime = 0;
        int min = Integer.MAX_VALUE;
        int pathNumber = 0;
        for (ArrayList<Processor> path : pathes) {
            if (path.contains(p1) && path.contains(p2)) {
                if (p1.equals(p2)) {
                    return pathes.get(0);
                }
                //не впихиваем пересылки, а добавляем после последней
                //Выбираем максимум из получаения данных и возможности пересылки
//                sendingTime = Math.max(p1.busyTo,
//                        Math.max((int)p1.sendings.get(p1.sendings.size() - 1).get(3),
//                                (int)path.get(1).sendings.get(path.get(1).sendings.size() - 1).get(3)));
                sendingTime = (p1.busyTo > getLastSendingTime(p1))
                        ? p1.busyTo
                        : getLastSendingTime(p1);
                for (Processor proc : path) {

                    //еще не дошли до конца пути
                    if (path.indexOf(proc) < path.indexOf(p2)) {
                        Processor p = path.get(path.indexOf(proc) + 1);
                        //проверка, что позже освободится: первый или второй процессор
                        sendingTime = Math.max(sendingTime,
                                getLastSendingTime(p));
                        sendingTime += weight;

                    }
                }
                if (sendingTime < min) {
                    min = sendingTime;
                    bestPathTime = min;
                    pathNumber = pathes.indexOf(path);
                }
                //записали инфу о пересылке во все процессоры

            }
        }
        ArrayList<Processor> path = pathes.get(pathNumber);
        sendingTime = (p1.busyTo > getLastSendingTime(p1))
                ? p1.busyTo
                : getLastSendingTime(p1);
        for (Processor proc : path) {
            if (path.indexOf(proc) < path.indexOf(p2)) {
                Processor p = path.get(path.indexOf(proc) + 1);
                //проверка, что позже освободится: первый или второй процессор
                sendingTime = Math.max(sendingTime,
                        getLastSendingTime(p));
                ArrayList<Object> sending = new ArrayList<>();
                sending.add(proc);
                sending.add(sendingTime);
                sending.add(p);
                sendingTime += weight;
                sending.add(sendingTime);
                sending.add(task);
                sending.add(p1);
                sending.add(p2);
                sending.add((p1.number + 1) + "->" + (p2.number + 1) + "(" + task.getNumber() + ")");
                //добавили инфу о пересылке между двумя соседними процами
                proc.sendings.add(sending);
                p.sendings.add(sending);
            }
        }
        return pathes.get(pathNumber);
    }

    private static int getLastSendingTime(Processor proc) {
        if (proc.sendings.isEmpty()) {
            return 0;
        } else {
            return (int)proc.sendings.get(proc.sendings.size() - 1).get(3);
        }
    }

    private static ArrayList<ArrayList<Processor>> getPathes(Processor p1) {
        ArrayList<ArrayList<Processor>> result = new ArrayList<>();
        ArrayList<ArrayList<Processor>> metalist = new ArrayList<>();
        ArrayList<Processor> buf = new ArrayList<>();
        buf.add(p1);
        result.add(buf);
        boolean incremented = false;
        while (true) {
            for (ArrayList<Processor> list : result) {
                incremented = false;
                Processor proc = list.get(list.size() - 1);
                for (int i = 0; i < sysMatrix.length; i++) {
                    if (sysMatrix[proc.number][i] != 0 ) {
                        if (!list.contains(processors[i])) {
                            buf = (ArrayList<Processor>)list.clone();
                            buf.add(processors[i]);
                            if (!result.contains(buf)){
                                metalist.add((ArrayList<Processor>)buf.clone());
                                incremented = true;
                            }
                        }
                    }
                }
            }

            if (!incremented) {
                break;
            } else {
//                result.clear();
                for (ArrayList<Processor> list : metalist) {
                    result.add(list);
                }
                metalist.clear();
            }
        }
        return result;
    }


    private static boolean isAvailableData(Processor proc, Task task) {
        boolean result = false;
        if (nodes[task.getNumber()].parents.isEmpty()) {
            return true;
        }
        for (Task parent : queue) {
            result = true;
            //если нужна пересылка
            if (nodes[task.getNumber()].parents.contains(nodes[parent.getNumber()]) && !parent.processor.equals(proc)) {
                result = false;
                for (ArrayList<Object> sending : proc.sendings) {
                    if (task.equals(sending.get(4))) {
                        result = true;
                        if (time < (int)sending.get(3)) {
                            result = false;
                            break;
                        }
                    }
                }
//                return false;
            }
            if (!result) {
                return result;
            }
        }
        return result;

    }

    private static Processor getFreeProc(Task task) {
        if (GenerationPanel.q2_2.isSelected() || GenerationPanel.q4_2.isSelected() || GenerationPanel.q16_2.isSelected()) {
            int max = 0;
            Processor result = null;
            for (Processor proc : processors) {
                if (proc.free || time >= proc.busyTo) {
                    if (proc.freeTime > max) {
                        max = proc.freeTime;
                        result = proc;
                    }
                }
            }
            return result;
        }  else {
//            doBackup();
            Processor result = null;
            int min = Integer.MAX_VALUE;
            int max = 0;
            int procNumber = 0;
            LinkedList<Processor> freeProcessors = new LinkedList<Processor>();
            //найти свободные  процы
            for (Processor proc : processors) {
                if (proc.free) {
                    freeProcessors.add(proc);
                }
            }
            if (freeProcessors.isEmpty()) {
                return null;
            }
            //найти родительские вершины
            LinkedList<Processor> parentProcessors = new LinkedList<>();
            LinkedList<Integer> weights = new LinkedList<>();
            for (Node node : nodes[task.getNumber()].parents) {
                parentProcessors.add(getTask(node).processor);
                weights.add(node.downLinks.get(node.children.indexOf(nodes[task.getNumber()])));
            }
            if (parentProcessors.isEmpty()) {
                return freeProcessors.get(0);
            }
            for (Processor freeProc : freeProcessors) {
                max = 0;
                for (Processor parentProc : parentProcessors) {
                    if (parentProc.busyTo + distances[freeProc.number][parentProc.number] * weights.get(parentProcessors.indexOf(parentProc)) > max) {
                        max = parentProc.busyTo + distances[freeProc.number][parentProc.number] * weights.get(parentProcessors.indexOf(parentProc));
                    }
                }
                if (max < min) {
                    min = max;
                    result = freeProc;
                }
            }
              //FIXME определние проца, который раньше всего начнет работу
//            for (Processor freeProc : freeProcessors) {
//                max = 0;
//                int[] sendingsToRemove = new int[processors.length];
//                ArrayList<Processor> path = new ArrayList<>();
//                for (Processor parentProc : parentProcessors) {
//                    getBestPath(parentProc, freeProc, task, weights.get(parentProcessors.indexOf(parentProc)));
//                }
//                if (getLastSendingTime(freeProc) < min) {
//                    min = getLastSendingTime(freeProc);
//                    procNumber = freeProc.number;
//                }
//                restoreBackup();
//            }
//            result = processors[procNumber];
            return result;
        }
    }

    private static int[][] findDistances(Integer[][] system) {
        int[][] result = new int[system.length][];
        for (int i = 0; i < system.length; i++) {
            result[i] = new int[system.length];
            for (int k = 0; k < system.length; k++) {
                result[i][k] = Integer.MAX_VALUE;
            }
            result[i][i] = 0;
            boolean changed = true;
            while (changed) {
                changed = false;
                for (int k = 0; k < system.length; k++) {
                    for (int j = 0; j < system.length; j++) {
                        if (system[k][j] == 1) {
                            if (result[i][k] + 1 < result[i][j] && result[i][k] + 1 >= 0) {
                                changed = true;
                                result[i][j] = result[i][k] + 1;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void doBackup() {
        backupQueue.clear();
        backupProcessors = new Processor[processors.length];
        for (int i = 0; i < processors.length; i++) {
            backupProcessors[i] = processors[i].clone();
        }
        backupProcessors = processors.clone();
        for (Task task : queue) {
            backupQueue.add(task.clone());
        }
    }

    private static void restoreBackup() {
        processors = new Processor[backupProcessors.length];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = backupProcessors[i].clone();
        }
        processors = backupProcessors.clone();
        queue.clear();
        for (Task task : backupQueue) {
            queue.add(task.clone());
        }
    }

    private static  Task getTask(Node node) {
        for (Task task : queue) {
            if (node.equals(nodes[task.getNumber()])) {
                return task;
            }
        }
        return null;
    }

    public static double[][] generateData() {
        double[][] data = new double[2 * 10][2 * processors.length];
        datasetNames = new String[data.length][2 * processors.length];

        queues.clear();
//        queues.add(new Task[processors.length]);
        for (Task task : executedTasks) {
            int noteNumber = 0;
            boolean needNewNote = true;
            for (Task[] element : queues) {
                if (element[task.processor.number] == null) {
                    needNewNote = false;
                    break;
                }
                noteNumber++;
            }
            if (needNewNote) {
                queues.add(new Task[processors.length]);
//                noteNumber++;
            }
            queues.get(noteNumber)[task.processor.number] = task;
        }
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = 0;
            }
        }
        for (Task[] element : queues) {
            for (int i = 0; i < element.length; i++) {

                int j = queues.indexOf(element);
                if (element[i] == null) {
                    data[2 * j][2 * i] = 0;
                    data[2 * j + 1][2 * i] = 0;
                    continue;
                }
                if (j == 0) {
                    data[2 * j][2 * i] = element[i].startTime;
                    datasetNames[2 * j][2 * i] = "";
                } else {
                    data[2 * j][2 * i] = element[i].startTime - queues.get(j - 1)[i].endTime;
                    datasetNames[2 * j][2 * i] = "";
                }
                data[2 * j + 1][2 * i] = element[i].endTime -  element[i].startTime;
                datasetNames[2 * j + 1][2 * i] = element[i].getNumber() + "";
            }
        }
        for (int i = 0; i < processors.length; i++) {
            for (ArrayList<Object> element : processors[i].sendings) {
                int j = processors[i].sendings.indexOf(element);
                if (processors[i].equals(element.get(0))) {
                    if (j == 0) {
                        data[2 * j][2 * i + 1] = Double.parseDouble(element.get(1).toString());
                        datasetNames[2 * j][2 * i + 1] = "";
                    } else {
                        data[2 * j][2 * i + 1] = Double.parseDouble(element.get(1).toString()) - Double.parseDouble(processors[i].sendings.get(j - 1).get(3).toString());
                        datasetNames[2 * j][2 * i + 1] = "";
                    }
                    data[2 * j + 1][2 * i + 1] = Double.parseDouble(element.get(3).toString()) - Double.parseDouble(element.get(1).toString());
                    datasetNames[2 * j + 1][2 * i + 1] = element.get(7).toString();
                } else {
                    if (j == 0) {
                        data[2 * j][2 * i + 1] = Double.parseDouble(element.get(1).toString());
                        datasetNames[2 * j][2 * i + 1] = "";
                    } else {
                        data[2 * j][2 * i + 1] = Double.parseDouble(element.get(1).toString()) - Double.parseDouble(processors[i].sendings.get(j - 1).get(3).toString());
                        datasetNames[2 * j][2 * i + 1] = "";
                    }
                    data[2 * j][2 * i + 1] += Double.parseDouble(element.get(3).toString()) - Double.parseDouble(element.get(1).toString());
                    data[2 * j + 1][2 * i + 1] = 0;
//                    datasetNames[2 * j + 1][2 * i + 1] = element.get(7).toString();
                }
            }
        }
        return data;
    }
}
