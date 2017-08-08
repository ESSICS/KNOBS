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


import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 8 Aug 2017
 */
@SuppressWarnings("ClassWithoutLogger")
public class KnobBuilder {

    public static KnobBuilder create() {
        return new KnobBuilder();
    }

    final Map<String, Property<?>> properties = new HashMap<>(4);

    protected KnobBuilder() {
    }

    public final KnobBuilder color( final Color color ) {

        properties.put("color", new SimpleObjectProperty<>(color));

        return this;

    }

    public final KnobBuilder currentValue( final double value ) {

        properties.put("currentValue", new SimpleDoubleProperty(value));

        return this;

    }

    public final KnobBuilder maxValue( final double maxValue ) {

        properties.put("maxValue", new SimpleDoubleProperty(maxValue));

        return this;

    }

    public final KnobBuilder minValue( final double minValue ) {

        properties.put("minValue", new SimpleDoubleProperty(minValue));

        return this;

    }

    public final KnobBuilder targetValue( final double targetValue ) {

        properties.put("targetValue", new SimpleDoubleProperty(targetValue));

        return this;

    }

    public final KnobBuilder textColor( final Color textColor ) {

        properties.put("textColor", new SimpleObjectProperty<>(textColor));

        return this;

    }

    public final KnobBuilder unit( final String unit ) {

        properties.put("unit", new SimpleStringProperty(unit));

        return this;

    }

    public final KnobBuilder unitColor( final Color unitColor ) {

        properties.put("unitColor", new SimpleObjectProperty<>(unitColor));

        return this;

    }

}
