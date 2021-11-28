package de.snaggly.bossmodellerfx.view.factory.nodetype;

import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.view.AttributeEditor;
import de.snaggly.bossmodellerfx.view.factory.ViewFactory;

import java.io.IOException;

public class AttributeEditorBuilder implements ViewFactory<Attribute, AttributeEditor> {
    private static AttributeEditorBuilder instance;

    private AttributeEditorBuilder() { }

    @Override
    public AttributeEditor buildView(Attribute model) throws IOException {
        return new AttributeEditor(model) {};
    }

    public static AttributeEditor buildAttributeEditor() throws IOException {
        if (instance == null)
            instance = new AttributeEditorBuilder();
        return instance.buildView(null);
    }

    public static AttributeEditor buildAttributeEditor(Attribute model) throws IOException {
        if (instance == null)
            instance = new AttributeEditorBuilder();
        return instance.buildView(model);
    }
}
