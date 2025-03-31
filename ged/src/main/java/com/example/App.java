package com.example;

import org.jgrapht.Graph;

public class App {
    public static void main(String[] args) {
        // Create the reference and subject graphs using your GraphFactory.
        Graph<String, TransitionEdge> referenceGraph = GraphFactory.createReferenceGraph();
        Graph<String, TransitionEdge> subjectGraph = GraphFactory.createSubjectGraph();

        // Instantiate the GraphEditDistanceCalculator.
        GraphEditDistanceCalculator gedCalculator = new GraphEditDistanceCalculator();

        // Compute the overall edit distance.
        double distance = gedCalculator.computeEditDistance(referenceGraph, subjectGraph);
        
        // Compute the detailed match result (which includes the edit operations).
        GraphMatchResult result = gedCalculator.computeDetailedEditDistance(referenceGraph, subjectGraph);
        
        // Print the results to the terminal.
        System.out.println("Computed Edit Distance: " + distance);
        System.out.println("Detailed Match Operations:");
        for (EditOperation op : result.getOperations()) {
            System.out.println(op);
        }
    }
}

