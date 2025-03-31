package com.example;

import org.jgrapht.graph.DefaultEdge;

public class TransitionEdge extends DefaultEdge {


    private final String input;
    private final String output;
    private final DiffType type;

    public TransitionEdge(String input, String output, DiffType type) {
        this.type = type;
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
    public DiffType getType(){
        return type;
    }

    @Override
    public String toString() {
        return input + " / " + output;
    }
}
