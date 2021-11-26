package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.guiLogic.GUIActionListener;
import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.view.AttributeEditor;
import de.snaggly.bossmodeller2.view.factory.nodetype.AttributeEditorBuilder;
import de.snaggly.bossmodeller2.view.factory.windowtype.UniqueCombinationEditorWindowBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.Collections;


public class EditEntityWindowController implements ViewController<Entity> {
    private Entity entity = new Entity();

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
                addNewAttributeToEntity(attributeEditor, (index + 1) / 2);
            } else if (attributesListVBOX.getChildren().size() >= 1){
                addNewAttributeToEntity(attributeEditor, 0);
                attributesListVBOX.getChildren().add(index, attributeEditor);
                attributesListVBOX.getChildren().add(index + 1, new Separator());
            } else {
                addNewAttributeToEntity(attributeEditor, 0);
                attributesListVBOX.getChildren().add(index, attributeEditor);
            }
        } catch (IOException e) {
            GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
        addAttrbBtn.requestFocus();
    }

    private void bindAttributeToGui(Attribute attribute, AttributeEditor guiEditor) {
        guiEditor.getController().getNameTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setName(newValue));
        guiEditor.getController().getIsPrimaryCheck().selectedProperty().addListener((observableValue, s, newValue) -> attribute.setPrimary(newValue));
        guiEditor.getController().getIsNonNullCheck().selectedProperty().addListener((observableValue, s, newValue) -> attribute.setNonNull(newValue));
        guiEditor.getController().getIsUniqueCheck().selectedProperty().addListener((observableValue, s, newValue) -> attribute.setUnique(newValue));
        guiEditor.getController().getCheckTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setCheckName(newValue));
        guiEditor.getController().getDefaultTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setDefaultName(newValue));
    }

    private void addNewAttributeToEntity(AttributeEditor attributeEditor, int index) {
        var newAttribute = new Attribute();
        bindAttributeToGui(newAttribute, attributeEditor);
        entity.getAttributes().add(index, newAttribute);
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
                removeAttributeFromEntity(selectedIndex);
            }
            else if (selectedIndex >= 1) {
                attributesListVBOX.getChildren().remove(--selectedIndex);
                attributesListVBOX.getChildren().remove(selectedIndex);
                removeAttributeFromEntity((selectedIndex + 1) / 2);
            } else {
                attributesListVBOX.getChildren().remove(selectedIndex);
                attributesListVBOX.getChildren().remove(selectedIndex);
                removeAttributeFromEntity(selectedIndex);
            }
            removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
            removeAttrbBtn.requestFocus();
        }
    }

    private void removeAttributeFromEntity(int index) {
        entity.removeAttribute(index);
    }

    private void removeAllAttributesAction() {
        attributesListVBOX.getChildren().clear();
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
    }

    @FXML
    private void initialize() {
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
        }

        if (!checkContainsPrimaryKey()) {
            if (canClose) {
                canClose = false;
                GUIMethods.showWarning(
                        Entity.class.getSimpleName(),
                        "Keine Primärschlüssel",
                        "Die Entität muss mindestens ein Primärschlüssel besitzen!");
            }
        }

        entity.setWeakType(isWeakTypeCheckBox.isSelected());

        if (canClose) {
            parentObserver.notify(entity);
            GUIMethods.closeWindow(actionEvent);
        }
    }

    @FXML
    private void editUniqueComboClick(ActionEvent actionEvent) {
        if (attributesListVBOX.getChildren().size() < 1 || !checkAttributesContent()) {
            GUIMethods.showWarning(
                Entity.class.getSimpleName(),
                "Keine Attribute",
                "Die Entität muss mindestens ein Attribut besitzen!"
            );
        }
        else {
            try {
                var uniqueEditorWindow = UniqueCombinationEditorWindowBuilder.buildEntityEditor(entity.getUniqueCombination(), entity.getAttributes());
                var stage = new Stage();
                stage.setScene(uniqueEditorWindow.getKey());
                stage.setTitle("UniqueCombo Editor");
                stage.show();
            } catch (IOException e) {
                GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
            }
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
                bindAttributeToGui(attributeModel, attributeEditor);
                attributesListVBOX.getChildren().add(new Separator());
            } catch (IOException e) {
                GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
            }
        }
        attributesListVBOX.getChildren().remove(attributesListVBOX.getChildren().size()-1);
    }

    private final EventHandler<MouseEvent> attributeEditorDownClick = mouseEvent ->  {
        var attributeEditor = getFocusedAttribute(attributesListVBOX.getScene().getFocusOwner());
        var index = attributesListVBOX.getChildren().indexOf(attributeEditor);

        if (index < attributesListVBOX.getChildren().size() - 1) {
            attributesListVBOX.getChildren().remove(index);
            attributesListVBOX.getChildren().remove(index);
            attributesListVBOX.getChildren().add(index + 1, new Separator());
            attributesListVBOX.getChildren().add(index + 2, attributeEditor);
            Collections.swap(entity.getAttributes(), index/2, (index/2)+1);
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
            Collections.swap(entity.getAttributes(), index/2, (index/2)-1);
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

    private boolean checkContainsPrimaryKey() {
        boolean result = false;
        for (int i = 0; i < attributesListVBOX.getChildren().size() && !result; i++) {
            var attributeEditView = (AttributeEditor)(attributesListVBOX.getChildren().get(i));
            result = attributeEditView.getController().getIsPrimaryCheck().isSelected();
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
