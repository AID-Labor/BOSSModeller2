package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.guiLogic.GUIActionListener;
import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.view.AttributeEditor;
import de.snaggly.bossmodeller2.view.factory.nodetype.AttributeEditorBuilder;
import de.snaggly.bossmodeller2.view.factory.windowtype.UniqueCombinationEditorWindowBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class EditEntityWindowController implements ViewController<Entity> {
    private Entity entity;

    public GUIActionListener<Entity> parentObserver;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField tableNameTextField;
    @FXML
    private CheckBox isWeakTypeCheckBox;
    @FXML
    private Button addAttrbBtn;
    @FXML
    private Button removeAttrbBtn;
    @FXML
    private VBox attributesListVBOX;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button doneBtn;

    @FXML
    public void addAttributeAction() {
        var index = 0;

        var currentScene = attributesListVBOX.getScene();
        if (currentScene != null) {
            var attributeEditor = getFocusedAttribute(currentScene.getFocusOwner());
            if (attributeEditor != null) {
                index = attributesListVBOX.getChildren().indexOf(attributeEditor) + 1;
            }
        }

        try {
            var attributeEditor = AttributeEditorBuilder.buildAttributeEditor();
            attributeEditor.getController().handleDownBtnClick(attributeEditorDownClick);
            attributeEditor.getController().handleUpBtnClick(attributeEditorUpClick);
            if (index >= 1) {
                attributesListVBOX.getChildren().add(index, new Separator());
                attributesListVBOX.getChildren().add(index + 1, attributeEditor);
            } else if (attributesListVBOX.getChildren().size() >= 1){
                attributesListVBOX.getChildren().add(index, attributeEditor);
                attributesListVBOX.getChildren().add(index + 1, new Separator());
            } else {
                attributesListVBOX.getChildren().add(index, attributeEditor);
            }
        } catch (IOException e) {
            GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
        }
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
        addAttrbBtn.requestFocus();
    }

    @FXML
    public void removeAttributeAction() {
        var currentScene = attributesListVBOX.getScene();
        if (currentScene == null)
            return;
        var attributeEditor = getFocusedAttribute(currentScene.getFocusOwner());
        if (attributeEditor != null) {
            var selectedIndex = attributesListVBOX.getChildren().indexOf(attributeEditor);
            if (selectedIndex == 0 && attributesListVBOX.getChildren().size() == 1) {
                attributesListVBOX.getChildren().remove(selectedIndex);
            }
            else if (selectedIndex >= 1) {
                attributesListVBOX.getChildren().remove(--selectedIndex);
                attributesListVBOX.getChildren().remove(selectedIndex);
            } else {
                attributesListVBOX.getChildren().remove(selectedIndex);
                attributesListVBOX.getChildren().remove(selectedIndex);
            }
            removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
            removeAttrbBtn.requestFocus();
        }
    }

    private void removeAllAttributesAction() {
        attributesListVBOX.getChildren().clear();
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
    }

    @FXML
    private void initialize() {
        if (entity == null) {
            entity = new Entity();
        }
        addAttributeAction();
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onDoneClick(ActionEvent actionEvent) {
        var canClose = true;

        if (tableNameTextField.getText().equals("")){
            canClose = false;
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    "Keine Namen",
                    "Die Entität muss ein Namen haben!");
        } else {
            entity.setName(tableNameTextField.getText());
        }

        if (attributesListVBOX.getChildren().size() < 1 || !checkAttributesContent()) {
            if (canClose) {
                canClose = false;
                GUIMethods.showWarning(
                        Entity.class.getSimpleName(),
                        "Keine Attribute",
                        "Die Entität muss mindestens ein Attribut besitzen!");
            }
        } else {
            entity.setAttributes(readAttributes());
        }

        entity.setWeakType(isWeakTypeCheckBox.isSelected());

        if (canClose) {
            parentObserver.notify(entity);
            GUIMethods.closeWindow(actionEvent);
        }
    }

    @FXML
    private void editUniqueComboClick(ActionEvent actionEvent) {
        try {
            var uniqueEditorWindow = UniqueCombinationEditorWindowBuilder.buildEntityEditor(readAttributes());
            var stage = new Stage();
            stage.setScene(uniqueEditorWindow.getKey());
            stage.setTitle("UniqueCombo Editor");
            stage.show();
        } catch (IOException e) {
            GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    @Override
    public void loadModel(Entity model) {
        this.entity = model;
        tableNameTextField.setText(model.getName());
        isWeakTypeCheckBox.setSelected(model.isWeakType());
        removeAllAttributesAction();
        removeAttrbBtn.setDisable(model.getAttributes().size() < 1);
        for (var attributeModel : model.getAttributes()) {
            try {
                var attributeEditor = AttributeEditorBuilder.buildAttributeEditor(attributeModel);
                attributesListVBOX.getChildren().add(attributeEditor);
                attributeEditor.getController().handleDownBtnClick(attributeEditorDownClick);
                attributeEditor.getController().handleUpBtnClick(attributeEditorUpClick);
                attributesListVBOX.getChildren().add(new Separator());
            } catch (IOException e) {
                GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
            }
        }
        attributesListVBOX.getChildren().remove(attributesListVBOX.getChildren().size()-1);
    }

    private ArrayList<Attribute> readAttributes() {
        var attributes = new ArrayList<Attribute>();
        for (var attribute : attributesListVBOX.getChildren()) {
            if (!(attribute instanceof AttributeEditor))
                continue;
            var attributeController = ((AttributeEditor)attribute).getController();
            if (attributeController.getNameTF().getText().equals(""))
                continue;
            attributes.add(new Attribute(
                    attributeController.getNameTF().getText(),
                    "integer",
                    attributeController.getIsPrimaryCheck().isSelected(),
                    attributeController.getIsNonNullCheck().isSelected(),
                    attributeController.getIsUniqueCheck().isSelected(),
                    attributeController.getCheckTF().getText(),
                    attributeController.getDefaultTF().getText(),
                    "",
                    ""));
        }
        return attributes;
    }

    private final EventHandler<MouseEvent> attributeEditorDownClick = mouseEvent ->  {
        //Das ist nicht besonders eine effiziente Art den Index zu ermitteln. Schade.
        var attributeEditor = getFocusedAttribute(attributesListVBOX.getScene().getFocusOwner());
        var index = attributesListVBOX.getChildren().indexOf(attributeEditor);

        if (index < attributesListVBOX.getChildren().size() - 1) {
            attributesListVBOX.getChildren().remove(index);
            attributesListVBOX.getChildren().remove(index);
            attributesListVBOX.getChildren().add(index + 1, new Separator());
            attributesListVBOX.getChildren().add(index + 2, attributeEditor);
        }
    };

    private final EventHandler<MouseEvent> attributeEditorUpClick = mouseEvent ->  {
        var attributeEditor = getFocusedAttribute(attributesListVBOX.getScene().getFocusOwner());
        var index = attributesListVBOX.getChildren().indexOf(attributeEditor);

        if (index > 0) {
            attributesListVBOX.getChildren().remove(index);
            attributesListVBOX.getChildren().remove(index - 1);
            attributesListVBOX.getChildren().add(index - 2, attributeEditor);
            attributesListVBOX.getChildren().add(index - 1, new Separator());
        }
    };

    private boolean checkAttributesContent() {
        boolean result = false;
        for (int i = 0; i < attributesListVBOX.getChildren().size() && !result; i++) {
            var attributeEditView = (AttributeEditor)(attributesListVBOX.getChildren().get(i));
            result = !attributeEditView.getController().getNameTF().getText().equals("");
        }

        return result;
    }

    private AttributeEditor getFocusedAttribute(Node focusedNode) {
        var currentNode = focusedNode;
        do
            currentNode = currentNode.getParent();
        while(currentNode != null && !(currentNode instanceof AttributeEditor));
        return (AttributeEditor)currentNode;
    }
}
