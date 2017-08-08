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


import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.paint.Color;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


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
     * Test of color method, of class KnobBuilder.
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public void testColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().color(value);

        assertNotNull(builder.properties.get("color"));
        assertEquals(SimpleObjectProperty.class, builder.properties.get("color").getClass());
        assertEquals(value, ((ObservableObjectValue<Color>) builder.properties.get("color")).get());

    }

    /**
     * Test of create method, of class KnobBuilder.
     */
    @Test
    public void testCreate() {

        KnobBuilder builder = KnobBuilder.create();

        assertNotNull(builder);
        assertTrue(builder.properties.isEmpty());

    }

    /**
     * Test of currentValue method, of class KnobBuilder.
     */
    @Test
    public void testCurrentValue() {
        
        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().currentValue(value);

        assertNotNull(builder.properties.get("currentValue"));
        assertEquals(SimpleDoubleProperty.class, builder.properties.get("currentValue").getClass());
        assertEquals(value, ((ObservableDoubleValue) builder.properties.get("currentValue")).get(), 0.0001);

    }

    /**
     * Test of maxValue method, of class KnobBuilder.
     */
    @Test
    public void testMaxValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().maxValue(value);

        assertNotNull(builder.properties.get("maxValue"));
        assertEquals(SimpleDoubleProperty.class, builder.properties.get("maxValue").getClass());
        assertEquals(value, ((ObservableDoubleValue) builder.properties.get("maxValue")).get(), 0.0001);

    }

    /**
     * Test of minValue method, of class KnobBuilder.
     */
    @Test
    public void testMinValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().minValue(value);

        assertNotNull(builder.properties.get("minValue"));
        assertEquals(SimpleDoubleProperty.class, builder.properties.get("minValue").getClass());
        assertEquals(value, ((ObservableDoubleValue) builder.properties.get("minValue")).get(), 0.0001);

    }

    /**
     * Test of targetValue method, of class KnobBuilder.
     */
    @Test
    public void testTargetValue() {

        double value = 123.456;
        KnobBuilder builder = KnobBuilder.create().targetValue(value);

        assertNotNull(builder.properties.get("targetValue"));
        assertEquals(SimpleDoubleProperty.class, builder.properties.get("targetValue").getClass());
        assertEquals(value, ((ObservableDoubleValue) builder.properties.get("targetValue")).get(), 0.0001);

    }

    /**
     * Test of textColor method, of class KnobBuilder.
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public void testTextColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().textColor(value);

        assertNotNull(builder.properties.get("textColor"));
        assertEquals(SimpleObjectProperty.class, builder.properties.get("textColor").getClass());
        assertEquals(value, ((ObservableObjectValue<Color>) builder.properties.get("textColor")).get());

    }

    /**
     * Test of unit method, of class KnobBuilder.
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public void testUnit() {

        String value = "123.456";
        KnobBuilder builder = KnobBuilder.create().unit(value);

        assertNotNull(builder.properties.get("unit"));
        assertEquals(SimpleStringProperty.class, builder.properties.get("unit").getClass());
        assertEquals(value, ((ObservableObjectValue<String>) builder.properties.get("unit")).get());

    }

    /**
     * Test of unitColor method, of class KnobBuilder.
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public void testUnitColor() {

        Color value = Color.GOLDENROD;
        KnobBuilder builder = KnobBuilder.create().unitColor(value);

        assertNotNull(builder.properties.get("unitColor"));
        assertEquals(SimpleObjectProperty.class, builder.properties.get("unitColor").getClass());
        assertEquals(value, ((ObservableObjectValue<Color>) builder.properties.get("unitColor")).get());

    }

}
