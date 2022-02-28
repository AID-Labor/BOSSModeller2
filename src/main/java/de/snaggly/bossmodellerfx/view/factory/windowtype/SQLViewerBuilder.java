package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.adapter.DBProjectHolder;
import de.snaggly.bossmodellerfx.model.adapter.ProjectDataAdapter;
import de.snaggly.bossmodellerfx.view.controller.SQLViewerController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Builds a new SQLViewer window
 * @author Omar Emshani
 */
public class SQLViewerBuilder implements WindowFactory<DBProjectHolder, SQLViewerController> {
    private static final SQLViewerBuilder instance = new SQLViewerBuilder();

    private SQLViewerBuilder() {}

    @Override
    public Map.Entry<Scene, SQLViewerController> buildWindow(DBProjectHolder model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/SQLViewer.fxml"), BOSS_Strings.resourceBundle);
        var scene = new Scene(fxmlLoader.load());
        var controller = (SQLViewerController)(fxmlLoader.getController());
        if (model!=null)
            controller.loadModel(model);

        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, SQLViewerController> buildSQLViewer(Project project) throws IOException {
        return instance.buildWindow(ProjectDataAdapter.convertFXToLegacyModel(project.getEntities(), project.getRelations()));
    }
}
