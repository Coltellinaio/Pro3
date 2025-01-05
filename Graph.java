import java.io.*;
import java.util.*;

public class Graph {

    private int pathCount = 0;
    private Map<String, Integer> nameToIndex = new HashMap<>();
    private List<String> indexToName = new ArrayList<>();
    private List<List<Edge>> adjacencyList = new ArrayList<>();

    private boolean isDirected;

    private static class Edge {
        int target;
        int weight;

        public Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    private int ensureVertex(String cityName) {
        if (!nameToIndex.containsKey(cityName)) {
            nameToIndex.put(cityName, indexToName.size());
            indexToName.add(cityName);
            adjacencyList.add(new ArrayList<>());
        }
        return nameToIndex.get(cityName);
    }

    private void addEdge(String city1, String city2, int weight) {
        int v1 = ensureVertex(city1);
        int v2 = ensureVertex(city2);
        adjacencyList.get(v1).add(new Edge(v2, weight));
    }

    public void ReadGraphFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] splitArrow = line.split("->");
                if (splitArrow.length < 2) {
                    continue; // malformed line
                }
                String source = splitArrow[0].trim();
                // Right side has edges separated by commas
                String edgesPart = splitArrow[1].trim();
                // Example edgesPart = "B:3, C:2, D:1, E:4"

