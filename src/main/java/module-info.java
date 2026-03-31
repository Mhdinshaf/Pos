module Pos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires lombok;

    opens org.Pos to javafx.fxml;
    opens com.pos.controller to javafx.fxml;
    opens com.pos.model to javafx.base;

    exports org.Pos;
    exports com.pos.controller;
    exports com.pos.model;
    exports com.pos.service;
    exports com.pos.repository;
}
