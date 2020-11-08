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
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ponclure
 */
public class MineSkinFetcher {

	private static final String MINESKIN_API = "https://api.mineskin.org/get/id/";
	private static final String MOJANG_SESSIONS = "https://sessionserver.mojang.com/session/minecraft/profile/{0}?unsigned=false";
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

	public static void fetchSkinFromIdAsync(int id, Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				StringBuilder builder = new StringBuilder();
				HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API + id).openConnection();
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.connect();

				Scanner scanner = new Scanner(httpURLConnection.getInputStream());
				while (scanner.hasNextLine()) {
					builder.append(scanner.nextLine());
				}

				scanner.close();
				httpURLConnection.disconnect();

				JsonObject jsonObject = (JsonObject) new JsonParser().parse(builder.toString());
				JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
				String value = textures.get("value").getAsString();
				String signature = textures.get("signature").getAsString();

				callback.call(new Skin(value, signature));
			} catch (IOException exception) {
				Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
				exception.printStackTrace();
				callback.call(null);
			}
		});
	}

	public static void fetchSkinFromUsernameAsync(String username, Callback callback) {
		Player player = Bukkit.getPlayer(username);
		if (player != null) {
			fetchSkinFromUuidAsync(player.getUniqueId(), username, callback);
		} else {
			Bukkit.getLogger().severe("Could not fetch skin! (Username: " + username + ").");
			// let's ensure the callback stays async
			EXECUTOR.execute(() -> callback.call(null));
		}
	}

	public static void fetchSkinFromUuidAsync(UUID uuid, String username, Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				String link = MOJANG_SESSIONS.replace("{0}", uuid.toString());
				HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(link).openConnection();
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.connect();

				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				while (true) {
					String line = br.readLine();
					if (line == null) {
						break;
					}
					sb.append(line);
				}
				httpURLConnection.disconnect();

				JsonObject json = (JsonObject) new JsonParser().parse(sb.toString());
				JsonObject properties = json.get("properties").getAsJsonObject();
				String value = properties.get("value").getAsString();
				String signature = properties.get("signature").getAsString();

				callback.call(new Skin(value, signature));
			} catch (IOException exception) {
				Bukkit.getLogger().severe("Could not fetch skin! (Username: " + username + ").");
				exception.printStackTrace();
				callback.call(null);
			}
		});
	}

	public interface Callback {

		void call(Skin skinData);

	}
}
