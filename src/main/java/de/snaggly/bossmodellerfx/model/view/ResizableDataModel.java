package de.snaggly.bossmodellerfx.model.view;

/**
 * A parents class for Models used in resizable views.
 * Tracks information about the user specified width and height to be restored when saved.
 *
 * @author Omar Emshani
 */
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
