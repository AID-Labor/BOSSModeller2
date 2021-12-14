package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.view.ResizableDataModel;
import de.snaggly.bossmodellerfx.model.view.ViewModel;
import de.snaggly.bossmodellerfx.view.EntityView;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import de.snaggly.bossmodellerfx.view.viewtypes.Draggable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class GUIMethods {
    //Bei Design sollen alle universell gleich behandelt werden bei einem Resize.
    //Setzt den Fokus auf ausgewähltes Objekt.
    private static final double NULLZONE = 8.0;
    private static boolean isDragging = false;

    public static <T extends ViewModel> void enableDrag(CustomNode<T> nodeToDrag, Region parent, T model) {
        nodeToDrag.addEventHandler(MouseEvent.ANY, new EventHandler<>() {
            private double lastMouseX = 0, lastMouseY = 0;

            @Override
            public void handle(MouseEvent event) {
                if (MouseEvent.MOUSE_MOVED == event.getEventType()) {
                    nodeToDrag.setCursor(Cursor.HAND);
                    event.consume();
                } else if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
                    if (nodeToDrag.contains(event.getX(), event.getY())) {
                        this.lastMouseX = event.getSceneX();
                        this.lastMouseY = event.getSceneY();
                        event.consume();
                    }
                } else if (MouseEvent.MOUSE_DRAGGED == event.getEventType()) {
                    if (isDragging)
                        return;
                    nodeToDrag.setCursor(Cursor.MOVE);
                    final double deltaX = event.getSceneX() - this.lastMouseX;
                    final double deltaY = event.getSceneY() - this.lastMouseY;

                    final double translateX = nodeToDrag.getLayoutX() + deltaX;
                    final double translateY = nodeToDrag.getLayoutY() + deltaY;

                    if (translateX >= 0 && translateX < parent.getWidth() - nodeToDrag.getWidth()) {
                        nodeToDrag.setLayoutX(translateX);
                        model.setXCoordinate(translateX);
                    }
                    if (translateY >= 0 && translateY < parent.getHeight() - nodeToDrag.getHeight()) {
                        nodeToDrag.setLayoutY(translateY);
                        model.setYCoordinate(translateY);
                    }

                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();

                    event.consume();
                } else if (MouseEvent.MOUSE_RELEASED == event.getEventType()) {
                    nodeToDrag.setCursor(Cursor.HAND);
                    event.consume();
                }
            }
        });
    }

    public static <T extends ViewModel> void enableClick(CustomNode<T> nodeToDrag, SelectionHandler workspace) {
        nodeToDrag.addEventFilter(MouseEvent.ANY, mouseEvent -> {
            if (MouseEvent.MOUSE_CLICKED == mouseEvent.getEventType()
                    || MouseEvent.MOUSE_PRESSED == mouseEvent.getEventType()) {
                nodeToDrag.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(3,158,211,0.8), 17, 0, 0, 0);");
                workspace.setCurrentSelected(nodeToDrag);
            }
        });
    }

    public static <T extends ResizableDataModel> void enableResizeable(CustomNode<T> nodeToResize, T model) {
        nodeToResize.addEventHandler(MouseEvent.ANY, mouseEvent -> {
            if (MouseEvent.MOUSE_MOVED == mouseEvent.getEventType()) {
                if (isInDragPosition(mouseEvent, nodeToResize)) {
                    nodeToResize.setCursor(Cursor.SE_RESIZE);
                }
                mouseEvent.consume();
            } else if (MouseEvent.MOUSE_PRESSED == mouseEvent.getEventType()) {
                if (isInDragPosition(mouseEvent, nodeToResize)) {
                    if (nodeToResize instanceof Draggable) {
                        isDragging = true;
                    }
                }
                mouseEvent.consume();
            } else if (MouseEvent.MOUSE_DRAGGED == mouseEvent.getEventType()) {
                if (isDragging) {
                    nodeToResize.setMinWidth(mouseEvent.getX());
                    nodeToResize.setMinHeight(mouseEvent.getY());
                    model.setWidth(mouseEvent.getX());
                    model.setHeight(mouseEvent.getY());
                }
                mouseEvent.consume();
            } else if (MouseEvent.MOUSE_RELEASED == mouseEvent.getEventType()) {
                isDragging = false;
                mouseEvent.consume();
            }
        });
    }

    public static ChangeListener<Number> bindEntityToRelationLineHandler(EntityView entity, RelationLineHandler handler) {
        var result = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                handler.handleRelationLines();
            }
        };

        entity.layoutXProperty().addListener(result);
        entity.layoutYProperty().addListener(result);
        entity.widthProperty().addListener(result);
        entity.heightProperty().addListener(result);

        return result;
    }

    private static boolean isInDragPosition(MouseEvent mouseEvent, CustomNode<?> nodeToResize) {
        return (mouseEvent.getX() >= nodeToResize.getWidth() - NULLZONE
                && mouseEvent.getY() >= nodeToResize.getHeight() - NULLZONE);
    }

    public static void closeWindow(Event source) {
        ((Stage) ((Button) source.getSource()).getScene().getWindow()).close();
    }

    private static void showAlert(Alert.AlertType type, String origin, String header, String info) {
        var alert = new Alert(type);
        alert.setResizable(true);
        alert.setTitle(origin);
        alert.setHeaderText(header);
        alert.getDialogPane().setContent(new Label(info));
        alert.showAndWait();
    }

    public static void showInfo(String origin, String header, String info) {
        showAlert(Alert.AlertType.INFORMATION, origin, header, info);
    }

    public static void showWarning(String origin, String header, String info) {
        showAlert(Alert.AlertType.WARNING, origin, header, info);
    }

    public static void showError(String origin, String header, String info) {
        showAlert(Alert.AlertType.ERROR, origin, header, info);
    }

    public static File showJSONFileSaveDialog(String title, Window window) {
        return getJSONFileChooser(title, window).showSaveDialog(window);
    }

    public static File showJSONFileOpenDialog(String title, Window window) {
        return getJSONFileChooser(title, window).showOpenDialog(window);
    }

    private static FileChooser getJSONFileChooser(String title, Window window) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName("BOSS-Project.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Datei", "*.json"));
        return fileChooser;
    }

    public static File showPNGFileSaveDialog(String title, Window window) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName("BOSS-Project.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG-Datei", "*.png"));
        return fileChooser.showSaveDialog(window);
    }
}
