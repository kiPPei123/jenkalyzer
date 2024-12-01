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

public enum ErrorPatternType {
	RAW_STRING, REGULAR_EXPRESSION;

	@Override
	public String toString() {
		switch (this) {
		case RAW_STRING: {
			return "String";
		}
		case REGULAR_EXPRESSION: {
			return "Regex";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
