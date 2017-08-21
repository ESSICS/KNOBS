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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Aug 2017
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class KnobPropertyEditorFactory extends DefaultPropertyEditorFactory {

    private static final Logger LOGGER = Logger.getLogger(KnobPropertyEditorFactory.class.getName());

    @Override
    public PropertyEditor<?> call( PropertySheet.Item item ) {

        Class<?> type = item.getType();

        if ( item.getPropertyEditorClass().isPresent() ) {

            Optional<PropertyEditor<?>> ed = Editors.createCustomEditor(item);

            if ( ed.isPresent() ) {
                return ed.get();
            }

        }

        if ( "gradientStops".equals(item.getName()) ) {
            return new StopListEditor(item);
        } else {
            return super.call(item);
        }

    }

    private class StopListEditor extends AbstractPropertyEditor<ObservableList<Stop>, StopListEditorButton> {

        @SuppressWarnings( "unchecked" )
        StopListEditor( PropertySheet.Item property ) {
            super(property, new StopListEditorButton((ObservableList<Stop>) property.getValue()));
        }

        @Override
        public void setValue( ObservableList<Stop> value ) {
            getEditor().setStopList(value);
        }

        @Override
        protected ObservableValue<ObservableList<Stop>> getObservableValue() {
            return getEditor().stopListProperty();
        }

    }

    private class StopListEditorButton extends Button {

        private PopOver popOver = new PopOver();
        private final ListProperty<Stop> stopList = new SimpleListProperty<>(FXCollections.observableArrayList());

        StopListEditorButton( ObservableList<Stop> stops ) {
            super();
            init(stops);
        }

        ObservableList<Stop> getStopList() {
            return stopList.get();
        }

        void setStopList( ObservableList<Stop> stopList ) {
            this.stopList.set(stopList);
        }

        @SuppressWarnings( "ReturnOfCollectionOrArrayField" )
        ListProperty<Stop> stopListProperty() {
            return stopList;
        }

        private void init( ObservableList<Stop> stops ) {

            setStopList(stops);

            textProperty().bind(Bindings.createStringBinding(
                () -> MessageFormat.format("{0,number,###0} Stops", getStopList().size()),
                stopListProperty()
            ));

            try {

                FXMLLoader loader = new FXMLLoader(KnobPropertyEditorFactory.class.getResource("/fxml/StopListEditorController.fxml"));

                popOver.setContentNode((Node) loader.load());
                popOver.setDetachable(true);
                popOver.setDetached(false);
                popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                popOver.setHeaderAlwaysVisible(true);
                popOver.setTitle("Gradient Stops");
                popOver.setAnimated(true);
                popOver.setAutoHide(false);
                popOver.setCloseButtonEnabled(true);
                popOver.getRoot().getStylesheets().add("/styles/dark-style.css");

                StopListEditorController controller = loader.<StopListEditorController>getController();

                controller.setStopListProperty(stopListProperty());

            } catch ( IOException ex ) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            setOnAction(e -> {
                if ( popOver.isShowing() ) {
                    popOver.hide(Duration.ZERO);
                } else {
                    popOver.show(this);
                }
            });

        }

    }

}
