package de.snaggly.bossmodeller2.view;

import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.view.controller.ViewController;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;

public class RelationViewNode extends CustomNode<Relation> {
    private final Relation model;
    public RelationLineView line1;
    public RelationLineView line2;
    public RelationLineView line3;
    public CrowsFootShape crowsFootA;
    public CrowsFootShape crowsFootB;

    public RelationViewNode(Relation model) {
        this.model = model;
    }

    @Override
    public Relation getModel() {
        return model;
    }

    @Override
    public ViewController<Relation> getController() {
        return null;
    }

    @Override
    public void setFocusStyle() {
        if (crowsFootA != null)
            crowsFootA.highlight();
        if (crowsFootB != null)
            crowsFootB.highlight();
        if (line1 != null)
            line1.highlight();
        if (line2 != null)
            line2.highlight();
        if (line3 != null)
            line3.highlight();
    }

    @Override
    public void setDeFocusStyle() {
        if (crowsFootA != null)
            crowsFootA.deHighlight();
        if (crowsFootB != null)
            crowsFootB.deHighlight();
        if (line1 != null)
            line1.deHighlight();
        if (line2 != null)
            line2.deHighlight();
        if (line3 != null)
            line3.deHighlight();
    }
}
