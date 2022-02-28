package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.view.Entity;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Controller for EntityView
 *
 * @author Omar Emshani
 */
public class EntityViewController implements ModelController<Entity> {
    @FXML
    private Label entityHeadTitle;
    @FXML
    private VBox entityAttributesVBox;
    @FXML
    private Line weakLineNW;
    @FXML
    private Line weakLineNE;
    @FXML
    private Line weakLineSW;
    @FXML
    private Line weakLineSE;

    @Override
    public void loadModel(Entity model) {
        entityHeadTitle.setText(model.getName());
        entityAttributesVBox.getChildren().clear();

        for (var attribute : model.getAttributes()) {
            var attributeLabel = new Label(attribute.getName());
            attributeLabel.setUnderline(attribute.isPrimary());
            if (attribute.getFkTableColumn() != null)
                attributeLabel.setStyle("-fx-font-style: italic; ");
            attributeLabel.setPadding(new Insets(2, 15, 2, 15));
            entityAttributesVBox.getChildren().add(attributeLabel);
        }

        weakLineNW.setVisible(model.isWeakType());
        weakLineNE.setVisible(model.isWeakType());
        weakLineSW.setVisible(model.isWeakType());
        weakLineSE.setVisible(model.isWeakType());
    }

    public void highlightAttributeAt(int index) throws IndexOutOfBoundsException {
        var attributeLabel = ((Label)entityAttributesVBox.getChildren().get(index));
        if (attributeLabel == null)
            throw new IndexOutOfBoundsException();

        attributeLabel.setTextFill(Color.rgb(3,158,211));
    }

    public void unHighlightAttributeAt(int index) throws IndexOutOfBoundsException {
        var attributeLabel = ((Label)entityAttributesVBox.getChildren().get(index));
        if (attributeLabel == null)
            throw new IndexOutOfBoundsException();

        attributeLabel.setTextFill(Color.BLACK);
    }
}
