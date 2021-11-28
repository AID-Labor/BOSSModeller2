package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.view.Comment;

public class SerializableComment extends Comment {
    public SerializableComment(String text) {
        super(text);
    }

    public SerializableComment(String text, double xCoordinate, double yCoordinate) {
        super(text, xCoordinate, yCoordinate);
    }

    public static SerializableComment serializableComment(Comment comment) {
        return new SerializableComment(
                comment.getText(),
                comment.getXCoordinate(),
                comment.getYCoordinate()
        );
    }

    public static Comment deserializableComment(SerializableComment serComment) {
        return new Comment(serComment.getText(), serComment.getXCoordinate(), serComment.getXCoordinate());
    }
}
