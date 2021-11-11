package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class CommentController implements ViewController<Comment> {
    private boolean isEditable = false;

    @FXML
    TextArea commentTextArea;

    @Override
    public void loadModel(Comment model) {
        commentTextArea.setText(model.getName());
    }

    @FXML
    private void initialize(){

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
