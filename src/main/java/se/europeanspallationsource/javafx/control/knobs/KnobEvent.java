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


import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @author HanSolo, creator of the original Regulators classes.
 * @version 1.0.0 9 Aug 2017
 * @see <a href="https://github.com/HanSolo/regulators">HanSolo's Regulators</a>
 */
@SuppressWarnings( { "ClassWithoutLogger", "CloneableImplementsClone" } )
public class KnobEvent extends Event {

    public static final EventType<KnobEvent> ADJUSTED = new EventType<>(ANY, "adjusted");
    public static final EventType<KnobEvent> ADJUSTING = new EventType<>(ANY, "adjusting");
    public static final EventType<KnobEvent> TARGET_SET = new EventType<>(ANY, "targetSet");

    private static final long serialVersionUID = -4112513825652025391L;

    public KnobEvent( final EventType<KnobEvent> type ) {
        super(type);
    }

    public KnobEvent( final Object source, final EventTarget target, final EventType<KnobEvent> type ) {
        super(source, target, type);
    }

}
