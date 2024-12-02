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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogDataBase {

	private final Set<Integer> downloadedBuildNumbers = new HashSet<>();
	private final Configuration configuration;
	private Path logDir = null;

	public LogDataBase(final Configuration aConfiguration) {
		configuration = Objects.requireNonNull(aConfiguration);
		aConfiguration.jobUrlProperty().addListener((aObs, aOld, aNew) -> update());
		update();
	}

	private void update() {
		try {
			final String urlPath = new URL(configuration.jobUrlProperty().get()).getPath();
			final Matcher jobMatch = Pattern.compile("^/job/([^/]*)/?$").matcher(urlPath);
			if (!jobMatch.matches()) {
				System.err.println("Could not parse URL.");
			}
			final String jobName = jobMatch.group(1);
			logDir = configuration.getConfigDir().resolve("logs").resolve(jobName);
			if (!logDir.toFile().exists())
				logDir.toFile().mkdirs();

			refreshDownloadedBuildNumberSet();

		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void refreshDownloadedBuildNumberSet() {
		downloadedBuildNumbers.clear();
		for (final File file : Arrays.stream(logDir.toFile().listFiles()).toList()) {
			final Matcher nameMatch = Pattern.compile("^([0-9]+)\\.txt$").matcher(file.getName());
			if (!nameMatch.matches())
				continue;
			downloadedBuildNumbers.add(Integer.parseInt(nameMatch.group(1)));
		}
		System.out.println("Found build numbers: " + downloadedBuildNumbers);
	}

	public void download() {
		final String jobUrl = configuration.jobUrlProperty().get();
		try {
			final int maxDownloadedNumber = downloadedBuildNumbers.stream().max(Integer::compareTo).orElse(0);
			final int maxActual = requestMaxBuildNumber();
			for (int i = maxDownloadedNumber + 1; i <= maxActual; i++) {
				final URL url = new URL(jobUrl + (jobUrl.endsWith("/") ? "" : "/") + i + "/consoleText");
				System.out.println("Requesting " + url);
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				if (con.getResponseCode() != 200) {
					System.err.println("Bad HTTP response: " + con.getResponseCode());
					continue;
				}
				try (FileOutputStream fileStream = new FileOutputStream(logDir.resolve(i + ".txt").toFile())) {
					con.getInputStream().transferTo(fileStream);
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private int requestMaxBuildNumber() throws IOException {
		final String jobUrl = configuration.jobUrlProperty().get();
		final URL url = new URL(jobUrl + (jobUrl.endsWith("/") ? "" : "/") + "api/xml");
		System.out.println("Requesting " + url);
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		if (con.getResponseCode() != 200) {
			throw new IOException("Bad HTTP response: " + con.getResponseCode());
		}
		int maxNumber = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				final Pattern pattern = Pattern.compile(".*<number>([0-9]+)</number>.*");
				final Matcher matcher = pattern.matcher(line);
				if (!matcher.matches())
					continue;
				maxNumber = Math.max(maxNumber, Integer.parseInt(matcher.group(1)));
			}
		}
		System.out.println("Got latest build number: " + maxNumber);
		return maxNumber;
	}
}
