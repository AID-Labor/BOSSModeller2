package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.BOSSModel;

/**
 * Interface for controllers used in views and windows.
 * Gives a generalized return type for factories.
 * @param <T> Model which the controller works with.
 * @author Omar Emshani
 */
public interface ModelController<T extends BOSSModel> {
    /**
     * Loads and displays model on given view.
     * @param model Loads the model on controller and prepares the view.
     */
    void loadModel(T model);
}
