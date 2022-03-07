package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.view.viewtypes.Pannable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Custom Pane to be used to display a project on the main window.
 *
 * @author Omar Emshani
 */
public class WorkbenchPane extends ScrollPane implements Pannable {
    boolean[] canExtendSize = {true, true};
    double[] startingPos = {0.0, 0.0};
    double[] minSize = {0.0, 0.0};
    private final Pane contentWorkfield = new Pane();

    public WorkbenchPane(EventHandler<MouseEvent> onMouseClick) {
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        this.setStyle("-fx-border-color: grey;");

        this.setContent(contentWorkfield);
        this.setPannable(true);
        this.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        this.setFitToHeight(true);
        this.setFitToWidth(true);

        setInfinitePannable();

        //If not translated properly, the pane ends up centered when zooming in.
        //The "proper" solution, wrapping the Pane into a group causes panning to bug out.
        contentWorkfield.scaleXProperty().addListener((observableValue, number, t1) -> {
            contentWorkfield.setTranslateX((contentWorkfield.getWidth()*t1.doubleValue() - contentWorkfield.getWidth())/2);
        });
        contentWorkfield.scaleYProperty().addListener((observableValue, number, t1) -> {
            contentWorkfield.setTranslateY((contentWorkfield.getHeight()*t1.doubleValue() - contentWorkfield.getHeight())/2);
        });

        this.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.doubleValue() > minSize[0]) {
                    minSize[0] = t1.doubleValue();
                    contentWorkfield.setMinWidth(minSize[0]);
                }
            }
        });
        this.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.doubleValue() > minSize[1]) {
                    minSize[1] = t1.doubleValue();
                    contentWorkfield.setMinHeight(t1.doubleValue());
                }
            }
        });

        this.getContent().setOnMouseClicked(onMouseClick);
    }

    public Pane getContentWorkfield() {
        return contentWorkfield;
    }

    /**
     * Makes a ScrollPane to be panned by mouse infinitely by always increasing the contents size,
     * when user drags with mouse over the edge.
     * Content has to be from regular Pane.
     */
    @Override
    public void setInfinitePannable() {
        this.hvalueProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() >= 1.0 || t1.doubleValue() <= 0.0) {
                canExtendSize[0] = true;
                contentWorkfield.setMinWidth(contentWorkfield.getWidth() + 5.0);
            } else {
                canExtendSize[0] = false;
            }
        });
        this.vvalueProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() >= 1.0 || t1.doubleValue() <= 0.0) {
                canExtendSize[1] = true;
                contentWorkfield.setMinHeight(contentWorkfield.getHeight() + 5.0);
            } else {
                canExtendSize[1] = false;
            }
        });
        this.getContent().addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            startingPos[0] = mouseEvent.getX();
            startingPos[1] = mouseEvent.getY();
        });
        this.getContent().addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (canExtendSize[0]) {
                var deltaX = startingPos[0] - mouseEvent.getX();
                if (deltaX > 0) {
                    contentWorkfield.setMinWidth(contentWorkfield.getWidth() + deltaX);
                }
            }
            if (canExtendSize[1]) {
                var deltaY = startingPos[1] - mouseEvent.getY();
                if (deltaY > 0) {
                    contentWorkfield.setMinHeight(contentWorkfield.getHeight() + deltaY);
                }
            }
        });
    }
}
