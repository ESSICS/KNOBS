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
package se.europeanspallationsource.javafx.control.knobs;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanProperty;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 9 Aug 2017
 */
public class KnobController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(KnobController.class.getName());

    @FXML private FlowPane knobContainer;
    @FXML private PropertySheet propertySheet;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean updateCurrentValue = false;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {

        propertySheet.setPropertyEditorFactory(new KnobPropertyEditorFactory());

        long beforeTime = System.currentTimeMillis();

        Knob knob = KnobBuilder.create()
                        .onAdjusted(e -> LOGGER.info(MessageFormat.format("Current value reached target: {0}", ((Knob) e.getSource()).getCurrentValue())))
                        .onTargetSet(e -> { 
                            LOGGER.info(MessageFormat.format("Target changed: {0}", ((Knob) e.getSource()).getTargetValue()));
                            updateCurrentValue = true;
                        })
                        .build();

        long afterTime = System.currentTimeMillis();

        LOGGER.log(Level.INFO, "Construction time: {0,number,#########0}ms", afterTime - beforeTime);

        knobContainer.getChildren().add(knob);

        Map<Class<?>, String> categories = new HashMap<>(5);

        categories.put(  Knob.class, "\u200BKnob");
        categories.put(Region.class, "\u200B\u200BRegion");
        categories.put(Parent.class, "\u200B\u200B\u200BParent");
        categories.put(  Node.class, "\u200B\u200B\u200B\u200BNode");
        categories.put(Object.class, "\u200B\u200B\u200B\u200B\u200BObject");

        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(Knob.class, Object.class);

            for ( PropertyDescriptor p : beanInfo.getPropertyDescriptors() ) {
                try {
                    if ( p.getReadMethod() != null && p.getWriteMethod() != null ) {
                        p.setValue(BeanProperty.CATEGORY_LABEL_KEY, categories.get(p.getReadMethod().getDeclaringClass()));
                        propertySheet.getItems().add(new BeanProperty(knob, p));
                    }
                } catch ( Exception iex ) {
                    LOGGER.log(Level.SEVERE, MessageFormat.format("Unable to handle property \"{0}\" [{1}].", p.getName(), iex.getMessage()));
                }
            }
            
        } catch ( IntrospectionException ex ) {
            LOGGER.log(Level.SEVERE, "Unable to initialize the controller.", ex);
        }

        propertySheet.setMode(PropertySheet.Mode.CATEGORY);

        timer.scheduleAtFixedRate(() -> {
            if ( updateCurrentValue ) {

                double step = ( knob.getMaxValue() - knob.getMinValue() ) / 234;
                double cValue = knob.getCurrentValue();
                double tValue = knob.getTargetValue();

                if ( cValue < tValue ) {
                    Platform.runLater(() -> {
                        if ( ( tValue - cValue ) > step ) {
                            knob.setCurrentValue(cValue + step);
                        } else {
                            knob.setCurrentValue(tValue);
                            updateCurrentValue = false;
                        }
                    });
                } else if ( cValue > tValue ) {
                    Platform.runLater(() -> {
                        if ( ( cValue - tValue ) > step ) {
                            knob.setCurrentValue(cValue - step);
                        } else {
                            knob.setCurrentValue(tValue);
                            updateCurrentValue = false;
                        }
                    });
                }

            }},
            2000,
            50,
            TimeUnit.MILLISECONDS
        );

    }

}
