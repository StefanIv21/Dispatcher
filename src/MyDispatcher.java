/* Implement this class. */
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {

    ConcurrentHashMap<Integer, TaskType> map = new ConcurrentHashMap<Integer, TaskType>(); 
    private AtomicInteger taskAdded = new AtomicInteger(0);

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if(algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            int index = taskAdded.get() % hosts.size(); 
            hosts.get(index).addTask(task);
            taskAdded.getAndIncrement();
        } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            int index = -1;
            int min = Integer.MAX_VALUE; 
            synchronized(this) {
                for (int i = 0; i < hosts.size(); i++) {
                    int queueSize = hosts.get(i).getQueueSize();
                    if((queueSize < min) || (queueSize == min && i < index)) {
                        min = queueSize;
                        index = i;
                    }
                }
                hosts.get(index).addTask(task);
            }
        } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            for (int i = 0; i < hosts.size(); i++) {
                map.putIfAbsent(i,task.getType());
                if(map.get(i) == task.getType()) {
                    hosts.get(i).addTask(task);
                    break;
                }
            }
        } else {
            int index = Integer.MAX_VALUE;
            long min = Integer.MAX_VALUE; 
            synchronized(this) {
                for (int i = 0; i < hosts.size(); i++) {
                    long workLeft = hosts.get(i).getWorkLeft();
                    if((workLeft < min) || (workLeft == min && i < index)) {
                        min = workLeft;
                        index = i;
                    }   
                }  
                hosts.get(index).addTask(task);
            } 
        }
    }
}

