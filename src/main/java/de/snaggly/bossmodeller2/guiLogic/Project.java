package de.snaggly.bossmodeller2.guiLogic;

import de.snaggly.bossmodeller2.model.Comment;
import de.snaggly.bossmodeller2.model.DataModel;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.struct.relations.RelationViewStruct;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Project {
    private Pane workField;
    private Node currentSelected;
    private final ArrayList<Entity> entities;
    private final ArrayList<Comment> comments;
    private final ArrayList<Relation> relations;

    public Project(Pane workField) {
        this.entities = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.workField = workField;
        this.currentSelected = workField;
    }

    public Pane getWorkField() {
        return workField;
    }

    public void setWorkField(Pane workField) {
        this.workField = workField;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
        currentSelected = workField;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        currentSelected = workField;
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }

    public void addRelation(Relation relation) {
        this.relations.add(relation);
    }

    public void removeRelation(Relation relation) {
        this.relations.remove(relation);
        currentSelected = workField;
    }

    public synchronized Node getCurrentSelected() {
        return currentSelected;
    }

    public final SelectionHandler getSelectionHandler = this::setCurrentSelected;

    public synchronized void setCurrentSelected(Node newSelection) {
        if (this.currentSelected instanceof CustomNode) {
            ((CustomNode<? extends DataModel>) this.currentSelected).setDeFocusStyle();
        }
        if (newSelection instanceof CustomNode) {
            ((CustomNode<? extends DataModel>) newSelection).setFocusStyle();
        }

        this.currentSelected = newSelection;

        if (previouslySelectionRelation != null) {
            deHighlightRelation();
        }
    }

    private RelationViewStruct previouslySelectionRelation;
    public void highlightRelation(RelationViewStruct relationViewStruct) {
        relationViewStruct.crowsFootA.highlight();
        relationViewStruct.crowsFootB.highlight();
        relationViewStruct.line1.highlight();
        relationViewStruct.line2.highlight();
        relationViewStruct.line3.highlight();
        previouslySelectionRelation = relationViewStruct;
    }
    public void deHighlightRelation() {
        previouslySelectionRelation.crowsFootA.deHighlight();
        previouslySelectionRelation.crowsFootB.deHighlight();
        previouslySelectionRelation.line1.deHighlight();
        previouslySelectionRelation.line2.deHighlight();
        previouslySelectionRelation.line3.deHighlight();
        previouslySelectionRelation = null;
    }

    public String serializeToJson() {
        return new Gson().toJson(this);
    }

    public static Project deserializeFromJson(String json) {
        return new Gson().fromJson(json, Project.class);
    }
}
