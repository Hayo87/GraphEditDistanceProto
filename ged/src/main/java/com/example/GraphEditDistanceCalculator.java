package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jgrapht.Graph;

public class GraphEditDistanceCalculator {
    private static final double INSERTION_COST = 10.0;
    private static final double DELETION_COST = 10.0;

    // Main method to compute the GED using A* search.
    public double computeEditDistance(Graph<String, TransitionEdge> referenceGraph,
        Graph<String, TransitionEdge> subjectGraph) {
        EditState initialState = initializeState(referenceGraph, subjectGraph);
        EditState goalState = aStarSearch(initialState, referenceGraph, subjectGraph);
        return goalState.getCumulativeCost();
    }

    // Main method to compute a detailed MatchResult.
    public GraphMatchResult computeDetailedEditDistance(Graph<String, TransitionEdge> referenceGraph,
                                                   Graph<String, TransitionEdge> subjectGraph) {
        EditState initialState = initializeState(referenceGraph, subjectGraph);
        EditState goalState = aStarSearch(initialState, referenceGraph, subjectGraph);
        // Here, we just collect the operations by traversing previous states.
        List<EditOperation> operations = new ArrayList<>();
        EditState current = goalState;
        while (current.getPreviousState() != null) {
            operations.add(current.getOperation());
            current = current.getPreviousState();
        }
        Collections.reverse(operations);
        return new GraphMatchResult(goalState.getCumulativeCost(), operations);
    }

    private EditState initializeState(Graph<String, TransitionEdge> referenceGraph,
                                  Graph<String, TransitionEdge> subjectGraph) {
    // Filter out edges that are COMMON from the reference graph.
    referenceGraph.edgeSet().forEach(edge ->
    System.out.println(edge + " type: " + edge.getType()));
    
    List<TransitionEdge> unmatchedRef = referenceGraph.edgeSet().stream()
            .filter(edge -> edge.getType() != DiffType.COMMON)
            .collect(Collectors.toList());            
    
    // Filter out edges that are COMMON from the subject graph.
    List<TransitionEdge> unmatchedSubj = subjectGraph.edgeSet().stream()
            .filter(edge -> edge.getType() != DiffType.COMMON)
            .collect(Collectors.toList());
         
    // Start with an empty mapping and zero cumulative cost.
    return new EditState(new HashMap<>(), 0.0, unmatchedRef, unmatchedSubj, null, null);
    }


    // A* search method that returns the goal state (complete EditState).
    private EditState aStarSearch(EditState initialState,
                              Graph<String, TransitionEdge> referenceGraph,
                              Graph<String, TransitionEdge> subjectGraph) {
        // Initialize the open list (priority queue) and closed set.
        // The comparator should sort states by (cumulativeCost + heuristicCost)
        PriorityQueue<EditState> openList = new PriorityQueue<>((s1, s2) ->
            Double.compare(s1.getCumulativeCost() + heuristicCost(s1),
                        s2.getCumulativeCost() + heuristicCost(s2))
        );
        Set<EditState> closedSet = new HashSet<>();
        
        openList.add(initialState);
        
        while (!openList.isEmpty()) {
            EditState currentState = openList.poll();
            
            // Check if currentState is the goal state.
            if (isGoalState(currentState)) {
                return currentState; // Found the optimal edit path.
            }
            
            closedSet.add(currentState);
            
            // Generate neighbor states using your neighbor generation function.
            List<EditState> neighbors = generateNeighbors(currentState, referenceGraph, subjectGraph);
            for (EditState neighbor : neighbors) {
                if (closedSet.contains(neighbor)) {
                    continue; // Skip if already processed.
                }
                openList.add(neighbor);
            }
        }
        
        throw new RuntimeException("No valid edit path found.");
    }


