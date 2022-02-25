package de.snaggly.bossmodellerfx.relation_logic;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Entity;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Static service class to manage the foreign Key according to the Entity-Relation-Model
 *
 * @author Omar Emshani
 */
public class ForeignKeyHandler {

    /**
     * Adds ForeignKey(s) according to set relation.
     * To be used after WeakType, relation and cardinality have been set in relation.
     */
    public static void addForeignKeys(Relation relation) {
        if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE
        && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            //1-1
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Can-Must -> FK to TabA
                performAddForeignKeys(relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Must-Can -> FK to TabB
                performAddForeignKeys(relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Must-Must -> User decides where FK goes to, or at the weak; Needs trigger
                if (relation.getTableA().isWeakType()) {
                    performAddForeignKeys(relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
                }
                else if (relation.getTableB().isWeakType()) {
                    performAddForeignKeys(relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
                }
                else {
                    var userInputResult = GUIMethods.showYesNoConfirmationDialog(BOSS_Strings.PRODUCT_NAME, BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_PROMPT_IF_TABLEA_GETS_FK);
                    if (userInputResult.isPresent() && userInputResult.get() == ButtonType.YES) {
                        performAddForeignKeys(relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
                    } else {
                        performAddForeignKeys(relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
                    }
                    GUIMethods.showWarning(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRIGGER_WARNING);
                }
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE
            && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY) {
            //1-N -> FK always goes to TabB
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Can-Can -> Can be transformed
                GUIMethods.showInfo(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING);
                performAddForeignKeys(relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), false, false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Must-Can
                performAddForeignKeys(relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, false);
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY
            && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            //N-1 -> FK always goes to TabA
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                GUIMethods.showInfo(BOSS_Strings.RELATION_EDITOR, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING_HEADER, BOSS_Strings.RELATION_EDITOR_TRANSFORMATION_WARNING);
                performAddForeignKeys(relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), false, false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Can-Must
                performAddForeignKeys(relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, false);
            }
        }
    }

    /**
     * Removes all ForeignKey(s) in relation.
     */
    public static void removeAllForeignKeys(Relation relation) {
        //Removing all ForeignKeys in TabA that reference TabB
        var itemsToRemove = new LinkedList<Attribute>();
        for (var attributeA : relation.getTableA().getAttributes()) {
            var fTable = attributeA.getFkTable();
            if (fTable == null)
                continue;
            if (fTable != relation.getTableB())
                continue;
            itemsToRemove.add(attributeA);
        }
        relation.getTableA().getAttributes().removeAll(itemsToRemove);

        //Removing all ForeignKeys in TabB that reference TabA
        itemsToRemove.clear();
        for (var attributeB : relation.getTableB().getAttributes()) {
            var fTable = attributeB.getFkTable();
            if (fTable == null)
                continue;
            if (fTable != relation.getTableA())
                continue;
            itemsToRemove.add(attributeB);
        }
        relation.getTableB().getAttributes().removeAll(itemsToRemove);
    }

    /**
     * Sets the entities weak type in relation
     */
    public static void setWeakType(Relation relation) {
        relation.getTableA().setWeakType(false);
        relation.getTableB().setWeakType(false);
        for (var fk : relation.getTableA().getAttributes()) {
            if (fk.getFkTable() == null)
                continue;
            if (fk.isPrimary()) {
                relation.getTableA().setWeakType(true);
                break;
            }
        }
        for (var fk : relation.getTableB().getAttributes()) {
            if (fk.getFkTable() == null)
                continue;
            if (fk.isPrimary()) {
                relation.getTableB().setWeakType(true);
                break;
            }
        }
    }

    /**
     * Checks if current relation is strong
     */
    private boolean isStrongRelation(Relation relation) {
        var result = false;
        if (relation.getTableA().isWeakType()) {
            for (var fk : relation.getFkAttributesA()) {
                if (fk.isPrimary() && fk.getFkTable() == relation.getTableB()) {
                    result = true;
                    break;
                }
            }
        }
        else if (relation.getTableB().isWeakType()) {
            for (var fk : relation.getFkAttributesB()) {
                if (fk.isPrimary() && fk.getFkTable() == relation.getTableA()) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    private static void performAddForeignKeys(Entity tableToAdd, Entity tableFKeyReferencesTo, boolean isStrongRelation, boolean isMust, boolean isUnique) {
        var primaryKeysInA = tableFKeyReferencesTo.getPrimaryKeys();
        for (var primaryKey : primaryKeysInA) {
            var fkName = primaryKey.getName();
            while (checkIfNameAlreadyExists(fkName, tableToAdd.getAttributes())) {
                fkName += "X";
            }
            tableToAdd.addAttribute(new Attribute(
                    fkName,
                    primaryKey.getType(),
                    isStrongRelation,
                    isMust, //Must->NotNull
                    isUnique, //Unsure if ForeignKey are always Unique!
                    primaryKey.getCheckName(),
                    primaryKey.getDefaultName(),
                    primaryKey,
                    tableFKeyReferencesTo
            ));
        }
    }

    private static boolean checkIfNameAlreadyExists(String suggestedName, ArrayList<Attribute> attributes) {
        for (var attribute : attributes) {
            if (attribute.getName().equals(suggestedName)) {
                return true;
            }
        }
        return false;
    }
}
