import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.testninja.selenium.TRexGameBot;

public class SocketServer {

    private static Boolean isGameStarted = false;

    public static void main(String[] args) throws Exception {

        /* Set the system property with the path to chromedriver executable file */
        System.setProperty("webdriver.chrome.driver", "/Users/sudharsan/Documents/Applications/chromedriver");
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9093);

        TRexGameBot bot = new TRexGameBot();

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("start", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, AckRequest ackRequest) {
                bot.addClient(client);
                if (!isGameStarted) {
                    bot.initialize();
                    bot.startGame();
                    System.out.println("Action received");
                    isGameStarted = true;
                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                bot.removeClient(client);
                server.stop();
            }
        });
        server.start();
    }

}