    // Generates the neighbor states from the given current state.
    private List<EditState> generateNeighbors(EditState currentState,
                                            Graph<String, TransitionEdge> referenceGraph,
                                            Graph<String, TransitionEdge> subjectGraph) {
        List<EditState> neighbors = new ArrayList<>();

        // Generate neighbor states using edge substitution.
        for (TransitionEdge refEdge : currentState.getUnmatchedReferenceEdges()) {
            for (TransitionEdge subjEdge : currentState.getUnmatchedSubjectEdges()) {
                // Calculate the substitution cost.
                double subCost = edgeSubstitutionCost(referenceGraph, subjectGraph, refEdge, subjEdge);
                
                // Create a new mapping by matching refEdge with subjEdge.
                Map<TransitionEdge, TransitionEdge> newMapping = new HashMap<>(currentState.getMapping());
                newMapping.put(refEdge, subjEdge);
                
                // Create new unmatched lists by removing the matched edges.
                List<TransitionEdge> newUnmatchedRef = new ArrayList<>(currentState.getUnmatchedReferenceEdges());
                newUnmatchedRef.remove(refEdge);
                List<TransitionEdge> newUnmatchedSubj = new ArrayList<>(currentState.getUnmatchedSubjectEdges());
                newUnmatchedSubj.remove(subjEdge);

                // Retrieve endpoints from the reference graph.
                String refSource = referenceGraph.getEdgeSource(refEdge);
                String refTarget = referenceGraph.getEdgeTarget(refEdge);
                
                double newCost = currentState.getCumulativeCost() + subCost;
                EditOperation op = new EditOperation(
                    EditOperation.OperationType.SUBSTITUTION,
                    "Substitute edge " + refEdge + " with " + subjEdge + " (" + refSource + " -> " + refTarget + ")",
                    subCost
                );    
                EditState neighbor = new EditState(newMapping, newCost, newUnmatchedRef, newUnmatchedSubj, currentState, op);
                neighbors.add(neighbor);
            }
        }

        // neighbor states using edge reassignment
        for (TransitionEdge refEdge : currentState.getUnmatchedReferenceEdges()) {
            for (TransitionEdge subjEdge : currentState.getUnmatchedSubjectEdges()) {
                // Calculate the reassignment cost.
                double reassignCost = edgeReassignmentCost(referenceGraph, subjectGraph, refEdge, subjEdge);
                
                // Create a new mapping by matching refEdge with subjEdge.
                Map<TransitionEdge, TransitionEdge> newMapping = new HashMap<>(currentState.getMapping());
                newMapping.put(refEdge, subjEdge);
                
                // Create new unmatched lists by removing the matched edges.
                List<TransitionEdge> newUnmatchedRef = new ArrayList<>(currentState.getUnmatchedReferenceEdges());
                newUnmatchedRef.remove(refEdge);
                List<TransitionEdge> newUnmatchedSubj = new ArrayList<>(currentState.getUnmatchedSubjectEdges());
                newUnmatchedSubj.remove(subjEdge);

                // Retrieve endpoints from the graphs.
                String refSource = referenceGraph.getEdgeSource(refEdge);
                String refTarget = referenceGraph.getEdgeTarget(refEdge);
                String subjSource = subjectGraph.getEdgeSource(subjEdge);
                String subjTarget = subjectGraph.getEdgeTarget(subjEdge);

                
                double newCost = currentState.getCumulativeCost() + reassignCost;
                EditOperation op = new EditOperation(
                    EditOperation.OperationType.SUBSTITUTION,
                    "Reassign edge " + refEdge + " from " + " (" + refSource + " -> " + refTarget + ")" + " to " + " (" + subjSource + " -> " + subjTarget + ")",
                    reassignCost
                );    
                EditState neighbor = new EditState(newMapping, newCost, newUnmatchedRef, newUnmatchedSubj, currentState, op);
                neighbors.add(neighbor);
            }
        }


        // Generate neighbor states by deleting an unmatched reference edge.
        // For deletion: remove one unmatched reference edge.
        for (TransitionEdge refEdge : currentState.getUnmatchedReferenceEdges()) {
            Map<TransitionEdge, TransitionEdge> newMapping = new HashMap<>(currentState.getMapping());
            List<TransitionEdge> newUnmatchedRef = new ArrayList<>(currentState.getUnmatchedReferenceEdges());
            newUnmatchedRef.remove(refEdge);
            List<TransitionEdge> unmatchedSubj = new ArrayList<>(currentState.getUnmatchedSubjectEdges());

            // Retrieve endpoints from the reference graph.
            String refSource = referenceGraph.getEdgeSource(refEdge);
            String refTarget = referenceGraph.getEdgeTarget(refEdge);

            double newCost = currentState.getCumulativeCost() + DELETION_COST;
            // Include endpoints in the description.
            EditOperation op = new EditOperation(
                EditOperation.OperationType.DELETION,
                "Delete reference edge " + refEdge + " (" + refSource + " -> " + refTarget + ")",
                DELETION_COST
            );
            EditState neighbor = new EditState(newMapping, newCost, newUnmatchedRef, unmatchedSubj, currentState, op);
            neighbors.add(neighbor);
        }

        // For insertion: remove one unmatched subject edge.
        for (TransitionEdge subjEdge : currentState.getUnmatchedSubjectEdges()) {
            Map<TransitionEdge, TransitionEdge> newMapping = new HashMap<>(currentState.getMapping());
            List<TransitionEdge> newUnmatchedSubj = new ArrayList<>(currentState.getUnmatchedSubjectEdges());
            newUnmatchedSubj.remove(subjEdge);
            List<TransitionEdge> unmatchedRef = new ArrayList<>(currentState.getUnmatchedReferenceEdges());

            // Retrieve endpoints from the subject graph.
            String subjSource = subjectGraph.getEdgeSource(subjEdge);
            String subjTarget = subjectGraph.getEdgeTarget(subjEdge);

            double newCost = currentState.getCumulativeCost() + INSERTION_COST;
            // Include endpoints in the description.
            EditOperation op = new EditOperation(
                EditOperation.OperationType.INSERTION,
                "Insert subject edge " + subjEdge + " (" + subjSource + " -> " + subjTarget + ")",
                INSERTION_COST
            );
            EditState neighbor = new EditState(newMapping, newCost, unmatchedRef, newUnmatchedSubj, currentState, op);
            neighbors.add(neighbor);
        }
        
        return neighbors;
    }

                                            
    // Heuristic function to estimate the remaining cost from a given state.
    private double heuristicCost(EditState state) {
        int unmatchedCount = state.getUnmatchedReferenceEdges().size() + state.getUnmatchedSubjectEdges().size();
        return unmatchedCount * 8.0;  // Assuming 8.0 is the minimal edge operation cost.
    }
    
