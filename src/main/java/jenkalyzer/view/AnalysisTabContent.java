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

import java.util.Objects;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jenkalyzer.model.Configuration;
import jenkalyzer.model.LogDataBase;

public class AnalysisTabContent extends VBox {
	private final LogDataBase logDataBase;

	public AnalysisTabContent(final Configuration aConfiguration, final LogDataBase aLogDataBase) {
		logDataBase = Objects.requireNonNull(aLogDataBase);
		final Label label = new Label("Results");
		getChildren().add(label);

		final int m = MainWindowContent.TAB_CONTENT_MARGIN;
		setMargin(label, new Insets(m, m, 0, m));
//		setMargin(jobUrlField, new Insets(m));
	}
}
