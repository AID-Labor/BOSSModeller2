package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.view.Comment;

public class SerializableComment extends Comment implements SerializableModel<Comment>{
    public SerializableComment(String text) {
        super(text);
    }

    public SerializableComment(String text, double xCoordinate, double yCoordinate) {
        super(text, xCoordinate, yCoordinate);
    }

    public SerializableComment(String text, double xCoordinate, double yCoordinate, double width, double height) {
        super(text, xCoordinate, yCoordinate, width, height);
    }

    private final static SerializableComment singletonInstance = new SerializableComment("");

    public static SerializableComment serializableComment(Comment comment) {
        return singletonInstance.serialize(comment);
    }

    public static Comment deserializableComment(SerializableComment serComment) {
        return singletonInstance.deserialize(serComment);
    }

    @Override
    public SerializableComment serialize(Comment model) {
        return new SerializableComment(
                model.getText(),
                model.getXCoordinate(),
                model.getYCoordinate(),
                model.getWidth(),
                model.getHeight()
        );
    }

    @Override
    public Comment deserialize(SerializableModel<Comment> serializedModel) {
        if (serializedModel instanceof SerializableComment)
        {
            var serComment = (SerializableComment)serializedModel;
            return new Comment(
                    serComment.getText(),
                    serComment.getXCoordinate(),
                    serComment.getYCoordinate(),
                    serComment.getWidth(),
                    serComment.getHeight()
            );
        }
        return null;
    }
}
