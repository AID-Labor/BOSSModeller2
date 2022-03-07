package de.snaggly.bossmodellerfx.relation_logic;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Entity;
import javafx.scene.control.ButtonType;

import java.util.*;

/**
 * Static service class to manage the foreign Key according to the Entity-Relation-Model
 *
 * @author Omar Emshani
 */
public class ForeignKeyHandler {

    public enum WarningType {
        BOTH_TABLES_CAN_HAVE_FK,
        TRIGGER,
        TRANSFORMATION,
        NO_WARNING
    }

    /**
     * Adds ForeignKey(s) according to set relation.
     * To be used after WeakType, relation and cardinality have been set in relation.
     * @param preferredTable Only used for cases where it's unclear what table gets ForeignKey.
     *                       Will return BOTH_TABLES_CAN_HAVE_FK if not set and no FK will be added!
     */
    public static WarningType addForeignKeys(Relation relation, Entity preferredTable) {
        var result = WarningType.NO_WARNING;
        if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE
        && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            //1-1
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Can-Must -> FK to TabA
                performAddForeignKeys(relation, relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Must-Can -> FK to TabB
                performAddForeignKeys(relation, relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Must-Must -> User decides where FK goes to, or at the weak; Needs trigger
                if (relation.getTableA().isWeakType()) {
                    performAddForeignKeys(relation, relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
                }
                else if (relation.getTableB().isWeakType()) {
                    performAddForeignKeys(relation, relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
                }
                else {
                    if (preferredTable == null) {
                        result = WarningType.BOTH_TABLES_CAN_HAVE_FK;
                    }
                    else if (preferredTable == relation.getTableA()) {
                        ForeignKeyHandler.performAddForeignKeys(relation, relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, true);
                        result = WarningType.TRIGGER;
                    } else if (preferredTable == relation.getTableB()){
                        ForeignKeyHandler.performAddForeignKeys(relation, relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, true);
                        result = WarningType.TRIGGER;
                    }
                }
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE
            && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.MANY) {
            //1-N -> FK always goes to TabB
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Can-Can -> Can be transformed
                result = WarningType.TRANSFORMATION;
                performAddForeignKeys(relation, relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), false, false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.MUST
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                //Must-Can
                performAddForeignKeys(relation, relation.getTableB(), relation.getTableA(), relation.isStrongRelation(), true, false);
            }
        }
        else if (relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.MANY
            && relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE) {
            //N-1 -> FK always goes to TabA
            if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
            && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN) {
                result = WarningType.TRANSFORMATION;
                performAddForeignKeys(relation, relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), false, false);
            }
            else if (relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN
                    && relation.getTableB_Obligation() == CrowsFootOptions.Obligation.MUST) {
                //Can-Must
                performAddForeignKeys(relation, relation.getTableA(), relation.getTableB(), relation.isStrongRelation(), true, false);
            }
        }
        return result;
    }

