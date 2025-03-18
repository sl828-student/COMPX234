import java.util.ArrayList;

class Assignment1Task {

    // Simulation Initialisation
    private static int NUM_MACHINES = 5; // Number of machines in the system that issue print requests
    private static int NUM_PRINTERS = 5; // Number of printers in the system that print requests
    private static int SIMULATION_TIME = 30;
    private static int MAX_PRINTER_SLEEP = 3;
    private static int MAX_MACHINE_SLEEP = 5;
    private static boolean sim_active = true;

    // Create an empty list of print requests
    printList list = new printList();

    public void startSimulation() {

        // ArrayList to keep for machine and printer threads
        ArrayList<Thread> mThreads = new ArrayList<Thread>();
        ArrayList<Thread> pThreads = new ArrayList<Thread>();

        // Create Machine and Printer threads
        // Write code here
        machineThread mThread;
        printerThread pThread;
        for (int i = 0; i < NUM_MACHINES; i++) {
            mThread = new machineThread(i);
            mThreads.add(mThread);
        }
        for (int i = 0; i < NUM_PRINTERS; i++) {
            pThread = new printerThread(i);
            pThreads.add(pThread);
        }

        System.out.println("Simulation started");
        System.out.println("Number of machines: " + NUM_MACHINES);
        System.out.println("Number of printers: " + NUM_PRINTERS);

        // start all the threads
        // Write code here
        for (Thread t : mThreads) {
            t.start();
        }
        for (Thread t : pThreads) {
            t.start();
        }

        // let the simulation run for some time
        sleep(SIMULATION_TIME);

        // finish simulation
        sim_active = false;

        // Wait until all printer threads finish by using the join function
        // Write code here
        for (Thread t : pThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread t : mThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    // Printer class
    public class printerThread extends Thread {
        private int printerID;

        public printerThread(int id) {
            printerID = id;
        }

        public void run() {
            while (sim_active) {
                // Simulate printer taking some time to print the document
                printerSleep();
                // Grab the request at the head of the queue and print it
                // Write code here
                synchronized (list) {
                    if (list.head != null) {
                        printDox(printerID);
                    }
                }
            }
        }

        public void printerSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_PRINTER_SLEEP);
            // sleep(sleepSeconds*1000);
            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printDox(int printerID) {
            // System.out.println("Printer ID:" + printerID + " : now available");
            // print from the queue
            synchronized (list) {
                list.queuePrint(list, printerID);
            }
        }

    }

    // Machine class
    public class machineThread extends Thread {
        private int machineID;

        public machineThread(int id) {
            machineID = id;
        }

        public void run() {
            while (sim_active) {
                // machine sleeps for a random amount of time
                machineSleep();
                // machine wakes up and sends a print request
                // Write code here
                synchronized (list) {
                    if (list.getLength() < NUM_PRINTERS) {
                        printRequest(machineID);
                    } else {
                        System.out.println("Machine " + machineID + " is waiting for a space in the queue");
                    }
                }
            }
        }

        // machine sleeps for a random amount of time
        public void machineSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_MACHINE_SLEEP);

            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printRequest(int id) {
            System.out.println("Machine " + id + " Sent a print request");
            // Build a print document
            printDoc doc = new printDoc("My name is machine " + id, id);
            // Insert it in print queue
            list = list.queueInsert(list, doc);
        }
    }

    private static void sleep(int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException ex) {
            System.out.println("Sleep Interrupted");
        }
    }
}
