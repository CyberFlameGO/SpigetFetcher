package org.spiget.fetcher;

import com.google.gson.JsonObject;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URLEncoder;

@Log4j2
public class Discord {

	public static void postMessage(String content, @NotNull JsonObject config) throws IOException {
		if(!config.has("discord.channel"))return;
		String channel = config.get("discord.channel").getAsString();

		try {
			JsonObject body = new JsonObject();
			body.addProperty("content", URLEncoder.encode(content, "utf8"));

			String bodyString = body.toString();

			Connection.Response response = Jsoup.connect("https://discordapp.com/api/channels/"+channel+"/messages")
					.method(Connection.Method.POST)
					.userAgent("Spiget")
					.header("Authorization","Bot "+config.get("discord.token").getAsString())
					.header("Content-Type", "application/json")
					.ignoreHttpErrors(true)
					.requestBody(bodyString)
					.execute();
			log.info("Discord Response code: " + response.statusCode());
			if (response.statusCode() != 200) {
				log.warn(response.body());
			}
		} catch (Exception e) {
			log.warn("Failed to post discord message", e);
			Sentry.captureException(e);
		}
	}

}