    /**
     * Removes all ForeignKey(s) in relation.
     * @return A list of a deleted Attributes to keep track of
     */
    public static Map.Entry<LinkedList<Attribute>, LinkedList<Integer>> removeAllForeignKeys(Relation relation) {
        var fKeysResult = new LinkedList<Attribute>();
        var indexResult = new LinkedList<Integer>();
        //Removing all ForeignKeys in TabA that reference TabB
        var itemsToRemove = relation.getFkAttributesA();
        var tableAAttributes = relation.getTableA().getAttributes();
        for (int i=0; i<tableAAttributes.size(); i++) {
            var attribute = tableAAttributes.get(i);
            if (itemsToRemove.contains(attribute)) {
                fKeysResult.add(attribute);
                indexResult.add(i);
            }
        }
        relation.getTableA().getAttributes().removeAll(itemsToRemove);
        //var result = new LinkedList<>(itemsToRemove);
        relation.getFkAttributesA().clear();

        //Removing all ForeignKeys in TabB that reference TabA
        itemsToRemove.clear();
        itemsToRemove = relation.getFkAttributesB();
        var tableBAttributes = relation.getTableB().getAttributes();
        for (int i=0; i<tableBAttributes.size(); i++) {
            var attribute = tableBAttributes.get(i);
            if (itemsToRemove.contains(attribute)) {
                fKeysResult.add(attribute);
                indexResult.add(i);
            }
        }
        relation.getTableB().getAttributes().removeAll(itemsToRemove);
        relation.getFkAttributesB().clear();

        return Map.entry(fKeysResult, indexResult);
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
     * Once a ForeignKey is deleted, the UniqueList will leave a ghost behind.
     * This method will readjust the correct object to remove the ghost if the Key does not live anymore.
     * @param table The table which contains the UniqueList to be readjusted.
     * @param removedKeysList A list of all previously removed Key objects. "removeAllForeignKeys" will give that list.
     * @param newFks A list of the new Key objects.
     */
    public static void readjustForeignKeysInUniqueLists(Entity table, LinkedList<Attribute> removedKeysList, LinkedList<Attribute> newFks) {
        var emptyAttrCombo = new LinkedList<AttributeCombination>();
        for (var uniqueCombo : table.getUniqueCombination().getCombinations()) {
            var removedAttrs = new LinkedList<Attribute>();
            var attrCombo = uniqueCombo.getAttributes();
            for (int j=0; j<attrCombo.size(); j++) {
                var attr = attrCombo.get(j);
                var removedKeyIndex = removedKeysList.indexOf(attr);
                if (removedKeyIndex >= 0) {
                    var removedKey = removedKeysList.get(removedKeyIndex);
                    int newIndex = -1;
                    for (int i = 0; i<newFks.size(); i++) {
                        var newFk = newFks.get(i);
                        if (newFk.getFkTableColumn() == removedKey.getFkTableColumn()
                                && newFk.getFkTable() == removedKey.getFkTable()) {
                            newIndex = i;
                            break;
                        }
                    }
                    if (newIndex < 0) {
                        removedAttrs.add(attr);
                    }
                    else {
                        attrCombo.set(j, newFks.get(newIndex));
                    }
                }
            }
            attrCombo.removeAll(removedAttrs);
        }

        //Platting duplicates
        for (var uniqueCombo : table.getUniqueCombination().getCombinations()) {
            var attributeHashSet = new HashSet<Attribute>();
            var attributeDuplicates = new LinkedList<Integer>();
            for (int i = 0; i < uniqueCombo.getAttributes().size(); i++) {
                if (!attributeHashSet.add(uniqueCombo.getAttributes().get(i))) {
                    attributeDuplicates.add(i);
                }
            }
            for (int i = attributeDuplicates.size()-1; i>=0; i--) {
                uniqueCombo.getAttributes().remove(attributeDuplicates.get(i).intValue());
            }
            if (uniqueCombo.getAttributes().size() < 1) { //There is nothing left -> Causes SQL Error
                emptyAttrCombo.add(uniqueCombo);
            }
        }
        table.getUniqueCombination().getCombinations().removeAll(emptyAttrCombo);
    }

    /**
     * Once a ForeignKey is deleted and regenerated, all previous user data (Name, Check, Default) will be lost.
     * This method will reapply all user inputs in the new Fks.
     * @param removedKeysList A list of all previously removed Key objects. "removeAllForeignKeys" will give that list.
     * @param newFks A list of the new Key objects.
     * @returns A map of adapted new keys to the old keys.
     */
    public static HashMap<Attribute, Attribute> reApplyUserDataInNewForeignKeys(LinkedList<Attribute> removedKeysList, LinkedList<Attribute> newFks) {
        var result = new HashMap<Attribute, Attribute>();
        for (var removedKey : removedKeysList) {
            Attribute newKey = null;
            for (var fk : newFks) {
                var innerFkKey = fk;
                var innerRmKey = removedKey;
                //Check if root key matches. FKs can reference other just changed FKs. FkC->FkB->FkA->PK.
                while (newKey == null && innerFkKey.getFkTable() != null && innerRmKey.getFkTable() != null) {
                    if (innerFkKey.getFkTable() == innerRmKey.getFkTable() && innerFkKey.getFkTableColumn() == innerRmKey.getFkTableColumn()) {
                        newKey = fk;
                    }
                    innerFkKey = innerFkKey.getFkTableColumn();
                    innerRmKey = innerRmKey.getFkTableColumn();
                }
                if (newKey != null) {
                    newKey.setName(removedKey.getName());
                    newKey.setDefaultName(removedKey.getDefaultName());
                    newKey.setCheckName(removedKey.getCheckName());
                    result.put(removedKey, newKey);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Calculated the complexity of one relation.
     * The complexity is given by how many layers one foreign key references.
     */
    public static void setRelationComplexity(Relation relation) {
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
    }

    /**
     * Rearrange the previous ForeignKey order in one entity.
     * @param table Table to edit.
     * @param deletedKeys What keys used to exist in given entity.
     * @param newKeysMaps Map oldKey->newKey.
     * @param originalIndexes In which index did the Key lie?
     */
    public static void reOrderDeletedFKeys(Entity table, LinkedList<Attribute> deletedKeys, HashMap<Attribute, Attribute> newKeysMaps, LinkedList<Integer> originalIndexes) {
        for (int i=0; i<deletedKeys.size(); i++) {
            var newKey = newKeysMaps.get(deletedKeys.get(i));
            if (newKey == null)
                continue;
            var attributesList = table.getAttributes();
            var index = attributesList.indexOf(newKey);
            var originalIndex = originalIndexes.get(i);
            if (index >= 0) {
                if (originalIndex < attributesList.size()) {
                    attributesList.add(originalIndex, attributesList.remove(index));
                }
                else {
                    attributesList.add(attributesList.size()-1, attributesList.remove(index));
                }
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

    private static void performAddForeignKeys(Relation relation, Entity tableToAdd, Entity tableFKeyReferencesTo, boolean isStrongRelation, boolean isMust, boolean isUnique) {
        var primaryKeysInA = tableFKeyReferencesTo.getPrimaryKeys();
        var newFks = relation.getFkAttributes(tableToAdd);

        AttributeCombination primaryCombination = null;
        for (var combination : tableToAdd.getUniqueCombination().getCombinations()) {
            if (combination.isPrimaryCombination()) {
                primaryCombination = combination;
                break;
            }
        }

        for (var primaryKey : primaryKeysInA) {
            var fkName = primaryKey.getName();
            while (checkIfNameAlreadyExists(fkName, tableToAdd.getAttributes())) {
                fkName += "X";
            }
            var fk = new Attribute(
                    fkName,
                    primaryKey.getType(),
                    isStrongRelation,
                    isMust, //Must->NotNull
                    isUnique, //Unsure if ForeignKey are always Unique!
                    "",
                    "",
                    primaryKey,
                    tableFKeyReferencesTo
            );
            newFks.add(fk);

            //New Key has to be enlisted into primary unique list if is set to PrimaryKey
            if (fk.isPrimary()) {
                if (primaryCombination == null) {
                    primaryCombination = new AttributeCombination();
                    primaryCombination.setPrimaryCombination(true);
                    primaryCombination.setCombinationName("Primary-Combination");
                    primaryCombination.getAttributes().addAll(tableToAdd.getPrimaryKeys());
                    tableToAdd.getUniqueCombination().addCombination(primaryCombination);
                }
                primaryCombination.addAttribute(fk);
            }
        }
        tableToAdd.getAttributes().addAll(newFks);

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
