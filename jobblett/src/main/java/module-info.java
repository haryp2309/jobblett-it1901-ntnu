module bolett {
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.controls;
    requires com.fasterxml.jackson.databind;

    exports jobblett.core;
	exports jobblett.json;
	exports jobblett.ui;

	opens jobblett.ui to javafx.fxml;
	opens jobblett.json to com.fasterxml.jackson.databind;
	opens jobblett.core to com.fasterxml.jackson.databind;
}