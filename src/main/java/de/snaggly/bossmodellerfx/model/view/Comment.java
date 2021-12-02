package de.snaggly.bossmodellerfx.model.view;

import de.snaggly.bossmodellerfx.model.view.ResizableDataModel;

public class Comment extends ResizableDataModel {
    private String text;

    public Comment(String text) {
        this(text, 0.0, 0.0);
    }

    public Comment(String text, double xCoordinate, double yCoordinate) {
        this(text, xCoordinate, yCoordinate, 0, 0);
    }
    public Comment(String text, double xCoordinate, double yCoordinate, double width, double height) {
        super(xCoordinate, yCoordinate, width, height);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
