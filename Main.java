import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph();
        graph.ReadGraphFromFile("graph.txt");
        boolean cont = false;
        while (!cont) {
            printMenu();
            System.out.print("Enter your choice :");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("The graph is " + (graph.IsDirected() ? "" : "not ") + "directed.");
                    break;
                case "2":
                    System.out.print("Enter source vertex :");
                    String c2_v1 = scanner.nextLine().trim();
                    System.out.print("Enter destination vertex :");
                    String c2_v2 = scanner.nextLine().trim();
                    if (graph.IsThereAPath(c2_v1, c2_v2))
                        System.out.println("There is a path between " + c2_v1 + " and " + c2_v2 + ".");
                    else
                        System.out.println("There is no path between " + c2_v1 + " and " + c2_v2 + ".");
                    break;

                case "3":
                    System.out.print("Enter source vertex for BFS: ");
                    String bfsSource = scanner.nextLine().trim();
                    System.out.print("Enter destination vertex for BFS: ");
                    String bfsDest = scanner.nextLine().trim();
                    graph.BFSfromTo(bfsSource, bfsDest);
                    break;

                case "4":
                    System.out.print("Enter source vertex for DFS: ");
                    String dfsSource = scanner.nextLine().trim();
                    System.out.print("Enter destination vertex for DFS: ");
                    String dfsDest = scanner.nextLine().trim();
                    graph.DFSfromTo(dfsSource, dfsDest);
                    break;
                case "5":
                    System.out.print("Enter source vertex for shortest path: ");
                    String spSource = scanner.nextLine().trim();
                    System.out.print("Enter destination vertex for shortest path: ");
                    String spDest = scanner.nextLine().trim();
                    int shortestPathLength = graph.WhatIsShortestPathLength(spSource, spDest);
                    if (shortestPathLength != -1) {
                        System.out.println("Shortest path length from " + spSource + " to " + spDest + " is: "
                                + shortestPathLength);
                    } else {
                        System.out.println("No path found from " + spSource + " to " + spDest + ".");
                    }
                    break;
                case "6":
                    System.out.print("Enter source vertex to count paths: ");
                    String pathSource = scanner.nextLine().trim();
                    System.out.print("Enter destination vertex to count paths: ");
                    String pathDest = scanner.nextLine().trim();
                    int numberOfPaths = graph.NumberOfSimplePaths(pathSource, pathDest);
                    System.out.println(
                            "Number of simple paths from " + pathSource + " to " + pathDest + " is: " + numberOfPaths);
                    break;
                case "7":
                    System.out.print("Enter vertex to list its neighbors: ");
                    String neighborVertex = scanner.nextLine().trim();
                    System.out.println("Neighbors of " + neighborVertex + ": " + graph.Neighbors(neighborVertex));
                    break;
                case "8":
                    System.out.println("Vertex/Vehicles with the highest degree: " + graph.HighestDegree());
                    break;
                case "9":
                    System.out.print("Enter first vertex to check adjacency: ");
                    String adjV1 = scanner.nextLine().trim();
                    System.out.print("Enter second vertex to check adjacency: ");
                    String adjV2 = scanner.nextLine().trim();
                    boolean adjacent = graph.AreTheyAdjacent(adjV1, adjV2);
                    System.out.println(adjV1 + " and " + adjV2 + " are " + (adjacent ? "" : "not ") + "adjacent.");
                    break;
                case "10":
                    System.out.print("Enter vertex to check for a cycle: ");
                    String cycleVertex = scanner.nextLine().trim();
                    boolean hasCycle = graph.IsThereACycle(cycleVertex);
                    System.out.println(
                            "There is " + (hasCycle ? "" : "no ") + "cycle involving vertex " + cycleVertex + ".");
                    break;
                case "11":
                    System.out.print("Enter vertex to find its connected component size: ");
                    String componentVertex = scanner.nextLine().trim();
                    int componentSize = graph.NumberOfVerticesInComponent(componentVertex);
                    System.out.println("Number of vertices in the connected component containing " + componentVertex
                            + ": " + componentSize);
                    break;
                case "12":
                    cont = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option (1-11).");
            }
            System.out.println("---------------------------------------------");
        }
        scanner.close();
    }

    final static void printMenu() {
        System.out.println("Please select an operation:");
        System.out.println("1. Check if the graph is directed");
        System.out.println("2. Check if there is a path between two vertices");
        System.out.println("3. Perform BFS from a source to a destination");
        System.out.println("4. Perform DFS from a source to a destination");
        System.out.println("5. Find the shortest path length between two vertices");
        System.out.println("6. Count the number of simple paths between two vertices");
        System.out.println("7. List neighbors of a vertex");
        System.out.println("8. Find vertex(es) with the highest degree");
        System.out.println("9. Check if two vertices are adjacent");
        System.out.println("10. Check if there's a cycle involving a vertex");
        System.out.println("11. Find the number of vertices in a vertex's connected component");
        System.out.println("12. Exit");
    }
}
