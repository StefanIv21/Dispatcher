# Task Scheduling System in a Datacenter using Java Threads
## Project Overview

This project simulates a task scheduling system within a datacenter using Java Threads. The main goal is to implement the logic for both the dispatcher (load balancer) and the computing nodes to efficiently manage and execute incoming tasks according to various scheduling policies.
## System Architecture

The system is composed of two main components:

- Dispatcher (Load Balancer): The dispatcher receives tasks and assigns them to the appropriate nodes based on predefined scheduling policies. It ensures that tasks are distributed efficiently across the datacenter.

- Computing Nodes: Each node in the datacenter executes the tasks assigned by the dispatcher. Nodes have queues to store incoming tasks that are pending execution. Nodes can also preempt running tasks if a higher-priority task is received.

## Scheduling Policies

The dispatcher can use one of the following four scheduling policies to allocate tasks to nodes:
1. Round Robin (RR)

    Tasks are assigned to nodes in a cyclic manner. If the last task was assigned to node i, the next task will be assigned to node (i + 1) % n, where n is the total number of nodes. The process starts with node 0.

2. Shortest Queue (SQ)

    The dispatcher assigns tasks to the node with the shortest queue. The length of the queue is determined by the number of tasks waiting to be executed, including the task currently running. If multiple nodes have the same queue length, the task is assigned to the node with the smallest ID.

3. Size Interval Task Assignment (SITA)

    In this policy, there are three fixed nodes. Tasks are categorized as short, medium, or long. Each node is responsible for a specific type of task. For example, short tasks go to node 0, medium tasks to node 1, and long tasks to node 2.

4. Least Work Left (LWL)

    Similar to the Shortest Queue policy, but instead of considering the number of tasks, it considers the total remaining execution time. The task is assigned to the node with the least remaining work. If two nodes have the same remaining work, the task is assigned to the node with the smallest ID.

Task Properties

Each task in the system is characterized by the following properties:

  - ID: A unique integer identifier for the task.
- Start Time: The time at which the task enters the system.
 -  Duration: The amount of time required to execute the task on any node.
- Type: The type of task (short, medium, long), relevant only for the SITA policy.
- Priority: An integer representing the importance of the task (higher priority tasks are scheduled before lower priority ones).
 - Preemptibility: A boolean indicating whether the task can be preempted by a higher-priority task.

Task Priority and Preemption

  - Priority: Tasks with higher priority are scheduled before those with lower priority. If two tasks have the same priority, they are scheduled in the order they arrived.
- Preemption: If a task is preemptible and a higher-priority task arrives, the running task will be interrupted, and the higher-priority task will start executing.

## Running the Project

To compile and run the project, follow these steps:

```bash
$ javac *.java
$ java Main
```

## Executing the Tests

When you run the program with the command above, the entire suite of tests will be executed.

If you wish to run only a subset of the tests, you can modify the tests array declaration in the main function of the Main class. The input files for each test contain lists of tasks that are part of that specific test.