                // Each edge is separated by comma
                String[] edgeDefs = edgesPart.split(",");
                for (String ed : edgeDefs) {
                    ed = ed.trim();
                    // Ed is something like "B:3"
                    String[] parts = ed.split(":");
                    if (parts.length == 2) {
                        String neighbor = parts[0].trim();
                        int weight = Integer.parseInt(parts[1].trim());
                        addEdge(source, neighbor, weight);
                    }
                }
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // 1) IsDirected()
    // Returns true if there is a path between vertex v1 and vertex v2 or false,
    // otherwise.
    // -------------------------------------------------------------
    public boolean IsDirected() {
        for (int u = 0; u < adjacencyList.size(); u++) {
            for (Edge edge : adjacencyList.get(u)) {
                int v = edge.target;
                int weight = edge.weight;
                boolean reverseEdgeExists = false;

                for (Edge reverseEdge : adjacencyList.get(v)) {
                    if (reverseEdge.target == u && reverseEdge.weight == weight) {
                        reverseEdgeExists = true;
                        break;
                    }
                }
                if (!reverseEdgeExists) {
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 2) IsThereAPath(String v1, String v2)
    // prints the sequence of vertices (names of the vertices) and edgesweights of
    // the edges between the vertices while starting a BFS fromv1 until reaching v2.
    // -------------------------------------------------------------
    public boolean IsThereAPath(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return false;
        }
        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        boolean[] visited = new boolean[adjacencyList.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == goal) {
                return true;
            }
            for (Edge edge : adjacencyList.get(current)) {
                int neighbor = edge.target;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 3) BFSfromTo(String v1, String v2)
    // Print the sequence of vertices (names) + edges (weights)
    // while starting a BFS from v1 until reaching v2.
    // BFS children are visited in ascending order of edge weight.
    // -------------------------------------------------------------
    public void BFSfromTo(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            System.out.println("No such vertices.");
            return;
        }

        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        int n = adjacencyList.size();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];

        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }

        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == goal) {
                System.out.println("Path is: " + printPathWithWeightsBFS(parent, start, goal));
                return;
            }
            for (Edge edge : adjacencyList.get(current)) {
                int neighbor = edge.target;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;
                    queue.offer(neighbor);
                }
            }
        }
        System.out.println("Between " + v1 + " ve " + v2 + "contains no way.");
    }

    private String printPathWithWeightsBFS(int[] parent, int start, int target) {
        List<Integer> path = new ArrayList<>();
        int cur = target;
        while (cur != -1) {
            path.add(cur);
            cur = parent[cur];
        }

        List<Integer> reversedPath = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }

        // PATH maker yapabilirim
        String pathStr = "";
        for (int i = 0; i < reversedPath.size(); i++) {
            pathStr += indexToName.get(reversedPath.get(i));
            if (i < reversedPath.size() - 1) {
                int w = getWeight(reversedPath.get(i), reversedPath.get(i + 1));
                pathStr += " -" + w + "-> ";
            }
        }
        return pathStr;
    }

    private int getWeight(int from, int to) {
        for (Edge e : adjacencyList.get(from)) {
            if (e.target == to) {
                return e.weight;
            }
        }
        return -1; // Bulunamadıysa
    }

    // -------------------------------------------------------------
    // 4) DFSfromTo(String v1, String v2)
    // Print the sequence of vertices (names) + edges (weights)
    // while starting a DFS from v1 until reaching v2.
    // We'll do a simple recursive DFS.
    // -------------------------------------------------------------
    public void DFSfromTo(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            System.out.println("No such vertices.");
            return;
        }
        int start = nameToIndex.get(v1);
        int finish = nameToIndex.get(v2);

        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> path = new ArrayList<>();
        path.add(start);

        if (!dfsCode(start, finish, visited, path)) {
            System.out.println(v1 + " --x-- " + v2 + " (no DFS path found)");
        }
    }

    private boolean dfsCode(int current, int goal, boolean[] visited, List<Integer> path) {
        if (current == goal) {
            String pathStr = "";
            for (int i = 0; i < path.size(); i++) {
                pathStr += indexToName.get(path.get(i));
                if (i < path.size() - 1) {
                    int w = getWeight(path.get(i), path.get(i + 1));
                    pathStr += " -" + w + "-> ";
                }
            }
            System.out.println("DFS path: " + pathStr);
            return true;
        }
        visited[current] = true;
        for (Edge edge : adjacencyList.get(current)) {
            int neighbor = edge.target;
            if (!visited[neighbor]) {
                path.add(neighbor);
                if (dfsCode(neighbor, goal, visited, path)) {
                    return true;
                }
                path.remove(path.size() - 1);
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 5) WhatIsShortestPathLength(String v1, String v2)
    // Returns the MINIMUM sum-of-weights among all simple paths.
    // Implementation without “known” algorithms -> do naive DFS
    // of all paths, track min cost. Potentially expensive!
    // -------------------------------------------------------------
    public int WhatIsShortestPathLength(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            System.out.println(v1 + " --x-- " + v2);
            return -1;
        }
        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        boolean[] visited = new boolean[adjacencyList.size()];
        int[] distance = new int[adjacencyList.size()];

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        distance[start] = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == goal) {
                return distance[current];
            }

            for (Edge edge : adjacencyList.get(current)) {
                int neighbor = edge.weight;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[current] + 1;
                    queue.offer(neighbor);
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < distance.length; i++) {
            if (distance[i] != 0) {
                sum++;
            }
        }
        return sum - 1;
    }

    // -------------------------------------------------------------
    // 6) NumberOfSimplePaths(String v1, String v2)
    // Count how many distinct simple paths exist from v1 to v2
    // -------------------------------------------------------------
    public int NumberOfSimplePaths(String v1, String v2) {
        // If either vertex doesn't exist, there are no paths.
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return 0;
        }

        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        // Keep track of visited vertices to avoid revisiting them in the same path.
        boolean[] visited = new boolean[adjacencyList.size()];

        // pathCount is a class-level integer we reset each time.
        pathCount = 0;

        // Recursively explore all simple paths from 'start' to 'goal'.
        countAllPaths(start, goal, visited);

        return pathCount;
    }

    private void countAllPaths(int current, int goal, boolean[] visited) {
        // If we've reached the goal, we found a complete path. Increment and return.
        if (!visited[current]) {
            if (current == goal) {
                pathCount++;
                return;
            }
        }
        visited[current] = true;

        for (Edge e : adjacencyList.get(current)) {
            int neighbor = e.target;
            if (!visited[neighbor]) {
                countAllPaths(neighbor, goal, visited);
            }
        }
    }

    // -------------------------------------------------------------
    // 7) Neighbors(String v1)
    // Return the names of neighbor vertices of v1
    // -------------------------------------------------------------
    public List<String> Neighbors(String v1) {
        if (!nameToIndex.containsKey(v1)) {
            return new ArrayList<>();
        }
        int v = nameToIndex.get(v1);
        Set<String> uniqueNeighbors = new LinkedHashSet<>();

        for (Edge e : adjacencyList.get(v)) {
            uniqueNeighbors.add(indexToName.get(e.target));
        }
        return new ArrayList<>(uniqueNeighbors);
    }

    // -------------------------------------------------------------
    // 8) HighestDegree()
    // returns the name of the vertex with the highest degree. If there is more than
    // one, it returns the names of all.
    // -------------------------------------------------------------
    public List<String> HighestDegree() {
        int maxDeg = 0;
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (Neighbors(indexToName.get(i)).size() > maxDeg) {
                maxDeg = Neighbors(indexToName.get(i)).size();
            }
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (Neighbors(indexToName.get(i)).size() == maxDeg) {
                result.add(indexToName.get(i));
            }
        }
        return result;
    }

    // -------------------------------------------------------------
    // 9) AreTheyAdjacent(String v1, String v2)
    // Return true if v1 and v2 are directly connected by an edge.
    // -------------------------------------------------------------
    public boolean AreTheyAdjacent(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return false;
        }
        int a = nameToIndex.get(v1);
        int b = nameToIndex.get(v2);
        for (Edge e : adjacencyList.get(a)) {
            if (e.target == b) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 10) IsThereACycle(String v1)
    // returns true if there is a cycle path which starts and ends on v1, otherwise
    // false.
    // -------------------------------------------------------------
    public boolean IsThereACycle(String v1) {
        if (!nameToIndex.containsKey(v1)) {
            return false;
        }
        int start = nameToIndex.get(v1);
        boolean[] visited = new boolean[adjacencyList.size()];
        return hasCycleDFS(start, start, visited, 0);
    }

    private boolean hasCycleDFS(int current, int start, boolean[] visited, int depth) {
        // Eğer depth > 0 ve current tekrar start düğümüne eşitse, bir döngü bulunmuş
        // demektir
        if (depth > 0 && current == start) {
            return true;
        }
        visited[current] = true;

        for (Edge e : adjacencyList.get(current)) {
            // Eğer Edge sınıfınızda komşu düğüm 'e.target' olarak tutuluyorsa:
            int neighbor = e.target;
            // Eğer neighbor start'a eşitse (ve depth > 0) bir döngü bulunmuş demektir
            if (neighbor == start && depth > 0) {
                return true;
            }
            if (!visited[neighbor]) {
                if (hasCycleDFS(neighbor, start, visited, depth + 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 11) NumberOfVerticesInComponent(String v1)
    // Print the number of vertices in the connected component that contains v1.
    // -------------------------------------------------------------
    public int NumberOfVerticesInComponent(String v1) {
        int total = 0;
        if (!nameToIndex.containsKey(v1)) {
            return 0;
        }
        int start = nameToIndex.get(v1);
        boolean[] visited = new boolean[adjacencyList.size()];
        visited[start] = true;
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (Neighbors(indexToName.get(i)).contains(v1)) {
                total++;
            }
        }

        return total;
    }

}
