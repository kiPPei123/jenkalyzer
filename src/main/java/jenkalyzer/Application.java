/*
 * This file is part of "Jenkalyzer".
 * Copyright (c) 2024 Patrik Larsson.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jenkalyzer;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage aPrimaryStage) throws Exception {
		Label label = new Label("Hello");
		HBox root = new HBox(label);
		Scene scene = new Scene(root);
		aPrimaryStage.setScene(scene);
		aPrimaryStage.show();
	}
}
