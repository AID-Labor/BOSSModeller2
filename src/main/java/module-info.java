module de.snaggly.bossmodellerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;

    opens de.snaggly.bossmodellerfx to javafx.fxml;
    exports de.snaggly.bossmodellerfx;
    exports de.snaggly.bossmodellerfx.view.controller;
    opens de.snaggly.bossmodellerfx.view.controller to javafx.fxml;
    exports de.snaggly.bossmodellerfx.model;
    exports de.snaggly.bossmodellerfx.guiLogic;
    exports de.snaggly.bossmodellerfx.view;
    exports de.snaggly.bossmodellerfx.view.viewtypes;
    exports de.snaggly.bossmodellerfx.model.view;
    exports de.snaggly.bossmodellerfx.model.subdata;
    exports de.snaggly.bossmodellerfx.model.serializable;
    exports de.snaggly.bossmodellerfx.model.abstraction;
    exports de.snaggly.bossmodellerfx.relation_logic;
    exports de.bossmodeler.logicalLayer.elements;
    exports de.snaggly.bossmodellerfx.model.adapter;
}