package de.snaggly.bossmodellerfx.model.view;

import de.snaggly.bossmodellerfx.model.abstraction.AbstractedModel;

/**
 * Model for a CommentView.
 * A CommentView is movable and resizable and contains only a text.
 * TODO: Hold color data to make a CommentView display in user defines colors.
 *
 * @author Omar Emshani
 */
public class Comment extends ResizableDataModel implements AbstractedModel {
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
