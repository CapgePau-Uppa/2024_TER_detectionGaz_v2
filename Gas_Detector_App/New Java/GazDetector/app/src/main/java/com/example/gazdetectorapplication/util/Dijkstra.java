package com.example.gazdetectorapplication.util;

import java.util.LinkedList;

public class Dijkstra {
    public LinkedList<Integer> globalPath;

    //trouver l'indice du nœud avec la distance minimale parmi les nœuds non visités
    public int minDistance(int dist[], Boolean b[]) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int x = 0; x < dist.length; x++) {
            if (!b[x] && dist[x] <= min) {
                min = dist[x];
                index = x;
            }
        }
        return index;
    }
    //affiche le chemin le plus court en remontant à partir du nœud destination jusqu'au nœud source
    public LinkedList<Integer> printShortestPath(int previous[], int destination) {
        LinkedList<Integer> path = new LinkedList<>();
        int current = destination;
        while (current != -1) {
            path.addFirst(current);
            current = previous[current];
        }
        return path;

    }
    // met à jour les distances et les nœuds précédents en appliquant l'algorithme de Dijkstra
    public LinkedList<Integer> dijkstra(int[][] graph, int src, int dest) {
        int[] dist = new int[graph.length];
        int[] previous = new int[graph.length];
        Boolean[] b = new Boolean[graph.length];

        for (int i = 0; i < graph.length; i++) {
            dist[i] = Integer.MAX_VALUE;
            b[i] = false;
            previous[i] = -1;
        }

        dist[src] = 0;
        for (int count = 0; count < graph.length; count++) {
            int u = minDistance(dist, b);
            b[u] = true;
            for (int x = 0; x < graph.length; x++) {
                if (!b[x] && graph[u][x] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][x] < dist[x]) {
                    dist[x] = dist[u] + graph[u][x];
                    previous[x] = u;
                }
            }
        }

        return printShortestPath(previous, dest);
    }

}