package com.github.ponclure.simplenpcframework.api.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncSkinFetcher {

	private static final String MINESKIN_API = "https://api.mineskin.org/get/id/";
	private static final String MOJANG_SESSIONS = "https://sessionserver.mojang.com/session/minecraft/profile/{0}?unsigned=false";
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

	private static String getRequest(final String url) throws IOException {
		final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.connect();
		final StringBuilder sb = new StringBuilder();
		final BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
		while (true) {
			final String line = br.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		httpURLConnection.disconnect();
		br.close();
		return sb.toString();
	}


	public static void fetchSkinFromIdAsync(final int id, final Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				final String request = getRequest(MINESKIN_API + id);
				final JsonObject jsonObject = new JsonParser().parse(request).getAsJsonObject();
				final JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
				final String value = textures.get("value").getAsString();
				final String signature = textures.get("signature").getAsString();
				callback.call(new Skin(value, signature));
			} catch (final IOException exception) {
				Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
				exception.printStackTrace();
				callback.call(null);
			}
		});
	}

	/*
	 *  @Deprecated replaced by fetchSkinFromUuidAsync(UUID uuid, Callback callback)
	 *  Player names are mutable and UUID should be the standard method.
	 */
	@Deprecated
	public static void fetchSkinFromUsernameAsync(final String username, final Callback callback) {
		final Player player = Bukkit.getPlayer(username);
		if (player != null) {
			fetchSkinFromUuidAsync(player.getUniqueId(), callback);
		} else {
			Bukkit.getLogger().severe("Could not fetch skin! (Username: " + username + ").");
			// let's ensure the callback stays async
			EXECUTOR.execute(() -> callback.call(null));
		}
	}

	public static void fetchSkinFromUuidAsync(final UUID uuid, final Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				final String link = MOJANG_SESSIONS.replace("{0}", uuid.toString());
				final String request = getRequest(link);
				final JsonObject json = new JsonParser().parse(request).getAsJsonObject();
				final JsonObject properties = json.get("properties").getAsJsonArray().get(0).getAsJsonObject();
				final String value = properties.get("value").getAsString();
				final String signature = properties.get("signature").getAsString();
				callback.call(new Skin(value, signature));
			} catch (final IOException exception) {
				Bukkit.getLogger().severe("Could not fetch skin! (UUID: " + uuid + ").");
				exception.printStackTrace();
				callback.call(null);
			}
		});
	}

	public interface Callback {

		void call(Skin skinData);
	}
}
