# Android app to live stream the browser in AR world:

# Requirements:
1. install **JDK**
2. Download latest version of **Android Studio**
3. Android device with API version **>=26** and a USB cable

# Steps:
1. Open **Android Studio** and click on **Import Project**.
2. Navigate and open **selenium-ar-app** directory.
3. Wait for gradle to download the **dependencies** and setup the project. 
4. Connect the **android device** and **computer** to the same network using **WIFI** or **Mobile Hotspot**.
5. Find the IP address of the computer by executing **ifconfig**(For Mac) and **ipconfig**(For windows) command in terminal.
6. Open [MainActivity.java](app/src/main/java/com/testninja/seleniumar/MainActivity.java) 
file and update the value of **serverUrl** in line no **34** with **http://ipaddress:9093**.
7. Connect the mobile to computer using USB cable and enable [USB debugging](https://www.phonearena.com/news/How-to-enable-USB-debugging-on-Android_id53909) from settings.
8. Run the application.
9. The app should be launched in the mobile device.
10. Point the **Camera** to a flat surface and wait for the **Guides**(White dots) to appear on the screen.
11. Calibrate the whitedots to the center of the screen and press the **+** button.
12. The automation script will be automatically triggered in the computer and the frame should be added to the android device.

Now you can play with the AR frame by rotating, flipping and moving your camera around it.
