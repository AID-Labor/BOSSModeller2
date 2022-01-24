package de.snaggly.bossmodellerfx.model.abstraction;

import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.view.ViewModel;

import java.util.ArrayList;

public class EntityAbstraction extends ViewModel implements AbstractedModel {
    private String name;
    private boolean isWeakType;

    public EntityAbstraction() {
        this(null, false);
    }

    public EntityAbstraction(String name, boolean isWeakType) {
        this(name, 0.0, 0.0, isWeakType);
    }

    public EntityAbstraction(String name, double xCoordinate, double yCoordinate, boolean isWeakType) {
        super(xCoordinate, yCoordinate);
        this.name = name;
        this.isWeakType = isWeakType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWeakType() {
        return isWeakType;
    }

    public void setWeakType(boolean weakType) {
        isWeakType = weakType;
    }
}
