package com.testninja.selenium;

import com.corundumstudio.socketio.SocketIOClient;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.page.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SocketStreamer {

    Map<UUID, SocketIOClient> clientList = new HashMap<>();
    ;
    ChromeDriver driver;
    DevTools devTools;

    public void initialize(ChromeDriver driver) {
        this.driver = driver;
        devTools = driver.getDevTools();
    }

    public void addClient(SocketIOClient client) {
        if (!clientList.containsKey(client.getSessionId())) {
            clientList.put(client.getSessionId(), client);
        }
    }

    public void removeClient(SocketIOClient client) {
        clientList.remove(client.getSessionId());
    }

    public void startStreaming() {
        devTools.createSessionIfThereIsNotOne();

        devTools.addListener(Page.screencastFrame(), event -> {
            for (Map.Entry<UUID, SocketIOClient> clientEntry : clientList.entrySet()) {
                if (clientEntry.getValue().isChannelOpen()) {
                    clientEntry.getValue().sendEvent("data", event.getData());
                }
            }
        });

        devTools.send(
                Page.startScreencast(
                        Optional.of(Page.StartScreencastFormat.PNG),
                        Optional.of(80),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(100000000)
                )
        );
    }

    public void stopStreaming() {
        devTools.send(Page.stopScreencast());
    }

    public boolean hasClient() {
        return !clientList.entrySet().isEmpty();
    }

}
