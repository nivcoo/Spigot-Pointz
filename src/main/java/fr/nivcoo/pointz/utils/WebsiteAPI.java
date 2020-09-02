package fr.nivcoo.pointz.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fr.nivcoo.pointz.constructor.ItemsConverter;
import fr.nivcoo.pointz.constructor.ItemsShop;
import fr.nivcoo.pointz.constructor.MWConfig;

public class WebsiteAPI {

	private KeyFactory keyFactory;
	private PublicKey publicKey;
	private final String USER__AGENT = "Mozilla/5.0";
	private String url;

	public WebsiteAPI(String publicKeyString, String url) throws Exception {
		this.keyFactory = KeyFactory.getInstance("RSA");
		this.url = url + "/pointz/api";
		String stringAfter = publicKeyString.replaceAll("\\n", "").replaceAll("-----BEGIN PUBLIC KEY-----", "")
				.replaceAll("-----END PUBLIC KEY-----", "").trim();

		byte[] decoded = Base64.getMimeDecoder().decode(stringAfter);

		KeySpec keySpec = new X509EncodedKeySpec(decoded);

		publicKey = keyFactory.generatePublic(keySpec);

	}

	public String encryptToBase64(String plainText) {
		String encoded = null;
		try {
			final Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, publicKey);
			rsa.update(plainText.getBytes());
			final byte[] result = rsa.doFinal();

			encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(result);

		} catch (Exception e) {
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #6");
		}
		return encoded;

	}

	static String urlEncodeUTF8(HashMap<?, ?> map) {
		StringBuilder sb = new StringBuilder();

		for (HashMap.Entry<?, ?> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey().toString()),
					urlEncodeUTF8(entry.getValue().toString())));
		}
		return sb.toString();
	}

	static String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	private String sendPost(String url, HashMap<?, ?> paramMap) throws Exception {

		URL obj = new URL(url);
		String params = "";
		if (paramMap != null)
			params = urlEncodeUTF8(paramMap);

		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER__AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(encryptToBase64(params));
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}

	public HashMap<String, String> getPlayerInfos(Player player) {

		HashMap<String, String> results = new HashMap<>();

		String response = "";
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "get_player_informations");
			params.put("username", player.getName());

			response = sendPost(url, params);
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject) parse.parse(response);
			results.put("error", String.valueOf(jobj.get("error")));
			if (jobj.get("money") != null)
				results.put("money", String.valueOf(jobj.get("money")));
			if (jobj.get("username") != null)
				results.put("username", String.valueOf(jobj.get("username")));

		} catch (Exception e) {
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #5");

		}

		return results;
	}

	public void setMoneyPlayer(Player player, int new_money) {
		String response = null;
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "set_money_player");
			params.put("username", player.getName());
			params.put("new_money", String.valueOf(new_money));

			response = sendPost(url, params);

		} catch (Exception e) {
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #4");

		}

	}

	public HashMap<String, String> check() throws Exception {
		HashMap<String, String> results = new HashMap<>();

		String response = "";

		HashMap<String, String> params = new HashMap<>();
		params.put("type", "check_key");

		response = sendPost(url, params);
		System.out.println(response);
		JSONParser parse = new JSONParser();
		JSONObject jobj = (JSONObject) parse.parse(response);
		results.put("error", String.valueOf(jobj.get("error")));

		return results;
	}

	public MWConfig initMWConfig() {

		MWConfig result = null;

		String response = "";
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "get_pointz_config");

			response = sendPost(url, params);
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject) parse.parse(response);
			if (String.valueOf(jobj.get("error")) == "false")

				result = new MWConfig(String.valueOf(jobj.get("name_shop")), String.valueOf(jobj.get("name_gui")));

		} catch (Exception e) {
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #3");

		}

		return result;
	}

	public List<ItemsConverter> initItemsConverter() {
		List<ItemsConverter> result = new ArrayList<>();

		String response = "";
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "get_pointz_items_converter");

			response = sendPost(url, params);
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject) parse.parse(response);
			if (String.valueOf(jobj.get("error")) == "true")
				return null;
			// loop array
			JSONArray list = (JSONArray) jobj.get("list");

			for (int i = 0; i < list.size(); i++) {
				JSONObject item = (JSONObject) list.get(i);
				item = (JSONObject) item.get("PointzItemsConverter");
				result.add(new ItemsConverter(String.valueOf(item.get("name")), String.valueOf(item.get("icon")),
						Integer.parseInt(String.valueOf(item.get("price"))),
						Integer.parseInt(String.valueOf(item.get("price_ig"))), String.valueOf(item.get("lores")),
						String.valueOf(item.get("commands"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #2");

		}

		return result;
	}

	public List<ItemsShop> initItemsShop() {
		List<ItemsShop> result = new ArrayList<>();

		String response = "";
		String response_2 = "";
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "get_pointz_items_shop");

			response = sendPost(url, params);
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject) parse.parse(response);
			if (String.valueOf(jobj.get("error")) == "true")
				return null;
			JSONArray list = (JSONArray) jobj.get("list");

			for (int i = 0; i < list.size(); i++) {
				JSONObject item = (JSONObject) list.get(i);
				item = (JSONObject) item.get("PointzItemsShop");

				HashMap<String, String> params_2 = new HashMap<>();
				params_2.put("type", "get_pointz_item");
				params_2.put("item_id", String.valueOf(item.get("item_id")));
				response_2 = sendPost(url, params_2);
				JSONParser parse_2 = new JSONParser();
				JSONObject jobj_2 = (JSONObject) parse_2.parse(response_2);
				jobj_2 = (JSONObject) jobj_2.get("item");

				result.add(new ItemsShop(String.valueOf(jobj_2.get("name")),
						Integer.parseInt(String.valueOf(jobj_2.get("price"))),
						Integer.parseInt(String.valueOf(item.get("price_ig"))), String.valueOf(item.get("icon")),
						String.valueOf(jobj_2.get("commands"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with public key #1");

		}

		return result;
	}

}
