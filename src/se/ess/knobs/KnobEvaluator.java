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


import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 8 Aug 2017
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class KnobEvaluator extends Application implements Initializable {

    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void initialize( URL location, ResourceBundle resources ) {
    }

    @Override
    public void start( Stage stage ) throws Exception {

        Parent root = FXMLLoader.load(KnobEvaluator.class.getResource("/fxml/KnobController.fxml"));
        Scene scene = new Scene(root);

        //  No more needed, because included in the FXML file.
        //scene.getStylesheets().add("/styles/dark-style.css");

        stage.setTitle("Knob Evaluator");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

}
