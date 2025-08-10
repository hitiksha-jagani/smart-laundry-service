package com.SmartLaundry.model;

public enum APIProviders {
    GOOGLE("Google"),
    OPENCAGE("Open Cage");

    private final String label;

    APIProviders(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
