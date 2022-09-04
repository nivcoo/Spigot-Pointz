package fr.nivcoo.pointz.utils;

import com.google.gson.GsonBuilder;
import fr.nivcoo.pointz.constructor.ItemsConverter;
import fr.nivcoo.pointz.constructor.ItemsShop;
import fr.nivcoo.pointz.constructor.MWConfig;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

public class WebsiteAPI {

    private KeyFactory keyFactory;
    private PrivateKey privateKey;
    private final String USER__AGENT = "Mozilla/5.0";
    private String url;

    public WebsiteAPI(String privateKeyString, String url) throws Exception {
        this.keyFactory = KeyFactory.getInstance("RSA");
        this.url = url + "/pointz/api";
        String stringAfter = privateKeyString.replaceAll("\\n", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "").trim();

        byte[] decoded = Base64.getMimeDecoder().decode(stringAfter);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        privateKey = keyFactory.generatePrivate(keySpec);

    }


    public String encryptToBase64(String plainText) {
        String encoded = null;
        try {
            final Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, privateKey);
            HashMap<String, String> informations = new HashMap<>();
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encodedKey = rsa.doFinal(secretKey.getEncoded());
            encoded = Base64.getEncoder().encodeToString(encodedKey);

            byte[] iv = new byte[16];
            informations.put("key", encoded);
            informations.put("iv", Base64.getEncoder().encodeToString(iv));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] encodedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            encoded = Base64.getEncoder().encodeToString(encodedData);
            informations.put("data", encoded);
            JSONObject jso = new JSONObject(informations);

            encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(jso.toString().getBytes());

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #6");
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
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        return response.toString();

    }

    public List<PlayersInformations> getPlayersInfos(List<String> players) {

        List<PlayersInformations> results = new ArrayList<>();

        String response;
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "get_players_informations");
            String playersList = String.join(",", players);

            params.put("players", playersList);

            response = sendPost(url, params);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);

            GsonBuilder gb = new GsonBuilder();
            String information = jsonObject.get("players").toString();
            PlayersInformations[] playersObject = gb.create().fromJson(information, PlayersInformations[].class);
            results.addAll(Arrays.asList(playersObject));

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #5");

        }

        return results;
    }

    public void setMoneyPlayer(String username, double getCibleMoneyAfter) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "set_money_player");
            params.put("username", username);
            params.put("new_money", String.valueOf(getCibleMoneyAfter));

            sendPost(url, params);

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #4");

        }

    }

    public HashMap<String, String> check() throws Exception {
        HashMap<String, String> results = new HashMap<>();

        String response;

        HashMap<String, String> params = new HashMap<>();
        params.put("type", "check_key");

        response = sendPost(url, params);
        JSONParser parse = new JSONParser();
        JSONObject jobj = (JSONObject) parse.parse(response);
        results.put("error", String.valueOf(jobj.get("error")));

        return results;
    }

    public MWConfig initMWConfig() {

        MWConfig result = null;

        String response;
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "get_pointz_config");

            response = sendPost(url, params);
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(response);
            if (String.valueOf(jobj.get("error")).equals("false"))

                result = new MWConfig(String.valueOf(jobj.get("name_shop")), String.valueOf(jobj.get("name_gui")));

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #3");

        }

        return result;
    }

    public List<ItemsConverter> initItemsConverter() {
        List<ItemsConverter> result = new ArrayList<>();

        String response;
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "get_pointz_items_converter");

            response = sendPost(url, params);
            JSONArray list = getListFromWebsite(response);
            if (list == null)
                return result;

            for (Object o : list) {
                JSONObject item = (JSONObject) o;
                item = (JSONObject) item.get("PointzItemsConverter");
                result.add(new ItemsConverter(String.valueOf(item.get("name")), String.valueOf(item.get("icon")),
                        Integer.parseInt(String.valueOf(item.get("price"))),
                        Integer.parseInt(String.valueOf(item.get("price_ig"))), String.valueOf(item.get("lores")),
                        String.valueOf(item.get("commands"))));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #2");

        }

        return result;
    }

    public List<ItemsShop> initItemsShop() {
        List<ItemsShop> result = new ArrayList<>();
        String response;
        String response_2;
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "get_pointz_items_shop");

            response = sendPost(url, params);
            JSONArray list = getListFromWebsite(response);

            if (list == null)
                return result;

            for (Object o : list) {
                JSONObject item = (JSONObject) o;
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
            Bukkit.getLogger().severe("[Pointz] You must Install the website plugin and link it with private key #1");

        }

        return result;
    }

    public JSONArray getListFromWebsite(String response) throws ParseException {
        JSONParser parse = new JSONParser();
        JSONObject jobj = (JSONObject) parse.parse(response);
        if (String.valueOf(jobj.get("error")).equals("true"))
            return null;
        return (JSONArray) jobj.get("list");
    }

}
