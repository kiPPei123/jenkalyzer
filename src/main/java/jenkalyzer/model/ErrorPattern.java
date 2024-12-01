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
import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorPattern {

	private final ObjectProperty<ErrorPatternType> typeproperty = new SimpleObjectProperty<>(
			ErrorPatternType.RAW_STRING);
	private final StringProperty nameProperty = new SimpleStringProperty();
	private final StringProperty stringProperty = new SimpleStringProperty();

	public ErrorPattern() {

	}

	public ObjectProperty<ErrorPatternType> typeproperty() {
		return typeproperty;
	}

	public StringProperty nameProperty() {
		return nameProperty;
	}

	public StringProperty stringProperty() {
		return stringProperty;
	}

	public static ErrorPattern fromStream(final DataInputStream aStream) throws IOException {
		final ErrorPattern result = new ErrorPattern();
		result.typeproperty()
				.set(aStream.readChar() == 'S' ? ErrorPatternType.RAW_STRING : ErrorPatternType.REGULAR_EXPRESSION);
		result.nameProperty().set(aStream.readUTF());
		result.stringProperty().set(aStream.readUTF());
		return result;
	}

	public void toStream(final DataOutputStream aStream) throws IOException {
		aStream.writeChar(typeproperty().get() == ErrorPatternType.RAW_STRING ? 'S' : 'R');
		writeUTF(aStream, nameProperty().get());
		writeUTF(aStream, stringProperty().get());
	}

	private static void writeUTF(final DataOutputStream aStream, final String aString) throws IOException {
		aStream.writeUTF(aString != null ? aString : "");
	}
}
