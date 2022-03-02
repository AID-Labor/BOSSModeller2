package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.serializable.*;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.WorkbenchPane;
import de.snaggly.bossmodellerfx.view.viewtypes.BiSelectable;
import de.snaggly.bossmodellerfx.view.viewtypes.Selectable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import com.google.gson.Gson;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.*;

/**
 * Service class to hold and manage project data.
 *
 * @author Omar Emshani
 */
public class Project {
    private final static ArrayList<Project> instances = new ArrayList<>(); //Multiton
    public static int activeProject = -1;
    private WorkbenchPane workField;
    private Node currentSelected;
    private Node secondSelection;

    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<Comment> comments = new ArrayList<>();
    private final ArrayList<Relation> relations = new ArrayList<>();

    public File activeFile;

    /**
     * This class currently holds the current pressed keyboard keys for KeyCombos.
     *
     * TODO: Check if moving out to own class makes sense.
     * @return current pressed keys
     */
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public Set<KeyCode> getPressedKeys() {
        return pressedKeys;
    }

    public void addPressedKey(KeyCode key) {
        pressedKeys.add(key);
    }

    public void removePressedKey(KeyCode key) {
        pressedKeys.remove(key);
    }

    /**
     * Creates a new Projects instance over the given Workbench.
     * Projects are being stored statically. Following Multiton pattern.
     *
     * A new instances will be pushed into project list.
     * If not called "getProject(int)" the newest Project can be retrieved by calling "getCurrentProject()"
     *
     * @param workField The WorkbenchField which the new project works on.
     * @return The new Project instance.
     */
    public static Project createNewProject(WorkbenchPane workField) {
        instances.add(new Project(workField));
        return instances.get(instances.size()-1);
    }

    private Project(WorkbenchPane workField) {
        this.workField = workField;
        this.currentSelected = workField;
    }

    public static Project getProject(int index) { //Multiton
        activeProject = index;
        return instances.get(activeProject);
    }

    public static Project getCurrentProject() {
        return instances.get(activeProject);
    }

    public static int getProjectsAmount() {
        return instances.size();
    }

    public static void removeProject(Project project) {
        instances.remove(project);
    }

    /**
     * Clears the current project. Might be obsolete if GarbageCollectors already clears all.
     */
    public void clear() {
        entities.clear();
        comments.clear();
        relations.clear();
        pressedKeys.clear();
        currentSelected = null;
        secondSelection = null;
        ((Pane)workField.getContent()).getChildren().clear();
    }

    public Pane getWorkField() {
        return workField.getContentWorkfield();
    }

    public WorkbenchPane getWorkFieldWrapper() {
        return workField;
    }

    public void setWorkField(WorkbenchPane workField) {
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
        var relationComplexityList = new LinkedList<Integer>();
        for (var fKeyA : relation.getFkAttributesA()) {
            var fk = fKeyA;
            var fkRelationComplexity = 0;
            while (fk.getFkTableColumn() != null) {
                fkRelationComplexity++;
                fk = fk.getFkTableColumn();
            }
            relationComplexityList.add(fkRelationComplexity);
        }
        for (var fKeyB : relation.getFkAttributesB()) {
            var fk = fKeyB;
            var fkRelationComplexity = 0;
            while (fk.getFkTableColumn() != null) {
                fkRelationComplexity++;
                fk = fk.getFkTableColumn();
            }
            relationComplexityList.add(fkRelationComplexity);
        }
        relation.relationComplexity = Collections.max(relationComplexityList);
        relations.add(relation);
        relations.sort(Comparator.comparingInt(r -> r.relationComplexity));
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

    /**
     * Project's Selection Handler.
     * Checks if selected Node is of Type Selectable and remembers its instance for when further interaction is required by Controller.
     *
     * Second selection will only work with Nodes of BiSelectable. Second selection is triggered by holding Control on Keyboard.
     */
    public synchronized void setCurrentSelected(Node newSelection) {
        if (this.secondSelection instanceof Selectable) {
            ((BiSelectable)this.secondSelection).setDeFocusStyle();
            this.secondSelection = null;
        }
        if (this.currentSelected instanceof Selectable) {
            if (pressedKeys.contains(KeyCode.CONTROL) && currentSelected instanceof BiSelectable && newSelection instanceof BiSelectable) {
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

    /**
     * Inner temporary class-structure for Serializable Project Data.
     * Used to save and open serialized JSON files.
     */
    private static class ProjectData {
        private ArrayList<SerializableEntity> entities;
        private ArrayList<SerializableComment> comments;
        private ArrayList<SerializableRelation> relations;
    }

    /**
     * Serializes this project into a JSON data format.
     * @return JSON Data
     */
    public String serializeToJson() {
        var serializableData = new ProjectData();

        serializableData.entities = new ArrayList<>();
        for (var entity : entities) {
            serializableData.entities.add(SerializableEntity.serializableEntity(entity));
        }
        SerializableAttribute.adjustFkOnSerialize(entities, serializableData.entities);

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

    /**
     * Deserializes an JSON String and retrieves a new instance of Project.
     * @param json JSON Data
     * @param workField The WorkbenchField which the new project works on.
     * @return The new Project instance.
     */
    public static Project deserializeFromJson(String json, WorkbenchPane workField) {
        var project = createNewProject(workField);
        var serializableData = new Gson().fromJson(json, ProjectData.class);
        for (var serEntities : serializableData.entities) {
            project.addEntity(SerializableEntity.deserializableEntity(serEntities));
        }
        SerializableAttribute.adjustFkOnDeserialize(serializableData.entities, project.entities);

        for (var serComment : serializableData.comments) {
            project.addComment(SerializableComment.deserializableComment(serComment));
        }
        for (var serRelation : serializableData.relations) {
            project.addRelation(SerializableRelation.deserializableRelation(serRelation, project.entities));
        }
        return project;
    }
}
