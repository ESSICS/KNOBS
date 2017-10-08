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
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 8 Aug 2017
 */
public class KnobBuilderTest {
    
    private static final Logger LOGGER = Logger.getLogger(KnobBuilderTest.class.getName());

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of backgroundColor method, of class KnobBuilder.
     */
    @Test
    public void testBackgroundColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().backgroundColor(value);

        assertThat(builder.properties)
            .containsKey("backgroundColor");
        assertThat(builder.properties.get("backgroundColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of build method, of class KnobBuilder.
     */
    @Test
    public void testBuild() {

        Knob knob1 = KnobBuilder.create()
                                .backgroundColor(Color.GOLDENROD)
                                .color(Color.CORAL)
                                .currentValueColor(Color.GAINSBORO)
                                .decimals(3)
                                .dragDisabled(true)
                                .extremaVisible(true)
                                .gradientStops((Stop) null)
                                .id("pippo")
                                .indicatorColor(Color.BLUEVIOLET)
                                .layoutX(0.234)
                                .layoutY(0.567)
                                .maxHeight(345.678)
                                .maxWidth(456.789)
                                .minHeight(1.678)
                                .minWidth(2.789)
                                .selected(true)
                                .selectionColor(Color.PEACHPUFF)
                                .tagColor(Color.NAVAJOWHITE)
                                .tagVisible(true)
                                .textColor(Color.SLATEGREY)
                                .unit("dB")
                                .minValue(-100)
                                .maxValue(100)
                                .currentValue(23.456)
                                .targetValue(34.567)
                                .targetValueAlwaysVisible(false)
                                .build();

        assertThat(knob1)
            .isNotNull()
            .hasFieldOrPropertyWithValue("backgroundColor", Color.GOLDENROD)
            .hasFieldOrPropertyWithValue("color", Color.CORAL)
            .hasFieldOrPropertyWithValue("currentValue", 23.456)
            .hasFieldOrPropertyWithValue("currentValueColor", Color.GAINSBORO)
            .hasFieldOrPropertyWithValue("decimals", 3)
            .hasFieldOrPropertyWithValue("dragDisabled", true)
            .hasFieldOrPropertyWithValue("extremaVisible", true)
            .hasFieldOrPropertyWithValue("gradientStops", FXCollections.observableArrayList((Stop) null))
            .hasFieldOrPropertyWithValue("indicatorColor", Color.BLUEVIOLET)
            .hasFieldOrPropertyWithValue("id", "pippo")
            .hasFieldOrPropertyWithValue("layoutX", 0.234)
            .hasFieldOrPropertyWithValue("layoutY", 0.567)
            .hasFieldOrPropertyWithValue("maxHeight", 345.678)
            .hasFieldOrPropertyWithValue("maxValue", 100.0)
            .hasFieldOrPropertyWithValue("maxWidth", 456.789)
            .hasFieldOrPropertyWithValue("minHeight", 1.678)
            .hasFieldOrPropertyWithValue("minValue", -100.0)
            .hasFieldOrPropertyWithValue("minWidth", 2.789)
            .hasFieldOrPropertyWithValue("selected", true)
            .hasFieldOrPropertyWithValue("selectionColor", Color.PEACHPUFF)
            .hasFieldOrPropertyWithValue("tagColor", Color.NAVAJOWHITE)
            .hasFieldOrPropertyWithValue("tagVisible", true)
            .hasFieldOrPropertyWithValue("targetValue", 34.567)
            .hasFieldOrPropertyWithValue("targetValueAlwaysVisible", false)
            .hasFieldOrPropertyWithValue("textColor", Color.SLATEGREY)
            .hasFieldOrPropertyWithValue("unit", "dB");

        Knob knob2 = KnobBuilder.create()
                                .backgroundColor(null)
                                .color(null)
                                .currentValueColor(null)
                                .decimals(80)
                                .dragDisabled(false)
                                .extremaVisible(false)
                                .gradientStops((List<Stop>) null)
                                .id(null)
                                .indicatorColor(null)
                                .layoutX(-1)
                                .layoutY(-1)
                                .maxSize(456.789, 345.678)
                                .minSize(2.789, 1.678)
                                .selected(false)
                                .selectionColor(null)
                                .tagColor(null)
                                .tagVisible(false)
                                .textColor(null)
                                .unit(null)
                                .minValue(-100)
                                .maxValue(100)
                                .currentValue(200)
                                .targetValue(200)
                                .targetValueAlwaysVisible(true)
                                .build();

        assertThat(knob2)
            .isNotNull()
            .hasFieldOrPropertyWithValue("backgroundColor", Color.TRANSPARENT)
            .hasFieldOrPropertyWithValue("color", Knob.DEFAULT_COLOR)
            .hasFieldOrPropertyWithValue("currentValue", 100.0)
            .hasFieldOrPropertyWithValue("currentValueColor", Knob.DEFAULT_CURRENT_VALUE_COLOR)
            .hasFieldOrPropertyWithValue("decimals", 6)
            .hasFieldOrPropertyWithValue("dragDisabled", false)
            .hasFieldOrPropertyWithValue("extremaVisible", false)
            .hasFieldOrPropertyWithValue("gradientStops", FXCollections.emptyObservableList())
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("indicatorColor", Knob.DEFAULT_COLOR.darker())
            .hasFieldOrPropertyWithValue("layoutX", -1.0)
            .hasFieldOrPropertyWithValue("layoutY", -1.0)
            .hasFieldOrPropertyWithValue("maxHeight", 345.678)
            .hasFieldOrPropertyWithValue("maxValue", 100.0)
            .hasFieldOrPropertyWithValue("maxWidth", 456.789)
            .hasFieldOrPropertyWithValue("minHeight", 1.678)
            .hasFieldOrPropertyWithValue("minValue", -100.0)
            .hasFieldOrPropertyWithValue("minWidth", 2.789)
            .hasFieldOrPropertyWithValue("selected", false)
            .hasFieldOrPropertyWithValue("selectionColor", Color.WHITE)
            .hasFieldOrPropertyWithValue("tagColor", Color.TRANSPARENT)
            .hasFieldOrPropertyWithValue("tagVisible", false)
            .hasFieldOrPropertyWithValue("targetValue", 100.0)
            .hasFieldOrPropertyWithValue("targetValueAlwaysVisible", true)
            .hasFieldOrPropertyWithValue("textColor", Color.WHITE)
            .hasFieldOrPropertyWithValue("unit", (String) null);

        double min = -100;
        double max =  100;
        List<Stop> stops = Arrays.asList(
            new Stop(( -120 - min ) / ( max - min ), new Color(252/255.0,  13/255.0, 27/255.0, 1.0)),
            new Stop(( -100 - min ) / ( max - min ), new Color(252/255.0, 242/255.0, 17/255.0, 1.0)),
            new Stop((  -80 - min ) / ( max - min ), new Color( 61/255.0, 216/255.0, 61/255.0, 1.0)),
            new Stop((   80 - min ) / ( max - min ), new Color( 61/255.0, 216/255.0, 61/255.0, 1.0)),
            new Stop((  100 - min ) / ( max - min ), new Color(252/255.0, 242/255.0, 17/255.0, 1.0)),
            new Stop((  120 - min ) / ( max - min ), new Color(252/255.0,  13/255.0, 27/255.0, 1.0))
        );
        Knob knob3 = KnobBuilder.create()
                          .backgroundColor(Color.TRANSPARENT)
                          .decimals(-3)
                          .gradientStops(stops)

                          .minValue(min)
                          .maxValue(max)
                          .currentValue(-200)
                          .targetValue(-200)
                          .build();

        assertThat(knob3)
            .isNotNull()
            .hasFieldOrPropertyWithValue("backgroundColor", Color.TRANSPARENT)
            .hasFieldOrPropertyWithValue("color", Knob.DEFAULT_COLOR)
            .hasFieldOrPropertyWithValue("currentValue", -100.0)
            .hasFieldOrPropertyWithValue("currentValueColor", Knob.DEFAULT_CURRENT_VALUE_COLOR)
            .hasFieldOrPropertyWithValue("decimals", 0)
            .hasFieldOrPropertyWithValue("dragDisabled", false)
            .hasFieldOrPropertyWithValue("extremaVisible", false)
            .hasFieldOrPropertyWithValue("gradientStops", FXCollections.observableList(stops))
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("indicatorColor", Knob.DEFAULT_COLOR.darker())
            .hasFieldOrPropertyWithValue("layoutX", 0.0)
            .hasFieldOrPropertyWithValue("layoutY", 0.0)
            .hasFieldOrPropertyWithValue("maxHeight", Knob.MAXIMUM_HEIGHT)
            .hasFieldOrPropertyWithValue("maxValue", max)
            .hasFieldOrPropertyWithValue("maxWidth", Knob.MAXIMUM_WIDTH)
            .hasFieldOrPropertyWithValue("minHeight", Knob.MINIMUM_HEIGHT)
            .hasFieldOrPropertyWithValue("minValue", min)
            .hasFieldOrPropertyWithValue("minWidth", Knob.MINIMUM_WIDTH)
            .hasFieldOrPropertyWithValue("selected", false)
            .hasFieldOrPropertyWithValue("selectionColor", Color.WHITE)
            .hasFieldOrPropertyWithValue("tagColor", Color.RED)
            .hasFieldOrPropertyWithValue("tagVisible", false)
            .hasFieldOrPropertyWithValue("targetValue", -100.0)
            .hasFieldOrPropertyWithValue("targetValueAlwaysVisible", false)
            .hasFieldOrPropertyWithValue("textColor", Color.WHITE)
            .hasFieldOrPropertyWithValue("unit", (String) null);

    }

    /**
     * Test of color method, of class KnobBuilder.
     */
    @Test
    public void testColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().color(value);

        assertThat(builder.properties)
            .containsKey("color");
        assertThat(builder.properties.get("color"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of create method, of class KnobBuilder.
     */
    @Test
    public void testCreate() {

        KnobBuilder builder = KnobBuilder.create();

        assertThat(builder).isNotNull();
        assertThat(builder.properties).isEmpty();

    }

    /**
     * Test of currentValue method, of class KnobBuilder.
     */
    @Test
    public void testCurrentValue() {
        
        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().currentValue(value);

        assertThat(builder.properties)
            .containsKey("currentValue");
        assertThat(builder.properties.get("currentValue"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of currentValueColor method, of class KnobBuilder.
     */
    @Test
    public void testCurrentValueColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().currentValueColor(value);

        assertThat(builder.properties)
            .containsKey("currentValueColor");
        assertThat(builder.properties.get("currentValueColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of decimals method, of class KnobBuilder.
     */
    @Test
    public void testDecimals() {

        int value = 3;
        KnobBuilder builder = KnobBuilder.create().decimals(value);

        assertThat(builder.properties)
            .containsKey("decimals");
        assertThat(builder.properties.get("decimals"))
            .isExactlyInstanceOf(Integer.class)
            .isEqualTo(value);

    }

    /**
     * Test of dragDisabled method, of class KnobBuilder.
     */
    @Test
    public void testDragDisabled() {

        boolean value = true;
        KnobBuilder builder = KnobBuilder.create().dragDisabled(value);

        assertThat(builder.properties)
            .containsKey("dragDisabled");
        assertThat(builder.properties.get("dragDisabled"))
            .isExactlyInstanceOf(Boolean.class)
            .isEqualTo(value);

    }

    /**
     * Test of extremaVisible method, of class KnobBuilder.
     */
    @Test
    public void testExtremaVisible() {

        boolean value = true;
        KnobBuilder builder = KnobBuilder.create().extremaVisible(value);

        assertThat(builder.properties)
            .containsKey("extremaVisible");
        assertThat(builder.properties.get("extremaVisible"))
            .isExactlyInstanceOf(Boolean.class)
            .isEqualTo(value);

    }

    /**
     * Test of gradientStops method, of class KnobBuilder.
     */
    @Test
    public void testGradientStops() {

        Stop stop1 = new Stop(0.0, Color.CORAL);
        Stop stop2 = new Stop(0.3, Color.DARKCYAN);
        Stop stop3 = new Stop(1.0, Color.MEDIUMTURQUOISE);
        List<Stop> value = Arrays.asList(stop1, stop2, stop3);

        KnobBuilder builder = KnobBuilder.create().gradientStops(value);

        assertThat(builder.properties)
            .containsKey("gradientStops");
        assertThat(builder.properties.get("gradientStops"))
            .isInstanceOf(List.class)
            .asList()
                .containsExactly(stop1, stop2, stop3);

        builder = KnobBuilder.create().gradientStops(stop2, stop3, stop1);

        assertThat(builder.properties)
            .containsKey("gradientStops");
        assertThat(builder.properties.get("gradientStops"))
            .isInstanceOf(List.class)
            .asList()
                .containsExactly(stop2, stop3, stop1);

        builder = KnobBuilder.create().gradientStops((List<Stop>) null);

        assertThat(builder.properties)
            .containsKey("gradientStops")
            .contains(entry("gradientStops", null));

    }

    /**
     * Test of id method, of class KnobBuilder.
     */
    @Test
    public void testID() {

        String value = "an identifier";
        KnobBuilder builder = KnobBuilder.create().id(value);

        assertThat(builder.properties)
            .containsKey("id");
        assertThat(builder.properties.get("id"))
            .isExactlyInstanceOf(String.class)
            .isEqualTo(value);

    }

    /**
     * Test of indicatorColor method, of class KnobBuilder.
     */
    @Test
    public void testIndicatorColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().indicatorColor(value);

        assertThat(builder.properties)
            .containsKey("indicatorColor");
        assertThat(builder.properties.get("indicatorColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of layoutX method, of class KnobBuilder.
     */
    @Test
    public void testLayoutX() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().layoutX(value);

        assertThat(builder.properties)
            .containsKey("layoutX");
        assertThat(builder.properties.get("layoutX"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of layoutY method, of class KnobBuilder.
     */
    @Test
    public void testLayoutY() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().layoutY(value);

        assertThat(builder.properties)
            .containsKey("layoutY");
        assertThat(builder.properties.get("layoutY"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of maxHeight method, of class KnobBuilder.
     */
    @Test
    public void testMaxHeight() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().maxHeight(value);

        assertThat(builder.properties)
            .containsKey("maxHeight");
        assertThat(builder.properties.get("maxHeight"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of maxSize method, of class KnobBuilder.
     */
    @Test
    public void testMaxSize() {

        double width = 123.456;
        double height = 234.567;
        KnobBuilder builder = KnobBuilder.create().maxSize(width, height);

        assertThat(builder.properties)
            .containsKey("maxSize");
        assertThat(builder.properties.get("maxSize"))
            .isExactlyInstanceOf(Dimension2D.class)
            .isEqualTo(new Dimension2D(width, height));

    }

    /**
     * Test of maxValue method, of class KnobBuilder.
     */
    @Test
    public void testMaxValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().maxValue(value);

        assertThat(builder.properties)
            .containsKey("maxValue");
        assertThat(builder.properties.get("maxValue"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of maxWidth method, of class KnobBuilder.
     */
    @Test
    public void testMaxWidth() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().maxWidth(value);

        assertThat(builder.properties)
            .containsKey("maxWidth");
        assertThat(builder.properties.get("maxWidth"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of minHeight method, of class KnobBuilder.
     */
    @Test
    public void testMinHeight() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().minHeight(value);

        assertThat(builder.properties)
            .containsKey("minHeight");
        assertThat(builder.properties.get("minHeight"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of minSize method, of class KnobBuilder.
     */
    @Test
    public void testMinSize() {

        double width = 123.456;
        double height = 234.567;
        KnobBuilder builder = KnobBuilder.create().minSize(width, height);

        assertThat(builder.properties)
            .containsKey("minSize");
        assertThat(builder.properties.get("minSize"))
            .isExactlyInstanceOf(Dimension2D.class)
            .isEqualTo(new Dimension2D(width, height));

    }

    /**
     * Test of minValue method, of class KnobBuilder.
     */
    @Test
    public void testMinValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().minValue(value);

        assertThat(builder.properties)
            .containsKey("minValue");
        assertThat(builder.properties.get("minValue"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of minWidth method, of class KnobBuilder.
     */
    @Test
    public void testMinWidth() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().minWidth(value);

        assertThat(builder.properties)
            .containsKey("minWidth");
        assertThat(builder.properties.get("minWidth"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of onAdjusted method, of class KnobBuilder.
     */
    @Test
    public void testOnAdjusted() {

        EventHandler<KnobEvent> value = e -> LOGGER.info("testOnAdjusted handler");
        KnobBuilder builder = KnobBuilder.create().onAdjusted(value);

        assertThat(builder.properties)
            .containsKey("onAdjusted");
        assertThat(builder.properties.get("onAdjusted"))
            .isInstanceOf(EventHandler.class)
            .isEqualTo(value);

    }

    /**
     * Test of onAdjusting method, of class KnobBuilder.
     */
    @Test
    public void testOnAdjusting() {

        EventHandler<KnobEvent> value = e -> LOGGER.info("testOnAdjusting handler");
        KnobBuilder builder = KnobBuilder.create().onAdjusting(value);

        assertThat(builder.properties)
            .containsKey("onAdjusting");
        assertThat(builder.properties.get("onAdjusting"))
            .isInstanceOf(EventHandler.class)
            .isEqualTo(value);

    }

    /**
     * Test of onTargetSet method, of class KnobBuilder.
     */
    @Test
    public void testOnTargetSet() {

        EventHandler<KnobEvent> value = e -> LOGGER.info("testOnTargetSet handler");
        KnobBuilder builder = KnobBuilder.create().onTargetSet(value);

        assertThat(builder.properties)
            .containsKey("onTargetSet");
        assertThat(builder.properties.get("onTargetSet"))
            .isInstanceOf(EventHandler.class)
            .isEqualTo(value);

    }

    /**
     * Test of opacity method, of class KnobBuilder.
     */
    @Test
    public void testOpacity() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().opacity(value);

        assertThat(builder.properties)
            .containsKey("opacity");
        assertThat(builder.properties.get("opacity"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of padding method, of class KnobBuilder.
     */
    @Test
    public void testPadding() {

        Insets insets = new Insets(1, 2, 3, 4);
        KnobBuilder builder = KnobBuilder.create().padding(insets);

        assertThat(builder.properties)
            .containsKey("padding");
        assertThat(builder.properties.get("padding"))
            .isExactlyInstanceOf(Insets.class)
            .isEqualTo(insets);

        builder = KnobBuilder.create().padding(1, 2, 3, 4);

        assertThat(builder.properties)
            .containsKey("padding");
        assertThat(builder.properties.get("padding"))
            .isExactlyInstanceOf(Insets.class)
            .isEqualTo(insets);

        insets = new Insets(7);
        builder = KnobBuilder.create().padding(7);

        assertThat(builder.properties)
            .containsKey("padding");
        assertThat(builder.properties.get("padding"))
            .isExactlyInstanceOf(Insets.class)
            .isEqualTo(insets);

    }

    /**
     * Test of prefHeight method, of class KnobBuilder.
     */
    @Test
    public void testPrefHeight() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().prefHeight(value);

        assertThat(builder.properties)
            .containsKey("prefHeight");
        assertThat(builder.properties.get("prefHeight"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of prefSize method, of class KnobBuilder.
     */
    @Test
    public void testPrefSize() {

        double width = 123.456;
        double height = 234.567;
        KnobBuilder builder = KnobBuilder.create().prefSize(width, height);

        assertThat(builder.properties)
            .containsKey("prefSize");
        assertThat(builder.properties.get("prefSize"))
            .isExactlyInstanceOf(Dimension2D.class)
            .isEqualTo(new Dimension2D(width, height));

    }

    /**
     * Test of prefWidth method, of class KnobBuilder.
     */
    @Test
    public void testPrefWidth() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().prefWidth(value);

        assertThat(builder.properties)
            .containsKey("prefWidth");
        assertThat(builder.properties.get("prefWidth"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of scaleX method, of class KnobBuilder.
     */
    @Test
    public void testScaleX() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().scaleX(value);

        assertThat(builder.properties)
            .containsKey("scaleX");
        assertThat(builder.properties.get("scaleX"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of scaleY method, of class KnobBuilder.
     */
    @Test
    public void testScaleY() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().scaleY(value);

        assertThat(builder.properties)
            .containsKey("scaleY");
        assertThat(builder.properties.get("scaleY"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of selected method, of class KnobBuilder.
     */
    @Test
    public void testSelected() {

        boolean value = true;
        KnobBuilder builder = KnobBuilder.create().selected(value);

        assertThat(builder.properties)
            .containsKey("selected");
        assertThat(builder.properties.get("selected"))
            .isExactlyInstanceOf(Boolean.class)
            .isEqualTo(value);

    }

    /**
     * Test of selectionColor method, of class KnobBuilder.
     */
    @Test
    public void testSelectionColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().selectionColor(value);

        assertThat(builder.properties)
            .containsKey("selectionColor");
        assertThat(builder.properties.get("selectionColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of tagColor method, of class KnobBuilder.
     */
    @Test
    public void testTagColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().tagColor(value);

        assertThat(builder.properties)
            .containsKey("tagColor");
        assertThat(builder.properties.get("tagColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of tagVisible method, of class KnobBuilder.
     */
    @Test
    public void testTagVisible() {

        boolean value = true;
        KnobBuilder builder = KnobBuilder.create().tagVisible(value);

        assertThat(builder.properties)
            .containsKey("tagVisible");
        assertThat(builder.properties.get("tagVisible"))
            .isExactlyInstanceOf(Boolean.class)
            .isEqualTo(value);

    }

    /**
     * Test of targetValue method, of class KnobBuilder.
     */
    @Test
    public void testTargetValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().targetValue(value);

        assertThat(builder.properties)
            .containsKey("targetValue");
        assertThat(builder.properties.get("targetValue"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of targetValueAlwaysVisible method, of class KnobBuilder.
     */
    @Test
    public void testTargetValueAlwaysVisible() {

        boolean value = true;
        KnobBuilder builder = KnobBuilder.create().targetValueAlwaysVisible(value);

        assertThat(builder.properties)
            .containsKey("targetValueAlwaysVisible");
        assertThat(builder.properties.get("targetValueAlwaysVisible"))
            .isExactlyInstanceOf(Boolean.class)
            .isEqualTo(value);

    }

    /**
     * Test of textColor method, of class KnobBuilder.
     */
    @Test
    public void testTextColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().textColor(value);

        assertThat(builder.properties)
            .containsKey("textColor");
        assertThat(builder.properties.get("textColor"))
            .isExactlyInstanceOf(Color.class)
            .isEqualTo(value);

    }

    /**
     * Test of translateX method, of class KnobBuilder.
     */
    @Test
    public void testTranslateX() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().translateX(value);

        assertThat(builder.properties)
            .containsKey("translateX");
        assertThat(builder.properties.get("translateX"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of translateY method, of class KnobBuilder.
     */
    @Test
    public void testTranslateY() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().translateY(value);

        assertThat(builder.properties)
            .containsKey("translateY");
        assertThat(builder.properties.get("translateY"))
            .isExactlyInstanceOf(Double.class)
            .isEqualTo(value);

    }

    /**
     * Test of unit method, of class KnobBuilder.
     */
    @Test
    public void testUnit() {

        String value = "123.456";
        KnobBuilder builder = KnobBuilder.create().unit(value);

        assertThat(builder.properties)
            .containsKey("unit");
        assertThat(builder.properties.get("unit"))
            .isExactlyInstanceOf(String.class)
            .isEqualTo(value);

    }

}
