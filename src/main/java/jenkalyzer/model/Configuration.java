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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Configuration {
	private final Path configDir = Path.of(System.getenv("AppData"), "jenkalyzer");
	private final Path jobUrlFile = configDir.resolve("joburl.txt");
	private final Path patternsFile = configDir.resolve("patterns.txt");

	private final StringProperty jobUrl = new SimpleStringProperty(null);
	private final ListProperty<ErrorPattern> errorPatterns = new SimpleListProperty<>();

	public Configuration() {
		try {
			load();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		final ChangeListener<? super Object> saveListener = (aObs, aOld, aNew) -> {
			try {
				save();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		};
		jobUrl.addListener(saveListener);
		errorPatterns.addListener(saveListener);
	}

	public StringProperty jobUrlProperty() {
		return jobUrl;
	}

	public ListProperty<ErrorPattern> errorPatternsProperty() {
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
			final List<String> lines = Files.readAllLines(patternsFile);
			for (final String unstripped : lines) {
				final String line = unstripped.strip();
				if (line.length() < 2)
					continue;
				errorPatterns.add(new ErrorPattern(
						line.charAt(0) == 'S' ? ErrorPatternType.RAW_STRING : ErrorPatternType.REGULAR_EXPRESSION,
						line.substring(1)));
			}
		}
	}

	public void save() throws IOException {
		System.out.println("Saving configuration");
		if (!configDir.toFile().exists()) {
			configDir.toFile().mkdir();
		}
		Files.writeString(jobUrlFile, jobUrl.get() != null ? jobUrl.get() : "");
		final List<String> patternLines = errorPatterns.stream()
				.map(ep -> (ep.type() == ErrorPatternType.RAW_STRING ? "S" : "R") + ep.string()).toList();
		Files.write(patternsFile, patternLines);
	}
}
