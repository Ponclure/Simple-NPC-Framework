package com.github.ponclure.simplenpcframework.api.skin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AsyncSkinFetcher {
	
	private static final String MINESKIN_API = "https://api.mineskin.org/get/id/";
	private static final String MOJANG_SESSIONS = "https://sessionserver.mojang.com/session/minecraft/profile/{0}?unsigned=false";
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	
	private static String getRequest(String url) throws MalformedURLException, IOException {
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
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
		br.close();
		return sb.toString();
	}
	

	public static void fetchSkinFromIdAsync(int id, Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				String request = getRequest(MINESKIN_API + id);
				JsonObject jsonObject = (JsonObject) new JsonParser().parse(request);
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
	
	/* 
	 *  @Deprecated replaced by fetchSkinFromUuidAsync(UUID uuid, Callback callback)
	 *  Player names are mutable and UUID should be the standard method.
	 */
	@Deprecated
	public static void fetchSkinFromUsernameAsync(String username, Callback callback) {
		Player player = Bukkit.getPlayer(username);
		if (player != null) {
			fetchSkinFromUuidAsync(player.getUniqueId(), callback);
		} else {
			Bukkit.getLogger().severe("Could not fetch skin! (Username: " + username + ").");
			// let's ensure the callback stays async
			EXECUTOR.execute(() -> callback.call(null));
		}
	}

	public static void fetchSkinFromUuidAsync(UUID uuid, Callback callback) {
		EXECUTOR.execute(() -> {
			try {
				String link = MOJANG_SESSIONS.replace("{0}", uuid.toString());
				String request = getRequest(link);
				JsonObject json = (JsonObject) new JsonParser().parse(request);
				JsonObject properties = json.get("properties").getAsJsonObject();
				String value = properties.get("value").getAsString();
				String signature = properties.get("signature").getAsString();
				callback.call(new Skin(value, signature));
			} catch (IOException exception) {
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
