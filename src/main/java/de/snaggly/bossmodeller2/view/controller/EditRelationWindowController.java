package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.guiLogic.*;
import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.view.CrowsFootShape;
import de.snaggly.bossmodeller2.view.EntityView;
import de.snaggly.bossmodeller2.view.factory.nodetype.EntityBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.ArrayList;

public class EditRelationWindowController implements ViewController<Relation> {
    private Relation relation;
    private Project workspace;
    private CrowsFootShape crowsFootTableA;
    private CrowsFootShape crowsFootTableB;
    private Entity entityAModelReference;
    private Entity entityBModelReference;

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
    private EntityView tableAexample;
    private EntityView tableBexample;
    private final Line exampleLine = new Line();

    @FXML
    private void initialize(){
        exampleLine.setStrokeWidth(3);
        windowAnchorPane.getChildren().add(exampleLine);
    }

    @FXML
    private void showInfo() {
        String infoText = "Ein ";
        infoText += relation.getTableA().getName();

        if (radioBtnObligationACan.isSelected()) {
            infoText += " kann";
        } else {
            infoText += " muss";
        }

        if (radioBtnPolyB1.isSelected()) {
            infoText += " einen ";
        } else {
            infoText += " mehrere ";
        }

        infoText += relation.getTableB().getName();
        infoText += " haben.\n";

        infoText += "Ein ";
        infoText += relation.getTableB().getName();

        if (radioBtnObligationBCan.isSelected()) {
            infoText += " kann";
        } else {
            infoText += " muss";
        }

        if (radioBtnPolyA1.isSelected()) {
            infoText += " einen ";
        } else {
            infoText += " mehrere ";
        }

        infoText += relation.getTableA().getName();
        infoText += " haben.";

        GUIMethods.showInfo("Relation Information", "", infoText);
    }

