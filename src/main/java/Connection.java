import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Connection {
    public static String getSimpleResponse(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseBody = null;

        try {
            Response response = client.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    public static String getLongPollResponse(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseBody = null;

        try {
            Response response = client.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;
    }
}
