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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jenkalyzer.model.Configuration;
import jenkalyzer.model.ErrorPattern;
import jenkalyzer.model.LogDataBase;

public class AnalysisTabContent extends VBox {

	public AnalysisTabContent(final Configuration aConfiguration, final LogDataBase aLogDataBase) {
		final CheckBox successBuildsCheckBox = new CheckBox("Include successful builds");
		getChildren().add(successBuildsCheckBox);
		final TextField nrRowsField = new TextField("100");
		final HBox nrRowsContainer = new HBox(MainWindowContent.TAB_CONTENT_MARGIN, new Label("Max number of rows"),
				nrRowsField);
		nrRowsContainer.setAlignment(Pos.CENTER_LEFT);
		getChildren().add(nrRowsContainer);

		final Button analyzeButton = new Button("Analyze now");
		getChildren().add(analyzeButton);
		final Label resultsHeader = new Label("Results");
		getChildren().add(resultsHeader);
		final VBox resultsListView = new VBox(MainWindowContent.TAB_CONTENT_MARGIN);
		final ScrollPane scrollPane = new ScrollPane(resultsListView);
		getChildren().add(scrollPane);

		analyzeButton.setOnAction(event -> analyze(aConfiguration, aLogDataBase, resultsListView,
				successBuildsCheckBox.isSelected(), Integer.parseInt(nrRowsField.getText())));

		final int m = MainWindowContent.TAB_CONTENT_MARGIN;
		final Insets insets = new Insets(m, m, 0, m);
		setMargin(successBuildsCheckBox, insets);
		setMargin(nrRowsContainer, insets);
		setMargin(analyzeButton, insets);
		setMargin(resultsHeader, insets);
		setMargin(scrollPane, new Insets(m));
	}

	private static void analyze(final Configuration aConfiguration, final LogDataBase aLogDataBase,
			final VBox aListView, final boolean aIncludeSuccessfulBuilds, final int aMaxRows) {
		aListView.getChildren().clear();
		final List<Integer> sortedBuildNumbers = aLogDataBase.getDownloadedBuildNumbers().stream()
				.sorted(Comparator.reverseOrder()).toList();
		for (final int buildNumber : sortedBuildNumbers) {
			if (aListView.getChildren().size() >= aMaxRows)
				break;
			final List<String> logLines = aLogDataBase.getLogLines(buildNumber);
			if (logLines.isEmpty())
				continue;
			if (!aIncludeSuccessfulBuilds && "Finished: SUCCESS".equals(logLines.get(logLines.size() - 1)))
				continue;

			final Optional<ErrorPattern> matchingPattern = aConfiguration.getErrorPatterns().stream()
					.filter(ep -> patternMatchesAnyLine(ep, logLines)).findFirst();

			final Label label = new Label(Integer.toString(buildNumber));
			final Label matchingLabel = new Label(matchingPattern.map(ep -> ep.nameProperty().get()).orElse(null));

			final HBox hBox = new HBox(MainWindowContent.TAB_CONTENT_MARGIN, label, matchingLabel);
			aListView.getChildren().add(hBox);
			hBox.setAlignment(Pos.CENTER_LEFT);
			label.setPrefWidth(50);
			matchingLabel.setPrefWidth(200);
		}
	}

	private static boolean patternMatchesAnyLine(final ErrorPattern aPattern, final List<String> aLines) {
		return aLines.stream().anyMatch(l -> patternMatchesLine(aPattern, l));
	}

	private static boolean patternMatchesLine(final ErrorPattern aPattern, final String aLine) {
		return switch (aPattern.typeproperty().get()) {
		case RAW_STRING -> aLine.contains(aPattern.stringProperty().get());
		case REGULAR_EXPRESSION -> Pattern.compile(aPattern.stringProperty().get()).matcher(aLine).find();
		default -> throw new IllegalArgumentException("Unexpected value: " + aPattern.typeproperty().get());
		};
	}
}
