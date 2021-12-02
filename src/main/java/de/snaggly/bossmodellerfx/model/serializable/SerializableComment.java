package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.view.Comment;

public class SerializableComment extends Comment {
    public SerializableComment(String text) {
        super(text);
    }

    public SerializableComment(String text, double xCoordinate, double yCoordinate) {
        super(text, xCoordinate, yCoordinate);
    }

    public SerializableComment(String text, double xCoordinate, double yCoordinate, double width, double height) {
        super(text, xCoordinate, yCoordinate, width, height);
    }

    public static SerializableComment serializableComment(Comment comment) {
        return new SerializableComment(
                comment.getText(),
                comment.getXCoordinate(),
                comment.getYCoordinate(),
                comment.getWidth(),
                comment.getHeight()
        );
    }

    public static Comment deserializableComment(SerializableComment serComment) {
        return new Comment(
                serComment.getText(),
                serComment.getXCoordinate(),
                serComment.getXCoordinate(),
                serComment.getWidth(),
                serComment.getHeight()
        );
    }
}
