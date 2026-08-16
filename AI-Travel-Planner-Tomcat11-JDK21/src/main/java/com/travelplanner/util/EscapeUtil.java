package com.travelplanner.util;

/**
 * Minimal HTML escaping used when user-controlled values are rendered inside
 * JSP output so script/style injection is neutralised.
 */
public final class EscapeUtil {

	private EscapeUtil() {
	}

	public static String escapeHtml(String value) {

		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
