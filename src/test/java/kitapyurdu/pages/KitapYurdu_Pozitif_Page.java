package kitapyurdu.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import kitapyurdu.utilities.Driver;
import kitapyurdu.utilities.ReusableMethods;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;


public class KitapYurdu_Pozitif_Page {
/*
Driver basit seviyede oldugundan appium calistirmak icin cmd acilir.
cmd ekranina appium yazilir.
appium calistiktan sonra test run edilir
 */
   public KitapYurdu_Pozitif_Page(){
        PageFactory.initElements(new AppiumFieldDecorator(Driver.getDriver()), this);
    }
    @AndroidFindBy(id = "com.mobisoft.kitapyurdu:id/imageViewProduct")
    private List<WebElement> kontrolKutusu;
    @AndroidFindBy(id = "com.mobisoft.kitapyurdu:id/mainPointsCatalogLayout")
    private List<WebElement> dunyaKlasikleri;
    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Puan Kataloğu\")")
    public WebElement puanKatalogu;

   public void ilkKitapSec(){
       kontrolKutusu.get(0).click();
   }
   public void scrollClick(){
       ReusableMethods.screenScrollDown(15);
       dunyaKlasikleri.get(1).click();
   }
}
