import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Main {

    // Task Class
    static class Task implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        String status;
        int priority;
        
        public Task(String taskName, String taskStatus, int taskPriority) {
            this.name = taskName;
            this.status = taskStatus;
            this.priority = taskPriority;
        }
    }

    // ToDoList Class
    static class ToDoList implements Serializable {
        private static final long serialVersionUID = 1L;

        ArrayList<Task> list;

        public ToDoList() {
            list = new ArrayList<>();
        }
        
        public void addToList(Task task) {
            list.add(task);
        }

        public void markAsComplete(ArrayList<Integer> indices) {
            for (int index: indices) {
                // Check if index is in range
                if (0 <= index - 1 && index - 1 < list.size()) {
                    list.get(index - 1).status = "Complete";
                }
            }
        }

        public void deleteFromList(ArrayList<Integer> indices) {
            for (int index: indices) {
                // Check if index is in range
                if (0 <= index - 1 && index - 1 < list.size()) {
                    list.remove(index - 1);
                }
            }
        }
    }

    public static ArrayList<Integer> parseArrayToIntegers(String numbers) {
                
        // Split the string by space
        String[] numberStrings = numbers.split(" ");

        // Convert each string into an integer
        ArrayList<Integer> numberArrayList = new ArrayList<>();
        for (String numberString: numberStrings) {
            try {
                int number = Integer.parseInt(numberString);
                numberArrayList.add(number);
            } catch (NumberFormatException _) {
                continue;
            }
        }
        return numberArrayList;
    }

    // Serialize ToDoList
    public static void serializeToDoList(ToDoList toDoList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("todolist.ser"))) {
            oos.writeObject(toDoList);
        } catch (IOException e) {
            System.out.println("An error occured during serialization.");
            e.printStackTrace();
        }
    }

    // Deserialize ToDoList
    public static ToDoList deserializeToDoList() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("todolist.ser"))) {
            ToDoList toDoList = (ToDoList) ois.readObject();
            // if toDoList is null, initialize empty one
            if (toDoList == null) {
                toDoList = new ToDoList();
            }
            return toDoList;
        } catch (FileNotFoundException _) { // if there is no file yet, create empty ToDoList
            ToDoList toDoList = new ToDoList();
            return toDoList;
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occured during deserialization.");
            e.printStackTrace();
            return null;
        }
    }

    // Sort list from highest to lowest priority tasks
    public static void sortToDoList(ToDoList toDoList) {
        Collections.sort(toDoList.list, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return Integer.compare(t1.priority, t2.priority);
            }
        });
    }

    public static void main(String[] args) {

        // Read saved ToDoList
        ToDoList toDoList = deserializeToDoList();

        // Create a Scanner object to read user input
        Scanner scannerIn = new Scanner(System.in);

        String option;
        boolean run = true;

        while (run) {

            // Ask user what to do
            System.out.println();
            System.out.println("What would you like to do?");
            System.out.println("\"V\": View tasks");
            System.out.println("\"A\": Add task");
            System.out.println("\"M\": Mark task(s) as complete");
            System.out.println("\"D\": Delete task(s)");
            System.out.println("\"E\": Exit program");

            // Read user's input
            option = scannerIn.nextLine().toUpperCase();

            switch(option) {
                case "E":
                    run = false;
                    serializeToDoList(toDoList);
                    break;
                case "D":
                    System.out.println("Which tasks would you like to delete?");
                    String numbersToDelete = scannerIn.nextLine();
                    ArrayList<Integer> numbersToDeleteList = parseArrayToIntegers(numbersToDelete);

                    // Convert to Set to get rid of duplicates, then again into ArrayList
                    Set<Integer> numbersToDeleteSet = new HashSet<>(numbersToDeleteList);
                    numbersToDeleteList.clear();
                    numbersToDeleteList.addAll(numbersToDeleteSet);

                    // Sort in descending order to make sure no task escapes due to changed index
                    Collections.sort(numbersToDeleteList, Collections.reverseOrder()); 
                    toDoList.deleteFromList(numbersToDeleteList);
                    break;
                case "M":
                    System.out.println("Which tasks would you like to mark as completed?");
                    String numbersToMark = scannerIn.nextLine();
                    ArrayList<Integer> numbersToMarkList = parseArrayToIntegers(numbersToMark);
                    toDoList.markAsComplete(numbersToMarkList);
                    break;
                case "A":
                    System.out.println("Set the name of the task: ");
                    String taskName = scannerIn.nextLine();
                    Integer taskPriority = null;
                    while (taskPriority == null) {
                        System.out.println("Set the priority of the task: ");
                        try {
                            taskPriority = scannerIn.nextInt();
                        } catch (java.util.InputMismatchException e) { // If priority isn't a number
                            System.out.println("Priority must be a number");
                            scannerIn.next();
                        }
                    }
                    Task newTask = new Task(taskName, "Incomplete", taskPriority);
                    toDoList.addToList(newTask);
                    sortToDoList(toDoList);
                    break;
                case "V":
                    int iteration = 1;
                    for (Task i: toDoList.list) {
                        System.out.printf("%d: %s, %d - %s%n", iteration, i.name, i.priority, i.status);
                        iteration++;
                    }
                    break;
                default:
                    System.out.println("Please input correct character.");
            }
        }
        scannerIn.close();
    }
}