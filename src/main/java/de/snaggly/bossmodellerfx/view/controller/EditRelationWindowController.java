package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.*;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;
import de.snaggly.bossmodellerfx.relation_logic.ForeignKeyHandler;
import de.snaggly.bossmodellerfx.view.CrowsFootShape;
import de.snaggly.bossmodellerfx.view.EntityView;
import de.snaggly.bossmodellerfx.view.factory.nodetype.EntityBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Controller for Relation Edit Window
 *
 * @author Omar Emshani
 */
public class EditRelationWindowController implements ModelController<Relation> {
    private Relation refRelation;
    private Relation relation;
    private Project workspace;
    private CrowsFootShape crowsFootTableA;
    private CrowsFootShape crowsFootTableB;
    private Entity entityAModelReference;
    private Entity entityBModelReference;

    private Double previousAHeight = 0.0;
    private Double previousBHeight = 0.0;

    public GUIActionListener<Relation> parentObserver;

    @FXML
    private AnchorPane windowAnchorPane;
    @FXML
    private HBox examplePane;
    @FXML
    private ComboBox<String> tableAEntityCmboBox;
    @FXML
    private ComboBox<String> tableBEntityCmboBox;
    @FXML
    private CheckBox tableAIsWeakChkBox;
    @FXML
    private CheckBox tableBIsWeakChkBox;
    @FXML
    private RadioButton radioBtnPolyA1;
    @FXML
    private ToggleGroup PolynomA;
    @FXML
    private RadioButton radioBtnPolyAN;
    @FXML
    private RadioButton radioBtnObligationACan;
    @FXML
    private ToggleGroup ObligationA;
    @FXML
    private RadioButton radioBtnObligationAMust;
    @FXML
    private RadioButton radioBtnPolyB1;
    @FXML
    private ToggleGroup PolynomB;
    @FXML
    private RadioButton radioBtnPolyBN;
    @FXML
    private RadioButton radioBtnObligationBCan;
    @FXML
    private ToggleGroup ObligationB;
    @FXML
    private RadioButton radioBtnObligationBMust;
    @FXML
    public Button saveBtn;
    @FXML
    public Button cancelBtn;

    private EntityView tableAexample;
    private EntityView tableBexample;
    private final Line exampleLine = new Line();

    @FXML
    private void initialize(){
        loadProject();
        exampleLine.setStrokeWidth(2.0);
        windowAnchorPane.getChildren().add(exampleLine);
    }

    @FXML
    private void showInfo() {
        String infoText = BOSS_Strings.RELATION_INFO_ONE1;
        infoText += relation.getTableA().getName();

        if (radioBtnObligationBCan.isSelected()) {
            infoText += BOSS_Strings.RELATION_INFO_CAN;
        } else {
            infoText += BOSS_Strings.RELATION_INFO_MUST;
        }

        if (radioBtnPolyB1.isSelected()) {
            infoText += BOSS_Strings.RELATION_INFO_ONE2;
        } else {
            infoText += BOSS_Strings.RELATION_INFO_MANY;
        }

        infoText += relation.getTableB().getName();
        infoText += BOSS_Strings.RELATION_INFO_HAS;

        infoText += BOSS_Strings.RELATION_INFO_ONE1;
        infoText += relation.getTableB().getName();

        if (radioBtnObligationACan.isSelected()) {
            infoText += BOSS_Strings.RELATION_INFO_CAN;
        } else {
            infoText += BOSS_Strings.RELATION_INFO_MUST;
        }

        if (radioBtnPolyA1.isSelected()) {
            infoText += BOSS_Strings.RELATION_INFO_ONE2;
        } else {
            infoText += BOSS_Strings.RELATION_INFO_MANY;
        }

        infoText += relation.getTableA().getName();
        infoText += BOSS_Strings.RELATION_INFO_HAS;

        GUIMethods.showInfo(BOSS_Strings.RELATION_INFO, "", infoText);
    }

