# Socket server:

Server application that executes the automation script and stream the live browser execution to client.

# Requirements:
1. install **JDK**
2. install **Intellij** IDE

# Steps:

1. Open **IntelliJ** and click on **Import Project**.
2. Navigate and open **socket-server** directory.
3. Select **Gradle** from ***Import project from external module dialog*** and click **Next**.
4. Click **Finish**.
5. Wait for gradle to download the **dependencies** and setup the project. 
6. Open [SocketServer.java](src/main/java/SocketServer.java) file and update the chromedriver path in line no **15**.
7. Run the main method.

Server should be started and below message will be displayed in the terminal.
```Server is running in port: 9093```

That's it. Server application is successfully configured.
