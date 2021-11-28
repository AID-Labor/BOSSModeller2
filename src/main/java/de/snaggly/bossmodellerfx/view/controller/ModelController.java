package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.BOSSModel;

public interface ModelController<T extends BOSSModel> {
    void loadModel(T model);
}
