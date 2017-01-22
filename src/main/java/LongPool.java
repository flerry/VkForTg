class LongPool {
    public String getLongPollHistory(String server, String key, Integer ts) {
        String url = "https://" + server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25&mode=2&version=1";
        return Connection.getLongPollResponse(url);
    }
}