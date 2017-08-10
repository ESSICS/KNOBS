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


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static se.ess.knobs.KnobEvent.ADJUSTED;
import static se.ess.knobs.KnobEvent.ADJUSTING;
import static se.ess.knobs.KnobEvent.TARGET_SET;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @author HanSolo, creator of the original Regulators classes.
 * @version 1.0.0 9 Aug 2017
 * @see <a href="https://github.com/HanSolo/regulators">HanSolo's Regulators</a>
 */
public class Knob extends Region {

    public static final Color DEFAULT_COLOR = Color.rgb(66,71,79);

    private static final double ANGLE_RANGE     = 280;
    private static final double PROXIMITY_ERROR = 0.01;

    private final KnobEvent  ADJUSTING_EVENT = new KnobEvent(this, null, ADJUSTING);
    private final KnobEvent   ADJUSTED_EVENT = new KnobEvent(this, null, ADJUSTED);
    private final KnobEvent TARGET_SET_EVENT = new KnobEvent(this, null, TARGET_SET);

    private double angleStep;
    private String formatString;
    private Arc currentValueBarArc;
    private double size;
    private Text targetText;
    private Text text;

    public Knob() {
        //  TODO: CR: To be implemented
        setPrefSize(220, 220);
        setOpacity(1);
        setBackground(new Background(new BackgroundFill(Color.GOLDENROD, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /*
     * ---- color --------------------------------------------------------------
     */
    private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(this, "color", DEFAULT_COLOR) {
        @Override
        protected void invalidated() {

            if ( get() == null ) {
                set(get() != null ? get() : DEFAULT_COLOR);
            }

            redraw();

        }
    };

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor( Color color ) {
        this.color.set(color);
    }

    /*
     * ---- currentValue -------------------------------------------------------
     */
    private final DoubleProperty currentValue = new SimpleDoubleProperty(this, "currentValue", 0) {
        @Override
        protected void invalidated() {

            set(clamp(get(), getMinValue(), getMaxValue()));

            if ( close(get(), getTargetValue(), ( getMaxValue() - getMinValue() ) * PROXIMITY_ERROR) ) {

                fireEvent(ADJUSTED_EVENT);

                if ( !isCurrentValueAlwaysVisible() ) {
                    targetText.setVisible(false);
                    currentValueBarArc.setVisible(false);
                }

            } else {

                fireEvent(ADJUSTING_EVENT);

                if ( !isCurrentValueAlwaysVisible() ) {
                    targetText.setVisible(true);
                    currentValueBarArc.setVisible(true);
                }

            }

            setText(get());
            updateCurrentValueBar(get());
            redraw();

        }
    };

    public DoubleProperty currentValueProperty() {
        return currentValue;
    }

    public double getCurrentValue() {
        return currentValue.get();
    }

    public void setCurrentValue( double currentValue ) {
        this.currentValue.set(currentValue);
    }

    /*
     * ---- currentValueAlwaysVisible ------------------------------------------
     */
    private final BooleanProperty currentValueAlwaysVisible = new SimpleBooleanProperty(this, "currentValueAlwaysVisible", true) {
        @Override
        protected void invalidated() {
            redraw();
        }
    };

    public BooleanProperty currentValueAlwaysVisibleProperty() {
        return currentValueAlwaysVisible;
    }

    public boolean isCurrentValueAlwaysVisible() {
        return currentValueAlwaysVisible.get();
    }

    public void setCurrentValueAlwaysVisible( boolean currentValueAlwaysVisible ) {
        this.currentValueAlwaysVisible.set(currentValueAlwaysVisible);
    }

    /*
     * ---- maxValue -----------------------------------------------------------
     */
    private final DoubleProperty maxValue = new SimpleDoubleProperty(this, "maxValue", 100) {
        @Override
        protected void invalidated() {

            set(clamp(get(), getMinValue(), Double.MAX_VALUE));
            setCurrentValue(clamp(getCurrentValue(), getMinValue(), get()));

            angleStep = ANGLE_RANGE / ( get() - getMinValue() );

            updateCurrentValueBar(get());
            redraw();

        }
    };

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public void setMaxValue( double maxValue ) {
        this.maxValue.set(maxValue);
    }

    /*
     * ---- minValue -----------------------------------------------------------
     */
    private final DoubleProperty minValue = new SimpleDoubleProperty(this, "minValue", 0) {
        @Override
        protected void invalidated() {

            set(clamp(get(), - Double.MAX_VALUE, getMaxValue()));
            setCurrentValue(clamp(getCurrentValue(), get(), getMaxValue()));

            angleStep = ANGLE_RANGE / ( getMaxValue() - get() );

            updateCurrentValueBar(get());
            redraw();

        }
    };

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public double getMinValue() {
        return minValue.get();
    }

    public void setMinValue( double minValue ) {
        this.minValue.set(minValue);
    }

    /*
     * ---- targetValue --------------------------------------------------------
     */
    private final DoubleProperty targetValue = new SimpleDoubleProperty(this, "targetValue", 0) {
        @Override
        protected void invalidated() {

//            set(clamp(get(), - Double.MAX_VALUE, getMaxValue()));
//            setCurrentValue(clamp(getCurrentValue(), get(), getMaxValue()));
//
//            angleStep = ANGLE_RANGE / ( getMaxValue() - get() );
//
//            drawReadBackBar(get());
//            redraw();

        }
    };

    public DoubleProperty targetValueProperty() {
        return targetValue;
    }

    public double getTargetValue() {
        return targetValue.get();
    }

    public void setTargetValue( double targetValue ) {
        this.targetValue.set(targetValue);
    }










    /*
     * -------------------------------------------------------------------------
     */
    @SuppressWarnings( "AssignmentToMethodParameter" )
    private void adjustTextSize( final Text textComponent, final double availableWidth, double fontSize ) {

        final String fontName = textComponent.getFont().getName();

        while ( textComponent.getLayoutBounds().getWidth() > availableWidth && fontSize > 0 ) {

            fontSize -= 0.005;

            textComponent.setFont(new Font(fontName, fontSize));

        }

    }

    /**
     * Clamp the given {@code value} inside a range defined by the given minimum
     * an maximum values.
     *
     * @param value The value to be clamped.
     * @param min   The clamp range minimum value.
     * @param max   The clamp range maximum value.
     * @return {@code value} if it's inside the range, otherwise {@code min} if
     *         {@code value} is below the range, or {@code max} if above the range.
     */
    private double clamp ( final double value, final double min, final double max ) {
        if ( value < min ) {
            return min;
        } else if ( value > max ) {
            return max;
        } else {
            return value;
        }
    }

    /**
     * Clamp the given {@code value} inside a range defined by the given minimum
     * an maximum values.
     *
     * @param value The value to be clamped.
     * @param min   The clamp range minimum value.
     * @param max   The clamp range maximum value.
     * @return {@code value} if it's inside the range, otherwise {@code min} if
     *         {@code value} is below the range, or {@code max} if above the range.
     */
    private int clamp ( final int value, final int min, final int max ) {
        if ( value < min ) {
            return min;
        } else if ( value > max ) {
            return max;
        } else {
            return value;
        }
    }

    /**
     * Check it the given {@code value} is close to the target one for less than
     * the specified {@code error}.
     *
     * @param value  The current value to be checked for proximity.
     * @param target The target reference value.
     * @param error  The proximity error.
     * @return {@code true} if {@code ( value > target - error ) && ( value < target + error )}.
     */
    private boolean close ( final double value, final double target, final double error ) {
        return ( value > target - error ) && ( value < target + error );
    }

    private void redraw() {
        //  TODO: CR: To be implemented
    }

    private void setText(final double value) {
        text.setText(String.format(formatString, value));
        adjustTextSize(text, size * 0.48, size * 0.216);
        text.setLayoutX((size - text.getLayoutBounds().getWidth()) * 0.5);
    }

    private void updateCurrentValueBar( final double value ) {
        currentValueBarArc.setLength(-( value - getMinValue() ) * angleStep);
    }

}
