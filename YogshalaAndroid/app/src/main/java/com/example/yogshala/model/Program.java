package com.example.yogshala.model;



public class Program {
    private String id;
    private String name;

    // Default constructor required for calls to DataSnapshot.getValue(Program.class)
    public Program() {
    }

    public Program(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

