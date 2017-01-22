import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Scanner;

class TgBot extends TelegramLongPollingBot {
    private static final String WRITE_BOT_NAME = "Введите имя бота (без @)";
    private static final String WRITE_BOT_TOKEN = "Введите токен, полученный у BotFather";
    private static final String WRITE_VK_TOKEN = "Введите vk токен (он для LongPool), полученный по этой ссылке (находится в адресе после разрешения доступа):\n";
    private static final String LINK_VK_OAUTH = "https://oauth.vk.com/authorize?client_id=5830324&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=messages,friends,offline&response_type=token&v=5.62&state=1";
    private static final String WRITE_VK_ID = "Введите свой id";
    private static final String WRITE_START = "Последний этап: напиши боту /start";

    private static String botName;
    private static String botToken;
    private static String vkToken;
    private static int vkUserId;

    private static VkWork vk;

    static long tgChatId;
    private static String vkChatId;

    private static boolean isStarted = false;

    public static void main(String[] args) {

        ApiContextInitializer.init();

        Scanner sc = new Scanner(System.in);

        System.out.println(WRITE_BOT_NAME);
        botName = sc.nextLine();

        System.out.println(WRITE_BOT_TOKEN);
        botToken = sc.nextLine();

        System.out.println(WRITE_VK_TOKEN + LINK_VK_OAUTH);
        vkToken = sc.nextLine();

        vk = new VkWork(vkToken);

        System.out.println(WRITE_VK_ID);
        vkUserId = sc.nextInt();

        new MapAudioCleare().start();

        System.out.println(WRITE_START);


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new TgBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            String messageBody = message.getText();

            if (tgChatId < 1) {
                tgChatId = message.getChatId();
            }

            if (messageBody.equals("/start") && !isStarted) {
                isStarted = true;
                sendMsg(message, "Поехали!");
                System.out.println("Поехали!");
                new LongPollHandler(vkUserId, vkToken).start();
            }

            if (messageBody.contains("/mp")) {
                try {
                    sendMsg(message, vk.searchAudio(messageBody.replaceAll("/mp ", "")));
                } catch (ClientException | ApiException e) {
                    e.printStackTrace();
                }
            }

            if (message.getText().contains("/disk")) {
                sendAudioFromUrl(VkWork.diskURL.get(message.getText().replaceAll("/disk", "")), message.getChatId().toString());
            }

            if (messageBody.equals("/friends")) {
                sendMsg(message, vk.getFriends().toString());
            }

            if (message.getText().contains("/info")) {
                sendMsg(message, vk.getUserInfo(messageBody.replaceAll("/info ", "")));
            }

            if (messageBody.contains("/id")) {
                vkChatId = messageBody.replaceAll("/id ", "");
                sendMsg(message, "Вы в чате с пользователем \"" + vk.getUserNames(vkChatId) + "\"");
            }

            if (messageBody.equals("/done")) {
                sendMsg(message, "Чат с пользователем" + " " + "\"" + vk.getUserNames(vkChatId) + "\"" + " " + "окончен!");
                vkChatId = null;
            }

            if (messageBody != "/start"
                    && !messageBody.contains("/mp")
                    && !messageBody.contains("/disk")
                    && !messageBody.contains("/info")
                    && messageBody != "/friends"
                    && !messageBody.contains("/info")
                    && !messageBody.contains("/id")
                    && messageBody != "/done"
                    && message.getFrom().getUserName() != botName
                    && vkChatId != null) {

                vk.sendMsg(vkChatId, messageBody);
            }

        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void sendMsgCustomUser(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAudioFromUrl(String url, String chatId) {
        SendAudio sendAudioRequest = new SendAudio();
        sendAudioRequest.setChatId(chatId);
        sendAudioRequest.setAudio(url);
        try {
            sendAudio(sendAudioRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
