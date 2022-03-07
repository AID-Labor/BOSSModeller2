package de.snaggly.bossmodellerfx.view.factory.nodetype;

import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.view.AttributeEditor;
import de.snaggly.bossmodellerfx.view.factory.ViewFactory;

import java.io.IOException;

/**
 * Builds the view to display a Comment on workbench.
 *
 * @author Omar Emshani
 */
public class AttributeEditorBuilder implements ViewFactory<Attribute, AttributeEditor> {
    private final static AttributeEditorBuilder instance = new AttributeEditorBuilder(); //Singleton

    private AttributeEditorBuilder() { }

    @Override
    public AttributeEditor buildView(Attribute model) throws IOException {
        return new AttributeEditor(model) {};
    }

    /**
     * Use this method to build a new view.
     * @param model Existing model to load on view. Can be null to create new model.
     * @return Returns the new view class.
     * @throws IOException
     */
    public static AttributeEditor buildAttributeEditor(Attribute model) throws IOException {
        return instance.buildView(model);
    }
}