    @FXML
    private void closeWindow(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void save(ActionEvent actionEvent) {
        entityAModelReference.setWeakType(relation.getTableA().isWeakType());
        entityAModelReference.setAttributes(relation.getTableA().getAttributes());
        entityBModelReference.setWeakType(relation.getTableB().isWeakType());
        entityBModelReference.setAttributes(relation.getTableB().getAttributes());
        relation.setTableA(entityAModelReference);
        relation.setTableB(entityBModelReference);
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
        var entityAttributes = new ArrayList<>(entityAModelReference.getAttributes());
        relation.setTableA(new Entity(
                entityAModelReference.getName(),
                entityAModelReference.getXCoordinate(),
                entityAModelReference.getYCoordinate(),
                entityAttributes,
                entityAModelReference.isWeakType()
        ));

        if (radioBtnPolyAN.isSelected())
            relation.setTableA_Cardinality(Relation.Cardinality.MANY);
        else
            relation.setTableA_Cardinality(Relation.Cardinality.ONE);
        if (radioBtnObligationAMust.isSelected())
            relation.setTableA_Obligation(Relation.Obligation.MUST);
        else
            relation.setTableA_Obligation(Relation.Obligation.CAN);
        relation.getTableA().setWeakType(tableAIsWeakChkBox.isSelected());

        entityBModelReference = workspace.getEntities().get(tableBEntityCmboBox.getSelectionModel().getSelectedIndex());
        entityAttributes = new ArrayList<>(entityBModelReference.getAttributes());
        relation.setTableB(new Entity(
                entityBModelReference.getName(),
                entityBModelReference.getXCoordinate(),
                entityBModelReference.getYCoordinate(),
                entityAttributes,
                entityBModelReference.isWeakType()
        ));

        if (radioBtnPolyBN.isSelected())
            relation.setTableB_Cardinality(Relation.Cardinality.MANY);
        else
            relation.setTableB_Cardinality(Relation.Cardinality.ONE);
        if (radioBtnObligationBMust.isSelected())
            relation.setTableB_Obligation(Relation.Obligation.MUST);
        else
            relation.setTableB_Obligation(Relation.Obligation.CAN);
        relation.getTableB().setWeakType(tableBIsWeakChkBox.isSelected());

        rebuildEntityView();
    }

    @Override
    public void loadModel(Relation model) {
        this.relation = model;
    }

    private void handleRelationLines() {
        exampleLine.startXProperty().bind(tableAexample.layoutXProperty().add(tableAexample.widthProperty()).add(tableAexample.getParent().layoutXProperty()));
        exampleLine.endXProperty().bind(tableBexample.layoutXProperty().add(tableBexample.getParent().layoutXProperty()));
        exampleLine.startYProperty().bind(tableAexample.layoutYProperty().add(tableAexample.heightProperty().divide(2)).add(tableAexample.getParent().layoutYProperty()));
        exampleLine.endYProperty().bind(tableAexample.layoutYProperty().add(tableAexample.heightProperty().divide(2)).add(tableAexample.getParent().layoutYProperty()));

        if (relation.getTableA().isWeakType() || relation.getTableB().isWeakType()) {
            exampleLine.getStrokeDashArray().clear();
        } else {
            exampleLine.getStrokeDashArray().addAll(3.0, 8.0);
        }

        crowsFootTableA.bindCrowsFootView(windowAnchorPane, relation.getTableA_Cardinality(), relation.getTableA_Obligation());
        crowsFootTableB.bindCrowsFootView(windowAnchorPane, relation.getTableB_Cardinality(), relation.getTableB_Obligation());
    };

    private void rebuildEntityView() {
        handleCheckBoxesDisable();

        try {
            examplePane.getChildren().clear();

            if (relation.getTableA_Cardinality() == Relation.Cardinality.MANY) {
                var fkAttribute = relation.getTableA().getPrimaryKey();
                relation.getTableB().addAttribute(new Attribute(
                        fkAttribute.getName(),
                        fkAttribute.getType(),
                        false,
                        fkAttribute.isNonNull(),
                        fkAttribute.isUnique(),
                        fkAttribute.getCheckName(),
                        fkAttribute.getDefaultName(),
                        relation.getTableA().getName(),
                        fkAttribute.getName()
                ));
            }
            if (relation.getTableB_Cardinality() == Relation.Cardinality.MANY) {
                var fkAttribute = relation.getTableB().getPrimaryKey();
                relation.getTableA().addAttribute(new Attribute(
                        fkAttribute.getName(),
                        fkAttribute.getType(),
                        false,
                        fkAttribute.isNonNull(),
                        fkAttribute.isUnique(),
                        fkAttribute.getCheckName(),
                        fkAttribute.getDefaultName(),
                        relation.getTableB().getName(),
                        fkAttribute.getName()
                ));
            }

            tableAexample = EntityBuilder.buildEntity(relation.getTableA(), windowAnchorPane, workspace.getSelectionHandler);
            tableBexample = EntityBuilder.buildEntity(relation.getTableB(), windowAnchorPane, workspace.getSelectionHandler);
            tableAexample.setDisable(true);
            tableBexample.setDisable(true);
            examplePane.getChildren().addAll(tableAexample, tableBexample);

            if (crowsFootTableA != null)
                crowsFootTableA.unbindCrowsFootView(windowAnchorPane);
            crowsFootTableA = new CrowsFootShape.East(tableAexample, .5);

            if (crowsFootTableB != null)
                crowsFootTableB.unbindCrowsFootView(windowAnchorPane);
            crowsFootTableB = new CrowsFootShape.West(tableBexample, .5);

            handleRelationLines();
        } catch (IOException e) {
            GUIMethods.showError(EditRelationWindowController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
        }
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
        else if (relation.getTableA_Cardinality() == Relation.Cardinality.MANY && relation.getTableB_Cardinality() == Relation.Cardinality.MANY) {
            tableAIsWeakChkBox.setDisable(true);
            tableBIsWeakChkBox.setDisable(true);
            tableAIsWeakChkBox.setSelected(false);
            tableBIsWeakChkBox.setSelected(false);
        }
        else if (relation.getTableA_Cardinality() == Relation.Cardinality.ONE && relation.getTableB_Cardinality() == Relation.Cardinality.ONE) {
            if (relation.getTableA_Obligation() == Relation.Obligation.CAN && relation.getTableB_Obligation() == Relation.Obligation.MUST) {
                tableBIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableA_Obligation() == Relation.Obligation.CAN && relation.getTableB_Obligation() == Relation.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
                tableBIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableA_Obligation() == Relation.Obligation.MUST && relation.getTableB_Obligation() == Relation.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Cardinality() == Relation.Cardinality.ONE && relation.getTableB_Cardinality() == Relation.Cardinality.MANY) {
            if (relation.getTableA_Obligation() == Relation.Obligation.MUST) {
                tableAIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
            }
            else if (relation.getTableB_Obligation() == Relation.Obligation.CAN) {
                tableAIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setDisable(true);
                tableAIsWeakChkBox.setSelected(false);
                tableBIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Cardinality() == Relation.Cardinality.MANY && relation.getTableB_Cardinality() == Relation.Cardinality.ONE) {
            if (relation.getTableB_Obligation() == Relation.Obligation.MUST) {
                tableBIsWeakChkBox.setDisable(true);
                tableBIsWeakChkBox.setSelected(false);
            }
        }
        else if (relation.getTableA_Obligation() == Relation.Obligation.CAN && relation.getTableB_Obligation() == Relation.Obligation.CAN) {
            tableAIsWeakChkBox.setDisable(true);
            tableBIsWeakChkBox.setDisable(true);
            tableAIsWeakChkBox.setSelected(false);
            tableBIsWeakChkBox.setSelected(false);
        }
    }

    public void loadModel(Project project) {
        this.workspace = project;
        for (var entity : workspace.getEntities()) {
            tableAEntityCmboBox.getItems().add(entity.getName());
            tableBEntityCmboBox.getItems().add(entity.getName());
        }
        tableAEntityCmboBox.getSelectionModel().selectFirst();
        tableBEntityCmboBox.getSelectionModel().selectFirst();

        var selectedEntity = workspace.getEntities().get(0);

        if (workspace.getCurrentSelected() instanceof EntityView) {
            selectedEntity = ((EntityView) workspace.getCurrentSelected()).getModel();
            tableAEntityCmboBox.getSelectionModel().select(workspace.getEntities().indexOf(selectedEntity));
        }

        if (workspace.getEntities().size() >= 1){
            relation = new Relation("simple",
                    selectedEntity,
                    workspace.getEntities().get(0),
                    Relation.Cardinality.ONE,
                    Relation.Cardinality.ONE,
                    Relation.Obligation.CAN,
                    Relation.Obligation.CAN);

            updateConnectionLine();
        }
    }

    public void loadModel(Relation model, Project project) {
        loadModel(project);
        loadModel(model);
    }
}
