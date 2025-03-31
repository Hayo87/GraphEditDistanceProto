package com.example;

import java.util.List;


public class GraphMatchResult {
    private final double totalCost;
    private final List<EditOperation> operations;

    public GraphMatchResult(double totalCost, List<EditOperation> operations) {
        this.totalCost = totalCost;
        this.operations = operations;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<EditOperation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "GraphMatchResult{" +
               "totalCost=" + totalCost +
               ", operations=" + operations +
               '}';
    }
}

