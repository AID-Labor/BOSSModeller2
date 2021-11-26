package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.DataModel;

public interface ViewController<T extends DataModel> {
    void loadModel(T model);
}
