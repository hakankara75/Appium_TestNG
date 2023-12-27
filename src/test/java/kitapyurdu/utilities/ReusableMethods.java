package kitapyurdu.utilities;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import static java.lang.Double.parseDouble;
import static kitapyurdu.utilities.Driver.getDriver;


public class ReusableMethods {


  /**
   * Element gorunur olmadigi surece ve sayfa sonuna gelinmedigi surece scroll down yapma metodu
   * @param element yerine android element locati verilmeli
   */
  public static void scrollForMobile(WebElement element) throws MalformedURLException {
    String previousPageSource="";
    while(isElementNotEnabled(element) && isNotEndOfPage(previousPageSource)){
      previousPageSource=getDriver().getPageSource();
      performScroll();

    }
  }

  /**
   * elementi listin icine alıp, listin boyutunu olcer. list bos ise true dondurecek.scrollForMobile() ile kullanilir
   * @param element element locate yazilmali
   * @return true yada false doner
   */
  private static boolean isElementNotEnabled(WebElement element) throws MalformedURLException {
    List<WebElement> elements=getDriver().findElements((By) element);
    boolean enabled;
    if (elements.size() <1) enabled = true;
    else enabled = false;
    return enabled;
  }

  /**
   * bir onceki sayfa pageSource ile simdiki aynı mı diye kontrol eder
   * @param previousPageSource
   * @return
   */
  private static boolean isNotEndOfPage(String previousPageSource) throws MalformedURLException {
    return ! previousPageSource.equals(getDriver().getPageSource());
  }
  public static void performScroll() throws MalformedURLException {
    Dimension size= getDriver().manage().window().getSize();
    int startX= size.getWidth()/2;
    int endX= size.getWidth()/2;
    int startY= size.getHeight()/2;
    int endY= (int)(size.getWidth()*0.25);
    performScrollUsingSequence(startX, startY, endX, endY);
  }
  private static void performScrollUsingSequence(int startX, int startY, int endX, int endY) throws MalformedURLException {
    PointerInput finger=new PointerInput(PointerInput.Kind.TOUCH, "first-finger");
    Sequence sequence=new Sequence(finger,0)
            .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), endX, endY))
            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    ((AppiumDriver)(getDriver())).perform(Collections.singletonList(sequence));
  }

  /**
   * bu metot ile aranan bir texti iceren elemente  scroll yapilir
   * @param textFromOutSide aranan text degeridir
   */
  public static void scrollTo(String textFromOutSide) throws MalformedURLException {
    AppiumBy.ByAndroidUIAutomator permissionElement = new AppiumBy.ByAndroidUIAutomator("new UiScrollable"+
            "(new UiSelector().scrollable(true).instance(0)."+
            "scrollIntoView(new UiSelector()"+".textMatches(\""+textFromOutSide+"\").instance(0)");
    getDriver().findElement(permissionElement);
  }

  /**
   * bu metot UiSelector cinsinden locate dondurur
   * @param text locate alinacak elementin text attribute icinde yazan metindir
   * @return
   */
  public static By locateElementByText(String text){
    return AppiumBy.androidUIAutomator("new UiSelector().text(\""+text+"\")");
  }
  public static void tapOnElementWithText(String text) {
    List<WebElement> mobileElementList = getDriver().findElements(By.className("android.widget.TextView"));
    for (WebElement page: mobileElementList) {
      if (page.getText().equalsIgnoreCase(text)){
        page.click();
      }else{
        scrollWithUiScrollableAndTapOn(getDriver(),text);
      }
      break;
    }
  }

  public static boolean isElementPresent(String text) {
    boolean elementFound = false;
    List<WebElement> mobileElementList = getDriver().findElements(By.xpath("//android.widget.TextView[@text='" + text + "']"));
    for (WebElement el : mobileElementList) {
      if (el.getText().equals(text)) {
        waitToBeVisible(el, Duration.ofSeconds(10));
        if (el.isDisplayed()) {
          elementFound = true;
        }
      }
    }
    return elementFound;
  }

  public static void wait(int second) {
    try {
      Thread.sleep(second * 1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void tapOn(WebElement element) {
    waitToBeClickable(element, Duration.ofSeconds(10));
    element.click();
  }

  /**
   * bu metot verilen koordinata dokunuyor. oraya tıklama islemi yapiyor.
   * @param driver driver verilmeli
   * @param x x koordinati
   * @param y y koordinati
   */
  public void tapOnWithPoint(AppiumDriver driver, int x, int y) {

    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence tap = new Sequence(finger, 1);
    tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y));
    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    driver.perform(Arrays.asList(tap));
  }

  public static void enterText(WebElement element, String text) {
    waitToBeClickable(element, Duration.ofSeconds(10));
    element.sendKeys(text);
  }

  public static void enterText(WebElement element, String text, boolean needClear) {
    waitToBeClickable(element, Duration.ofSeconds(10));
    if (needClear) {
      element.clear();
    }
    element.sendKeys(text);
  }

  public static boolean isElementPresent(WebElement webElement) {
    boolean elementFound = false;
    waitToBeVisible(webElement, Duration.ofSeconds(10));
    if (webElement.isDisplayed()) {
      elementFound = true;
    }
    return elementFound;
  }

  /**
   * Element gorunene kadar kodlari bekletir
   * @param element beklenilecek elementin locate
   * @param timeout ne kadar sure beklenilecegi int olarak verilir
   */
  public static void waitToBeVisible(WebElement element, Duration timeout) {
    WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
    wait.until(ExpectedConditions.visibilityOf(element));
  }

  public static void waitToBeClickable(WebElement element, Duration timeout) {
    WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
    wait.until(ExpectedConditions.elementToBeClickable(element));
  }

  /**
   * bu metot ile scrollable özelliği olan elementlere scroll yapılıp ardından tap yapilir
   * @param elementText elementin text value'su verilir
   * @param driver yerine AndroidDriver verilir
   */
  public static void scrollWithUiScrollableAndTapOn( AndroidDriver driver, String elementText) {
    driver.findElement(AppiumBy.ByAndroidUIAutomator.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+elementText+"\"))"));

    tapOn(driver.findElement(By.xpath("//android.widget.TextView[@text='" + elementText + "']")));
  }

  public static void tap(AppiumDriver driver, WebElement element) {
    Point location = element.getLocation();
    Dimension size = element.getSize();

    Point centerOfElement = getCenterOfElement(location, size);

    PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
    Sequence sequence = new Sequence(finger1, 1)
            .addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerOfElement))
            .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger1, Duration.ofMillis(200)))
            .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));
  }

  public static void doubleTap(AppiumDriver driver, WebElement element) {
    Point location = element.getLocation();
    Dimension size = element.getSize();

    Point centerOfElement = getCenterOfElement(location, size);

    PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

    Sequence sequence = new Sequence(finger1, 1)
            .addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerOfElement))
            .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger1, Duration.ofMillis(100)))
            .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger1, Duration.ofMillis(100)))
            .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));

  }

  public static void longTap(AppiumDriver driver, WebElement element) {
    Point location = element.getLocation();
    Dimension size = element.getSize();

    Point centerOfElement = getCenterOfElement(location, size);
    PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

    Sequence sequence = new Sequence(finger1, 1).
            addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerOfElement)).
            addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).
            addAction(new Pause(finger1, Duration.ofSeconds(4))).
            addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));
  }

  public static void scroll(AppiumDriver driver, int scroll) throws InterruptedException {
    Dimension size = driver.manage().window().getSize();
    int startX = size.getWidth() / 2 ;
    int startY = size.getHeight() / 2 ;
    int endX = startX;
    int endY = (int) (size.getHeight()*0.25);
    //buradaki 0,25 şu şekildedir; imleç ekranın ortasında yani 0,50 de,
    // y ekseninde 0,25 seçtiğimizde 0,50 den 0,25 e çekiyor yani aşağı  kayıyor.
    // Eğer 0,75 deseydik ters yönde  kaydıracaktı. Ne kadar kaydıracağı ise değişiyor.


    PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

    for (int i = 0; i <scroll ; i++) {
      Sequence sequence = new Sequence(finger1,1).
              addAction(finger1.createPointerMove(Duration.ZERO,PointerInput.Origin.viewport(), startX, startY)).
              addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).
              addAction(new Pause(finger1, Duration.ofMillis(100))).
              addAction(finger1.createPointerMove(Duration.ofMillis(300),PointerInput.Origin.viewport(),endX,endY)).
              addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

      driver.perform(Collections.singletonList(sequence));}
    Thread.sleep(3000);
  }

  //Sağa kaydırma
  public static void scrollHorizontal(AppiumDriver driver, int scroll) throws InterruptedException {
    Dimension size = driver.manage().window().getSize();
    int startX = size.getWidth() / 2 ;
    int startY = size.getHeight() / 2 ;
    int endX = (int) (size.getWidth()*0.25);
    int endY = startY;
    //buradaki 0,25 şu şekildedir; imleç ekranın ortasında yani 0,50 de,
    // x ekseninde 0,25 seçtiğimizde 0,50 den 0,25 e çekiyor yani sola  kayıyor.
    // Eğer 0,75 deseydik ters yönde  kaydıracaktı. Ne kadar kaydıracağı ise değişiyor.


    PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

    for (int i = 0; i <scroll ; i++) {
      Sequence sequence = new Sequence(finger1,1).
              addAction(finger1.createPointerMove(Duration.ZERO,PointerInput.Origin.viewport(), startX, startY)).
              addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).
              addAction(new Pause(finger1, Duration.ofMillis(400))).
              addAction(finger1.createPointerMove(Duration.ofMillis(100),PointerInput.Origin.viewport(),endX,endY)).
              addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

      driver.perform(Collections.singletonList(sequence));}
    Thread.sleep(3000);
  }

  public static void dragAndDrop(AppiumDriver driver, WebElement element1, WebElement element2){

    Point sourceCenter = getCenterOfElement(element1.getLocation(), element1.getSize());
    Point targetCenter = getCenterOfElement(element2.getLocation(), element2.getSize());

    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

    Sequence sequence = new Sequence(finger,1).
            addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sourceCenter)).
            addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).
            addAction(new Pause(finger, Duration.ofMillis(200))).
            addAction(finger.createPointerMove(Duration.ofMillis(3000), PointerInput.Origin.viewport(), targetCenter)).
            addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));
  }

  private static Point getCenterOfElement(Point location, Dimension size){
    return new Point(location.getX() + size.getWidth() /2,
            location.getY() + size.getHeight() /2);
  }

  public static  void tabOnElementWithText(String text) throws InterruptedException {

    List<WebElement> elements = getDriver().findElements(AppiumBy.className("android.widget.TextView"));

    for (WebElement element : elements) {
      System.out.println("element.getText() = " + element.getText());
      if (element.getText().contains(text)) {
        System.out.println("element.getText()111 = " + element.getText());
        element.click();
        break;
      } else ReusableMethods.scroll(getDriver(), 1);

    }

  }

  public static void backToPreScreen(){
    getDriver().navigate().back();
  }

  /**
   * Bu method locate alanlarında class tag name i aynı olan elementlerin attirubute isimlerinde farklılık varsa
   * farklı olan kısımlarını text parametresi ile locate alanına bir loop içinde ekleyip,
   * tek locate ile bütün elementleri gezmemizi sağlar. Byrada gezilen elementlerin assertion ları yapılmaktadır.
   * @param text :buraya attirubute alanınıdaki farklı text ler yazılır.
   * @throws InterruptedException
   */
  public static  void isElementVisibleWithText(String text) throws InterruptedException {

    List<WebElement> elements = getDriver().findElements(AppiumBy.className("android.widget.TextView"));

    for (WebElement element : elements) {
      System.out.println("element.getText() = " + element.getText());
      if (element.getText().contains(text)) {
        System.out.println("element.getText()111 = " + element.getText());

        Assert.assertTrue(isElementPresent(element));
        break;
      } else scroll(getDriver(), 1);
      break;
    }

  }
  public static void getScreenshot() throws IOException {
    //after verification take screenshot
    //I use this code to take a screenshot when needed
    // naming the screenshot with the current date to avoid duplication

    String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

    // TakesScreenshot is an interface of selenium that takes the screenshot
    TakesScreenshot ts = (TakesScreenshot) getDriver();
    File source = ts.getScreenshotAs(OutputType.FILE);

    // full path to the screenshot location
    String target = System.getProperty("user.dir") + "/test-output/Screenshots/" + date + ".png";
    File finalDestination = new File(target);

    // save the screenshot to the path given
    FileUtils.copyFile(source, finalDestination);
  }
  public static void validateCompabilitiyOfSubTitleWithTheTitle(String subTitle, String title) throws IOException {
    //Title alanı birden fazla mainword içeriyorsa  ayırıyoruz
    String[] titleElements = title.split(" ");
    for (int i = 0; i < titleElements.length; i++) {
      System.out.println("subtitle :" + subTitle);
      System.out.println("i :" + titleElements[i]);
      System.out.println("i+1 :" + titleElements[i + 1]);
      if (subTitle.contains(titleElements[i])) {
        Assert.assertTrue(subTitle.contains(titleElements[i]));
        System.out.println("sub title :" + subTitle + " başlık " + titleElements[i] + " kapsıyor");
        break;
      } else if (subTitle.contains(titleElements[i + 1])) {
        Assert.assertTrue(subTitle.contains(titleElements[i + 1]));
        System.out.println("sub title :" + subTitle + " başlık " + titleElements[i + 1] + " kapsıyor");
        break;
      } else System.out.println("subtitle :" + subTitle + " başlık değerlerini KAPSAMIYOR");
      getScreenshot();
      Assert.assertTrue(false);
    }
  }

  /**
   * Bu method Sıralama seçenekleri arasında 'Pahalıdan Ucuza' veya 'Ucuzdan Pahalıya'
   * şeklindeki parametreler ile sıralanan ürünlerin doğru şekilde görüntülenip görüntülenmediğini doğrular
   * @param option alanına 'Pahalıdan Ucuza' veya 'Ucuzdan Pahalıya' gelmelidir.
   */
  public static void validateProductsSortingByPrice(String option)  {
    List<WebElement> priceList1= getDriver().findElements(By.id(("com.mobisoft.kitapyurdu:id/textViewLeftPrice")));
    int sizeOfList=priceList1.size();


    if(option.equals("Ucuzdan Pahalıya")){


      for (int n = 0; n < priceList1.size()-1; n++) {
        String price1 = priceList1.get(n).getText().replace("TL","").replace(",",".").trim();
        System.out.println("price1 = " + price1);
        String price2 = priceList1.get(n+1).getText().replace("TL","").replace(",",".").trim();
        System.out.println("price2 = " + price2);
        double first= parseDouble(price1);
        double second= parseDouble(price2);
        Assert.assertTrue(first<=second);

      }

    } else if (option.equals("Pahalıdan Ucuza")) {



      for (int n = 0; n < priceList1.size()-1; n++) {
        String price1 = priceList1.get(n).getText().replace("TL","").replace(",",".").trim();
        System.out.println("price1 = " + price1);
        String price2 = priceList1.get(n+1).getText().replace("TL","").replace(",",".").trim();
        System.out.println("price2 = " + price2);
        double first= parseDouble(price1);
        double second= parseDouble(price2);
        Assert.assertTrue(first>=second);

      }
    } else System.out.println("Parametreniz hatalı olabilir, Kontrol edin");

  }


  /**
   * Bu metot sayfadaki ürünlerin texlerini tek tek alıp Set içine koyar. Scroll yaparak aşağıya iner.
   * Son ürünü de aldıktan sonra kapanır.
   * @param locate Ürün sayısını gösteren text elementinin locate'dir. Xpath olarak verirseniz metinden
   *               sadece sayıyı alıp Set'in size ile karşılaştırır.
   * @throws InterruptedException
   */
  public static void urunDogrula(String locate) throws InterruptedException {
    Set<String> elements = new HashSet();
    List<WebElement> list = null;
    String count = getDriver().findElement(By.xpath(locate)).getAttribute("text");
    Integer expectedElementSize = Integer.parseInt(count.replaceAll("[^0-9]", ""));
    System.out.println("count = " + expectedElementSize);
    Integer actualElementSize = -1;

    int size=0;
    do {
      for(size = 0; size < 4; ++size) {
        try {
          list = getDriver().findElements(By.xpath("//android.widget.TextView[@resource-id='com.mobisoft.kitapyurdu:id/textViewProductName']"));
          elements.add(((WebElement)list.get(size)).getAttribute("text"));
        } catch (Exception var7) {
        }
      }

      if (expectedElementSize.equals(actualElementSize)) {
        break;
      }

      scroll(getDriver(), 1);
      actualElementSize = elements.size();


    } while(actualElementSize != expectedElementSize);
    System.out.println("actualElementSize = " + actualElementSize);
    System.out.println("expectedElementSize = " + expectedElementSize);

    Assert.assertEquals(actualElementSize , expectedElementSize);
  }

  public static void touchAction(int a,int b,int c,int d) {

    TouchAction action = new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(a, b))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(PointOption.point(c, d)).release().perform();
  }
  public static void touchActionClick(int a,int b) {

    TouchAction action = new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(a, b))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .release().perform();
  }
  public static void koordinatTiklama(int xDegiskeni,int yDegiskeni,int bekleme) throws InterruptedException {
    TouchAction action=new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(xDegiskeni,yDegiskeni)).release().perform();
    Thread.sleep(bekleme);
  }
  public static void screenScrollDown(int wait){
    TouchAction action=new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(471,1371))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(wait)))
            .moveTo(PointOption.point(471,186))
            .release()
            .perform();
  }

  public static void screenScrollUp(int wait){
    TouchAction action=new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(1052,1016))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(wait)))
            .moveTo(PointOption.point(31,1016))
            .release()
            .perform();
  }
  public static void screenScrollRight(int wait) {
    TouchAction action = new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(1052, 1016))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(wait)))
            .moveTo(PointOption.point(31, 1016))
            .release()
            .perform();
  }

  public static void screenScrollLeft(int wait) {
    TouchAction action = new TouchAction<>((PerformsTouchActions) getDriver());
    action.press(PointOption.point(31, 1016))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(wait)))
            .moveTo(PointOption.point(1052, 1016))
            .release()
            .perform();

  }

  /**
   * Bu metot ile click yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine elementin id turunden locate'i verilir
   */
  public static void clickGesture(AndroidDriver driver, WebElement element) {
    driver.executeScript("mobile:clickGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId()
    ));
  }

  /**
   * Bu metot ile koordinat vererek click yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine x coordinate verilir
   * @param y yerine y coordinate verilir
   */
    public static void clickGestureWithCoordinates(AndroidDriver driver, int x, int y){
      driver.executeScript("mobile:clickGesture",ImmutableMap.of(
              "x", x,
              "y", y
      ));

    }

  /**
   * bu metot ile locate verilen elemente double click yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element double click yapilacak elementinid turunden locate'i verilecek
   */
  public static void doubleClick(AndroidDriver driver, WebElement element){
      driver.executeScript("mobile: doubleClickGesture", ImmutableMap.of(
              "elementId", ((RemoteWebElement) element).getId()
      ));
    }

  /**
   * bu metot ile koordinati verilen elemente double click yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x double click yapilacak elementin x koordinati
   * @param y double click yapilacak elementin y koordinati
   */
  public static void doubleClickWithCoordinates(AndroidDriver driver, int x, int y){
    driver.executeScript("mobile: doubleClickGesture", ImmutableMap.of(
            "x", x,
            "y", y
    ));
  }

  /**
   * bu metot ile bir elementin ustune long click yapilir
  * @param driver yerine AndroidDriver objesi verilir
  * @param element yerine de elementin id turunden locate'i verilir
  * @param duration yerine int cinsinden saniye olarak sure koyulur
   */
  public static void longClick(AndroidDriver driver, WebElement element, int duration){
    driver.executeScript("mobile: longClickGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "duration", duration*1000
    ));
  }

  /**
   * bu metot ile koordinat verilerek bir elemente long click atilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine elementin x koordinati
   * @param y yerine de elementin y koordinati verilir
   * @param duration yerine int cinsinden saniye olarak sure koyulur
   */
  public static void longClickWithCoordinates(AndroidDriver driver, int x, int y, int duration){
    driver.executeScript("mobile: longClickGesture", ImmutableMap.of(
            "x", x,
            "y", y,
            "duration", duration*1000
    ));
  }

  /**
   * bu metot ile drag ve drop islemi yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine suruklenecek elementin id turunden locate'i verilir
   * @param endX uzerine suruklenecek elementin x koordinati verilir
   * @param endY uzerine suruklenecek elementin y koordinati verilir
   */
  public static void dragDrop(AndroidDriver driver, WebElement element, int endX, int endY){

    driver.executeScript("mobile: dragGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "endX", endX,
            "endY", endY,
            "speed", 500
    ));

  }

  /**
   * bu metot ile drag ve drop islemi koordinat verilerek yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param startX suruklenecek elementin x koordinati verilir
   * @param startY suruklenecek elementin y koordinati verilir
   * @param endX uzerine suruklenecek elementin x koordinati verilir
   * @param endY uzerine suruklenecek elementin y koordinati verilir
   */
  public static void dragDropWithCoordinates(AndroidDriver driver, int startX, int startY, int endX, int endY){

    driver.executeScript("mobile: dragGesture", ImmutableMap.of(
            "startX", startX,
            "startY", startY,
            "endX", endX,
            "endY", endY,
            "speed", 500
    ));

  }

  /**
   * bu metot ile sayfada asagi dogru scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine ekranın tam olarak secildigi halinin id turunden locate'i verilir
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */
  public static void scrollDown(AndroidDriver driver, WebElement element, double x){

    driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "direction", "down",
            "percent", x,
            "speed", 1500
    ));
  }

  /**
   * bu metot ile sayfanin en altina kadar scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */
  public static void scrollToTheDownOfThePage(AndroidDriver driver, WebElement element, double x){

    boolean canScrollMore =true;
    while (canScrollMore){
      canScrollMore= (Boolean) driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
              "elementId", ((RemoteWebElement) element).getId(),
              "direction", "down",
              "percent", x,
              "speed", 1500
    ));
    }
  }

  /**
   * bu metot ile koordinat vererek asagi dogru scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param left yerine x koordianati verilir 100 vb.
   * @param top yerine y koordianati verilir 100 vb.
   * @param width yerine genislik verilir 100 vb.
   * @param height yerine yukseklik verilir 100 vb.
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */

  public static void scrollDownWithCoordinates(AndroidDriver driver, int left, int top, int width, int height, double x){

    driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
            "left", left, "top", top, "width", width, "height", height,
            "direction", "down",
            "percent",x,
            "speed", 1500
    ));
  }

  /**
   * bu metot ile sayfada yukari dogru scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine ekranın tam olarak secildigi halinin id turunden locate'i verilir
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */
  public static void scrollUp(AndroidDriver driver, WebElement element, double x){

    driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "direction", "up",
            "percent", x,
            "speed", 1500
    ));
  }

  /**
   * bu metot ile sayfanin en ustune kadar scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param left yerine x koordianati verilir 100 vb.
   * @param top yerine y koordianati verilir 100 vb.
   * @param width yerine genislik verilir 100 vb.
   * @param height yerine yukseklik verilir 100 vb.
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */
  public static void scrollToTheTopOfThePageWithCoordinates(AndroidDriver driver, int left, int top, int width, int height, double x){

    boolean canScrollMore =true;
    while (canScrollMore){
      canScrollMore= (Boolean) driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
              "left", left, "top", top, "width", width, "height", height,
              "direction", "up",
              "percent", x,
              "speed", 1500
      ));
    }
  }

  /**
   * bu metot ile sayfanin en ustune kadar scroll yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine scroll yapilacak olcu verilir. 1.0 tam ekran, 0.50 yarim ekran vb.
   */
  public static void scrollToTheTopOfThePage(AndroidDriver driver, WebElement element, double x){

    boolean canScrollMore =true;
    while (canScrollMore){
      canScrollMore= (Boolean) driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
              "elementId", ((RemoteWebElement) element).getId(),
              "direction", "up",
              "percent", x,
              "speed", 1500
      ));
    }
  }

  public static void scrollToElementWithDirection(AndroidDriver driver,WebElement element, String direction){
    driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "direction", direction,
            "percent",1.0,
            "speed", 500
    ));
  }

  /**
   * bu metot ile text degeri verilen elemente kadar sayfa kaydirilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param text yerine elementin text degeri verilir
   */
  public static void scrollToElementWithText(AndroidDriver driver, String text){
    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"))"));

  }

  /**
   * bu metot ile coordinat verilerek istenen yone (left, rigtht, up, down) kaydirma yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine elementin locate verilir
   * @param direction yerine (left, rigtht, up, down) yonlerinden birisi text olarak verilir
   * @param percent yerine 0 ile 1 arasi bir deger verilir. Bu deger kaydirma oranini belirler.
   * @param speed  yerine int cinsinden sayi girilir. Bu deger kaydirmanin ivmesini belirler. Ne kadar hizli olmasini istersek sayi o kadar buyuk olmali (1000 veya 10000 gibi).
   */
  public static void swipe(AndroidDriver driver, WebElement element, String direction, double percent, int speed){
    driver.executeScript("mobile: swipeGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "direction", direction,
            "percent", percent,
            "speed", speed
    ));
  }


  /**
   * bu metot ile coordinat verilerek istenen yone (left, rigtht, up, down) kaydirma yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine x koordinat degeri verilir
   * @param y yerine y koordinat degeri verilir
   * @param w yerine genislik verilir
   * @param h yerine yukseklik verilir
   * @param direction yerine (left, rigtht, up, down) yonlerinden birisi text olarak verilir
   * @param percent  yerine 0 ile 1 arasi bir deger verilir. Bu deger kaydirma oranini belirler.
   * @param speed  yerine int cinsinden sayi girilir. Bu deger kaydirmanin ivmesini belirler. Ne kadar hizli olmasini istersek sayi o kadar buyuk olmali (1000 veya 10000 gibi).
   */
  public static void swipeWithCoordinate(AndroidDriver driver,  int x, int y, int w, int h, String direction, double percent, int speed){
    driver.executeScript("mobile: swipeGesture", ImmutableMap.of(
            "left", x, "top", y, "width", w, "height", h,
            "direction", direction,
            "percent", percent,
            "speed", speed
    ));
  }

  /**
   * bu metot ile element verilerek istenen asagi dogru firlatma yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine elementin locate verilir
   * @param speed  yerine int cinsinden sayi girilir. Bu deger kaydirmanin ivmesini belirler. Ne kadar hizli olmasini istersek sayi o kadar buyuk olmali (1000 veya 10000 gibi).
   */
  public static void fling(AndroidDriver driver, WebElement element, int speed){
    driver.executeScript("mobile: flingGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "direction", "down",
            "speed", speed
    ));
  }


  /**
   * bu metot ile coordinat verilerek istenen asagi dogru firlatma yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine x koordinat degeri verilir
   * @param y yerine y koordinat degeri verilir
   * @param w yerine genislik verilir
   * @param h yerine yukseklik verilir
   * @param speed  yerine int cinsinden sayi girilir. Bu deger kaydirmanin ivmesini belirler. Ne kadar hizli olmasini istersek sayi o kadar buyuk olmali (1000 veya 10000 gibi).
   */
  public static void flingWithCoordinate(AndroidDriver driver,  int x, int y, int w, int h, int speed){
    driver.executeScript("mobile: flingGesture", ImmutableMap.of(
            "left", x, "top", y, "width", w, "height", h,
            "direction", "down",
            "speed", speed
    ));
  }

  /**
   * bu metot ile bir element zoom yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine zoom yapilacak element locate verilir
   * @param percent yerine zoom orani girilir. 0 ile 1 arasi bir deger double olarak verilir
   * @param speed yerine int cinsinden hiz icin deger verilir
   */
  public static void pinchOpen(AndroidDriver driver, WebElement element, double percent, int speed){
   driver.executeScript("mobile: pinchOpenGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "percent", percent,
           "speed", speed
    ));
  }


  /**
   * bu metot ile bir element koordinate verilerek zoom yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine x koordinat degeri verilir
   * @param y yerine y koordinat degeri verilir
   * @param w yerine genislik verilir
   * @param h yerine yukseklik verilir
   * @param percent yerine zoom orani girilir. 0 ile 1 arasi bir deger double olarak verilir
   * @param speed yerine int cinsinden hiz icin deger verilir
   */
  public static void pinchOpenWithCoordinate(AndroidDriver driver, int x, int y, int w, int h, double percent, int speed){
     driver.executeScript("mobile: pinchOpenGesture", ImmutableMap.of(
             "left", x, "top", y, "width", w, "height", h,
            "percent", percent,
             "speed", speed
    ));
  }

  /**
   * bu metot ile bir element zoom yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param element yerine zoom yapilacak element locate verilir
   * @param percent yerine zoom orani girilir. 0 ile 1 arasi bir deger double olarak verilir
   * @param speed yerine int cinsinden hiz icin deger verilir
   */
  public static void pinchClose(AndroidDriver driver, WebElement element, double percent, int speed){
    driver.executeScript("mobile: pinchCloseGesture", ImmutableMap.of(
            "elementId", ((RemoteWebElement) element).getId(),
            "percent", percent,
            "speed", speed
    ));
  }

  /**
   * bu metot ile bir element koordinate verilerek zoom yapilir
   * @param driver yerine AndroidDriver objesi verilir
   * @param x yerine x koordinat degeri verilir
   * @param y yerine y koordinat degeri verilir
   * @param w yerine genislik verilir
   * @param h yerine yukseklik verilir
   * @param percent yerine zoom orani girilir. 0 ile 1 arasi bir deger double olarak verilir
   * @param speed yerine int cinsinden hiz icin deger verilir
   */
  public static void pincClosehWithCoordinate(AndroidDriver driver, int x, int y, int w, int h, double percent, int speed){
    driver.executeScript("mobile: pinchCloseGesture", ImmutableMap.of(
            "left", x, "top", y, "width", w, "height", h,
            "percent", percent,
            "speed", speed
    ));
  }

  /**
   * bu metot ile PointerInput ile tap yapilir
   * @param driver AppiumDriver verilir
   * @param x tap yapilacak elementin x koordinati girilir
   * @param y tap yapilacak elementin y koordinati girilir
   */
  public static void pointerTap(AppiumDriver driver, int x, int y){
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence sequence = new Sequence(finger, 1)
            // 3- sequence objesine addAction() eklenerek yapilacak isler belirlenir
            // Parmak, ekran üzerindeki tap yapılacak koordinata yazilir.
            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
            // Parmak ile (farenin sol tuşuna basar gibi) ekranda belirlediğimiz koordinata gidilir.
            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            // Ekranda parmagin basili kalacagi sureyi veririz
            .addAction(new Pause(finger, Duration.ofMillis(300)))
            // Parmagimizi ekranda bastigimiz koordinattan kaldiririz
            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    //4- bitirme islemi
    driver.perform(Collections.singletonList(sequence));
  }
  /**
   * bu metot ile PointerInput ile doubltap yapilir
   * @param driver AppiumDriver verilir
   * @param x tap yapilacak elementin x koordinati girilir
   * @param y tap yapilacak elementin y koordinati girilir
   * @param a tap yapilacak elementin x koordinatinin az farkli sekli girilir
   * @param b tap yapilacak elementin y koordinatinin az farkli sekli girilir
   */
  public static void pointerDoubleTap(AppiumDriver driver, int x, int y, int a, int b){
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

    Sequence sequence = new Sequence(finger, 1)

            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger, Duration.ofMillis(300)))
            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))


            .addAction(finger.createPointerMove(Duration.ofMillis(10), PointerInput.Origin.viewport(), a, b)) //hata alinmamasi icin yukaridakinden az farkli koordinat girildi
            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger, Duration.ofMillis(300)))
            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));
  }

  /**
   * Bu metot ile elemente long press yapilir
   * @param driver AppiumDriver verilir
   * @param x long press yapilacak elementin x koordinati girilir
   * @param y long press yapilacak elementin y koordinati girilir
   */
  public static void longPress(AppiumDriver driver, int x, int y){
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

    Sequence sequence = new Sequence(finger, 1)
            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new Pause(finger, Duration.ofMillis(1000)))
            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

    driver.perform(Collections.singletonList(sequence));
  }
}