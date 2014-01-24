package com.isador.btce.api.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonTools {
	private static Gson builder = new GsonBuilder().setPrettyPrinting()
			.create();

	public static String toString(JsonObject json) {
		return json.toString();
	}

	public static String toPrettyString(JsonObject json) {
		return builder.toJson(json);
	}
}
