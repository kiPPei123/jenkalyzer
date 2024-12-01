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
package jenkalyzer.view;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Pane;
import jenkalyzer.model.Configuration;

public class MainWindowContent extends Pane {

	public static final int TAB_CONTENT_MARGIN = 5;

	public MainWindowContent(final Configuration aConfiguration) {
		final TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new Tab("Job", new JobTabContent(aConfiguration)));
		tabPane.getTabs().add(new Tab("Patterns", new PatternsTabContent(aConfiguration)));
		tabPane.getTabs().add(new Tab("Results", new ResultsTabContent(aConfiguration)));
		getChildren().add(tabPane);

		tabPane.setPrefSize(800, 600);
	}
}
