package com.github.simplenpcframework.api.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;

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
			}
		});
	}
	
	public static void fetchSkinFromUUIDAsync(UUID uuid, Callback callback) {
		fetchSkinFromUsernameAsync(Bukkit.getPlayer(uuid).getName(), callback);
	}

	public static void fetchSkinFromUsernameAsync(String username, Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				String link = "https://sessionserver.mojang.com/session/minecraft/profile/"
						+ Bukkit.getPlayer(username).getUniqueId() + "?unsigned=false";
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
				Bukkit.getLogger().severe(
						"Could not fetch skin! (Username: " + username + "). Message: " + exception.getMessage());
				exception.printStackTrace();
			}
		});
	}

	public interface Callback {

		void call(Skin skinData);

	}
}
