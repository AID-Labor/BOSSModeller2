package de.snaggly.bossmodellerfx.model;

public class ResizableDataModel extends DataModel{
    private double width;
    private double height;

    public ResizableDataModel(String name, double xCoordinate, double yCoordinate) {
        super(name, xCoordinate, yCoordinate);
    }

    public ResizableDataModel(String name, double xCoordinate, double yCoordinate, double width, double height) {
        super(name, xCoordinate, yCoordinate);
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
