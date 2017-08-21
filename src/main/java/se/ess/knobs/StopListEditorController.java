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


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Aug 2017
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class StopListEditorController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private TableColumn<Stop, Color> colorColumn;
    @FXML
    private Button editButton;
    @FXML
    private TableColumn<Stop, Double> offsetColumn;
    @FXML
    private Button removeButton;
    @FXML
    private TableView<Stop> stopsTable;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {

        offsetColumn.setCellValueFactory(new PropertyValueFactory<>("offset"));

        colorColumn.setCellFactory(c -> new ColorTableCell());
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));

        stopsTable.getSelectionModel().selectedItemProperty().addListener(( observable, oldValue, newValue ) -> {
            editButton.setDisable(newValue == null);
            removeButton.setDisable(newValue == null);
        });

    }

    @FXML
    void addPressed( ActionEvent event ) {

        Stop stop = getNewStop(null);

        if ( stop != null ) {

            boolean done = false;

            for ( int i = 0; i < stopsTable.getItems().size(); i++ ) {
                if ( stopsTable.getItems().get(i).getOffset() > stop.getOffset() ) {

                    done = true;

                    stopsTable.getItems().add(i, stop);
                    break;

                }
            }

            if ( !done ) {
                stopsTable.getItems().add(stop);
            }

        }

    }

    @FXML
    void editPressed( ActionEvent event ) {

        int index = stopsTable.getSelectionModel().getSelectedIndex();

        Stop stop = getNewStop(stopsTable.getItems().get(index));

        if ( stop != null ) {

            stopsTable.getItems().remove(index);

            boolean done = false;

            for ( int i = 0; i < stopsTable.getItems().size(); i++ ) {
                if ( stopsTable.getItems().get(i).getOffset() > stop.getOffset() ) {

                    done = true;

                    stopsTable.getItems().add(i, stop);
                    break;

                }
            }

            if ( !done ) {
                stopsTable.getItems().add(stop);
            }

        }

    }

    @FXML
    void removePressed( ActionEvent event ) {
        stopsTable.getItems().remove(stopsTable.getSelectionModel().getSelectedIndex());
    }

    void setStopListProperty( ListProperty<Stop> stopListProperty ) {

        stopsTable.itemsProperty().bindBidirectional(stopListProperty);

    }

    private Stop getNewStop ( Stop previous ) {

        Dialog<Stop> dialog = new Dialog<>();

        dialog.setTitle("Stop Editor");
        dialog.setHeaderText(previous == null ? "Define a new Stop" : "Edit the selected Stop");
        dialog.getDialogPane().getButtonTypes().addAll(OK, CANCEL);

        Spinner<Double> offsetSpinner = new Spinner<>(0.0, 1.0, previous == null ? 0.0 : previous.getOffset(), 0.01);
        ColorPicker colorPicker = new ColorPicker(previous == null ? Color.GOLDENROD : previous.getColor());
        GridPane grid = new GridPane();

        offsetSpinner.setEditable(true);
        offsetSpinner.setPrefWidth(USE_COMPUTED_SIZE);
        colorPicker.setPrefWidth(USE_COMPUTED_SIZE);

        grid.setHgap(6);
        grid.setVgap(12);
        grid.setPadding(new Insets(12, 12, 12, 12));
        grid.getColumnConstraints().add(0, new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.RIGHT, true));
        grid.getColumnConstraints().add(1, new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.LEFT, true));
        grid.add(new Label("Offset:"), 0, 0);
        grid.add(offsetSpinner, 1, 0);
        grid.add(new Label("Color:"), 0, 1);
        grid.add(colorPicker, 1, 1);

        dialog.initOwner(stopsTable.getScene().getWindow());
        dialog.getDialogPane().getScene().getStylesheets().add("/styles/dark-style.css");
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if ( b == OK ) {
                return new Stop(offsetSpinner.getValue(), colorPicker.getValue());
            } else {
                return null;
            }
        });

        Platform.runLater(() -> offsetSpinner.requestFocus());

        return dialog.showAndWait().orElse(null);

    }

    private static class ColorTableCell extends TableCell<Stop, Color> {

        @SuppressWarnings( "FieldMayBeFinal" )
        private static Map<Color, String> colorNames = null;
        
        @SuppressWarnings( "ReturnOfCollectionOrArrayField" )
        private static Map<Color, String> getColorNames() {
            
            if ( colorNames == null ) {
                
                colorNames = new HashMap<>(64);
                
                Field[] fields = Color.class.getDeclaredFields();
                
                for ( Field f : fields ) {
                    
                    if ( Modifier.isStatic(f.getModifiers()) && Objects.equals(f.getType(), Color.class) ) {
                        try {
                            colorNames.put((Color) f.get(Color.class), f.getName());
                        } catch ( IllegalArgumentException | IllegalAccessException ex ) {
                        }
                    }
                    
                }
                
            }
            
            return colorNames;
            
        }

        private final Rectangle icon;

        ColorTableCell() {

            icon = new Rectangle(16, 16);

            icon.setFill(Color.TRANSPARENT);
            icon.setStroke(Color.TRANSPARENT);

            setGraphic(icon);

        }

        @Override
        protected void updateItem( Color item, boolean empty ) {

            super.updateItem(item, empty);

            if ( item == null || empty ) {
                setText(null);
                icon.setFill(Color.TRANSPARENT);
                icon.setStroke(Color.TRANSPARENT);
            } else {

                if ( Color.TRANSPARENT.equals(item) ) {
                    icon.setFill(Color.TRANSPARENT);
                    icon.setStroke(Color.BLACK);
                } else {
                    icon.setFill(item);
                    icon.setStroke(item.darker().darker());
                }

                if ( getColorNames().containsKey(item) ) {
                    setText(getColorNames().get(item));
                } else {
                    setText(item.toString());
                }

            }

        }

    }

}
