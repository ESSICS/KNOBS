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


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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

    @FXML
    private FlowPane knobContainer;
    @FXML
    private PropertySheet propertySheet;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {

        Knob knob = KnobBuilder.create()
                        .build();

        knobContainer.getChildren().add(knob);

        Map<Class<?>, String> categories = new HashMap<>(5);

        categories.put(  Knob.class, "1. Knob");
        categories.put(Region.class, "2. Region");
        categories.put(Parent.class, "3. Parent");
        categories.put(  Node.class, "4. Node");
        categories.put(Object.class, "5. Object");

        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(Knob.class, Object.class);

            for ( PropertyDescriptor p : beanInfo.getPropertyDescriptors() ) {

                p.setValue(BeanProperty.CATEGORY_LABEL_KEY, categories.get(p.getReadMethod().getDeclaringClass()));
                propertySheet.getItems().add(new BeanProperty(knob, p));

            }
            
        } catch ( IntrospectionException ex ) {
            LOGGER.throwing(getClass().getName(), "initialize", ex);
        }

        propertySheet.setMode(PropertySheet.Mode.CATEGORY);

    }

}
