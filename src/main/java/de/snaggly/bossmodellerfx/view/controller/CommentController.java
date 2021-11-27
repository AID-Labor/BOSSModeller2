package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.Comment;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class CommentController implements ViewController<Comment> {
    private boolean isEditable = false;
    private Comment model;

    @FXML
    TextArea commentTextArea;

    @Override
    public void loadModel(Comment model) {
        this.model = model;
        commentTextArea.setText(model.getName());
    }

    @FXML
    private void initialize(){
        commentTextArea.textProperty().addListener((observableValue, s, newText) -> {
            if (model != null)
                model.setName(newText);
        });
    }

    @FXML
    private void onMouseClickOnPane(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            enableEdit();
        }
    }

    @FXML
    private void onMouseClickOnText(MouseEvent mouseEvent) {
        /*if (mouseEvent.getClickCount() == 2){
            disableEdit();
        }*/
    }

    public void enableEdit() {
        commentTextArea.setDisable(false);
        commentTextArea.requestFocus();
        isEditable = true;
    }

    public void disableEdit() {
        commentTextArea.setText(commentTextArea.getText());
        commentTextArea.setDisable(true);
        isEditable = false;
    }

    public synchronized boolean isEditable() {
        return isEditable;
    }

    public synchronized void setEditable(boolean editable) {
        isEditable = editable;
    }
}
