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
package jenkalyzer.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Configuration {
	private final int MAGIC_COOKIE = 3873480;

	private final Path configDir = Path.of(System.getenv("AppData"), "jenkalyzer");
	private final Path jobUrlFile = configDir.resolve("joburl.txt");
	private final Path patternsFile = configDir.resolve("patterns");

	private final StringProperty jobUrl = new SimpleStringProperty(null);
	private final ObservableList<ErrorPattern> errorPatterns = FXCollections.observableArrayList();

	public Configuration() {
		try {
			load();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		jobUrl.addListener((ChangeListener<Object>) (aObs, aOld, aNew) -> save());
		errorPatterns.addListener((ListChangeListener<ErrorPattern>) aChange -> {
			save();
			while (aChange.next()) {
				for (final ErrorPattern addedPattern : aChange.getAddedSubList()) {
					addSaveListener(addedPattern);
				}
			}
		});
		for (final ErrorPattern addedPattern : errorPatterns) {
			addSaveListener(addedPattern);
		}
	}

	private void addSaveListener(final ErrorPattern aPattern) {
		aPattern.typeproperty().addListener((aObs, aOld, aNew) -> save());
		aPattern.nameProperty().addListener((aObs, aOld, aNew) -> save());
		aPattern.stringProperty().addListener((aObs, aOld, aNew) -> save());
	}

	public Path getConfigDir() {
		return configDir;
	}

	public StringProperty jobUrlProperty() {
		return jobUrl;
	}

	public ObservableList<ErrorPattern> getErrorPatterns() {
		return errorPatterns;
	}

	private void load() throws IOException {
		System.out.println("Loading configuration");
		jobUrl.set(null);
		errorPatterns.clear();

		if (jobUrlFile.toFile().isFile()) {
			jobUrl.set(Files.readString(jobUrlFile).strip());
		}
		if (jobUrlFile.toFile().isFile()) {
			try (DataInputStream stream = new DataInputStream(new FileInputStream(patternsFile.toFile()))) {
				if (stream.readInt() != MAGIC_COOKIE) {
					return;
				}
				final int count = stream.readInt();
				for (int i = 0; i < count; i++) {
					errorPatterns.add(ErrorPattern.fromStream(stream));
				}
			}
		}
	}

	public void save() {
		System.out.println("Saving configuration");
		if (!configDir.toFile().exists()) {
			configDir.toFile().mkdir();
		}
		try {
			Files.writeString(jobUrlFile, jobUrl.get() != null ? jobUrl.get() : "");
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		try (final DataOutputStream stream = new DataOutputStream(new FileOutputStream(patternsFile.toFile()))) {
			stream.writeInt(MAGIC_COOKIE);
			stream.writeInt(errorPatterns.size());
			for (final ErrorPattern errorPattern : errorPatterns) {
				errorPattern.toStream(stream);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
