/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class MyHost extends Host {

    private volatile PriorityQueue<Task> priorityQueue;
    private volatile Task currentTask = null;
    private volatile boolean shouldStop = false;
    private volatile Semaphore semaphore = new Semaphore(1);
    private volatile double startTime = 0 ;
    private volatile boolean notified = false;
    public MyHost() {
        priorityQueue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.getPriority() > o2.getPriority())
                    return -1;
                else if (o1.getPriority() < o2.getPriority())
                    return 1;
                else 
                {
                    if(o1.getStart() < o2.getStart())
                        return -1;
                    else if (o1.getStart() > o2.getStart())
                        return 1;
                    else
                        return 0;
                }
                    
            }
        });
    }

    @Override
    public void run() {
        while (!shouldStop) {
            
            double time = Timer.getTimeDouble();
            if (priorityQueue.size() == 0 && currentTask == null) {
               continue;
            }
            if (currentTask == null) {
                if(priorityQueue.peek().getStart() > time) {
                    continue;
                }
                synchronized(this) {
                    currentTask = priorityQueue.poll();
                }
            }
            Task taskurm;
            synchronized(this) {
                taskurm = priorityQueue.peek();
            }
            if(taskurm != null && currentTask.getPriority() < taskurm.getPriority()
                && taskurm.getStart() <= time && currentTask.isPreemptible()) {
                synchronized(this) {
                    priorityQueue.add(currentTask);
                    currentTask = priorityQueue.poll();
                }
            }
            try {
                startTime = Timer.getTimeDouble();
                notified = semaphore.tryAcquire(1000, TimeUnit.MILLISECONDS);
                if (notified) {
                        synchronized(this) {
                            priorityQueue.add(currentTask);
                            currentTask = priorityQueue.poll();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                    }  
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTask.setLeft(currentTask.getLeft() - 1000);
            if (currentTask.getLeft() == 0)
            {
                currentTask.finish();
                synchronized(this) {
                    currentTask = null;
                }
            }
        }
    }

    @Override
    synchronized public void addTask(Task task) {
        priorityQueue.add(task);
        if (currentTask != null && currentTask.getPriority() < task.getPriority() &&
                task.getStart() == Math.round(Timer.getTimeDouble()) &&
                Timer.getTimeDouble() - startTime < 0.1 &&
                Math.round(startTime) == task.getStart() &&
                currentTask.isPreemptible()) {
            semaphore.release();
        }
    }

    @Override
    public int getQueueSize() {
        int size = 0;
        synchronized(this) {
        if (currentTask != null)
            size =  priorityQueue.size() + 1;
        else
            size = priorityQueue.size();
        }
        return size;
    }

    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        synchronized(this) {
            if (currentTask != null) {
                workLeft += currentTask.getLeft();
            }
            for (Task task : priorityQueue) {
                if (task != currentTask) {
                    workLeft += task.getLeft();
                }
            }
        }
        return workLeft;
    }

    @Override
    public void shutdown() {
        shouldStop = true;
    }
  
   
}
