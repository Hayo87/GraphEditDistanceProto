package com.example;

import java.util.List;
import java.util.Map;

public class EditState {

    // Mapping from a reference edge to its matched subject edge.
    // If a reference edge is not mapped, it won't be in this map.
    private final Map<TransitionEdge, TransitionEdge> mapping;
    
    // Cumulative cost (g-value) from the initial state to this state.
    private final double cumulativeCost;
    
    // Unmatched edges in the reference graph.
    private final List<TransitionEdge> unmatchedReferenceEdges;
    
    // Unmatched edges in the subject graph.
    private final List<TransitionEdge> unmatchedSubjectEdges;
    
    // Optional: pointer to the previous state (for reconstructing the edit path).
    private final EditState previousState;
    
    // Optional: operation performed to get from previousState to this state.
    private final EditOperation operation;
    
    // Constructor for the initial state (no previous state, no operation)
    public EditState(Map<TransitionEdge, TransitionEdge> mapping,
                     double cumulativeCost,
                     List<TransitionEdge> unmatchedReferenceEdges,
                     List<TransitionEdge> unmatchedSubjectEdges) {
        this(mapping, cumulativeCost, unmatchedReferenceEdges, unmatchedSubjectEdges, null, null);
    }
    
    // Full constructor
    public EditState(Map<TransitionEdge, TransitionEdge> mapping,
                     double cumulativeCost,
                     List<TransitionEdge> unmatchedReferenceEdges,
                     List<TransitionEdge> unmatchedSubjectEdges,
                     EditState previousState,
                     EditOperation operation) {
        this.mapping = mapping;
        this.cumulativeCost = cumulativeCost;
        this.unmatchedReferenceEdges = unmatchedReferenceEdges;
        this.unmatchedSubjectEdges = unmatchedSubjectEdges;
        this.previousState = previousState;
        this.operation = operation;
    }
    
    // Getters
    public Map<TransitionEdge, TransitionEdge> getMapping() {
        return mapping;
    }

    public double getCumulativeCost() {
        return cumulativeCost;
    }

    public List<TransitionEdge> getUnmatchedReferenceEdges() {
        return unmatchedReferenceEdges;
    }

    public List<TransitionEdge> getUnmatchedSubjectEdges() {
        return unmatchedSubjectEdges;
    }

    public EditState getPreviousState() {
        return previousState;
    }

    public EditOperation getOperation() {
        return operation;
    }
    
    /**
     * Determines if this state is a goal state.
     * For example, the goal state could be defined as when there are no unmatched edges left.
     */
    public boolean isComplete() {
        return unmatchedReferenceEdges.isEmpty() && unmatchedSubjectEdges.isEmpty();
    }
}
