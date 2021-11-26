package de.snaggly.bossmodellerfx.model;

public class Comment extends ResizableDataModel {

    public Comment(String text) {
        super(text, 0.0, 0.0);
    }

    public Comment(String text, double xCoordinate, double yCoordinate) {
        super(text, xCoordinate, yCoordinate);
    }
}
