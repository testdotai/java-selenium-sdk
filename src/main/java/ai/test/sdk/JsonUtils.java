package ai.test.sdk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Response;

/**
 * Shared utility methods for common tasks
 * 
 * @author Alexander Wu (alec@test.ai)
 *
 */
final class JsonUtils
{

	/**
	 * Convenience method, extract the body of a {@code Response} as a {@code JsonObject}.
	 * 
	 * @param r The Response object to use
	 * @return The body of {@code r} as a {@code JsonObject}.
	 */
	public static JsonObject responseAsJson(Response r)
	{
		try
		{
			String b = r.body().string();
			// System.err.printf("%d ---- %s%n", r.code(), b);

			return JsonParser.parseString(b).getAsJsonObject();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Convenience method, extract a String value associated with the specified key on a JsonObject.
	 * 
	 * @param jo The JsonObject to extract a String from
	 * @param key The key associated with the value to extract
	 * @return The value associated with {@code key}, or the empty String if {@code key} was not in {@code jo}.
	 */
	public static String stringFromJson(JsonObject jo, String key)
	{
		return jo.has(key) ? jo.get(key).getAsString() : "";
	}

	/**
	 * Convenience method, extract a double value associated with the specified key on a JsonObject.
	 * 
	 * @param jo The JsonObject to extract a double from
	 * @param key The key associated with the value to extract
	 * @return The value associated with {@code key}, or 0.0 if {@code key} was not in {@code jo}.
	 */
	public static double doubleFromJson(JsonObject jo, String key)
	{
		return jo.has(key) ? jo.get(key).getAsDouble() : 0;
	}

	/**
	 * Convenience method, extract an int value associated with the specified key on a JsonObject.
	 * 
	 * @param jo The JsonObject to extract an int from
	 * @param key The key associated with the value to extract
	 * @return The value associated with {@code key}, or 0 if {@code key} was not in {@code jo}.
	 */
	public static int intFromJson(JsonObject jo, String key)
	{
		return jo.has(key) ? jo.get(key).getAsInt() : 0;
	}

	/**
	 * Convenience method, extract a boolean value associated with the specified key on a JsonObject.
	 * 
	 * @param jo The JsonObject to extract a boolean from
	 * @param key The key associated with the value to extract
	 * @return The value associated with {@code key}, or false if {@code key} was not in {@code jo}.
	 */
	public static boolean booleanFromJson(JsonObject jo, String key)
	{
		return jo.has(key) ? jo.get(key).getAsBoolean() : false;
	}

}
