package com.travelplanner.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public final class RememberMeUtil {

	public static final String COOKIE_NAME = "travelmate_remember";

	public static final int MAX_AGE_SECONDS = 30 * 24 * 60 * 60;

	private static final String SECRET = "TravelMate-AI-remember-me-secret-2026";

	private static final long MAX_AGE_MILLIS = MAX_AGE_SECONDS * 1000L;

	private RememberMeUtil() {
	}

	public static String createToken(int userId) {

		long expiry = Instant.now().toEpochMilli() + MAX_AGE_MILLIS;

		String payload = userId + ":" + expiry;

		String signature = hmac(payload);

		String token = payload + ":" + signature;

		return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
	}

	public static Optional<Integer> getUserId(String token) {

		if (token == null || token.isBlank()) {
			return Optional.empty();
		}

		try {

			String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);

			String[] parts = decoded.split(":");

			if (parts.length != 3) {
				return Optional.empty();
			}

			String payload = parts[0] + ":" + parts[1];

			if (!hmac(payload).equals(parts[2])) {
				return Optional.empty();
			}

			long expiry = Long.parseLong(parts[1]);

			if (expiry < Instant.now().toEpochMilli()) {
				return Optional.empty();
			}

			return Optional.of(Integer.parseInt(parts[0]));

		} catch (RuntimeException exception) {
			return Optional.empty();
		}
	}

	private static String hmac(String data) {

		try {

			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);

			md.update(keyBytes);

			md.update((byte) 0);

			md.update(data.getBytes(StandardCharsets.UTF_8));

			byte[] hash = md.digest();

			StringBuilder sb = new StringBuilder();

			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}

			return sb.toString();

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
