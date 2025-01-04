import java.io.*;
import java.util.*;

public class Graph {

    // We will map each city name (String) to an integer index.
    private Map<String, Integer> nameToIndex = new HashMap<>();
    // We also keep the reverse mapping if we want to recover the name by index.
    private List<String> indexToName = new ArrayList<>();
    // adjacencyList[v] will hold a list of edges going out from vertex v
    private List<List<Edge>> adjacencyList = new ArrayList<>();

    private boolean isDirected;

    private static class Edge {
        int from;
        int to;

        public Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Ensures that a city name has a corresponding unique index.
     */
    private int ensureVertex(String cityName) {
        if (!nameToIndex.containsKey(cityName)) {
            nameToIndex.put(cityName, indexToName.size());
            indexToName.add(cityName);
            adjacencyList.add(new ArrayList<>());
        }
        return nameToIndex.get(cityName);
    }

    /**
     * Add edge from city1 to city2 with the given weight.
     * If the graph is undirected, also add edge city2 -> city1.
     */
    private void addEdge(String city1, String city2, int weight) {
        int v1 = ensureVertex(city1);
        int v2 = ensureVertex(city2);
        adjacencyList.get(v1).add(new Edge(v2, weight));
        if (!isDirected) {
            adjacencyList.get(v2).add(new Edge(v1, weight));
        }
    }

    // -------------------------------------------------------------
    // 1) ReadGraphFromFile()
    // Example line format:
    // A -> B:3, C:2, D:1, E:4
    // The first token ("A") is the source city,
    // After "->", edges are listed with "C:2" meaning (C, weight=2), etc.
    // -------------------------------------------------------------
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

            // After reading the file, we may want to sort adjacency lists by weight
            for (List<Edge> edges : adjacencyList) {
                edges.sort(Comparator.comparingInt(e -> e.to));
            }

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // 2) IsThereAPath(String v1, String v2)
    // Returns true if there is at least one path from v1 to v2, false otherwise.
    // -------------------------------------------------------------
    public boolean IsThereAPath(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return false;
        }
        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        // Use a simple queue-based BFS (unweighted style) or DFS
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
                int neighbor = edge.from;
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

        // We'll keep track of parent to reconstruct path
        int n = adjacencyList.size();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.offer(start);

