package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.guiLogic.GUIActionListener;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.AttributeEditor;
import de.snaggly.bossmodellerfx.view.factory.nodetype.AttributeEditorBuilder;
import de.snaggly.bossmodellerfx.view.factory.windowtype.UniqueCombinationEditorWindowBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Controller for Relation Edit Window
 *
 * @author Omar Emshani
 */
public class EditEntityWindowController implements ModelController<Entity> {
    private Entity entity = new Entity();
    private Entity entityRef;
    private AttributeEditor selectedAttributeEditor = null;
    private AttributeCombination primaryCombination = null;

    private final HashMap<Attribute, LinkedList<Attribute>> foreignAttributes = new HashMap<>();
    private final HashMap<Attribute, Attribute> foreignKeyMap = new HashMap<>();

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
            var newAttribute = new Attribute();
            var attributeEditor = AttributeEditorBuilder.buildAttributeEditor(newAttribute);
            attributeEditor.getController().handleDownBtnClick(attributeEditorDownClick);
            attributeEditor.getController().handleUpBtnClick(attributeEditorUpClick);
            if (index >= 1) {
                attributesListVBOX.getChildren().add(index, new Separator());
                attributesListVBOX.getChildren().add(index + 1, attributeEditor);
                entity.getAttributes().add((index + 1) / 2, newAttribute);
            } else if (attributesListVBOX.getChildren().size() >= 1){
                attributesListVBOX.getChildren().add(index, attributeEditor);
                attributesListVBOX.getChildren().add(index + 1, new Separator());
                entity.getAttributes().add(0, newAttribute);
            } else {
                attributesListVBOX.getChildren().add(index, attributeEditor);
                entity.getAttributes().add(0, newAttribute);
            }
            bindAttributeToGui(newAttribute, attributeEditor);
        } catch (IOException e) {
            GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
        removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
        addAttrbBtn.requestFocus();
    }

    private void bindAttributeToGui(Attribute attribute, AttributeEditor guiEditor) {
        guiEditor.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (selectedAttributeEditor!= null)
                selectedAttributeEditor.setDeFocusStyle();
            selectedAttributeEditor = guiEditor;
            guiEditor.setFocusStyle();
        });
        guiEditor.getController().getNameTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setName(newValue));
        guiEditor.getController().getDataTypeComboBox().getEditor().textProperty().addListener((observableValue, s, newValue) -> attribute.setType(newValue));
        guiEditor.getController().getIsPrimaryCheck().selectedProperty().addListener((observableValue, s, newValue) -> {
            if (!newValue) {
                var usedByFks = foreignAttributes.get(attribute);
                if (usedByFks != null && usedByFks.size() > 0) {
                    GUIMethods.showWarning(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.ENTITY_EDIT_ATTRIBUTED_USED_IN_FKS);
                    guiEditor.getController().getIsPrimaryCheck().setSelected(true);
                } else {
                    attribute.setPrimary(false);
                }
                //Remove PrimaryKey from PrimaryList
                if (primaryCombination != null) {
                    primaryCombination.removeAttribute(attribute);
                    //When there is only one PrimaryKey left, there is no reason to keep unique list
                    if (primaryCombination.getAttributes().size() <= 1) {
                        entity.getUniqueCombination().getCombinations().remove(primaryCombination);
                        primaryCombination = null;
                    }
                }

            } else {
                attribute.setPrimary(true);
                //Multiple PrimaryKeys needs to be in one unique list!
                var primaryKeysList = entity.getPrimaryKeys();
                if (primaryKeysList.size() > 1) {
                    if (primaryCombination == null) {
                        //Create a new list of the new PrimaryKeys when more than one
                        primaryCombination = new AttributeCombination();
                        primaryCombination.setPrimaryCombination(true);
                        primaryCombination.setCombinationName("Primary-Combination");
                        primaryCombination.getAttributes().addAll(primaryKeysList);
                        entity.getUniqueCombination().addCombination(primaryCombination);
                    }
                    else {
                        if (!primaryCombination.getAttributes().contains(attribute))
                            primaryCombination.addAttribute(attribute);
                    }
                }
            }
        });
        guiEditor.getController().getIsNonNullCheck().selectedProperty().addListener((observableValue, s, newValue) -> attribute.setNonNull(newValue));
        guiEditor.getController().getIsUniqueCheck().selectedProperty().addListener((observableValue, s, newValue) -> attribute.setUnique(newValue));
        guiEditor.getController().getCheckTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setCheckName(newValue));
        guiEditor.getController().getDefaultTF().textProperty().addListener((observableValue, s, newValue) -> attribute.setDefaultName(newValue));
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
                if (removeAttributeFromEntity(selectedIndex)) {
                    attributesListVBOX.getChildren().remove(selectedIndex);
                }
            }
            else if (selectedIndex >= 1) {
                if (removeAttributeFromEntity((selectedIndex + 1) / 2)) {
                    attributesListVBOX.getChildren().remove(--selectedIndex);
                    attributesListVBOX.getChildren().remove(selectedIndex);
                }
            } else {
                if (removeAttributeFromEntity(selectedIndex)) {
                    attributesListVBOX.getChildren().remove(selectedIndex);
                    attributesListVBOX.getChildren().remove(selectedIndex);
                }
            }
            removeAttrbBtn.setDisable(attributesListVBOX.getChildren().size() < 1);
            removeAttrbBtn.requestFocus();

            //Also remove ghost from all Combinations
            for (var uniqueCombination : entity.getUniqueCombination().getCombinations()) {
                uniqueCombination.removeAttribute(attributeEditor.getModel());
            }
            //Check if PrimaryCombo still has at least more than 1
            if (primaryCombination != null && primaryCombination.getAttributes().size() <= 1) {
                entity.getUniqueCombination().getCombinations().remove(primaryCombination);
                primaryCombination = null;
            }
        }
    }

    private boolean removeAttributeFromEntity(int index) {
        var isFk = entity.getAttributes().get(index).getFkTableColumn() != null;
        var usedByFks = foreignAttributes.get(entity.getAttributes().get(index));
        if (isFk) {
            GUIMethods.showWarning(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.ENTITY_EDIT_FK_DELETE_WARNING);
            return false;
        }
        else if (usedByFks != null && usedByFks.size() > 0) {
            GUIMethods.showWarning(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.ENTITY_EDIT_ATTRIBUTED_USED_IN_FKS);
            return false;
        }
        else {
            entity.removeAttribute(index);
            return true;
        }
    }

    private void removeAllAttributesAction() {
        attributesListVBOX.getChildren().clear();
        removeAttrbBtn.setDisable(true);
    }

    @FXML
    private void initialize() {
        addAttributeAction();
        removeAttrbBtn.setDisable(true);
        removeAttrbBtn.setTooltip(new Tooltip(BOSS_Strings.ENTITY_EDITOR_TOOLTIP_REMOVE_ATTR));
        addAttrbBtn.setTooltip(new Tooltip(BOSS_Strings.ENTITY_EDITOR_TOOLTIP_ADD_ATTR));
        tableNameTextField.setTooltip(new Tooltip(BOSS_Strings.ENTITY_EDITOR_TABLE_NAME_PROMPT));
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onDoneClick(ActionEvent actionEvent) {
        if (tableNameTextField.getText().equals("")){
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    BOSS_Strings.ENTITY_EDIT_NO_NAME_HEADER,
                    BOSS_Strings.ENTITY_EDIT_NO_NAME_WARNING);
            return;
        } else {
            entity.setName(tableNameTextField.getText());
        }

        /*if (attributesListVBOX.getChildren().size() < 1 || checkAttributesHaveNoNames()) {
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    "Keine Attribute",
                    "Die Entität muss mindestens ein Attribut besitzen!");
            return;
        }

        if (!checkContainsPrimaryKey()) {
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    "Keine Primärschlüssel",
                    "Die Entität muss mindestens ein Primärschlüssel besitzen!");
            return;
        }*/

        if (!checkIfAllAttributesContainDataType()) {
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    BOSS_Strings.ENTITY_EDIT_ATTRIBUTES_NO_DATATYPE_HEADER,
                    BOSS_Strings.ENTITY_EDIT_ATTRIBUTES_NO_DATATYPE_WARNING);
            return;
        }

        var testDoubleNameResult = checkDoubleAttributeName();
        if (testDoubleNameResult != null) {
            GUIMethods.showWarning(
                    Entity.class.getSimpleName(),
                    BOSS_Strings.ENTITY_EDIT_ATTRIBUTES_IDENTICAL_NAME_HEADER,
                    BOSS_Strings.ENTITY_EDIT_ATTRIBUTES_IDENTICAL_NAME_THE_ATTRIBUTE + testDoubleNameResult + BOSS_Strings.ENTITY_EDIT_ATTRIBUTES_IDENTICAL_NAME_USED_MULTIPLE_TIMES);
            return;
        }

        if (checkIfEntityNameAlreadyExists()) {
            if (!(entityRef != null && entity.getName().equals(entityRef.getName()))) {
                GUIMethods.showWarning(
                        Entity.class.getSimpleName(),
                        BOSS_Strings.ENTITY_EDIT_IDENTICAL_NAME_HEADER,
                        BOSS_Strings.ENTITY_EDIT_IDENTICAL_NAME_WARNING);
                return;
            }
        }

        entity.setWeakType(isWeakTypeCheckBox.isSelected());
        entity.getAttributes().removeIf(attribute -> attribute.getName() == null || attribute.getName().equals(""));

        if (entityRef != null) {
            adjustForeignKeys();
            for (var fkSet : foreignKeyMap.entrySet()) { //Reuse object
                fkSet.getValue().setName(fkSet.getKey().getName());
                fkSet.getValue().setCheckName(fkSet.getKey().getCheckName());
                fkSet.getValue().setDefaultName(fkSet.getKey().getDefaultName());
                entity.getAttributes().set(entity.getAttributes().indexOf(fkSet.getKey()), fkSet.getValue());
                //Also take account for UniqueCombos, if one of those FKs is used there. Otherwise->Ghost
                for (var uniqueCombo : entity.getUniqueCombination().getCombinations()) {
                    var attrCombo = uniqueCombo.getAttributes();
                    for (int i=0; i<uniqueCombo.getAttributes().size(); i++) {
                        var origAttrObj = foreignKeyMap.get(uniqueCombo.getAttributes().get(i));
                        if (origAttrObj != null) {
                            attrCombo.set(i, origAttrObj);
                        }
                    }
                }
            }
            entityRef.setUniqueCombination(entity.getUniqueCombination());
            entityRef.setAttributes(entity.getAttributes());
            entityRef.setName(entity.getName());
            entityRef.setWeakType(entity.isWeakType());

            entity = entityRef;
        }

        parentObserver.notify(entity);
        GUIMethods.closeWindow(actionEvent);
    }

    private void adjustForeignKeys() {
        for (var foreignKeysSet : foreignAttributes.entrySet()) {
            for (var foreignAttribute : foreignKeysSet.getValue()) {
                foreignAttribute.setType(foreignKeysSet.getKey().getType());
                foreignAttribute.setFkTableColumn(foreignKeysSet.getKey());
            }
        }
    }

    @FXML
    private void editUniqueComboClick(ActionEvent actionEvent) {
        if (attributesListVBOX.getChildren().size() < 1 || checkAttributesHaveNoNames()) {
            GUIMethods.showWarning(
                Entity.class.getSimpleName(),
                    BOSS_Strings.ENTITY_EDIT_NO_ATTRIBUTES_HEADER,
                    BOSS_Strings.ENTITY_EDIT_NO_ATTRIBUTES_WARNING
            );
        }
        else {
            try {
                var uniqueEditorWindow = UniqueCombinationEditorWindowBuilder.buildEntityEditor(entity.getUniqueCombination(), entity.getAttributes());
                var stage = new Stage();
                stage.setScene(uniqueEditorWindow.getKey());
                stage.setTitle(BOSS_Strings.UNIQUE_COMBO_EDITOR_TITLE);
                stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
                stage.show();
            } catch (IOException e) {
                GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void loadModel(Entity model) {
        this.entityRef = model;
        entity.setName(model.getName());

        entity.setWeakType(model.isWeakType());
        entity.setAttributes(new ArrayList<>());
        for (var modelAttribute : model.getAttributes()) {
            var newAttribute = new Attribute(
                    modelAttribute.getName(),
                    modelAttribute.getType(),
                    modelAttribute.isPrimary(),
                    modelAttribute.isNonNull(),
                    modelAttribute.isUnique(),
                    modelAttribute.getCheckName(),
                    modelAttribute.getDefaultName(),
                    modelAttribute.getFkTableColumn(),
                    modelAttribute.getFkTable()
            );
            entity.addAttribute(newAttribute);

            if (newAttribute.isPrimary())
                foreignAttributes.put(newAttribute, getForeignEntities(modelAttribute));

            if (modelAttribute.getFkTableColumn() != null) { //It's a ForeignKey. Making sure to reuse the same object on save, to match Object on relation's foreignKeyList
                foreignKeyMap.put(newAttribute, modelAttribute);
            }
        }

        var uniqueCombination = new UniqueCombination();
        var attributeCombinations = new ArrayList<AttributeCombination>();
        for (var combination : model.getUniqueCombination().getCombinations()) {
            var attributeCombination = new AttributeCombination();
            for (var attribute : combination.getAttributes()) {
                attributeCombination.addAttribute(entity.getAttributes().get(model.getAttributes().indexOf(attribute)));
            }
            attributeCombination.setPrimaryCombination(combination.isPrimaryCombination());
            attributeCombination.setCombinationName(combination.getCombinationName());
            attributeCombinations.add(attributeCombination);
        }
        uniqueCombination.setCombinations(attributeCombinations);
        entity.setUniqueCombination(uniqueCombination);
        //Get Primary UniqueList of given Model
        for (var combination : entity.getUniqueCombination().getCombinations()) {
            if (combination.isPrimaryCombination()) {
                primaryCombination = combination;
                break;
            }
        }

        tableNameTextField.setText(entity.getName());
        isWeakTypeCheckBox.setSelected(entity.isWeakType());
        removeAllAttributesAction();
        if (entity.getAttributes().size() > 0) {
            for (var attributeModel : entity.getAttributes()) {
                try {
                    var attributeEditor = AttributeEditorBuilder.buildAttributeEditor(attributeModel);
                    attributesListVBOX.getChildren().add(attributeEditor);
                    attributeEditor.getController().handleDownBtnClick(attributeEditorDownClick);
                    attributeEditor.getController().handleUpBtnClick(attributeEditorUpClick);
                    bindAttributeToGui(attributeModel, attributeEditor);
                    attributesListVBOX.getChildren().add(new Separator());
                } catch (IOException e) {
                    GUIMethods.showError(EditEntityWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            }
            attributesListVBOX.getChildren().remove(attributesListVBOX.getChildren().size()-1);
        } else {
            addAttributeAction();
        }
        removeAttrbBtn.setDisable(entity.getAttributes().size()<=1);
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

    private boolean checkAttributesHaveNoNames() {
        boolean result = false;
        for (int i = 0; i < attributesListVBOX.getChildren().size() && !result; i++) {
            var attributeEditView = (AttributeEditor)(attributesListVBOX.getChildren().get(i));
            result = !attributeEditView.getController().getNameTF().getText().equals("");
        }

        return !result;
    }

    private boolean checkContainsPrimaryKey() {
        boolean result = false;
        for (int i = 0; i < attributesListVBOX.getChildren().size() && !result; i++) {
            var node = attributesListVBOX.getChildren().get(i);
            if (!(node instanceof AttributeEditor))
                continue;
            var attributeEditView = (AttributeEditor)(node);
            result = attributeEditView.getController().getIsPrimaryCheck().isSelected();
        }

        return result;
    }

    private boolean checkIfAllAttributesContainDataType() {
        for (int i = 0; i < attributesListVBOX.getChildren().size(); i++) {
            var node = attributesListVBOX.getChildren().get(i);
            if (!(node instanceof AttributeEditor))
                continue;
            var attributeEditView = (AttributeEditor)(node);
            var attributeType = attributeEditView.getController().getDataTypeComboBox().getEditor().getText();
            var attributeName = attributeEditView.getController().getNameTF().getText();
            if ((attributeName != null && !attributeName.equals("")) && (attributeType == null || attributeType.equals("")))
                return false;
        }
        return true;
    }

    private AttributeEditor getFocusedAttribute(Node focusedNode) {
        if (selectedAttributeEditor != null)
            return selectedAttributeEditor;
        var currentNode = focusedNode;
        do
            currentNode = currentNode.getParent();
        while(currentNode != null && !(currentNode instanceof AttributeEditor));
        return (AttributeEditor)currentNode;
    }

    private boolean checkIfEntityNameAlreadyExists() {
        var result = false;
        var currentProjectsEntities = Project.getCurrentProject().getEntities();
        for (var projEntity : currentProjectsEntities) {
            result = projEntity.getName().equals(entity.getName());
        }
        return result;
    }

    private String checkDoubleAttributeName() {
        var namesSet = new HashSet<String>();
        for (var attribute : entity.getAttributes()) {
            if (!namesSet.add(attribute.getName())) {
                return attribute.getName();
            }
        }

        return null;
    }

    private LinkedList<Attribute> getForeignEntities(Attribute foreignKey) {
        var result = new LinkedList<Attribute>();
        for (var projEntity : Project.getCurrentProject().getEntities()) {
            for (var projAttr : projEntity.getAttributes()) {
                if (foreignKey == projAttr.getFkTableColumn()) {
                    result.add(projAttr);
                }
            }
        }
        return result;
    }
}
