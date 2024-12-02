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

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jenkalyzer.model.Configuration;
import jenkalyzer.model.LogDataBase;

public class LogsTabContent extends VBox {
	public LogsTabContent(final Configuration aConfiguration, final LogDataBase aLogDataBase) {
		final Label urlLabel = new Label("Job URL");
		final TextField jobUrlField = new TextField();
		jobUrlField.textProperty().bindBidirectional(aConfiguration.jobUrlProperty());
		getChildren().add(urlLabel);
		getChildren().add(jobUrlField);

		final Label jobNameValueLabel = new Label();
		jobNameValueLabel.textProperty().bind(aLogDataBase.jobNameProperty());

		final HBox jobNameContainer = new HBox(MainWindowContent.TAB_CONTENT_MARGIN, new Label("Job name:"),
				jobNameValueLabel);
		getChildren().add(jobNameContainer);

		final Label latestBuildValueLabel = new Label();
		latestBuildValueLabel.textProperty().bind(aLogDataBase.highestBuildNumberProperty().asString());

		final HBox latestBuildContainer = new HBox(MainWindowContent.TAB_CONTENT_MARGIN, new Label("Latest build:"),
				latestBuildValueLabel);
		getChildren().add(latestBuildContainer);

		final Button syncButton = new Button("Sync");
		syncButton.setOnAction(event -> aLogDataBase.download());
		getChildren().add(syncButton);

		final int m = MainWindowContent.TAB_CONTENT_MARGIN;
		final Insets insets = new Insets(m, m, 0, m);
		setMargin(urlLabel, insets);
		setMargin(jobUrlField, insets);
		setMargin(new Label("Job name"), insets);
		setMargin(jobNameContainer, insets);
		setMargin(latestBuildContainer, insets);
		setMargin(syncButton, new Insets(m));
	}
}
