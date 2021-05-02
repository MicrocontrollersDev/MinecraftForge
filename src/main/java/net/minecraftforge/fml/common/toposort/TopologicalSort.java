/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common.toposort;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import net.minecraftforge.fml.common.FMLLog;

import java.util.*;

/**
 * Topological sort for mod loading
 * <p>
 * Based on a variety of sources, including http://keithschwarz.com/interesting/code/?dir=topological-sort
 *
 * @author cpw
 */
public class TopologicalSort {
    public static class DirectedGraph<T> implements Iterable<T> {
        private final Map<T, SortedSet<T>> graph = new HashMap<>();
        private final List<T> orderedNodes = new ArrayList<>();

        public boolean addNode(T node) {
            // Ignore nodes already added
            if (graph.containsKey(node)) {
                return false;
            }

            orderedNodes.add(node);
            graph.put(node, new TreeSet<>(Comparator.comparingInt(orderedNodes::indexOf)));
            return true;
        }

        public void addEdge(T from, T to) {
            if (!(graph.containsKey(from) && graph.containsKey(to))) {
                throw new NoSuchElementException("Missing nodes from graph");
            }

            graph.get(from).add(to);
        }

        public void removeEdge(T from, T to) {
            if (!(graph.containsKey(from) && graph.containsKey(to))) {
                throw new NoSuchElementException("Missing nodes from graph");
            }

            graph.get(from).remove(to);
        }

        public boolean edgeExists(T from, T to) {
            if (!(graph.containsKey(from) && graph.containsKey(to))) {
                throw new NoSuchElementException("Missing nodes from graph");
            }

            return graph.get(from).contains(to);
        }

        public Set<T> edgesFrom(T from) {
            if (!graph.containsKey(from)) {
                throw new NoSuchElementException("Missing node from graph");
            }

            return Collections.unmodifiableSortedSet(graph.get(from));
        }

        @Override
        public Iterator<T> iterator() {
            return orderedNodes.iterator();
        }

        public int size() {
            return graph.size();
        }

        public boolean isEmpty() {
            return graph.isEmpty();
        }

        @Override
        public String toString() {
            return graph.toString();
        }
    }

    /**
     * Sort the input graph into a topologically sorted list
     * <p>
     * Uses the reverse depth first search as outlined in ...
     *
     * @param graph
     * @return The sorted mods list.
     */
    public static <T> List<T> topologicalSort(DirectedGraph<T> graph) {
        DirectedGraph<T> rGraph = reverse(graph);
        List<T> sortedResult = new ArrayList<>();
        Set<T> visitedNodes = new HashSet<>();
        // A list of "fully explored" nodes. Leftovers in here indicate cycles in the graph
        Set<T> expandedNodes = new HashSet<>();

        for (T node : rGraph) {
            explore(node, rGraph, sortedResult, visitedNodes, expandedNodes);
        }

        return sortedResult;
    }

    public static <T> DirectedGraph<T> reverse(DirectedGraph<T> graph) {
        DirectedGraph<T> result = new DirectedGraph<>();

        for (T node : graph) {
            result.addNode(node);
        }

        for (T from : graph) {
            for (T to : graph.edgesFrom(from)) {
                result.addEdge(to, from);
            }
        }

        return result;
    }

    public static <T> void explore(T node, DirectedGraph<T> graph, List<T> sortedResult, Set<T> visitedNodes, Set<T> expandedNodes) {
        // Have we been here before?
        if (visitedNodes.contains(node)) {
            // And have completed this node before
            if (expandedNodes.contains(node)) {
                // Then we're fine
                return;
            }

            FMLLog.severe("Mod Sorting failed.");
            FMLLog.severe("Visiting node %s", node);
            FMLLog.severe("Current sorted list : %s", sortedResult);
            FMLLog.severe("Visited set for this node : %s", visitedNodes);
            FMLLog.severe("Explored node set : %s", expandedNodes);
            SetView<T> cycleList = Sets.difference(visitedNodes, expandedNodes);
            FMLLog.severe("Likely cycle is in : %s", cycleList);
            throw new ModSortingException("There was a cycle detected in the input graph, sorting is not possible", node, cycleList);
        }

        // Visit this node
        visitedNodes.add(node);

        // Recursively explore inbound edges
        for (T inbound : graph.edgesFrom(node)) {
            explore(inbound, graph, sortedResult, visitedNodes, expandedNodes);
        }

        // Add ourselves now
        sortedResult.add(node);
        // And mark ourselves as explored
        expandedNodes.add(node);
    }
}
