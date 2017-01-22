class MapAudioCleare extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10800000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            VkWork.diskURL.clear();
        }
    }
}
