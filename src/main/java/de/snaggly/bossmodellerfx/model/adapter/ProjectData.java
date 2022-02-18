package de.snaggly.bossmodellerfx.model.adapter;

import de.bossmodeler.logicalLayer.elements.DBColumn;
import de.bossmodeler.logicalLayer.elements.DBRelation;
import de.bossmodeler.logicalLayer.elements.DBTable;
import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ProjectData implements BOSSModel {
    public String projectName;
    public ArrayList<Entity> entities;
    public ArrayList<Relation> relations;

    public static ProjectData convertLegacyToFXModel(String name, LinkedList<DBTable> dbTables, LinkedList<DBRelation> dbRelations) {
        var projectData = new ProjectData();
        projectData.projectName = name;
        projectData.entities = new ArrayList<>();
        projectData.relations = new ArrayList<>();

        //To map old dataset to new
        var entityMap = new HashMap<DBTable, Entity>();
        var attributeMap = new HashMap<DBColumn, Attribute>();

        for (var dbTable : dbTables) {
            //Create new entity
            var entity = new Entity();
            entity.setName(dbTable.getdBTName());
            entity.setWeakType(dbTable.isdBTWeakEntity());

            for (var dbColumn : dbTable.getdBTColumns()) {
                //Create new attribute for current entity
                var attribute = new Attribute();
                attribute.setName(dbColumn.getdBCName());
                attribute.setType(dbColumn.getdBCType());
                attribute.setCheckName(dbColumn.getdBCCheck());
                attribute.setNonNull(dbColumn.isdBCNotNull());
                attribute.setDefaultName(dbColumn.getdBCDefault());

                attributeMap.put(dbColumn, attribute);
                entity.addAttribute(attribute);
            }

            //Map unique attributes
            for (var dbColumn : dbTable.getdBTUniqueList()) {
                var attribute = attributeMap.get(dbColumn);
                if (attribute != null) {
                    attribute.setUnique(true);
                }
            }

            //Map primary attributes
            for (var dbColumn : dbTable.getdBTPKeyList()) {
                var attribute = attributeMap.get(dbColumn);
                if (attribute != null) {
                    attribute.setPrimary(true);
                }
            }

            var uniqueCombo = new UniqueCombination();
            for (var legacyUniqueCombo : dbTable.getuniqueCombinations()) {
                //Create new UniqueCombination for current entity
                var attributeCombo = new AttributeCombination();
                attributeCombo.setCombinationName(legacyUniqueCombo.getShortForm());
                for (var dbColumn : legacyUniqueCombo.getColumns()) {
                    attributeCombo.addAttribute(attributeMap.get(dbColumn));
                }
                uniqueCombo.addCombination(attributeCombo);
            }
            entity.setUniqueCombination(uniqueCombo);

            entityMap.put(dbTable, entity);
            projectData.entities.add(entity);
        }

        for (var dbRelation : dbRelations) {
            //Create new relation
            var tableA = entityMap.get(dbRelation.getTableA());
            var tableB = entityMap.get(dbRelation.getTableB());
            var relation = new Relation(
                    tableA,
                    tableB,
                    dbRelation.getCardinalityA() == 0 ? CrowsFootOptions.Cardinality.MANY : CrowsFootOptions.Cardinality.ONE,
                    dbRelation.getCardinalityB() == 0 ? CrowsFootOptions.Cardinality.MANY : CrowsFootOptions.Cardinality.ONE,
                    dbRelation.getRelationTypeA() == 0 ? CrowsFootOptions.Obligation.MUST : CrowsFootOptions.Obligation.CAN,
                    dbRelation.getRelationTypeB() == 0 ? CrowsFootOptions.Obligation.MUST : CrowsFootOptions.Obligation.CAN
            );

            //Adjust ForeignKeys in TableA
            for (var legacyForeignKey : dbRelation.getTableA().getdBTFKeyList()) {
                var newForeignKey = attributeMap.get(legacyForeignKey);
                newForeignKey.setFkTable(tableB);
                for (var tableAttribute : tableB.getAttributes()) {
                    if (tableAttribute.getName().equals(legacyForeignKey.getdBCFKRefName())) {
                        newForeignKey.setFkTableColumn(tableAttribute);
                        break;
                    }
                }
            }

            //Adjust ForeignKeys in TableB
            for (var legacyForeignKey : dbRelation.getTableB().getdBTFKeyList()) {
                var newForeignKey = attributeMap.get(legacyForeignKey);
                newForeignKey.setFkTable(tableA);
                for (var tableAttribute : tableA.getAttributes()) {
                    if (tableAttribute.getName().equals(legacyForeignKey.getdBCFKRefName())) {
                        newForeignKey.setFkTableColumn(tableAttribute);
                        break;
                    }
                }
            }

            projectData.relations.add(relation);
        }

        return projectData;
    }

    public static DBProjectHolder convertFXToLegacyModel(ArrayList<Entity> entities, ArrayList<Relation> relations) {
        var projectHolder = new DBProjectHolder();

        //Map new dataset to old
        var columnMap = new HashMap<Attribute, DBColumn>();
        var tableMap = new HashMap<Entity, DBTable>();

        //Build Tables
        for (var entity : entities) {
            var dbTable = new DBTable(entity.getName());
            dbTable.setdBTWeakEntity(entity.isWeakType());

            //Build Columns with Primary,Unique,Foreign-List
            for (var attribute : entity.getAttributes()) {
                var dbColumn = new DBColumn(
                        attribute.getName(),
                        attribute.getType()
                );
                dbColumn.setdBCCheck(attribute.getCheckName());
                dbColumn.setdBCNotNull(attribute.isNonNull());
                dbColumn.setdBCDefault(attribute.getDefaultName());
                dbTable.addColumn(dbColumn);
                if (attribute.isUnique()) {
                    dbTable.addUniqueList(dbColumn);
                }
                if (attribute.isPrimary()) {
                    dbTable.addPKey(dbColumn);
                }
                if (attribute.getFkTable() != null) {
                    dbColumn.setdBCFKRefName(attribute.getFkTableColumn().getName());
                    dbColumn.setdBCFKRefTableName(attribute.getFkTable().getName());
                    dbColumn.setdBCFKConstraintName(dbTable.getdBTName() + "_" + attribute.getName() + "_fkey");
                    dbTable.addFKey(dbColumn);
                }

                columnMap.put(attribute, dbColumn);
            }

            //Build UniqueList
            for (var attributeCombination : entity.getUniqueCombination().getCombinations()) {
                var columnList = new LinkedList<DBColumn>();
                for (var attribute : attributeCombination.getAttributes()) {
                    columnList.add(columnMap.get(attribute));
                }
                dbTable.addUniqueCombination(
                        new de.bossmodeler.logicalLayer.elements.UniqueCombination(columnList, attributeCombination.getCombinationName())
                );
            }

            tableMap.put(entity, dbTable);
            projectHolder.addDbTable(dbTable);
        }

        //Build Relations
        for (var relation : relations) {
            //Set Tables
            var dbRelation = new DBRelation(
                    tableMap.get(relation.getTableA()),
                    tableMap.get(relation.getTableB())
            );

            //Set Cardinality and Relation
            dbRelation.setCardinalityA(relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE ? 1 : 0);
            dbRelation.setCardinalityB(relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE ? 1 : 0);
            dbRelation.setRelationTypeA(relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN ? 1 : 0);
            dbRelation.setRelationTypeB(relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN ? 1 : 0);

            //Set strong table
            if (relation.getTableA().isWeakType())
                dbRelation.setStrongTable(dbRelation.getTableB());
            else if (relation.getTableB().isWeakType())
                dbRelation.setStrongTable(dbRelation.getTableA());

            //Set foreign Table and Keys
            var fkColumns = new LinkedList<DBColumn>();
            if (dbRelation.getCardinalityA() > 0) {
                dbRelation.setFkTable(dbRelation.getTableA());
                fkColumns.addAll(dbRelation.getTableA().getdBTFKeyList());
            }
            else if (dbRelation.getCardinalityB() > 0) {
                dbRelation.setFkTable(dbRelation.getTableB());
                fkColumns.addAll(dbRelation.getTableB().getdBTFKeyList());
            }
            dbRelation.setFkColumn(fkColumns);

            projectHolder.addDbRelation(dbRelation);
        }

        return projectHolder;
    }
}