        // Standard BFS, but our adjacency lists are already sorted by weight
        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == goal) {
                // Reconstruct path with weights
                printPathWithWeightsBFS(parent, start, goal);
                return;
            }
            for (Edge edge : adjacencyList.get(current)) {
                int neighbor = edge.from;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;
                    queue.offer(neighbor);
                }
            }
        }

        System.out.println(v1 + " --x-- " + v2 + " (no BFS path found)");
    }

    // Helper to reconstruct the BFS path and print edges
    private void printPathWithWeightsBFS(int[] parent, int start, int goal) {
        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        int cur = goal;
        while (cur != -1) {
            path.add(cur);
            cur = parent[cur];
        }
        Collections.reverse(path);

        // Print the names and weights
        // We have the actual vertices, so we must look up adjacency to find weights
        // between consecutive vertices in 'path'
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(indexToName.get(path.get(i)));
            if (i < path.size() - 1) {
                // find the weight from path[i] to path[i+1]
                int w = getWeight(path.get(i), path.get(i + 1));
                sb.append(" -").append(w).append("-> ");
            }
        }
        System.out.println("BFS path: " + sb.toString());
    }

    // Quick helper to get the weight between two neighbors
    private int getWeight(int from, int to) {
        for (Edge e : adjacencyList.get(from)) {
            if (e.from == to) {
                return e.to;
            }
        }
        return -1; // not found
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

        if (!dfsHelper(start, finish, visited, path)) {
            System.out.println(v1 + " --x-- " + v2 + " (no DFS path found)");
        }
    }

    private boolean dfsHelper(int current, int goal, boolean[] visited, List<Integer> path) {
        if (current == goal) {
            // Print path
            printDFSPath(path);
            return true;
        }
        visited[current] = true;
        // If you want edges visited in ascending weight, sort adjacency by weight
        // (already sorted in the constructor)
        for (Edge edge : adjacencyList.get(current)) {
            int neighbor = edge.from;
            if (!visited[neighbor]) {
                path.add(neighbor);
                if (dfsHelper(neighbor, goal, visited, path)) {
                    return true;
                }
                path.remove(path.size() - 1);
            }
        }
        return false;
    }

    private void printDFSPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(indexToName.get(path.get(i)));
            if (i < path.size() - 1) {
                int w = getWeight(path.get(i), path.get(i + 1));
                sb.append(" -").append(w).append("-> ");
            }
        }
        System.out.println("DFS path: " + sb.toString());
    }

    // -------------------------------------------------------------
    // 5) WhatIsShortestPathLength(String v1, String v2)
    // Returns the MINIMUM sum-of-weights among all simple paths.
    // Implementation without “known” algorithms -> do naive DFS
    // of all paths, track min cost. Potentially expensive!
    // -------------------------------------------------------------
    public int WhatIsShortestPathLength(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return -1;
        }
        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        boolean[] visited = new boolean[adjacencyList.size()];
        visited[start] = false;

        // We'll track a global minimum
        this.minCost = Integer.MAX_VALUE;
        dfsAllPaths(start, goal, 0, visited);
        return (minCost == Integer.MAX_VALUE) ? -1 : minCost;
    }

    private int minCost;

    /**
     * Backtracking DFS to explore all simple paths from current to goal.
     * costSoFar accumulates the sum of weights along the path.
     */
    private void dfsAllPaths(int current, int goal, int costSoFar, boolean[] visited) {
        if (current == goal) {
            // update global min cost
            minCost = Math.min(minCost, costSoFar);
            return;
        }
        visited[current] = true;

        for (Edge e : adjacencyList.get(current)) {
            if (!visited[e.from]) {
                dfsAllPaths(e.from, goal, costSoFar + e.to, visited);
            }
        }
        visited[current] = false;
    }

    // -------------------------------------------------------------
    // 6) NumberOfSimplePaths(String v1, String v2)
    // Count how many distinct simple paths exist from v1 to v2
    // -------------------------------------------------------------
    public int NumberOfSimplePaths(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return 0;
        }
        int start = nameToIndex.get(v1);
        int goal = nameToIndex.get(v2);

        boolean[] visited = new boolean[adjacencyList.size()];
        visited[start] = false;

        pathCount = 0;
        countAllPaths(start, goal, visited);
        return pathCount;
    }

    private int pathCount;

    private void countAllPaths(int current, int goal, boolean[] visited) {
        if (current == goal) {
            pathCount++;
            return;
        }
        visited[current] = true;
        for (Edge e : adjacencyList.get(current)) {
            if (!visited[e.from]) {
                countAllPaths(e.from, goal, visited);
            }
        }
        visited[current] = false;
    }

    // -------------------------------------------------------------
    // 7) Neighbors(String v1)
    // Return the names of neighbor vertices of v1
    // -------------------------------------------------------------
    public List<String> Neighbors(String v1) {
        List<String> result = new ArrayList<>();
        if (!nameToIndex.containsKey(v1)) {
            return result;
        }
        int v = nameToIndex.get(v1);
        for (Edge e : adjacencyList.get(v)) {
            result.add(indexToName.get(e.from));
        }
        return result;
    }

    // -------------------------------------------------------------
    // 8) HighestDegree()
    // Return the name(s) of vertex(ices) with the highest degree.
    // For directed graphs, decide if you want out-degree only or total in+out.
    // Here we’ll just do out-degree for demonstration.
    // -------------------------------------------------------------
    public List<String> HighestDegree() {
        int maxDeg = 0;
        // compute out-degree
        int[] degrees = new int[adjacencyList.size()];
        for (int i = 0; i < adjacencyList.size(); i++) {
            degrees[i] = adjacencyList.get(i).size();
            maxDeg = Math.max(maxDeg, degrees[i]);
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < degrees.length; i++) {
            if (degrees[i] == maxDeg) {
                result.add(indexToName.get(i));
            }
        }
        return result;
    }

    // -------------------------------------------------------------
    // 9) IsDirected()
    // -------------------------------------------------------------
    public boolean IsDirected() {
        for (int u = 0; u < adjacencyList.size(); u++) {
            for (Edge edge : adjacencyList.get(u)) {
                int v = edge.from;
                int weight = edge.to;

                // Flag to check if the reverse edge exists
                boolean reverseEdgeExists = false;

                // Iterate through the adjacency list of vertex v to find the reverse edge
                for (Edge reverseEdge : adjacencyList.get(v)) {
                    if (reverseEdge.from == u && reverseEdge.to == weight) {
                        reverseEdgeExists = true;
                        break;
                    }
                }

                // If the reverse edge does not exist, the graph is directed
                if (!reverseEdgeExists) {
                    return true;
                }
            }
        }

        // All edges have their corresponding reverse edges; the graph is undirected
        return false;
    }

    // -------------------------------------------------------------
    // 10) AreTheyAdjacent(String v1, String v2)
    // Return true if v1 and v2 are directly connected by an edge.
    // -------------------------------------------------------------
    public boolean AreTheyAdjacent(String v1, String v2) {
        if (!nameToIndex.containsKey(v1) || !nameToIndex.containsKey(v2)) {
            return false;
        }
        int a = nameToIndex.get(v1);
        int b = nameToIndex.get(v2);
        for (Edge e : adjacencyList.get(a)) {
            if (e.from == b) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // 11) IsThereACycle(String v1)
    // Returns true if there is a cycle path that starts AND ends on v1,
    // i.e., we can leave v1, follow edges, and eventually come back to v1
    // without repeating any vertex in between.
    // -------------------------------------------------------------
    public boolean IsThereACycle(String v1) {
        if (!nameToIndex.containsKey(v1)) {
            return false;
        }
        int start = nameToIndex.get(v1);
        boolean[] visited = new boolean[adjacencyList.size()];
        return hasCycleDFS(start, start, visited, 0);
    }

    /**
     * DFS to detect a cycle that returns to 'start'. We keep track of
     * how many vertices we’ve visited so far. If we come back to 'start'
     * after at least 1 edge, that’s a cycle.
     */
    private boolean hasCycleDFS(int current, int start, boolean[] visited, int depth) {
        if (depth > 0 && current == start) {
            return true; // cycle found
        }
        visited[current] = true;
        for (Edge e : adjacencyList.get(current)) {
            int neighbor = e.from;
            // We can allow returning to 'start' even if visited[start] is true,
            // but must not return to any other visited vertex
            if (neighbor == start && depth > 0) {
                return true;
            }
            if (!visited[neighbor]) {
                if (hasCycleDFS(neighbor, start, visited, depth + 1)) {
                    return true;
                }
            }
        }
        visited[current] = false;
        return false;
    }

    // -------------------------------------------------------------
    // 12) NumberOfVerticesInComponent(String v1)
    // Print the number of vertices in the connected component that contains v1.
    // -------------------------------------------------------------
    public int NumberOfVerticesInComponent(String v1) {
        if (!nameToIndex.containsKey(v1)) {
            return 0;
        }
        int start = nameToIndex.get(v1);

        boolean[] visited = new boolean[adjacencyList.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start] = true;
        int count = 1;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Edge e : adjacencyList.get(current)) {
                if (!visited[e.from]) {
                    visited[e.from] = true;
                    queue.add(e.from);
                    count++;
                }
            }
        }
        return count;
    }
}
