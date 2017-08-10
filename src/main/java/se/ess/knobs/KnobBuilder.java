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


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @author HanSolo, creator of the original Regulators classes.
 * @version 1.0.0 8 Aug 2017
 * @see <a href="https://github.com/HanSolo/regulators">HanSolo's Regulators</a>
 */
@SuppressWarnings("ClassWithoutLogger")
public class KnobBuilder {

    public static KnobBuilder create() {
        return new KnobBuilder();
    }

    final Map<String, Object> properties = new HashMap<>(4);

    protected KnobBuilder() {
    }

    public Knob build() {

        final Knob knob = new Knob();

        if ( properties.containsKey("color") ) {
            knob.setColor((Color) properties.get("color"));
        }
        if ( properties.containsKey("currentValue") ) {
            knob.setCurrentValue((double) properties.get("currentValue"));
        }
        if ( properties.containsKey("currentValueAlwaysVisible") ) {
            knob.setCurrentValueAlwaysVisible((boolean) properties.get("currentValueAlwaysVisible"));
        }
        if ( properties.containsKey("maxValue") ) {
            knob.setMaxValue((double) properties.get("maxValue"));
        }
        if ( properties.containsKey("minValue") ) {
            knob.setMinValue((double) properties.get("minValue"));
        }
        if ( properties.containsKey("targetValue") ) {
            knob.setTargetValue((double) properties.get("targetValue"));
        }

        return knob;

    }

    public final KnobBuilder color( final Color color ) {

        properties.put("color", color);

        return this;

    }

    public final KnobBuilder currentValue( final double value ) {

        properties.put("currentValue", value);

        return this;

    }

    public final KnobBuilder currentValueAlwaysVisible( final boolean value ) {

        properties.put("currentValueAlwaysVisible", value);

        return this;

    }

    public final KnobBuilder currentValueColor( final Color color ) {

        properties.put("currentValueColor", color);

        return this;

    }

    public final KnobBuilder decimals( final int decimals ) {

        properties.put("decimals", decimals);

        return this;

    }

    public final KnobBuilder extremaColor( final Color color ) {

        properties.put("extremaColor", color);

        return this;

    }

    public final KnobBuilder gradientStops( final Stop... stops ) {
        return gradientStops(Arrays.asList(stops));
    }

    public final KnobBuilder gradientStops( final List<Stop> stops ) {

        properties.put("gradientStops", stops);

        return this;

    }

    public final KnobBuilder id( final String unit ) {

        properties.put("id", unit);

        return this;

    }

    public final KnobBuilder indicatorColor( final Color color ) {

        properties.put("indicatorColor", color);

        return this;

    }

    public final KnobBuilder layoutX( final double scale ) {

        properties.put("layoutX", scale);

        return this;

    }

    public final KnobBuilder layoutY( final double scale ) {

        properties.put("layoutY", scale);

        return this;

    }

    public final KnobBuilder maxHeight( final double height ) {

        properties.put("maxHeight", height);

        return this;

    }

    public final KnobBuilder maxSize( final double width, final double height ) {

        properties.put("maxSize", new Dimension2D(width, height));

        return this;

    }

    public final KnobBuilder maxValue( final double value ) {

        properties.put("maxValue", value);

        return this;

    }

    public final KnobBuilder maxWidth( final double width ) {

        properties.put("maxWidth", width);

        return this;

    }

    public final KnobBuilder minHeight( final double height ) {

        properties.put("minHeight", height);

        return this;

    }

    public final KnobBuilder minSize( final double width, final double height ) {

        properties.put("minSize", new Dimension2D(width, height));

        return this;

    }

    public final KnobBuilder minValue( final double value ) {

        properties.put("minValue", value);

        return this;

    }

    public final KnobBuilder minWidth( final double width ) {

        properties.put("minWidth", width);

        return this;

    }

    public final KnobBuilder onAdjusted( final EventHandler<KnobEvent> handler ) {

        properties.put("onAdjusted", handler);

        return this;

    }

    public final KnobBuilder onAdjusting( final EventHandler<KnobEvent> handler ) {

        properties.put("onAdjusting", handler);

        return this;

    }

    public final KnobBuilder onTargetSet( final EventHandler<KnobEvent> handler ) {

        properties.put("onTargetSet", handler);

        return this;

    }

    public final KnobBuilder padding( final double topRightBottomLeft ) {
        return padding(new Insets(topRightBottomLeft));
    }

    public final KnobBuilder padding( final double top, final double right, final double bottom, final double left ) {
        return padding(new Insets(top, right, bottom, left));
    }

    public final KnobBuilder padding( final Insets insets ) {

        properties.put("padding", insets);

        return this;

    }

    public final KnobBuilder prefHeight( final double height ) {

        properties.put("prefHeight", height);

        return this;

    }

    public final KnobBuilder prefSize( final double width, final double height ) {

        properties.put("prefSize", new Dimension2D(width, height));

        return this;

    }

    public final KnobBuilder prefWidth( final double width ) {

        properties.put("prefWidth", width);

        return this;

    }

    public final KnobBuilder scaleX( final double scale ) {

        properties.put("scaleX", scale);

        return this;

    }

    public final KnobBuilder scaleY( final double scale ) {

        properties.put("scaleY", scale);

        return this;

    }

    public final KnobBuilder selected( final boolean value ) {

        properties.put("selected", value);

        return this;

    }

    public final KnobBuilder showExtrema( final boolean value ) {

        properties.put("showExtrema", value);

        return this;

    }

    public final KnobBuilder showTag( final boolean value ) {

        properties.put("showTag", value);

        return this;

    }

    public final KnobBuilder selectionColor( final Color color ) {

        properties.put("selectionColor", color);

        return this;

    }

    public final KnobBuilder tagColor( final Color color ) {

        properties.put("tagColor", color);

        return this;

    }

    public final KnobBuilder targetValue( final double value ) {

        properties.put("targetValue", value);

        return this;

    }

    public final KnobBuilder textColor( final Color color ) {

        properties.put("textColor", color);

        return this;

    }

    public final KnobBuilder translateX( final double scale ) {

        properties.put("translateX", scale);

        return this;

    }

    public final KnobBuilder translateY( final double scale ) {

        properties.put("translateY", scale);

        return this;

    }

    public final KnobBuilder unit( final String unit ) {

        properties.put("unit", unit);

        return this;

    }

    public final KnobBuilder unitColor( final Color color ) {

        properties.put("unitColor", color);

        return this;

    }

}
