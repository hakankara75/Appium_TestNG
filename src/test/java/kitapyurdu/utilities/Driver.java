package kitapyurdu.utilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Driver {
    public static AndroidDriver driver;

    public static AndroidDriver getDriver() {
        if(driver==null) {
            String appUrl = System.getProperty("user.home")+
                    File.separator + "IdeaProjects" +
                    File.separator + "apps" +
                    File.separator + "Kitapyurdu.apk";

            UiAutomator2Options options = new UiAutomator2Options()
                    .setApp(appUrl);

            URL url = null;
            try {
                url = new URL("http://0.0.0.0:4723");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            driver = new AndroidDriver(url, options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return driver;
    }

}