    @FXML
    private void closeWindow(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void save(ActionEvent actionEvent) {
        //Check if any foreignKeys have had been set.
        if (relation.getFkAttributesA().size() <= 0 && relation.getFkAttributesB().size() <= 0) {
            GUIMethods.showWarning(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_NO_FOREIGN_KEYS_HEADER, BOSS_Strings.RELATION_EDITOR_NO_FOREIGN_KEYS_WARNING);
            return;
        }

        //Move temp models to real models
        entityAModelReference.setWeakType(relation.getTableA().isWeakType());
        entityAModelReference.setAttributes(relation.getTableA().getAttributes());
        entityBModelReference.setWeakType(relation.getTableB().isWeakType());
        entityBModelReference.setAttributes(relation.getTableB().getAttributes());
        //Readjust ForeignKey from TempEntity
        for (var attribute : relation.getFkAttributesA()) { //In TabA
            attribute.setFkTable(entityBModelReference);
        }
        for (var attribute : relation.getFkAttributesB()) { //In TabB
            attribute.setFkTable(entityAModelReference);
        }
        var preStashedFksA = relation.getFkAttributesA();
        var preStashedFksB = relation.getFkAttributesB();
        relation.setTableA(entityAModelReference);
        relation.setTableB(entityBModelReference);
        relation.setFkAttributesA(preStashedFksA);
        relation.setFkAttributesB(preStashedFksB);
        if (refRelation != null) {
            refRelation.setTableA_Cardinality(relation.getTableA_Cardinality());
            refRelation.setTableA_Obligation(relation.getTableA_Obligation());
            refRelation.setTableB_Cardinality(relation.getTableB_Cardinality());
            refRelation.setTableB_Obligation(relation.getTableB_Obligation());
            refRelation.setFkAttributesA(relation.getFkAttributesA());
            refRelation.setFkAttributesB(relation.getFkAttributesB());
            relation = refRelation;
        }
        relation.setStrongRelation(isStrongRelation());
        parentObserver.notify(relation);
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onTableAIsWeakCheckBoxAction() {
        if (tableAIsWeakChkBox.isSelected())
            tableBIsWeakChkBox.setSelected(false);

        updateConnectionLine();
    }

    @FXML
    private void onTableBIsWeakCheckBoxAction() {
        if (tableBIsWeakChkBox.isSelected())
            tableAIsWeakChkBox.setSelected(false);

        updateConnectionLine();
    }

    @FXML
    private void updateConnectionLine() {
        entityAModelReference = workspace.getEntities().get(tableAEntityCmboBox.getSelectionModel().getSelectedIndex());
        LinkedList<Attribute> fKeysA = null;
        LinkedList<Attribute> fKeysB = null;
        if (refRelation != null) {
            fKeysA = refRelation.getFkAttributesA();
            fKeysB = refRelation.getFkAttributesB();
        }
        var entityAttributes = new ArrayList<>(entityAModelReference.getAttributes());
        //Work on a temp Model, do not apply changes on main model until user saves!
        relation.setTableA(new Entity(
                entityAModelReference.getName(),
                entityAModelReference.getXCoordinate(),
                entityAModelReference.getYCoordinate(),
                entityAttributes,
                entityAModelReference.isWeakType()
        ));
        if (fKeysA != null) {
            relation.setFkAttributesA(fKeysA);
        }

        if (radioBtnPolyAN.isSelected())
            relation.setTableA_Cardinality(CrowsFootOptions.Cardinality.MANY);
        else
            relation.setTableA_Cardinality(CrowsFootOptions.Cardinality.ONE);
        if (radioBtnObligationAMust.isSelected())
            relation.setTableA_Obligation(CrowsFootOptions.Obligation.MUST);
        else
            relation.setTableA_Obligation(CrowsFootOptions.Obligation.CAN);

        entityBModelReference = workspace.getEntities().get(tableBEntityCmboBox.getSelectionModel().getSelectedIndex());
        if (entityAModelReference == entityBModelReference) { //SelfRelation
            relation.setTableB(relation.getTableA());
        }
        else {
            entityAttributes = new ArrayList<>(entityBModelReference.getAttributes());
            relation.setTableB(new Entity(
                    entityBModelReference.getName(),
                    entityBModelReference.getXCoordinate(),
                    entityBModelReference.getYCoordinate(),
                    entityAttributes,
                    entityBModelReference.isWeakType()
            ));
        }
        if (fKeysB != null) {
            relation.setFkAttributesB(fKeysB);
        }

        if (radioBtnPolyBN.isSelected())
            relation.setTableB_Cardinality(CrowsFootOptions.Cardinality.MANY);
        else
            relation.setTableB_Cardinality(CrowsFootOptions.Cardinality.ONE);
        if (radioBtnObligationBMust.isSelected())
            relation.setTableB_Obligation(CrowsFootOptions.Obligation.MUST);
        else
            relation.setTableB_Obligation(CrowsFootOptions.Obligation.CAN);

        //Adjust ForeignKey Table Reference
        var fKeysInA = relation.getFkAttributesA();
        var allAttributesInA = relation.getTableA().getAttributes();
        for (int i=0; i<fKeysInA.size(); i++) {
            var attribute = fKeysInA.get(i);
            var tableIndex = allAttributesInA.indexOf(attribute);
            var fk = new Attribute(attribute.getName(),
                    attribute.getType(),
                    attribute.isPrimary(),
                    attribute.isNonNull(),
                    attribute.isUnique(),
                    attribute.getCheckName(),
                    attribute.getDefaultName(),
                    attribute.getFkTableColumn(),
                    relation.getTableB());
            fKeysInA.set(i, fk);
            allAttributesInA.set(tableIndex, fk);
        }
        var fKeysInB = relation.getFkAttributesB();
        var allAttributesInB = relation.getTableB().getAttributes();
        for (int i=0; i<relation.getFkAttributesB().size(); i++) {
            var attribute = fKeysInB.get(i);
            var tableIndex = allAttributesInB.indexOf(attribute);
            var fk = new Attribute(attribute.getName(),
                    attribute.getType(),
                    attribute.isPrimary(),
                    attribute.isNonNull(),
                    attribute.isUnique(),
                    attribute.getCheckName(),
                    attribute.getDefaultName(),
                    attribute.getFkTableColumn(),
                    relation.getTableA());
            fKeysInB.set(i, fk);
            allAttributesInB.set(tableIndex, fk);
        }

        relation.setStrongRelation(tableAIsWeakChkBox.isSelected() || tableBIsWeakChkBox.isSelected());

        rebuildEntityView();
    }

    @Override
    public void loadModel(Relation model) {
        this.refRelation = model;
        this.relation = new Relation(
                model.getTableA(),
                model.getTableB(),
                model.getTableA_Cardinality(),
                model.getTableB_Cardinality(),
                model.getTableA_Obligation(),
                model.getTableB_Obligation()
        );
        relation.setFkAttributesA(refRelation.getFkAttributesA());
        relation.setFkAttributesB(refRelation.getFkAttributesB());
        relation.setStrongRelation(model.isStrongRelation());
        entityAModelReference = relation.getTableA();
        entityBModelReference = relation.getTableB();
        tableAEntityCmboBox.getSelectionModel().select(workspace.getEntities().indexOf(model.getTableA()));
        radioBtnPolyAN.setSelected(model.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY);
        radioBtnPolyA1.setSelected(model.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE);
        radioBtnObligationAMust.setSelected(model.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST);
        radioBtnObligationACan.setSelected(model.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN);
        tableBEntityCmboBox.getSelectionModel().select(workspace.getEntities().indexOf(model.getTableB()));
        radioBtnPolyBN.setSelected(model.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY);
        radioBtnPolyB1.setSelected(model.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE);
        radioBtnObligationBMust.setSelected(model.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST);
        radioBtnObligationBCan.setSelected(model.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN);
        if (relation.isStrongRelation()) {
            tableAIsWeakChkBox.setSelected(model.getTableA().isWeakType());
            tableBIsWeakChkBox.setSelected(model.getTableB().isWeakType());
        }
        tableAEntityCmboBox.setDisable(true);
        tableBEntityCmboBox.setDisable(true);
        updateConnectionLine();
    }

    private void handleRelationLines() {
        exampleLine.startXProperty().bind(tableAexample.layoutXProperty().add(tableAexample.widthProperty()).add(tableAexample.getParent().layoutXProperty()));
        exampleLine.endXProperty().bind(tableBexample.layoutXProperty().add(tableBexample.getParent().layoutXProperty()));
        exampleLine.startYProperty().bind(tableAexample.layoutYProperty().add(tableAexample.heightProperty().divide(2)).add(tableAexample.getParent().layoutYProperty()));
        exampleLine.endYProperty().bind(tableAexample.layoutYProperty().add(tableAexample.heightProperty().divide(2)).add(tableAexample.getParent().layoutYProperty()));

        if (isStrongRelation()) {
            exampleLine.getStrokeDashArray().clear();
        } else {
            exampleLine.getStrokeDashArray().addAll(10.0, 8.0);
        }

        crowsFootTableA.bindCrowsFootView(windowAnchorPane, relation.getTableA_Cardinality(), relation.getTableA_Obligation());
        crowsFootTableB.bindCrowsFootView(windowAnchorPane, relation.getTableB_Cardinality(), relation.getTableB_Obligation());
    }

    private void rebuildEntityView() {
        handleCheckBoxesDisable();
        handleSaveBtnDisable();
        try {
            examplePane.getChildren().clear();

            ForeignKeyHandler.removeAllForeignKeys(relation);
            var warning = ForeignKeyHandler.addForeignKeys(relation, null);
            switch (warning) {
                case TRANSFORMATION:
                    GUIMethods.showInfo(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING);
                    break;
                case BOTH_TABLES_CAN_HAVE_FK:
                    var userInputResult = GUIMethods.showYesNoConfirmationDialog(BOSS_Strings.PRODUCT_NAME, BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_PROMPT_IF_TABLEA_GETS_FK);
                    if (userInputResult.isPresent() && userInputResult.get() == ButtonType.YES) {
                        ForeignKeyHandler.addForeignKeys(relation, relation.getTableA());
                    } else {
                        ForeignKeyHandler.addForeignKeys(relation, relation.getTableB());
                    }
                    GUIMethods.showWarning(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING);
                    break;
                case TRIGGER:
                    GUIMethods.showWarning(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING);
                    break;
            }

            //Determine if Tables are really weak
            ForeignKeyHandler.setWeakType(relation);

            tableAexample = EntityBuilder.buildEntity(relation.getTableA(), windowAnchorPane, workspace.getSelectionHandler);
            tableBexample = EntityBuilder.buildEntity(relation.getTableB(), windowAnchorPane, workspace.getSelectionHandler);
            tableAexample.setDisable(true);
            tableBexample.setDisable(true);
            tableAexample.heightProperty().addListener((observableValue, number, newValue) -> {
                var delta = (Double)newValue - previousAHeight;
                previousAHeight = (Double)newValue;
                var window = examplePane.getScene().getWindow();
                window.setHeight(window.getHeight() + delta);
            });
            tableBexample.heightProperty().addListener((observableValue, number, newValue) -> {
                var delta = (Double)newValue - previousBHeight;
                previousBHeight = (Double)newValue;
                var window = examplePane.getScene().getWindow();
                window.setHeight(window.getHeight() + delta);
            });
            examplePane.getChildren().addAll(tableAexample, tableBexample);

            if (crowsFootTableA != null)
                crowsFootTableA.unbindCrowsFootView(windowAnchorPane);
            crowsFootTableA = new CrowsFootShape.East(tableAexample, .5);

            if (crowsFootTableB != null)
                crowsFootTableB.unbindCrowsFootView(windowAnchorPane);
            crowsFootTableB = new CrowsFootShape.West(tableBexample, .5);

            handleRelationLines();
        } catch (IOException e) {
            GUIMethods.showError(EditRelationWindowController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    private void handleSaveBtnDisable() { //Future: EasyMode->Automatically create Transformation
        boolean disableSaving = false;
        if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY){
            disableSaving = true;
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                disableSaving = true;
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                disableSaving = true;
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY) {
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                disableSaving = true;
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                disableSaving = true;
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                disableSaving = true;
            }
        }

        saveBtn.setDisable(disableSaving);
    }

    private void handleCheckBoxesDisable() {
        tableAIsWeakChkBox.setDisable(false);
        tableBIsWeakChkBox.setDisable(false);

        if (relation.getTableA() == relation.getTableB()) {
            tableAIsWeakChkBox.setDisable(true);
            tableBIsWeakChkBox.setDisable(true);
            tableAIsWeakChkBox.setSelected(false);
            tableBIsWeakChkBox.setSelected(false);
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY) {
            tableAIsWeakChkBox.setDisable(true);
            tableBIsWeakChkBox.setDisable(true);
            tableAIsWeakChkBox.setSelected(false);
            tableBIsWeakChkBox.setSelected(false);
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                tableBIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
                tableBIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY) {
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST) {
                tableAIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
                tableBIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            if (relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                tableBIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
            tableAIsWeakChkBox.setDisable(true);
            tableBIsWeakChkBox.setDisable(true);
            tableAIsWeakChkBox.setSelected(false);
            tableBIsWeakChkBox.setSelected(false);
        }
    }

    public void loadProject() {
        this.workspace = Project.getCurrentProject();
        for (var entity : workspace.getEntities()) {
            tableAEntityCmboBox.getItems().add(entity.getName());
            tableBEntityCmboBox.getItems().add(entity.getName());
        }
        tableAEntityCmboBox.getSelectionModel().selectFirst();
        tableBEntityCmboBox.getSelectionModel().selectFirst();

        var firstSelectedEntity = workspace.getEntities().get(0);
        var secondSelectedEntity = firstSelectedEntity;

        if (workspace.getCurrentSelected() instanceof EntityView) {
            firstSelectedEntity = ((EntityView) workspace.getCurrentSelected()).getModel();
            tableAEntityCmboBox.getSelectionModel().select(workspace.getEntities().indexOf(firstSelectedEntity));
        }

        if (workspace.getCurrentSecondSelection() instanceof EntityView) {
            secondSelectedEntity = ((EntityView) workspace.getCurrentSecondSelection()).getModel();
            tableBEntityCmboBox.getSelectionModel().select(workspace.getEntities().indexOf(secondSelectedEntity));
        }

        if (workspace.getEntities().size() >= 1){
            relation = new Relation(
                    firstSelectedEntity,
                    secondSelectedEntity,
                    CrowsFootOptions.Cardinality.ONE,
                    CrowsFootOptions.Cardinality.ONE,
                    CrowsFootOptions.Obligation.CAN,
                    CrowsFootOptions.Obligation.CAN);

            updateConnectionLine();
        }
    }

    private boolean isStrongRelation() {
        var result = false;
        if (relation.getTableA().isWeakType()) {
            for (var fk : relation.getFkAttributesA()) {
                if (fk.isPrimary() && fk.getFkTable() == relation.getTableB()) {
                    result = true;
                    break;
                }
            }
        }
        if (!result && relation.getTableB().isWeakType()) {
            for (var fk : relation.getFkAttributesB()) {
                if (fk.isPrimary() && fk.getFkTable() == relation.getTableA()) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}
