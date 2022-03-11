package ai.test.sdk;

import java.util.HashMap;

/**
 * Shared classes and methods enhancing collections functionality.
 * 
 * @author Alexander Wu (alec@test.ai)
 *
 */
final class CollectionUtils
{
	/**
	 * Builds a {@code HashMap} out of a list of {@code String}s. Pass in values such that {@code [ k1, v1, k2, v2, k3, v3... ]}.
	 * 
	 * @param sl The {@code String}s to use
	 * @return A {@code HashMap} derived from the values in {@code sl}
	 */
	public static HashMap<String, String> keyValuesToHM(String... sl)
	{
		HashMap<String, String> m = new HashMap<>();

		for (int i = 0; i < sl.length; i += 2)
			m.put(sl[i], sl[i + 1]);

		return m;
	}
}
