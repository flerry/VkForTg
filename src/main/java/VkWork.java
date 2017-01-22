import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.audio.AudioSearchSort;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

class VkWork {
    private static String token;

    static HashMap<String, String> diskURL = new HashMap<>();

    public VkWork(String token2) {
        token = token2;
    }

    public StringBuilder getFriends() {
        String query = "https://api.vk.com/method/friends.get?order=hints&fields=name&access_token=" + token + "&v=5.62";

        StringBuilder listFriends = new StringBuilder();

        JSONObject response = new JSONObject(Connection.getSimpleResponse(query));

        JSONArray items = response
                .getJSONObject("response")
                .getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject allArr = items.getJSONObject(i);

            listFriends.append("id")
                    .append(allArr.getInt("id"))
                    .append(" ")
                    .append(allArr.getString("first_name"))
                    .append(" ")
                    .append(allArr.getString("last_name"))
                    .append("\n");
        }

        return listFriends;
    }

    public String getHistory(String peer_id) {
        String query = "https://api.vk.com/method/messages.getHistory?peer_id=" + peer_id + "&count=1&access_token=" + token + "&v=5.62";

        JSONObject response = new JSONObject(Connection.getSimpleResponse(query));

        JSONArray items = response
                .getJSONObject("response")
                .getJSONArray("items");

        return items.getJSONObject(0).getString("body");
    }


    public void sendMsg(String userId, String message) {
        String query;

        try {
            query = "https://api.vk.com/method/messages.send?user_id=" + userId + "&message="
                    + URLEncoder.encode(message, "UTF-8") + "&access_token=" + token + "&v=5.58 ";
            Connection.getSimpleResponse(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getUserNames(String userId) {
        String query = "https://api.vk.com/method/users.get?user_ids=" + userId + "&fields=name&access_token=" + token + "&v=5.62";

        JSONObject response = new JSONObject(Connection.getSimpleResponse(query).toString());

        JSONObject info = response.getJSONArray("response")
                .getJSONObject(0);

        String firstName = info.getString("first_name");
        String lastName = info.getString("last_name");

        return firstName + " " + lastName;
    }

    public String getUserInfo(String userId) {
        String query = "http://api.vlad805.ru/vk.getDateUserRegistration?screen_name=id" + userId;

        JSONObject response = new JSONObject(Connection.getSimpleResponse(query).toString())
                .getJSONObject("response");

        StringBuilder info = new StringBuilder()
                .append("Ссылка:").append("\n")
                .append("http://vk.com/")
                .append(response.getInt("userId")).append("\n")
                .append("Зарегистрирован:").append("\n")
                .append(response.getString("date")).append("\n")
                .append("Время регистрации:").append("\n")
                .append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(new java.util.Date(response.getLong("unixtime") * 1000))).append("\n")
                .append("Аккаунт существует (дней):").append("\n")
                .append(response.getString("date"));


        return info.toString();
    }

    public String searchAudio(String songName) throws ClientException, ApiException {
        final TransportClient transportClient = HttpTransportClient.getInstance();
        final VkApiClient vk = new VkApiClient(transportClient);
        final UserActor actor = new UserActor(325573913, "64ab5f700ed1e57dd74fca29b5abe28f60473960092e58a48e4e37efa5e2403abfb56896d4a0ca8d79dd2");

        StringBuilder sb = new StringBuilder();

        JSONObject response = new JSONObject(vk.audio().search(actor).q(songName).count(20).sort(AudioSearchSort.BY_POPULARITY).execute());

        JSONArray items = response.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            try {

                String diskPath = "/disk" + String.valueOf((int) (Math.random() * 10000000));

                JSONObject all = items.getJSONObject(i);

                sb.append("Исполнитель:").append(" ").append(all.getString("artist")).append("\n");

                sb.append("Название:").append(" ").append(all.getString("title")).append("\n");

                sb.append("Ссылка:").append(" ").append(diskPath).append("\n\n");

                diskURL.put(diskPath.replaceAll("/disk", ""), all.getString("url"));

            } catch (Exception e) {
                System.out.println("Not find");
            }
        }

        return sb.toString();
    }
}
