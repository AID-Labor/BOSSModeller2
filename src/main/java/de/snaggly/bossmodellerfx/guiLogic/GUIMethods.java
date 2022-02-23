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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Optional;

/**
 * A collection of static methods for GUI operations.
 * @author Omar Emshani
 */
public class GUIMethods {
    /**
     * This constant is used for resizable views.
     * The Nullzone defines the area which the cursor might still detect a corner.
     * Increasing value allows a higher hit range
     */
    private static final double NULLZONE = 8.0;

    /**
     * This trigger is kept global to prevent dragging with resizing.
     */
    private static boolean isDragging = false;

    /**
     * Enables move-ability of the given ViewNode across the given region or pane.
     * The ViewNode will be able to move by the mouse's click&drag.
     * @param nodeToDrag The ViewNode you want to enable drag.
     * @param parent The region you want to move the ViewNode around. Can be direct parent.
     * @param model To keep track the new X&Y-Coordinates.
     * @param <T> The model Type of the given ViewNode. Has to be from ViewModel to keep track of the new X&Y-Coordinates.
     */
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

                    if (translateX >= 0 && translateX < parent.getWidth() - nodeToDrag.getWidth() - 40) {
                        nodeToDrag.setLayoutX(translateX);
                        model.setXCoordinate(translateX);
                    }
                    if (translateY >= 0 && translateY < parent.getHeight() - nodeToDrag.getHeight() - 40) {
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

    /**
     * Enables the click feature on one ViewNode.
     * When user clicks on given Node, it will be highlighted and handled of by the SelectionHandler.
     * @param nodeToDrag The ViewNode to enable click feature.
     * @param selectionHandler The handler to decide what to do when this Node has been clicked.
     * @param <T> The model Type of the given ViewNode.
     */
    public static <T extends ViewModel> void enableClick(CustomNode<T> nodeToDrag, SelectionHandler selectionHandler) {
        nodeToDrag.addEventFilter(MouseEvent.ANY, mouseEvent -> {
            if (MouseEvent.MOUSE_CLICKED == mouseEvent.getEventType()
                    || MouseEvent.MOUSE_PRESSED == mouseEvent.getEventType()) {
                nodeToDrag.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(3,158,211,0.8), 17, 0, 0, 0);");
                selectionHandler.setCurrentSelected(nodeToDrag);
            }
        });
    }

    /**
     * Enables resizing on given Node. The resizing only works when cursor is on buttom right corner of the ViewNode.
     * The hit box can be tweaked by the constant NULLZONE
     * @param nodeToResize The ViewNode to enable resize.
     * @param model To keep track the new Width&Height-Values.
     * @param <T> The model Type of the given ViewNode. Has to be from ResizableDataModel to keep track of the new Width&Height-Values.
     */
    public static <T extends ResizableDataModel> void enableResizeable(CustomNode<T> nodeToResize, T model) {
        nodeToResize.addEventHandler(MouseEvent.ANY, mouseEvent -> {
            if (MouseEvent.MOUSE_MOVED == mouseEvent.getEventType()) {
                if (isInResizePosition(mouseEvent, nodeToResize)) {
                    nodeToResize.setCursor(Cursor.SE_RESIZE);
                }
                mouseEvent.consume();
            } else if (MouseEvent.MOUSE_PRESSED == mouseEvent.getEventType()) {
                if (isInResizePosition(mouseEvent, nodeToResize)) {
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

    /**
     * Binds an EntityView to a given RelationLineHandler by its Properties.
     * On LayoutX&Y and Width&Height property change, the given RelationLineHandler will be called.
     *
     * TODO: RelationLineHandler needs to know of the value change to directly move the associated lines, currently redrawing all lines.
     *
     * @param entity The EntityView to bind.
     * @param handler The RelationLineHandler to redraw the relation lines on any property change.
     * @return The listener to unbind by caller when relation is removed by user.
     */
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

    /**
     * Checks if cursor is in button right corner. Used for resizing.
     */
    private static boolean isInResizePosition(MouseEvent mouseEvent, CustomNode<?> nodeToResize) {
        return (mouseEvent.getX() >= nodeToResize.getWidth() - NULLZONE
                && mouseEvent.getY() >= nodeToResize.getHeight() - NULLZONE);
    }

    /**
     * Closes a window by an event. E.g. MouseClickEvent
     */
    public static void closeWindow(Event source) {
        closeWindow(((Node) source.getSource()).getScene().getWindow());
    }

    /**
     * Closes a given window.
     */
    public static void closeWindow(Window sourceWindow) {
        sourceWindow.fireEvent(new WindowEvent(sourceWindow, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Shows a new general alert window.
     */
    private static void showAlert(Alert.AlertType type, String origin, String header, String info) {
        var alert = new Alert(type);
        alert.setResizable(true);
        alert.setTitle(origin);
        alert.setHeaderText(header);
        alert.getDialogPane().setContent(new Label(info));
        alert.showAndWait();
    }

    /**
     * Shows an info window.
     * @param origin Text to be shown on title
     * @param header Text to be shown on header
     * @param info Information text
     */
    public static void showInfo(String origin, String header, String info) {
        showAlert(Alert.AlertType.INFORMATION, origin, header, info);
    }

    /**
     * Shows a warning window.
     * @param origin Text to be shown on title
     * @param header Text to be shown on header
     * @param info Warning text
     */
    public static void showWarning(String origin, String header, String info) {
        showAlert(Alert.AlertType.WARNING, origin, header, info);
    }

    /**
     * Shows an error window.
     * @param origin Text to be shown on title
     * @param header Text to be shown on header
     * @param info Error text
     */
    public static void showError(String origin, String header, String info) {
        showAlert(Alert.AlertType.ERROR, origin, header, info);
    }

    /**
     * Shows the save file dialog to save a project file.
     * @param title Text to be shown on title
     * @param window Related window to draw dialog from.
     * @return Selected file.
     */
    public static File showJSONFileSaveDialog(String title, Window window) {
        return getJSONFileChooser(title).showSaveDialog(window);
    }

    /**
     * Shows the open file dialog to save a project file.
     * @param title Text to be shown on title
     * @param window Related window to draw dialog from.
     * @return Selected file.
     */
    public static File showJSONFileOpenDialog(String title, Window window) {
        return getJSONFileChooser(title).showOpenDialog(window);
    }

    private static FileChooser getJSONFileChooser(String title) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName("BOSS-Project.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Datei", "*.json"));
        return fileChooser;
    }

    /**
     * Shows the export project to picture dialog.
     * @param title Text to be shown on title
     * @param window Related window to draw dialog from.
     * @return Selected file.
     */
    public static File showPNGFileSaveDialog(String title, Window window) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName("BOSS-Project.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG-Datei", "*.png"));
        return fileChooser.showSaveDialog(window);
    }

    /**
     * Prompts user to click on either Yes or No
     * @param origin Text to be shown on title
     * @param header Text to be shown on header
     * @param text What are you asking the user?
     * @return Returns an Optional container. Call ifPresen() to see if there is an answer and get() to check what answer.
     */
    public static Optional<ButtonType> showYesNoConfirmationDialog(String origin, String header, String text) {
        var userInputDialog = new Alert(Alert.AlertType.CONFIRMATION);
        userInputDialog.getButtonTypes().clear();
        userInputDialog.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        userInputDialog.getDialogPane().setContent(new Label(text));
        userInputDialog.setTitle(origin);
        userInputDialog.setHeaderText(header);
        userInputDialog.setResizable(true);
        return userInputDialog.showAndWait();
    }
}
