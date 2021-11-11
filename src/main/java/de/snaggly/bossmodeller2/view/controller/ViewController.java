package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.DataModel;

public interface ViewController<T extends DataModel> {
    void loadModel(T model);
}
