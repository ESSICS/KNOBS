/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2017 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.ess.knobs;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Aug 2017
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class StopListEditorController implements Initializable {

    @FXML private Button addButton;
    @FXML private TableColumn<Stop, Color> colorColumn;
    @FXML private Button editButton;
    @FXML private TableColumn<Stop, Double> offsetColumn;
    @FXML private Button removeButton;
    @FXML private TableView<Stop> stopsTable;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {
        offsetColumn.setCellValueFactory(new PropertyValueFactory<>("offset"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        stopsTable.getSelectionModel().selectedItemProperty().addListener(( observable, oldValue, newValue ) -> {
            editButton.setDisable(newValue == null);
            removeButton.setDisable(newValue == null);
        });
    }

    @FXML
    void addPressed(ActionEvent event) {

    }

    @FXML
    void editPressed(ActionEvent event) {

    }

    @FXML
    void removePressed(ActionEvent event) {

    }

    void setStopListProperty( ListProperty<Stop> stopListProperty ) {

        stopsTable.itemsProperty().bindBidirectional(stopListProperty);

    }

}
