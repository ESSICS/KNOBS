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
import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
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
import org.controlsfx.control.PopOver;

import static se.ess.knobs.KnobEvent.ADJUSTED;
import static se.ess.knobs.KnobEvent.ADJUSTING;
import static se.ess.knobs.KnobEvent.TARGET_SET;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @author HanSolo, creator of the original Regulators classes.
 * @version 1.0.3 23 Aug 2017
 * @see <a href="https://github.com/HanSolo/regulators">HanSolo's Regulators</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
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

    protected Pane pane;

    private Arc barArc;
    private ConicalGradient barGradient;
    private Arc currentValueBarArc;
    private EventHandler<MouseEvent> doubleClickHandler = e -> {
        if ( e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 ) {
            openEditor();
        }
    };
    private DropShadow dropShadow;
    private InnerShadow highlight;
    private Circle indicator;
    private DropShadow indicatorGlow;
    private InnerShadow indicatorHighlight;
    private InnerShadow indicatorInnerShadow;
    private Rotate indicatorRotate;
    private volatile boolean inited = false;
    private Thread initThread;
    private InnerShadow innerShadow;
    private Circle mainCircle;
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
    private final List<Runnable> waitingEvents = Collections.synchronizedList(new ArrayList<>(4));

    @SuppressWarnings( "CallToThreadStartDuringObjectConstruction" )
    public Knob() {

        initSize();

        widthProperty().addListener(w -> {
            if ( inited ) {
                resize();
            } else {
                waitingEvents.add(() -> resize());
            }
        });
        heightProperty().addListener(h -> {
            if ( inited ) {
                resize();
            } else {
                waitingEvents.add(() -> resize());
            }
        });
        disabledProperty().addListener(d -> setOpacity(isDisabled() ? 0.4 : 1.0));

        initThread = new Thread(new Task<Void>() {
            @Override
            @SuppressWarnings( "CallToThreadYield" )
            protected Void call() throws Exception {

                initComponents();

                synchronized ( waitingEvents ) {
                    while ( !waitingEvents.isEmpty() ) {
                        Platform.runLater(waitingEvents.remove(0));
                        Thread.yield();
                    }
                }

                inited = true;
                initThread = null;

                return null;

            }
        });

        initThread.setDaemon(true);
        initThread.start();

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
            if ( get() == null ) {
                set(Color.TRANSPARENT);
            }
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
            if ( get() == null ) {
                set(DEFAULT_COLOR);
            }
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

            double val = get();
            double min = getMinValue();
            double max = getMaxValue();

            if ( needsClamping(val, min, max) ) {

                val = clamp(val, min, max);

                set(val);

            }

            if ( close(val, getTargetValue(), ( max - min ) * PROXIMITY_ERROR) ) {
                fireEvent(ADJUSTED_EVENT);
            } else {
                fireEvent(ADJUSTING_EVENT);
            }

            if ( inited ) {
                setText(val);
            } else {

                final double fval = val;

                waitingEvents.add(() -> setText(fval));

            }

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
     * ---- currentValueColor --------------------------------------------------
     */
    private final ObjectProperty<Color> currentValueColor = new SimpleObjectProperty<Color>(this, "currentValueColor", DEFAULT_CURRENT_VALUE_COLOR) {
        @Override
        protected void invalidated() {
            if ( get() == null ) {
                set(DEFAULT_CURRENT_VALUE_COLOR);
            }
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

            int val = get();

            if ( needsClamping(val, 0, 6) ) {

                val = clamp(get(), 0, 6);

                set(val);

            }

            format = MessageFormat.format("%.{0,number,###0}f", val);

            if ( inited ) {
                setText(getCurrentValue());
                setTextMax(getMaxValue());
                setTextMin(getMinValue());
                setTargetText(getTargetValue());
            } else {
                waitingEvents.add(() -> {
                    setText(getCurrentValue());
                    setTextMax(getMaxValue());
                    setTextMin(getMinValue());
                    setTargetText(getTargetValue());
                });
            }

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
     * ---- dragDisabled -----------------------------------------------------
     */
    private final BooleanProperty dragDisabled = new SimpleBooleanProperty(this, "dragDisabled", false);

    public BooleanProperty dragDisabledProperty() {
        return dragDisabled;
    }

    public boolean isDragDisabled() {
        return dragDisabled.get();
    }

    public void setDragDisabled( boolean dragDisabled ) {
        this.dragDisabled.set(dragDisabled);
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

            ObservableList<Stop> val = get();

            if ( val == null ) {

                val = DEFAULT_STOPS;

                set(val);

            }

            ConicalGradient bGrad = new ConicalGradient(reorderStops(val));

            if ( inited ) {

                double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
                double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

                barGradient = bGrad;
                size = width < height ? width : height;

                barArc.setCache(false);
                barArc.setStroke(barGradient.getImagePattern(new Rectangle(0, 0, size, size)));
                barArc.setCacheHint(CacheHint.SPEED);

            } else {
                waitingEvents.add(() -> {

                    double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
                    double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

                    barGradient = bGrad;
                    size = width < height ? width : height;

                    barArc.setCache(false);
                    barArc.setStroke(barGradient.getImagePattern(new Rectangle(0, 0, size, size)));
                    barArc.setCacheHint(CacheHint.SPEED);

                });
            }

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
            if ( get() == null ) {
                set(DEFAULT_COLOR.darker());
            }
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

            double val = get();
            double min = getMinValue();

            if ( needsClamping(val, min, Double.MAX_VALUE) ) {

                val = clamp(val, min, Double.MAX_VALUE);

                set(val);

            }

            if ( inited ) {
                setTextMax(val);
            } else {

                double fval = val;

                waitingEvents.add(() -> setTextMax(fval));

            }

            double cur = getCurrentValue();

            if ( needsClamping(cur, min, val) ) {
                setCurrentValue(clamp(cur, min, val));
            }

            double tgt = getTargetValue();

            if ( needsClamping(tgt, min, val) ) {
                setTargetValue(clamp(tgt, min, val));
            }

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

            double val = get();
            double max = getMaxValue();

            if ( needsClamping(val, - Double.MAX_VALUE, max) ) {

                val = clamp(val, - Double.MAX_VALUE, max);

                set(val);

            }

            if ( inited ) {
                setTextMin(val);
            } else {

                double fval = val;

                waitingEvents.add(() -> setTextMin(fval));

            }

            double cur = getCurrentValue();

            if ( needsClamping(cur, val, max) ) {
                setCurrentValue(clamp(cur, val, max));
            }

            double tgt = getTargetValue();

            if ( needsClamping(tgt, val, max) ) {
                setTargetValue(clamp(tgt, val, max));
            }

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
            if ( get() == null ) {
                set(Color.WHITE);
            }
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
            if ( get() == null ) {
                set(Color.TRANSPARENT);
            }
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
            
            double val = get();
            double min = getMinValue();
            double max = getMaxValue();

            if ( needsClamping(val, min, max) ) {
                
                val = clamp(val, min, max);
                
                set(val);
                
            }

            if ( close(getCurrentValue(), val, ( max - min ) * PROXIMITY_ERROR) ) {
                fireEvent(ADJUSTED_EVENT);
            } else {
                fireEvent(ADJUSTING_EVENT);
            }

            if ( inited ) {
                setTargetText(val);
            } else {

                double fval = val;

                waitingEvents.add(() -> setTargetText(fval));

            }

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
     * ---- targetValueAlwaysVisible ------------------------------------------
     */
    private final BooleanProperty targetValueAlwaysVisible = new SimpleBooleanProperty(this, "targetValueAlwaysVisible", false);

    public BooleanProperty targetValueAlwaysVisibleProperty() {
        return targetValueAlwaysVisible;
    }

    public boolean isTargetValueAlwaysVisible() {
        return targetValueAlwaysVisible.get();
    }

    public void setTargetValueAlwaysVisible( boolean targetValueAlwaysVisible ) {
        this.targetValueAlwaysVisible.set(targetValueAlwaysVisible);
    }

    /*
     * ---- textColor ----------------------------------------------------------
     */
    private final ObjectProperty<Color> textColor = new SimpleObjectProperty<Color>(this, "textColor", Color.WHITE) {
        @Override
        protected void invalidated() {
            if ( get() == null ) {
                set(Color.WHITE);
            }
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
            if ( inited ) {
                setUnitText(get());
            } else {
                waitingEvents.add(() -> setUnitText(get()));
            }
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
    public void fireTargeValueSet() {
        fireEvent(TARGET_SET_EVENT);
    }

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

    protected void initComponents() {

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

        currentValueBarArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.46, PREFERRED_HEIGHT * 0.46, BAR_START_ANGLE, 0);

        currentValueBarArc.setType(ArcType.OPEN);
        currentValueBarArc.setStrokeLineCap(StrokeLineCap.ROUND);
        currentValueBarArc.setFill(null);
        currentValueBarArc.strokeProperty().bind(currentValueColorProperty());
        currentValueBarArc.lengthProperty().bind(Bindings.multiply(angleStepProperty(), Bindings.subtract(minValueProperty(), currentValueProperty())));

        double center = PREFERRED_WIDTH * 0.5;

        ring = Shape.subtract(
            new Circle(center, center, PREFERRED_WIDTH * 0.42),
            new Circle(center, center, PREFERRED_WIDTH * 0.3)
        );

        ring.fillProperty().bind(colorProperty());
        ring.setEffect(dropShadow);
        ring.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if ( !isDisabled() && !isDragDisabled() ) {
                touchRotate(e.getSceneX(), e.getSceneY());
            }
        });
        ring.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if ( !isDisabled() && !isDragDisabled() ) {
                touchRotate(e.getSceneX(), e.getSceneY());
            }
        });
        ring.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if ( !isDisabled() && !isDragDisabled() ) {
                fireTargeValueSet();
            }
        });

        mainCircle = new Circle();

        mainCircle.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker(), colorProperty()));
        mainCircle.setOnMouseClicked(doubleClickHandler);

        text = new Text(String.format(format, getCurrentValue()));

        text.fillProperty().bind(textColorProperty());
        text.setOnMouseClicked(doubleClickHandler);
        text.setTextOrigin(VPos.CENTER);

        targetText = new Text(String.format(format, getTargetValue()));

        targetText.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        targetText.setOnMouseClicked(doubleClickHandler);
        targetText.setTextOrigin(VPos.CENTER);
        targetText.visibleProperty().bind(Bindings.createBooleanBinding(
            () -> isTargetValueAlwaysVisible() || !close(getCurrentValue(), getTargetValue(), ( getMaxValue() - getMinValue() ) * PROXIMITY_ERROR),
            targetValueAlwaysVisibleProperty(),
            currentValueProperty(),
            targetValueProperty(),
            maxValueProperty(),
            minValueProperty()
        ));

        unitText = new Text(getUnit());

        unitText.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        unitText.setOnMouseClicked(doubleClickHandler);
        unitText.setTextOrigin(VPos.CENTER);

        textMinTag = new Polygon(0.0, 0.7, 0.6, 0.7, 0.6, 0.9, 0.0, 0.9);

        textMinTag.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker(), colorProperty()));
        textMinTag.visibleProperty().bind(extremaVisibleProperty());

        textMin = new Text(String.format(format, getMinValue()));

        textMin.fillProperty().bind(Bindings.createObjectBinding(() -> getTextColor().darker(), textColorProperty()));
        textMin.setTextOrigin(VPos.CENTER);
        textMin.visibleProperty().bind(extremaVisibleProperty());

        textMaxTag = new Polygon(0.0, 0.7, 0.6, 0.7, 0.6, 0.9, 0.0, 0.9);

        textMaxTag.fillProperty().bind(Bindings.createObjectBinding(() -> getColor().darker().darker(), colorProperty()));
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
        indicator.disableProperty().bind(dragDisabledProperty());
        indicator.fillProperty().bind(Bindings.createObjectBinding(
            () -> {

                Color c = isSelected() ? getSelectionColor() : getIndicatorColor();

                return isDragDisabled() ? c.deriveColor(0, 1, 0.92, 0.6) : c;

            },
            colorProperty(),
            dragDisabledProperty(),
            indicatorColorProperty(),
            selectionColorProperty(),
            selectedProperty()
        ));
        indicator.strokeProperty().bind(Bindings.createObjectBinding(
            () -> {

                Color c = isSelected() ? getSelectionColor().darker().darker() : getIndicatorColor().darker().darker();

                return isDragDisabled() ? c.deriveColor(0, 1, 0.92, 0.6) : c;

            },
            colorProperty(),
            dragDisabledProperty(),
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

        Platform.runLater(() -> getChildren().setAll(pane));

    }

    protected void resize() {

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

            targetText.setFont(Fonts.robotoLight(size * 0.11));
            targetText.relocate(( size - targetText.getLayoutBounds().getWidth() ) * 0.5, size * 0.25);

            unitText.setFont(Fonts.robotoLight(size * 0.11));
            unitText.relocate(( size - unitText.getLayoutBounds().getWidth() ) * 0.5, size * 0.6);

            textMinTag.getPoints().set(0, size * 0.0 );     textMinTag.getPoints().set(1, size * 0.886);
            textMinTag.getPoints().set(2, size * 0.27);     textMinTag.getPoints().set(3, size * 0.886);
            textMinTag.getPoints().set(4, size * 0.27);     textMinTag.getPoints().set(5, size * 0.966);
            textMinTag.getPoints().set(6, size * 0.0 );     textMinTag.getPoints().set(7, size * 0.966);

            textMin.setFont(Fonts.robotoRegular(size * 0.072));
            textMin.relocate(size * 0.007, size * 0.878);

            setTextMin(getMinValue());

            textMaxTag.getPoints().set(0, size * 1.0 );     textMaxTag.getPoints().set(1, size * 0.886);
            textMaxTag.getPoints().set(2, size * 0.73);     textMaxTag.getPoints().set(3, size * 0.886);
            textMaxTag.getPoints().set(4, size * 0.73);     textMaxTag.getPoints().set(5, size * 0.966);
            textMaxTag.getPoints().set(6, size * 1.0 );     textMaxTag.getPoints().set(7, size * 0.966);

            textMax.setFont(Fonts.robotoRegular(size * 0.072));
            textMax.relocate(size * 0.737, size * 0.878);

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
     * and maximum values.
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
     * and maximum values.
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

    private void initSize() {
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
    }

    /**
     * Tell if the given {@code value} needs to be clamped into the range defined
     * by the given minimum and maximum values.
     *
     * @param value The value to be tested.
     * @param min   The clamp range minimum value.
     * @param max   The clamp range maximum value.
     * @return {@code false} if the given value is inside the range, {@code true}
     *         if it needs to be clamped.
     */
    private boolean needsClamping ( final double value, final double min, final double max ) {
        return ( value < min ||  value > max );
    }

    /**
     * Tell if the given {@code value} needs to be clamped into the range defined
     * by the given minimum and maximum values.
     *
     * @param value The value to be tested.
     * @param min   The clamp range minimum value.
     * @param max   The clamp range maximum value.
     * @return {@code false} if the given value is inside the range, {@code true}
     *         if it needs to be clamped.
     */
    private boolean needsClamping ( final int value, final int min, final int max ) {
        return ( value < min ||  value > max );
    }

    private void openEditor() {

        final PopOver popOver = new PopOver();
        final TextField textEditor = new TextField(text.getText());
        BorderPane editorPane = new BorderPane(textEditor);

        BorderPane.setMargin(textEditor, new Insets(12));
        textEditor.setOnKeyReleased(e -> {
            if ( KeyCode.ESCAPE.equals(e.getCode()) ) {
                popOver.hide();
            }
        });
        textEditor.setOnAction(e -> {
            try {
                setTargetValue(Double.parseDouble(textEditor.getText()));
                fireTargeValueSet();
            } catch ( NumberFormatException nfex ) {
                Toolkit.getDefaultToolkit().beep();
            } finally {
                popOver.hide();
            }
        });

        popOver.setContentNode(editorPane);
        popOver.setDetachable(false);
        popOver.setDetached(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setHideOnEscape(true);
        popOver.setTitle("Set Target Value");
        popOver.setAnimated(true);
        popOver.setAutoHide(true);
        popOver.setCloseButtonEnabled(true);

        text.getScene().getStylesheets().stream().forEach(s -> popOver.getRoot().getStylesheets().add(s));

        Bounds bounds = getBoundsInLocal();
        Bounds screenBounds = localToScreen(bounds);
        int x = (int) screenBounds.getMinX();
        int y = (int) screenBounds.getMinY();
        int w = (int) screenBounds.getWidth();
        int h = (int) screenBounds.getHeight();

        popOver.show(this, x + w / 2, y + h / 2);
        
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
            adjustTextSize(textMax, size * 0.256, size * 0.072);
            textMax.setLayoutX(size * 0.737 + ( size * 0.256 - textMax.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setTextMin( final double value ) {
        if ( textMin != null ) {
            textMin.setText(String.format(format, value));
            adjustTextSize(textMin, size * 0.256, size * 0.072);
            textMin.setLayoutX(size * 0.007 + ( size * 0.256 - textMin.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setTargetText( final double value ) {
        if ( targetText != null ) {
            targetText.setText(String.format(format, value));
            adjustTextSize(targetText, size * 0.39, size * 0.11);
            targetText.setLayoutX(( size - targetText.getLayoutBounds().getWidth() ) * 0.5);
        }
    }

    private void setUnitText( final String value ) {
        if ( unitText != null ) {
            unitText.setText(value);
            adjustTextSize(unitText, size * 0.39, size * 0.11);
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
