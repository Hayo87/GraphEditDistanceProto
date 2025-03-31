package com.example;

public class StateNode {

    private final String label;
    private final DiffType type;
    
    public StateNode(String label, DiffType type) {
        this.label = label;
        this.type = type;
    }
    
    public String getLabel() {
        return label;
    }
    
    public DiffType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return label + " (" + type + ")";
    }
    
        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateNode)) return false;
            StateNode other = (StateNode) o;
            return label.equals(other.label) && type == other.type;
        }
    
    @Override
    public int hashCode() {
        return label.hashCode() * 31 + type.hashCode();
        }
}

