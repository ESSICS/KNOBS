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


import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.tools.ConicalGradient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

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

    public static final Color               DEFAULT_COLOR = Color.rgb(66, 71, 79);
    public static final Color DEFAULT_CURRENT_VALUE_COLOR = Color.rgb( 0,  0,  0, 0.6);

    public static final ObservableList<Stop> DEFAULT_STOPS = FXCollections.observableArrayList(
        new Stop(0.0,   Color.RED),
        new Stop(0.5,   Color.YELLOW),
        new Stop(1.0,   Color.GREEN)
    );
    public static final ObservableList<Stop> TRANSPARENT_STOPS = FXCollections.observableArrayList(
        new Stop(0.0,   Color.rgb(  0,   0,   0, 0)),
        new Stop(1.0,   Color.rgb(255, 255, 255, 0))
    );

    public static final double MAXIMUM_HEIGHT   = 1024;
    public static final double MAXIMUM_WIDTH    = 1024;
    public static final double MINIMUM_HEIGHT   =   64;
    public static final double MINIMUM_WIDTH    =   64;
    public static final double PREFERRED_HEIGHT =  400;
    public static final double PREFERRED_WIDTH  =  400;

    private static final double ANGLE_RANGE      =  280;
    private static final double BAR_START_ANGLE  = -130;
    private static final double PROXIMITY_ERROR  = 0.001;

    private final KnobEvent  ADJUSTING_EVENT = new KnobEvent(this, null, ADJUSTING);
    private final KnobEvent   ADJUSTED_EVENT = new KnobEvent(this, null, ADJUSTED);
    private final KnobEvent TARGET_SET_EVENT = new KnobEvent(this, null, TARGET_SET);

    private Arc barArc;
    private ConicalGradient barGradient;
    private Arc currentValueBarArc;
    private DropShadow dropShadow;
    private InnerShadow highlight;
    private Circle indicator;
    private DropShadow indicatorGlow;
    private InnerShadow indicatorHighlight;
    private InnerShadow indicatorInnerShadow;
    private Rotate indicatorRotate;
    private InnerShadow innerShadow;
    private Circle mainCircle;
    private Pane pane;
    private Shape ring;
    private double size;
    private String format = "%.2f";
    private Arc tagBarArc;
    private Text targetText;
    private Text text;
    private Text textMax;
    private Polygon textMaxTag;
    private Text textMin;
    private Polygon textMinTag;
    private Text unitText;

    public Knob() {
        init();
        registerListeners();
    }

    /*
     * ---- angleStep ----------------------------------------------------------
     */
    private final DoubleProperty angleStep = new SimpleDoubleProperty(this, "angleStep", ANGLE_RANGE / 100.0);

    protected DoubleProperty angleStepProperty() {
        return angleStep;
    }

    protected double getAngleStep() {
        return angleStep.get();
    }

    /*
     * ---- backgroundColor ----------------------------------------------------
     */
    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<Color>(this, "backgroundColor", Color.TRANSPARENT) {
        @Override
        protected void invalidated() {
            set(get() == null ? Color.TRANSPARENT : get());
        }
    };

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor.set(backgroundColor);
    }

    /*
     * ---- color --------------------------------------------------------------
     */
    private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(this, "color", DEFAULT_COLOR) {
        @Override
        protected void invalidated() {
            set(get() == null ? DEFAULT_COLOR : get());
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
            } else {
                fireEvent(ADJUSTING_EVENT);
            }

            setText(get());

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
    private final BooleanProperty currentValueAlwaysVisible = new SimpleBooleanProperty(this, "currentValueAlwaysVisible", true);

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
     * ---- currentValueColor --------------------------------------------------
     */
    private final ObjectProperty<Color> currentValueColor = new SimpleObjectProperty<Color>(this, "currentValueColor", DEFAULT_CURRENT_VALUE_COLOR) {
        @Override
        protected void invalidated() {
            set(get() == null ? DEFAULT_CURRENT_VALUE_COLOR : get());
        }
    };

    public ObjectProperty<Color> currentValueColorProperty() {
        return currentValueColor;
    }

    public Color getCurrentValueColor() {
        return currentValueColor.get();
    }

    public void setCurrentValueColor( Color currentValueColor ) {
        this.currentValueColor.set(currentValueColor);
    }

    /*
     * ---- decimals -----------------------------------------------------------
     */
    private final IntegerProperty decimals = new SimpleIntegerProperty(this, "decimals", 2) {
        @Override
        protected void invalidated() {

            set(clamp(get(), 0, 6));

            format = MessageFormat.format("%.{0,number,###0}f", get());

            setText(getCurrentValue());
            setTextMax(getMaxValue());
            setTextMin(getMinValue());
            setTargetText(getTargetValue());

        }
    };

    public IntegerProperty decimalsProperty() {
        return decimals;
    }

    public int getDecimals() {
        return decimals.get();
    }

    public void setDecimals( int decimals ) {
        this.decimals.set(decimals);
    }

    /*
     * ---- extremaVisible -----------------------------------------------------
     */
    private final BooleanProperty extremaVisible = new SimpleBooleanProperty(this, "extremaVisible", false);

    public BooleanProperty extremaVisibleProperty() {
        return extremaVisible;
    }

    public boolean isExtremaVisible() {
        return extremaVisible.get();
    }

    public void setExtremaVisible( boolean extremaVisible ) {
        this.extremaVisible.set(extremaVisible);
    }

    /*
     * ---- gradientStops ------------------------------------------------------
     */
    private final ListProperty<Stop> gradientStops = new SimpleListProperty<Stop>(this, "gradientStops", DEFAULT_STOPS) {
        @Override
        protected void invalidated() {

            set(get() == null ? DEFAULT_STOPS : get());

            barGradient = new ConicalGradient(reorderStops(get()));

            double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
            double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

            size = width < height ? width : height;

            barArc.setCache(false);
            barArc.setStroke(barGradient.getImagePattern(new Rectangle(0, 0, size, size)));
            barArc.setCacheHint(CacheHint.SPEED);

        }
    };

    @SuppressWarnings( "ReturnOfCollectionOrArrayField" )
    public ListProperty<Stop> gradientStopsProperty() {
        return gradientStops;
    }

    public ObservableList<Stop> getGradientStops() {
        return gradientStops.get();
    }

    public void setGradientStops( Stop... stops ) {
        this.gradientStops.set(FXCollections.observableArrayList(stops));
    }

    public void setGradientStops( List<Stop> gradientStops ) {
        this.gradientStops.set(gradientStops != null ? FXCollections.observableList(gradientStops) : FXCollections.emptyObservableList());
    }

    public void setGradientStops( ObservableList<Stop> gradientStops ) {
        this.gradientStops.set(gradientStops);
    }

    /*
     * ---- indicatorColor -----------------------------------------------------
     */
    private final ObjectProperty<Color> indicatorColor = new SimpleObjectProperty<Color>(this, "indicatorColor", DEFAULT_COLOR.darker()) {
        @Override
        protected void invalidated() {
            set(get() == null ? DEFAULT_COLOR.darker() : get());
        }
    };

    public ObjectProperty<Color> indicatorColorProperty() {
        return indicatorColor;
    }

    public Color getIndicatorColor() {
        return indicatorColor.get();
    }

    public void setIndicatorColor( Color indicatorColor ) {
        this.indicatorColor.set(indicatorColor);
    }

    /*
     * ---- maxValue -----------------------------------------------------------
     */
    private final DoubleProperty maxValue = new SimpleDoubleProperty(this, "maxValue", 100) {
        @Override
        protected void invalidated() {
            set(clamp(get(), getMinValue(), Double.MAX_VALUE));
            setTextMax(get());
            setCurrentValue(clamp(getCurrentValue(), getMinValue(), get()));
            setTargetValue(clamp(getCurrentValue(), getMinValue(), get()));
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
            setTextMin(get());
            setCurrentValue(clamp(getCurrentValue(), get(), getMaxValue()));
            setTargetValue(clamp(getCurrentValue(), get(), getMaxValue()));
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
     * ---- selected -----------------------------------------------------------
     */
    private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected( boolean selected ) {
        this.selected.set(selected);
    }

    /*
     * ---- selectionColor -----------------------------------------------------
     */
    private final ObjectProperty<Color> selectionColor = new SimpleObjectProperty<Color>(this, "selectionColor", Color.WHITE) {
        @Override
        protected void invalidated() {
            set(get() == null ? Color.WHITE : get());
        }
    };

    public ObjectProperty<Color> selectionColorProperty() {
        return selectionColor;
    }

    public Color getSelectionColor() {
        return selectionColor.get();
    }

    public void setSelectionColor( Color selectionColor ) {
        this.selectionColor.set(selectionColor);
    }

    /*
     * ---- tagColor -----------------------------------------------------------
     */
    private final ObjectProperty<Color> tagColor = new SimpleObjectProperty<Color>(this, "tagColor", Color.RED) {
        @Override
        protected void invalidated() {
            set(get() == null ? Color.TRANSPARENT : get());
        }
    };

    public ObjectProperty<Color> tagColorProperty() {
        return tagColor;
    }

    public Color getTagColor() {
        return tagColor.get();
    }

    public void setTagColor( Color tagColor ) {
        this.tagColor.set(tagColor);
    }

    /*
     * ---- tagVisible ---------------------------------------------------------
     */
    private final BooleanProperty tagVisible = new SimpleBooleanProperty(this, "tagVisible", false);

    public BooleanProperty tagVisibleProperty() {
        return tagVisible;
    }

    public boolean isTagVisible() {
        return tagVisible.get();
    }

    public void setTagVisible( boolean tagVisible ) {
        this.tagVisible.set(tagVisible);
    }

    /*
     * ---- targetValue --------------------------------------------------------
     */
    private final DoubleProperty targetValue = new SimpleDoubleProperty(this, "targetValue", 0) {
        @Override
        protected void invalidated() {

            set(clamp(get(), getMinValue(), getMaxValue()));

            if ( close(getCurrentValue(), get(), ( getMaxValue() - getMinValue() ) * PROXIMITY_ERROR) ) {
                fireEvent(ADJUSTED_EVENT);
            } else {
                fireEvent(ADJUSTING_EVENT);
            }

            setTargetText(get());

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
     * ---- textColor ----------------------------------------------------------
     */
    private final ObjectProperty<Color> textColor = new SimpleObjectProperty<Color>(this, "textColor", Color.WHITE) {
        @Override
        protected void invalidated() {
            set(get() == null ? Color.WHITE : get());
        }
    };

    public ObjectProperty<Color> textColorProperty() {
        return textColor;
    }

    public Color getTextColor() {
        return textColor.get();
    }

    public void setTextColor( Color textColor ) {
        this.textColor.set(textColor);
    }

   /*
     * ---- unit ---------------------------------------------------------------
     */
    private final StringProperty unit = new SimpleStringProperty(this, "unit", null) {
        @Override
        protected void invalidated() {
            setUnitText(get());
        }
    };

    public StringProperty unitProperty() {
        return unit;
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit( String unit ) {
        this.unit.set(unit);
    }

     /*
     * -------------------------------------------------------------------------
     */
    public void removeOnAdjusted( final EventHandler<KnobEvent> handler ) {
        removeEventHandler(KnobEvent.ADJUSTED, handler);
    }

    public void removeOnAdjusting( final EventHandler<KnobEvent> handler ) {
        removeEventHandler(KnobEvent.ADJUSTING, handler);
    }

    public void removeOnTargetSet( final EventHandler<KnobEvent> handler ) {
        removeEventHandler(KnobEvent.TARGET_SET, handler);
    }

    public void setOnAdjusted( final EventHandler<KnobEvent> handler ) {
        addEventHandler(KnobEvent.ADJUSTED, handler);
    }

    public void setOnAdjusting( final EventHandler<KnobEvent> handler ) {
        addEventHandler(KnobEvent.ADJUSTING, handler);
    }

    public void setOnTargetSet( final EventHandler<KnobEvent> handler ) {
        addEventHandler(KnobEvent.TARGET_SET, handler);
    }

    @SuppressWarnings( "AssignmentToMethodParameter" )
    private void adjustTextSize( final Text textComponent, final double availableWidth, double fontSize ) {

        final String fontName = textComponent.getFont().getName();

        textComponent.setFont(new Font(fontName, fontSize));

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

    private void init() {

        setPrefSize(
            getPrefWidth()  > 0 ? getPrefWidth()  : PREFERRED_WIDTH,
            getPrefHeight() > 0 ? getPrefHeight() : PREFERRED_HEIGHT
        );
        setMinSize(
            getMinWidth()  > 0 ? getMinWidth()  : MINIMUM_WIDTH,
            getMinHeight() > 0 ? getMinHeight() : MINIMUM_HEIGHT
        );
        setMaxSize(
            getMaxWidth()  > 0 ? getMaxWidth()  : MAXIMUM_WIDTH,
            getMaxHeight() > 0 ? getMaxHeight() : MAXIMUM_HEIGHT
        );

        angleStepProperty().bind(Bindings.divide(ANGLE_RANGE, Bindings.subtract(maxValueProperty(), minValueProperty())));
        backgroundProperty().bind(Bindings.createObjectBinding(
            () -> Color.TRANSPARENT.equals(getBackgroundColor())
                  ? Background.EMPTY
                  : new Background(new BackgroundFill(getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)),
            backgroundColorProperty()
        ));

        dropShadow  = new DropShadow (BlurType.TWO_PASS_BOX, Color.rgb(  0,   0,   0, 0.65), PREFERRED_WIDTH * 0.016, 0.0, 0,  PREFERRED_WIDTH * 0.028);
        highlight   = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(255, 255, 255, 0.20), PREFERRED_WIDTH * 0.008, 0.0, 0,  PREFERRED_WIDTH * 0.008);
        innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(  0,   0,   0, 0.20), PREFERRED_WIDTH * 0.008, 0.0, 0, -PREFERRED_WIDTH * 0.008);

        highlight.setInput(innerShadow);
        dropShadow.setInput(highlight);

        barGradient = new ConicalGradient(reorderStops(getGradientStops()));
        barArc      = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.46, PREFERRED_HEIGHT * 0.46, BAR_START_ANGLE, 0);

        barArc.setType(ArcType.OPEN);
        barArc.setStrokeLineCap(StrokeLineCap.ROUND);
        barArc.setFill(null);
        barArc.setStroke(barGradient.getImagePattern(new Rectangle(0, 0, PREFERRED_WIDTH, PREFERRED_HEIGHT)));

        BooleanBinding currentValueVisibleBinding = Bindings.createBooleanBinding(
            () -> isCurrentValueAlwaysVisible() || !close(getCurrentValue(), getTargetValue(), ( getMaxValue() - getMinValue() ) * PROXIMITY_ERROR),
            currentValueAlwaysVisibleProperty(),
            currentValueProperty(),
            targetValueProperty(),
            maxValueProperty(),
            minValueProperty()
        );

        currentValueBarArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.46, PREFERRED_HEIGHT * 0.46, BAR_START_ANGLE, 0);

        currentValueBarArc.setType(ArcType.OPEN);
        currentValueBarArc.setStrokeLineCap(StrokeLineCap.ROUND);
        currentValueBarArc.setFill(null);
        currentValueBarArc.strokeProperty().bind(currentValueColorProperty());
        currentValueBarArc.lengthProperty().bind(Bindings.multiply(angleStepProperty(), Bindings.subtract(minValueProperty(), currentValueProperty())));
        currentValueBarArc.visibleProperty().bind(currentValueVisibleBinding);

        double center = PREFERRED_WIDTH * 0.5;

        ring = Shape.subtract(
            new Circle(center, center, PREFERRED_WIDTH * 0.42),
            new Circle(center, center, PREFERRED_WIDTH * 0.3)
        );

        ring.fillProperty().bind(colorProperty());
        ring.setEffect(dropShadow);

        mainCircle = new Circle();

        mainCircle.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker(), colorProperty()));

        text = new Text(String.format(format, getCurrentValue()));

        text.fillProperty().bind(textColorProperty());
        text.setTextOrigin(VPos.CENTER);

        targetText = new Text(String.format(format, getTargetValue()));

        targetText.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        targetText.setTextOrigin(VPos.CENTER);
        targetText.visibleProperty().bind(currentValueVisibleBinding);

        unitText = new Text(getUnit());

        unitText.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        unitText.setTextOrigin(VPos.CENTER);

        textMinTag = new Polygon(0.0, 0.7, 0.3, 0.7, 0.4, 0.5, 0.4, 0.9, 0.0, 0.9);

        textMinTag.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker().darker(), colorProperty()));
        textMinTag.visibleProperty().bind(extremaVisibleProperty());

        textMin = new Text(String.format(format, getMinValue()));

        textMin.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        textMin.setTextOrigin(VPos.CENTER);
        textMin.visibleProperty().bind(extremaVisibleProperty());

        textMaxTag = new Polygon(0.0, 0.7, 0.3, 0.7, 0.4, 0.5, 0.4, 0.9, 0.0, 0.9);

        textMaxTag.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker().darker(), colorProperty()));
        textMaxTag.visibleProperty().bind(extremaVisibleProperty());

        textMax = new Text(String.format(format, getMaxValue()));

        textMax.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        textMax.setTextOrigin(VPos.CENTER);
        textMax.visibleProperty().bind(extremaVisibleProperty());

        tagBarArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.46, PREFERRED_HEIGHT * 0.46, BAR_START_ANGLE + 15, 50);

        tagBarArc.setType(ArcType.OPEN);
        tagBarArc.setStrokeLineCap(StrokeLineCap.ROUND);
        tagBarArc.setFill(null);
        tagBarArc.strokeProperty().bind(tagColorProperty());
        tagBarArc.visibleProperty().bind(tagVisibleProperty());

        indicatorRotate      = new Rotate(-ANGLE_RANGE *  0.5, center, center);
        indicatorGlow        = new DropShadow (BlurType.TWO_PASS_BOX, getIndicatorColor(),            PREFERRED_WIDTH * 0.020, 0.0, 0, 0);
        indicatorInnerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(  0,   0,   0, 0.50), PREFERRED_WIDTH * 0.008, 0.0, 0,  PREFERRED_WIDTH * 0.008);
        indicatorHighlight   = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(255, 255, 255, 0.35), PREFERRED_WIDTH * 0.008, 0.0, 0, -PREFERRED_WIDTH * 0.008);

        indicatorRotate.angleProperty().bind(Bindings.subtract(Bindings.multiply(Bindings.subtract(targetValueProperty(), minValueProperty()), angleStepProperty()), ANGLE_RANGE * 0.5));
        indicatorGlow.colorProperty().bind(selectionColorProperty());
        indicatorHighlight.setInput(indicatorInnerShadow);

        indicator = new Circle();

        indicator.effectProperty().bind(Bindings.createObjectBinding(
            () -> isSelected() ? indicatorGlow : null,
            selectionColorProperty(),
            selectedProperty()
        ));
        indicator.fillProperty().bind(Bindings.createObjectBinding(
            () -> isSelected() ? getSelectionColor() : getIndicatorColor(),
            colorProperty(),
            indicatorColorProperty(),
            selectionColorProperty(),
            selectedProperty()
        ));
        indicator.strokeProperty().bind(Bindings.createObjectBinding(
            () -> isSelected() ? getSelectionColor().darker().darker() : getIndicatorColor().darker().darker(),
            colorProperty(),
            indicatorColorProperty(),
            selectionColorProperty(),
            selectedProperty()
        ));
        indicator.setMouseTransparent(true);
        indicator.getTransforms().add(indicatorRotate);

        Group indicatorGroup = new Group(indicator);

        indicatorGroup.setEffect(indicatorHighlight);

        pane = new Pane(barArc, currentValueBarArc, ring, mainCircle, text, targetText, unitText, textMinTag, textMin, textMaxTag, textMax, tagBarArc, indicatorGroup);

        pane.setPrefSize(PREFERRED_HEIGHT, PREFERRED_HEIGHT);
        pane.backgroundProperty().bind(Bindings.createObjectBinding(
            () -> new Background(new BackgroundFill(getColor().darker(), new CornerRadii(1024), Insets.EMPTY)),
            colorProperty()
        ));
        pane.setEffect(highlight);

        getChildren().setAll(pane);

    }

    private void registerListeners() {
        widthProperty().addListener(w -> resize());
        heightProperty().addListener(h -> resize());
        disabledProperty().addListener(d -> setOpacity(isDisabled() ? 0.4 : 1.0));
        ring.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if ( !isDisabled() ) {
                touchRotate(e.getSceneX(), e.getSceneY());
            }
        });
        ring.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> { 
            if (! isDisabled() ) {
                touchRotate(e.getSceneX(), e.getSceneY());
            }
        });
        ring.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> { 
            if ( !isDisabled() ) {
                fireEvent(TARGET_SET_EVENT);
            }
        });
    }

    private List<Stop> reorderStops( final List<Stop> stops ) {

        /*
         * 0.0 -> 0.611
         * 0.5 -> 0.0 & 1.0
         * 1.0 -> 0.389
         */
        double range = 0.778;
        double halfRange = range * 0.5;
        Map<Double, Color> stopMap = new HashMap<>(stops.size());

        stops.stream().filter(s -> s != null).forEach(s -> stopMap.put(s.getOffset(), s.getColor()));

        List<Stop> sortedStops = new ArrayList<>(stops.size());

        if ( !stopMap.isEmpty() ) {

            SortedSet<Double> sortedFractions = new TreeSet<>(stopMap.keySet());

            if ( sortedFractions.last() < 1 ) {
                stopMap.put(1.0, stopMap.get(sortedFractions.last()));
                sortedFractions.add(1.0);
            }

            if ( sortedFractions.first() > 0 ) {
                stopMap.put(0.0, stopMap.get(sortedFractions.first()));
                sortedFractions.add(0.0);
            }

            sortedFractions.stream().forEach(f -> {

                double offset = f * range - halfRange;

                offset = offset < 0 ? 1.0 + offset : offset;

                sortedStops.add(new Stop(offset, stopMap.get(f)));

            });

        }

        return sortedStops;

    }

    private void resize() {

        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        size = width < height ? width : height;

        if ( width > 0 && height > 0 ) {

            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate(( getWidth() - size ) * 0.5, ( getHeight() - size ) * 0.5);

            barArc.setCache(false);
            barArc.setCenterX(size * 0.5);
            barArc.setCenterY(size * 0.5);
            barArc.setRadiusX(size * 0.46);
            barArc.setRadiusY(size * 0.46);
            barArc.setStrokeWidth(size * 0.04);
            barArc.setStroke(barGradient.getImagePattern(new Rectangle(0, 0, size, size)));
            barArc.setLength(-( getMaxValue() - getMinValue() ) * getAngleStep());
            barArc.setCache(true);
            barArc.setCacheHint(CacheHint.SPEED);

            currentValueBarArc.setCenterX(size * 0.5);
            currentValueBarArc.setCenterY(size * 0.5);
            currentValueBarArc.setRadiusX(size * 0.46);
            currentValueBarArc.setRadiusY(size * 0.46);
            currentValueBarArc.setStrokeWidth(size * 0.03);

            double shadowRadius = clamp(1.0, 2.0, size * 0.004);

            dropShadow.setRadius(shadowRadius);
            dropShadow.setOffsetY(shadowRadius);
            highlight.setRadius(shadowRadius);
            highlight.setOffsetY(shadowRadius);
            innerShadow.setRadius(shadowRadius);
            innerShadow.setOffsetY(-shadowRadius);

            double center      = size * 0.5;
            double scaleFactor = size / PREFERRED_WIDTH;

            ring.setCache(false);
            ring.getTransforms().setAll(new Scale(scaleFactor, scaleFactor, 0, 0));
            ring.setCache(true);
            ring.setCacheHint(CacheHint.SPEED);

            mainCircle.setCache(false);
            mainCircle.setRadius(size * 0.3);
            mainCircle.setCenterX(center);
            mainCircle.setCenterY(center);
            mainCircle.setCache(true);
            mainCircle.setCacheHint(CacheHint.SPEED);

            text.setFont(Fonts.robotoMedium(size * 0.216));
            text.relocate(( size - text.getLayoutBounds().getWidth() ) * 0.5, size * 0.33);

            targetText.setFont(Fonts.robotoLight(size * 0.082));
            targetText.relocate(( size - targetText.getLayoutBounds().getWidth() ) * 0.5, size * 0.25);

            unitText.setFont(Fonts.robotoLight(size * 0.082));
            unitText.relocate(( size - unitText.getLayoutBounds().getWidth() ) * 0.5, size * 0.64);
            
            textMinTag.getPoints().set(0, size * 0.0 );     textMinTag.getPoints().set(1, size * 0.886);
            textMinTag.getPoints().set(2, size * 0.19);     textMinTag.getPoints().set(3, size * 0.886);
            textMinTag.getPoints().set(4, size * 0.21);     textMinTag.getPoints().set(5, size * 0.856);
            textMinTag.getPoints().set(6, size * 0.21);     textMinTag.getPoints().set(7, size * 0.946);
            textMinTag.getPoints().set(8, size * 0.0 );     textMinTag.getPoints().set(9, size * 0.946);

            textMin.setFont(Fonts.robotoLight(size * 0.04));
            textMin.relocate(size * 0.007, size * 0.891);

            setTextMin(getMinValue());

            textMaxTag.getPoints().set(0, size * 1.0 );     textMaxTag.getPoints().set(1, size * 0.886);
            textMaxTag.getPoints().set(2, size * 0.81);     textMaxTag.getPoints().set(3, size * 0.886);
            textMaxTag.getPoints().set(4, size * 0.79);     textMaxTag.getPoints().set(5, size * 0.856);
            textMaxTag.getPoints().set(6, size * 0.79);     textMaxTag.getPoints().set(7, size * 0.946);
            textMaxTag.getPoints().set(8, size * 1.0 );     textMaxTag.getPoints().set(9, size * 0.946);

            textMax.setFont(Fonts.robotoLight(size * 0.04));
            textMax.relocate(size * 0.797, size * 0.891);

            setTextMax(getMaxValue());

            tagBarArc.setCenterX(size * 0.5);
            tagBarArc.setCenterY(size * 0.5);
            tagBarArc.setRadiusX(size * 0.46);
            tagBarArc.setRadiusY(size * 0.46);
            tagBarArc.setStrokeWidth(size * 0.03);

            indicatorGlow.setRadius(size * 0.02);
            indicatorInnerShadow.setRadius(size * 0.008);
            indicatorInnerShadow.setOffsetY(size * 0.006);
            indicatorHighlight.setRadius(size * 0.008);
            indicatorHighlight.setOffsetY(-size * 0.004);

            indicator.setRadius(size * 0.032);
            indicator.setCenterX(center);
            indicator.setCenterY(size * 0.148);

            indicatorRotate.setPivotX(center);
            indicatorRotate.setPivotY(center);

        }

    }

    private void setText( final double value ) {
        if ( text != null ) {
            text.setText(String.format(format, value));
            adjustTextSize(text, size * 0.48, size * 0.216);
            text.setLayoutX(( size - text.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setTextMax( final double value ) {
        if ( textMax != null ) {
            textMax.setText(String.format(format, value));
            adjustTextSize(textMax, size * 0.196, size * 0.04);
            textMax.setLayoutX(size * 0.797 + ( size * 0.196 - textMax.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setTextMin( final double value ) {
        if ( textMin != null ) {
            textMin.setText(String.format(format, value));
            adjustTextSize(textMin, size * 0.196, size * 0.04);
            textMin.setLayoutX(size * 0.007 + ( size * 0.196 - textMin.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setTargetText( final double value ) {
        if ( targetText != null ) {
            targetText.setText(String.format(format, value));
            adjustTextSize(targetText, size * 0.30, size * 0.082);
            targetText.setLayoutX(( size - targetText.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setUnitText( final String value ) {
        if ( unitText != null ) {
            unitText.setText(value);
            adjustTextSize(unitText, size * 0.30, size * 0.082);
            unitText.setLayoutX(( size - unitText.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void touchRotate( final double X, final double Y ) {

        Point2D p      = sceneToLocal(X, Y);
        double  deltaX = p.getX() - ( pane.getLayoutX() + size * 0.5 );
        double  deltaY = p.getY() - ( pane.getLayoutY() + size * 0.5 );
        double  radius = Math.sqrt(( deltaX * deltaX ) + ( deltaY * deltaY ));
        double  nx     = deltaX / radius;
        double  ny     = deltaY / radius;
        double  theta  = Math.atan2(ny, nx);

        theta = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees(( theta )) + 360.0;

        double angle = ( theta + 230 ) % 360;

        if ( angle > 320 && angle < 360 ) {
            angle = 0;
        } else if ( angle <= 320 && angle > ANGLE_RANGE ) {
            angle = ANGLE_RANGE;
        }

        setTargetValue(angle / getAngleStep() + getMinValue());

    }

}
