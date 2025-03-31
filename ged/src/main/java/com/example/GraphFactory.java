package com.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class GraphFactory {

    /**
     * Creates a simple reference graph.
     * This graph might represent the reference automaton with common (black) or reference-only (green) transitions.
     */
    public static Graph<String, TransitionEdge> createReferenceGraph() {
        Graph<String, TransitionEdge> refGraph = GraphTypeBuilder
            .<String, TransitionEdge>directed()
            .allowingMultipleEdges(true)
            .allowingSelfLoops(true)
            .edgeClass(TransitionEdge.class)
            .buildGraph();

        
        // Add vertices (states)
        refGraph.addVertex("S1");
        refGraph.addVertex("S2");
        refGraph.addVertex("S3");
        
        // Add an edge with a sample transition label (input / output)

        refGraph.addEdge("S1", "S1", new TransitionEdge("ROBOT_OK", "TIMEOUT",DiffType.COMMON));
        refGraph.addEdge("S1", "S1", new TransitionEdge("DISPLAY_TEXT", "LONG_ERROR", DiffType.COMMON));
        refGraph.addEdge("S1", "S1", new TransitionEdge("SIGNDATA", "LONG_ERROR", DiffType.COMMON));
        refGraph.addEdge("S1", "S2", new TransitionEdge("COMBINED_PIN", "OK", DiffType.COMMON));

        refGraph.addEdge("S2", "S2", new TransitionEdge("COMBINED_PIN", "SIGNDATA", DiffType.COMMON));
        refGraph.addEdge("S2", "S2", new TransitionEdge("ROBOT_OK", "TIMEOUT", DiffType.COMMON));
        refGraph.addEdge("S2", "S3", new TransitionEdge("SIGNDATA", "OK", DiffType.COMMON));
        refGraph.addEdge("S2", "S3", new TransitionEdge("DISPLAY_TEXT", "TIMEOUT", DiffType.COMMON));

        refGraph.addEdge("S3", "S2", new TransitionEdge("ROBOT_OK", "OK", DiffType.COMMON));
        refGraph.addEdge("S3", "S2", new TransitionEdge("COMBINED_PIN", "OK", DiffType.COMMON));

        refGraph.addEdge("S1", "S1", new TransitionEdge("GEN_CRYPTOGRAM", "LONG_ERROR", DiffType.REFERENCE_ONLY));
        refGraph.addEdge("S2", "S1", new TransitionEdge("GEN_CRYPTOGRAM", "CRYPTOGRAM", DiffType.REFERENCE_ONLY));

        refGraph.addEdge("S3", "S3", new TransitionEdge("GEN_CRYPTOGRAM", "LONG_ERROR", DiffType.REFERENCE_ONLY));
        refGraph.addEdge("S3", "S3", new TransitionEdge("DISPLAY_TEXT", "LONG_ERROR", DiffType.REFERENCE_ONLY));
        refGraph.addEdge("S3", "S3", new TransitionEdge("SIGNDATA", "LONG_ERROR", DiffType.REFERENCE_ONLY));

        return refGraph;
    }

    /**
     * Creates a simple subject graph.
     * This graph might represent the subject automaton with common (black) or subject-only (red) transitions.
     */
    public static Graph<String, TransitionEdge> createSubjectGraph() {
        Graph<String, TransitionEdge> subjGraph = GraphTypeBuilder
        .<String, TransitionEdge>directed()
        .allowingMultipleEdges(true)
        .allowingSelfLoops(true)
        .edgeClass(TransitionEdge.class)
        .buildGraph();
        
        // Add vertices (states)
        subjGraph.addVertex("S1");
        subjGraph.addVertex("S2");
        subjGraph.addVertex("S3");
        subjGraph.addVertex("S4");
        
        // Add an edge where the transition might differ slightly (e.g., different output)
        subjGraph.addEdge("S1", "S1", new TransitionEdge("ROBOT_OK", "TIMEOUT",DiffType.COMMON));
        subjGraph.addEdge("S1", "S1", new TransitionEdge("DISPLAY_TEXT", "LONG_ERROR", DiffType.COMMON));
        subjGraph.addEdge("S1", "S1", new TransitionEdge("SIGNDATA", "LONG_ERROR", DiffType.COMMON));
        subjGraph.addEdge("S1", "S2", new TransitionEdge("COMBINED_PIN", "OK", DiffType.COMMON));

        subjGraph.addEdge("S2", "S2", new TransitionEdge("COMBINED_PIN", "SIGNDATA", DiffType.COMMON));
        subjGraph.addEdge("S2", "S2", new TransitionEdge("ROBOT_OK", "TIMEOUT", DiffType.COMMON));
        subjGraph.addEdge("S2", "S3", new TransitionEdge("SIGNDATA", "OK", DiffType.COMMON));
        subjGraph.addEdge("S2", "S3", new TransitionEdge("DISPLAY_TEXT", "TIMEOUT", DiffType.COMMON));

        subjGraph.addEdge("S3", "S2", new TransitionEdge("ROBOT_OK", "OK", DiffType.COMMON));
        subjGraph.addEdge("S3", "S2", new TransitionEdge("COMBINED_PIN", "OK", DiffType.COMMON));

        subjGraph.addEdge("S1", "S1", new TransitionEdge("USB8_CRYPTOGRAM", "LONG_ERROR", DiffType.SUBJECT_ONLY));
        subjGraph.addEdge("S2", "S1", new TransitionEdge("USB8_CRYPTOGRAM", "CRYPTOGRAM", DiffType.SUBJECT_ONLY));

        subjGraph.addEdge("S3", "S3", new TransitionEdge("DISPLAY_TEXT", "TIMEOUT", DiffType.SUBJECT_ONLY));
        subjGraph.addEdge("S3", "S3", new TransitionEdge("SIGNDATA", "OK", DiffType.SUBJECT_ONLY));

        subjGraph.addEdge("S4", "S4", new TransitionEdge("USB8_CRYPTOGRAM", "LONG_ERROR", DiffType.SUBJECT_ONLY));
        subjGraph.addEdge("S4", "S4", new TransitionEdge("DISPLAY_TEXT", "LONG_ERROR", DiffType.SUBJECT_ONLY));        
        subjGraph.addEdge("S4", "S4", new TransitionEdge("SIGNDATA", "LONG_ERROR", DiffType.SUBJECT_ONLY));

        subjGraph.addEdge("S4", "S1", new TransitionEdge("ROBOT_OK", "OK", DiffType.SUBJECT_ONLY));
        subjGraph.addEdge("S4", "S2", new TransitionEdge("COMBINED_PIN", "OK", DiffType.SUBJECT_ONLY));
        subjGraph.addEdge("S3", "S4", new TransitionEdge("USB8_CRYPTOGRAM", "CRYPTOGRAM", DiffType.SUBJECT_ONLY));
 
        return subjGraph;
    }
}

