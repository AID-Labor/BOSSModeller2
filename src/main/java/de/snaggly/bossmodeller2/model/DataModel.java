package de.snaggly.bossmodeller2.model;

public abstract class DataModel {
    private String name;
    private double xCoordinate;
    private double yCoordinate;

    public DataModel() {
        this.name = "Placeholder";
        this.xCoordinate = 10.0;
        this.yCoordinate = 10.0;
    }

    public DataModel(String name, double xCoordinate, double yCoordinate) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
