import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.testninja.selenium.TRexGameBot;

public class SocketServer {

    private static Boolean isGameStarted = false;
    private static final String HOST = "0.0.0.0";
    private static final int PORT = 9093;

    public static void main(String[] args) throws Exception {

        /* Set the system property with the path to chromedriver executable file */
        System.setProperty("webdriver.chrome.driver", "path/to/chrome/driver");

        Configuration config = new Configuration();
        config.setHostname(HOST);
        config.setPort(PORT);

        TRexGameBot bot = new TRexGameBot();

        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println(client.getSessionId());
                bot.addClient(client);
                if (!isGameStarted) {
                    bot.initialize();
                    bot.startGame();
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
        System.out.println("Server is running in port: "+ PORT);
    }

}