    // Goal test: checks if the given state represents a complete mapping.
    private boolean isGoalState(EditState state) {
        return state.getUnmatchedReferenceEdges().isEmpty() && state.getUnmatchedSubjectEdges().isEmpty();
    }
    

    // COST FUNCTIONS

    public double edgeSubstitutionCost(
        Graph<String, TransitionEdge> referenceGraph,
        Graph<String, TransitionEdge> subjectGraph,
        TransitionEdge refEdge,
        TransitionEdge subjEdge) {
        
        // Retrieve endpoints from the reference graph.
        String refSource = referenceGraph.getEdgeSource(refEdge);
        String refTarget = referenceGraph.getEdgeTarget(refEdge);
        
        // Retrieve endpoints from the subject graph.
        String subjSource = subjectGraph.getEdgeSource(subjEdge);
        String subjTarget = subjectGraph.getEdgeTarget(subjEdge);
        
        // Compare endpoints: if they don't match, return a high cost.
        if (!refSource.equals(subjSource) || !refTarget.equals(subjTarget)) {
            return 250.0;
        }
        
        // If endpoints match, compute cost based on label differences.
        double inputCost = computeStringCost(refEdge.getInput(), subjEdge.getInput());
        double outputCost = computeStringCost(refEdge.getOutput(), subjEdge.getOutput());

        if (inputCost > 0 && outputCost>0) {return 1000;}
        
        return inputCost + outputCost;
    }
    
    public double edgeReassignmentCost(
        Graph<String, TransitionEdge> referenceGraph,
        Graph<String, TransitionEdge> subjectGraph,
        TransitionEdge refEdge,
        TransitionEdge subjEdge) {
    
    // First, verify that the labels are the same.
        if (!refEdge.getInput().equals(subjEdge.getInput()) ||
            !refEdge.getOutput().equals(subjEdge.getOutput())) {
            return 250;
        }
        
        // Retrieve endpoints for the reference edge.
        String refSource = referenceGraph.getEdgeSource(refEdge);
        String refTarget = referenceGraph.getEdgeTarget(refEdge);
        
        // Retrieve endpoints for the subject edge.
        String subjSource = subjectGraph.getEdgeSource(subjEdge);
        String subjTarget = subjectGraph.getEdgeTarget(subjEdge);
        
        // If endpoints are different, assign a reassignment cost.
        if (!refSource.equals(subjSource) || !refTarget.equals(subjTarget)) {
            return 2.0;
        }
        // If endpoints are the same (and labels are the same), no reassignment is needed.
        return 0.0;
        }

        public double computeStringCost(String s1, String s2) {
            LevenshteinDistance ld = new LevenshteinDistance();
        return ld.apply(s1, s2);
        }
}


