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


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 8 Aug 2017
 */
public class KnobEvaluator extends Application {

    public static void main( String[] args ) {
        launch(args);
    }

    /**
     * Initialize the given {@link Stage} and return it.
     *
     * @param stage The {@link Stage} to be initialized.
     * @return Return the given {@link Stage} after being initialized.
     */
    static Stage initStage ( Stage stage ) {

        HBox pane = new HBox();

        pane.setSpacing(20);
        pane.setPadding(new Insets(20));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(66, 71, 79), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setScene(scene);

        return stage;

    }

    @Override
    public void start( Stage stage ) throws Exception {
        initStage(stage).show();
    }

}
