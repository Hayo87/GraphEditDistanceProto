package com.example;

public class EditOperation {
    
    private final OperationType operationType;
    private final String description;
    private final double cost;
    
    public EditOperation(OperationType operationType, String description, double cost) {
        this.operationType = operationType;
        this.description = description;
        this.cost = cost;
    }
    
    public OperationType getOperationType() {
        return operationType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getCost() {
        return cost;
    }
    
    @Override
    public String toString() {
        return "EditOperation{" +
                "operationType=" + operationType +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                '}';
    }
    
    // Enum to represent different types of edit operations.
    public enum OperationType {
        INSERTION,
        DELETION,
        SUBSTITUTION,
        REASSIGNMENT
    }
}

