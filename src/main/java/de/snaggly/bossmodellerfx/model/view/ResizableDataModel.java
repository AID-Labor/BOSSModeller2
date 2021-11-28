package de.snaggly.bossmodellerfx.model.view;

public abstract class ResizableDataModel extends ViewModel {
    private double width;
    private double height;

    public ResizableDataModel(double xCoordinate, double yCoordinate) {
        super(xCoordinate, yCoordinate);
    }

    public ResizableDataModel(double xCoordinate, double yCoordinate, double width, double height) {
        super(xCoordinate, yCoordinate);
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
