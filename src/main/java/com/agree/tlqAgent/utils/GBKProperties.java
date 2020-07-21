package com.agree.tlqAgent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class GBKProperties extends Properties {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String whiteSpaceChars = " \t\r\n\f";
	private static final String keyValueSeparators = "=: \t\r\n\f";
	private static final String strictKeyValueSeparators = "=:";

	private boolean continueLine(String line) {
		int slashCount = 0;
		int index = line.length() - 1;
		while (index >= 0 && line.charAt(index--) == '\\') {
			slashCount++;
		}
		return slashCount % 2 == 1;
	}

	public synchronized void load(InputStreamReader in) throws IOException {
		BufferedReader bf = new BufferedReader(in);
		while (true) {
			String line = bf.readLine();
			if (line == null) {
				return;
			}
			if (line.length() > 0) {
				int len = line.length();
				int keyStart;

				for (keyStart = 0; keyStart < len; keyStart++) {
					if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == 1) {
						break;
					}
				}

				if (keyStart == len) {
					continue;
				}

				char firstChar = line.charAt(keyStart);
				if (firstChar != '#' && firstChar != '!') {
					while (continueLine(line)) {
						String nextLine = bf.readLine();
						if (nextLine == null) {
							nextLine = "";
						}
						String loppedLine = line.substring(0, len - 1);

						int startIndex;
						for (startIndex = 0; startIndex < nextLine.length(); startIndex++) {
							if (whiteSpaceChars.indexOf(nextLine
									.charAt(startIndex)) == -1) {
								break;
							}
						}

						nextLine = nextLine.substring(startIndex, nextLine
								.length());
						line = new String(loppedLine + nextLine);
						len = line.length();
					}

					int separatorIndex;
					for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
						char currentChar = line.charAt(separatorIndex);
						if (currentChar == '\\') {
							separatorIndex++;
						} else if (keyValueSeparators.indexOf(currentChar) != -1) {
							break;
						}
					}

					int valueIndex;
					for (valueIndex = separatorIndex; valueIndex < len; valueIndex++) {
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1) {
							break;
						}
					}

					if (valueIndex < len) {
						if (strictKeyValueSeparators.indexOf(line
								.charAt(valueIndex)) != -1) {
							valueIndex++;
						}
					}

					while (valueIndex < len) {
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1) {
							break;
						}
						valueIndex++;
					}

					String key = line.substring(keyStart, separatorIndex);
					String value = (separatorIndex < len) ? line.substring(
							valueIndex, len) : "";

					key = loadConvert(key);
					value = loadConvert(value);

					int lastSharpIndex = value.lastIndexOf('#');
					if (lastSharpIndex > 0) {
						value = value.substring(0, lastSharpIndex).trim();
					}
					put(key, value);
				}
			}
		}
	}

	private String loadConvert(String str) {
		char c;
		int len = str.length();
		StringBuffer bf = new StringBuffer(len);

		for (int x = 0; x < len;) {
			c = str.charAt(x++);
			if (c == '\\') {
				if (c == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						c = str.charAt(x++);
						switch (c) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + c - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + c - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + c - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed \\uxxxx encoding.");
						}
					}
					bf.append((char) value);
				} else {
					if (c == 't')
						c = '\t';
					else if (c == 'r')
						c = '\r';
					else if (c == 'n')
						c = '\n';
					else if (c == 'f')
						c = '\f';
					bf.append(c);
				}
			} else {
				bf.append(c);
			}
		}
		return bf.toString();
	}
}