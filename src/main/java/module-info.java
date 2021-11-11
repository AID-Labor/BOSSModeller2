module de.snaggly.bossmodeller2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens de.snaggly.bossmodeller2 to javafx.fxml;
    exports de.snaggly.bossmodeller2;
    exports de.snaggly.bossmodeller2.view.controller;
    opens de.snaggly.bossmodeller2.view.controller to javafx.fxml;
    exports de.snaggly.bossmodeller2.model;
    exports de.snaggly.bossmodeller2.guiLogic;
    exports de.snaggly.bossmodeller2.view;
    exports de.snaggly.bossmodeller2.view.viewtypes;
}