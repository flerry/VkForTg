import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.LongpollParams;
import org.json.JSONArray;
import org.json.JSONObject;

public class LongPollHandler extends Thread {
    private static final TgBot bot = new TgBot();

    private static TransportClient transportClient = HttpTransportClient.getInstance();
    private static VkApiClient vk = new VkApiClient(transportClient);
    private static UserActor actor;

    private static String key;
    private static String server;
    private static Integer ts;

    private static int id;
    private static String token;

    public LongPollHandler(int id, String token) {
        this.id = id;
        this.token = token;
        actor = new UserActor(id, token);
        authLongPool();
    }

    @Override
    public void run() {
        LongPool lp = new LongPool();

        while (true) {
            JSONObject response = new JSONObject(lp.getLongPollHistory(server, key, ts));
            JSONArray updates = response.getJSONArray("updates");

            ts = response.getInt("ts");

            for (int i = 0; i < updates.length(); i++) {
                JSONArray update = updates.getJSONArray(i);

                if (update.getInt(0) != 61) {

                    if (update.getInt(0) == 4 && update.getInt(2) != 35 && update.getInt(2) != 51) {

                        String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                .format(new java.util.Date(update.getLong(4) * 1000));

                        String message = update.getString(6);

                        String userId = String.valueOf(update.getInt(3));

                        String userNames = new VkWork(token)
                                .getUserNames(userId);

                        StringBuilder sb = new StringBuilder()
                                .append("[").append(date).append("]")
                                .append("[").append("id").append(userId).append("]")
                                .append("\n")
                                .append(userNames).append(":")
                                .append("\n")
                                .append("\"").append(message).append("\"");

                        bot.sendMsgCustomUser(bot.tgChatId, sb.toString());
                    }
                }
            }
        }
    }


    private void authLongPool() {
        try {
            LongpollParams getResponse = vk.messages()
                    .getLongPollServer(actor)
                    .execute();

            key = getResponse.getKey();
            server = getResponse.getServer();
            ts = getResponse.getTs();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
