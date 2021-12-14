package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.*;
import de.snaggly.bossmodellerfx.model.serializable.*;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.viewtypes.BiSelectable;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import de.snaggly.bossmodellerfx.view.viewtypes.Selectable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Project {
    private Pane workField;
    private Node currentSelected;
    private Node secondSelection;

    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<Comment> comments = new ArrayList<>();
    private final ArrayList<Relation> relations = new ArrayList<>();
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public Project(Pane workField) {
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
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        currentSelected = workField;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        currentSelected = workField;
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }

    public void addRelation(Relation relation) {
        relations.add(relation);
    }

    public void removeRelation(Relation relation) {
        relations.remove(relation);
        currentSelected = workField;
    }

    public synchronized Node getCurrentSelected() {
        return currentSelected;
    }

    public synchronized Node getCurrentSecondSelection() {
        return secondSelection;
    }

    public final SelectionHandler getSelectionHandler = this::setCurrentSelected;

    public synchronized void setCurrentSelected(Node newSelection) {
        if (this.secondSelection instanceof Selectable) {
            ((BiSelectable)this.secondSelection).setDeFocusStyle();
            this.secondSelection = null;
        }
        if (this.currentSelected instanceof Selectable) {
            if (pressedKeys.contains(KeyCode.CONTROL) && newSelection instanceof BiSelectable) {
                ((BiSelectable) newSelection).setSecondFocusStyle();
                this.secondSelection = newSelection;
            } else {
                ((Selectable) this.currentSelected).setDeFocusStyle();
                if (newSelection instanceof Selectable) {
                    ((Selectable) newSelection).setFocusStyle();
                }
                this.currentSelected = newSelection;
            }
        }
        else {
            if (newSelection instanceof Selectable) {
                ((Selectable) newSelection).setFocusStyle();
            }
            this.currentSelected = newSelection;
        }
    }

    public void addPressedKey(KeyCode key) {
        pressedKeys.add(key);
    }

    public void removePressedKey(KeyCode key) {
        pressedKeys.remove(key);
    }

    private static class ProjectData {
        private ArrayList<SerializableEntity> entities;
        private ArrayList<SerializableComment> comments;
        private ArrayList<SerializableRelation> relations;
    }

    public String serializeToJson() {
        var serializableData = new ProjectData();

        serializableData.entities = new ArrayList<>();
        for (var entity : entities) {
            serializableData.entities.add(SerializableEntity.serializableEntity(entity));
        }

        serializableData.comments = new ArrayList<>();
        for (var comment : comments) {
            serializableData.comments.add(SerializableComment.serializableComment(comment));
        }

        serializableData.relations = new ArrayList<>();
        for (var relation : relations) {
            serializableData.relations.add(SerializableRelation.serializableRelation(relation, entities));
        }

        return new Gson().toJson(serializableData);
    }

    public static Project deserializeFromJson(String json, Pane workField) {
        var project = new Project(workField);
        var serializableData = new Gson().fromJson(json, ProjectData.class);
        for (var serEntities : serializableData.entities) {
            project.addEntity(SerializableEntity.deserializableEntity(serEntities));
        }
        for (var serComment : serializableData.comments) {
            project.addComment(SerializableComment.deserializableComment(serComment));
        }
        for (var serRelation : serializableData.relations) {
            project.addRelation(SerializableRelation.deserializableRelation(serRelation, project.entities));
        }
        return project;
    }
}
