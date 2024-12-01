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

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jenkalyzer.model.Configuration;
import jenkalyzer.model.ErrorPattern;
import jenkalyzer.model.ErrorPatternType;

public class PatternsTabContent extends VBox {

	public PatternsTabContent(final Configuration aConfiguration) {
		final Button addButton = new Button("Add");
		addButton.setOnAction(event -> aConfiguration.getErrorPatterns().add(new ErrorPattern()));
		final Node patternsListView = createListView(aConfiguration);
		getChildren().add(addButton);
		getChildren().add(patternsListView);

		final int m = MainWindowContent.TAB_CONTENT_MARGIN;
		setMargin(addButton, new Insets(m, m, 0, m));
		setMargin(patternsListView, new Insets(m));
	}

	private Node createListView(final Configuration aConfiguration) {
		final VBox listView = new VBox(MainWindowContent.TAB_CONTENT_MARGIN);
		aConfiguration.getErrorPatterns()
				.addListener((ListChangeListener<ErrorPattern>) change -> updateListView(listView,
						aConfiguration.getErrorPatterns()));
		updateListView(listView, aConfiguration.getErrorPatterns());
		return listView;
	}

	private void updateListView(final VBox listView, final ObservableList<ErrorPattern> aList) {
		listView.getChildren().clear();
		for (final ErrorPattern ep : aList) {
			final Label nameLabel = new Label("Name");
			final Label typeLabel = new Label("Type");
			final Label stringLabel = new Label("String");
			final Button deleteButton = new Button("Delete");
			deleteButton.setOnAction(event -> aList.remove(ep));
			final ComboBox<ErrorPatternType> typeBox = new ComboBox<>(FXCollections
					.observableArrayList(List.of(ErrorPatternType.RAW_STRING, ErrorPatternType.REGULAR_EXPRESSION)));
			typeBox.valueProperty().bindBidirectional(ep.typeproperty());
			final TextField nameField = new TextField();
			nameField.textProperty().bindBidirectional(ep.nameProperty());
			final TextField stringField = new TextField();
			stringField.textProperty().bindBidirectional(ep.stringProperty());
			final HBox hBox = new HBox(nameLabel, nameField, stringLabel, stringField, typeLabel, typeBox,
					deleteButton);
			listView.getChildren().add(hBox);

			hBox.setSpacing(MainWindowContent.TAB_CONTENT_MARGIN);
			hBox.setAlignment(Pos.CENTER_LEFT);
		}
	}
}
