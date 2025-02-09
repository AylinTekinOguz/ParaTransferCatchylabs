package step;

import base.BaseTest;
import com.github.javafaker.Faker;
import com.thoughtworks.gauge.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ElementInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BaseSteps extends BaseTest {

    public static int DEFAULT_MAX_ITERATION_COUNT = 100; //150
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 50;

    private static String SAVED_ATTRIBUTE;

    private String compareText;
    private static int index;

    public BaseSteps() {
        initMap(getFileList());
    }

    private static final Logger logger = LogManager.getLogger(BaseSteps.class);

    Faker faker = new Faker(Locale.forLanguageTag("TR"));
    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }
    private Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 120);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    @Step("<int> saniye bekle")
    public void waitBySeconds(int seconds) {
        try {
            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }


    public By getElementInfoToBy(utils.ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    public boolean isElementVisible(String key, long timeOut) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, timeOut);
        try {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Step("Elementine tıkla <key>")
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            //hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tıklandı.");
        }
    }
    private void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }
    @Step({"<key> Elementine kadar kaydır",
            "Scroll to Element <key>"})
    public WebElement scrollToElementToBeVisible(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement webElement = driver.findElement(getElementInfoToBy(elementInfo));
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
        return webElement;
    }

    @Step("<key> elementi ekranda gorunur mu kontrol et")
    public void elementGorunurlukKontrolu(String key) {
        if (isElementVisible(key, 20)) {
            logger.info(key + " elementi bulundu!");
        } else {
            Assertions.fail(key + " Element bulunamadı");
            logger.info(key + " elementi bulunamadı");
        }
    }

    @Step("<key> elementi ekranda gorunur degil mi kontrol et")
    public void elementGorunmezlikKontrolu(String key) {
        if (isElementVisible(key, 10) == false) {
            logger.info(key + " elementi ekranda gorunur degil");
        } else {
            Assertions.fail(key + " elementi ekranda gorunur olmamalı");
            logger.info(key + " elementi ekranda gorunur olmamali");
        }
    }

    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    @Step("<key> li elementi bul, temizle ve <text> değerini yaz")
    public void sendKeysByKey(String key, String text) {
        WebElement webElement = findElementWithKey(key);
        webElement.clear();
        webElement.sendKeys(text);
        logger.info(key + " alanina " + text + " degeri yazildi ");
    }

    @Step("<key> elementine <text> değerini js ile yaz")
    public void writeToKeyWithJavaScript(String key, String text) {
        WebElement element = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value=arguments[1]", element, text);
        logger.info(key + " elementine " + text + " değeri js ile yazıldı.");
    }
    @Step("Elemente ENTER keyi yolla <key>")
    public void sendKeyToElementENTER(String key) {
        findElement(key).sendKeys(Keys.ENTER);
        logger.info(key + " elementine ENTER keyi yollandı.");
    }
    @Step("Elemente ESC keyi yolla <key>")
    public void sendKeyToElemenESC(String key) {
        findElement(key).sendKeys(Keys.ESCAPE);
        logger.info(key + " elementine ESC keyi yollandı.");
    }

    @Step("<key> li elementi bul, temizle rastgele isim soyisim yaz ve hafizaya <saveUsername> olarak kaydet")
    public void sendRandomUsername(String key, String saveUsername) {
        WebElement webElement = findElementWithKey(key);
        webElement.clear();
        String username = faker.name().fullName();
        webElement.sendKeys(username);
        saveValue(saveUsername, username);
        logger.info(key + " alanina " + username + " degeri yazildi ");

    }

    @Step("<key> li elementin text degerini hafizada kayitli olan <saveKey> degeriyle karsilastir")
    public void compareRuleSet(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertTrue(elementTextValue.equalsIgnoreCase(getValue(saveKey)));
    }

    @Step("<key> li elementin text degeri hafizada kayitli olan <saveKey> degerini içeriyor mu")
    public void containTextInMemory(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertTrue(elementTextValue.contains(getValue(saveKey)));
    }

    @Step("<key> li elementin text degeri hafizada kayitli olan <saveKey> degerini içermiyor mu")
    public void notContainTextInMemory(String key, String saveKey) {
        String elementTextValue = findElement(key).getText();
        System.out.println("Keyli elementin text degeri : " + elementTextValue);
        System.out.println("Hafizada saklanan elementin text degeri : " + getValue(saveKey));
        assertFalse(elementTextValue.contains(getValue(saveKey)));
    }

    @Step("<key> elementi <text> değerini içeriyor mu kontrol et")
    public void checkElementContainsText(String key, String expectedText) {
        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertTrue(containsText, "Expected text is not contained. Actual text = " + findElement(key).getText());
        logger.info(key + " elementi " + expectedText + " değerini içeriyor.");
    }

    @Step("<key> elementi <text> değerini içermiyor mu kontrol et")
    public void checkElementNotContainsText(String key, String expectedText) {
        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertFalse(containsText, "Expected text is contained. Actual text = " + findElement(key).getText());
        logger.info(key + " elementi " + expectedText + " değerini içermiyor.");
    }

    @Step("<key> elementini bul ve temizle")
    public void clearInputArea(String key) {
        findElement(key).sendKeys(Keys.BACK_SPACE);
        logger.info(key + " elementinin text alani temizlendi.");
        }



    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Step("<key> elementinin <attribute> niteliği <value> değerini içeriyor mu")
    public void checkElementAttributeDoesntContains(String key, String attribute, String value) {
        String attributeText = findElement(key).getAttribute(attribute);
        assertEquals(attributeText, value, key + "elementinin " + attribute + "niteliği" + value + "değerini içermiyor!");
    }

    @Step("<key> li elementin text degeri hafizaya <saveKey> ismiyle kaydet")
    public void saveTextValueOfElement(String key, String saveKey) {
        String saveValueText = findElement(key).getText();
        System.out.println("Elementin text degeri: " + saveValueText);
        saveValue(saveKey, saveValueText);
    }

    @Step("<key> keyli elementin texti bugünün tarihi mi kontrol et")
    public void isItTodayCheck(String key) throws ParseException {
        WebElement webElement = findElementWithKey(key);
        String textDate = webElement.getText().split(" ")[0];
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("tr", "TR"));
        String date = simpleDateFormat.format(new Date());
        System.out.println("İşlem tarihi:" + textDate);
        System.out.println("Bugün:" + date);
        assertTrue(textDate.equals(date));
        logger.info(key + " keyli element bugunun tarihini iceriyor");

    }

    @Step("<recordedAmount> - <subtractedAmount> = <newAmount> isleminin dogrulugu kontrol edilir")
    public void checkSubtraction(String recordedAmount, int subtractedAmount, String newAmount) {
      String newAmountText = findElement(newAmount).getText().split("\\.")[0].replace(",","");
     Double newAmountDouble = Double.parseDouble(newAmountText);
        System.out.println("Yeni Tutar: " + newAmountDouble);

     String recordedAmountText = getValue(recordedAmount).split("\\.")[0].replace(",","");
     Double recordedAmountDouble = Double.parseDouble(recordedAmountText);
        System.out.println("Ilk Tutar: " + recordedAmountDouble);

        System.out.println("Beklenen tutar: " + (recordedAmountDouble-subtractedAmount));
        System.out.println("Goruntulenen Tutar: " + newAmountText);

       assertTrue(recordedAmountDouble-subtractedAmount==newAmountDouble);
        logger.info("Hesaplanan tutar doğru");


    }

    @Step("<recordedAmount> + <subtractedAmount> = <newAmount> isleminin dogrulugu kontrol edilir")
    public void checkAddition(String recordedAmount, int subtractedAmount, String newAmount) {
        String newAmountText = findElement(newAmount).getText().split("\\.")[0].replace(",","");
        int newAmountDouble = Integer.parseInt(newAmountText);
        System.out.println("Yeni Tutar: " + newAmountDouble);

        String recordedAmountText = getValue(recordedAmount).split("\\.")[0].replace(",","");
        int recordedAmountDouble = Integer.parseInt(recordedAmountText);
        System.out.println("Ilk Tutar: " + recordedAmountDouble);

        System.out.println("Beklenen tutar: " + (recordedAmountDouble-subtractedAmount));
        System.out.println("Goruntulenen Tutar: " + newAmountText);

        assertTrue(recordedAmountDouble+subtractedAmount==newAmountDouble);
        logger.info("Hesaplanan tutar doğru");


    }

    @Step("Alert görüntülenme kontrolu")
    public void isAlertPresent() {
        Boolean isAlertPresent;
        try {
            driver.switchTo().alert(); // Eğer alert varsa buraya geçer
            isAlertPresent = true; // Alert bulundu
        } catch (NoAlertPresentException e) {
            isAlertPresent = false; // Alert bulunamadı
        }
        assertTrue(isAlertPresent==true, "Alert görüntülenmedi");
    }

    @Step("Alert görüntülenmeme kontrolu")
    public void isAlertNotPresent() {
        Boolean isAlertPresent;
        try {
            driver.switchTo().alert(); // Eğer alert varsa buraya geçer
            isAlertPresent = true; // Alert bulundu
        } catch (NoAlertPresentException e) {
            isAlertPresent = false; // Alert bulunamadı
        }
        assertTrue(isAlertPresent==false, "Alert görüntülendi");
    }

    @Step("Alertü kabul et")
    public void acceptAlert() {
        Alert alert = driver.switchTo().alert();
        alert.accept();


    }
    @Step("<key> menusünden random seçim yap")
    public void clickOnRandomItemInList(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = 1 + random.nextInt(elements.size() - 1);
        elements.get(index).click();
        logger.info("Tiklanan elementin indexi : " + key);
        logger.info(index + " elementine random tiklandi");
    }

    @Step("<key> elementine <kaydedilenHesapTutari> + <arttirilacakTutar> değeri yazılır")
    public void checkSubtraction(String key, String kaydedilenHesapTutari, int arttirilacakTutar) {
        String kaydedilenHesapTutariText = getValue(kaydedilenHesapTutari).split("\\.")[0].replace(",","");
        System.out.println("kaydedilenHesapTutariText = " + kaydedilenHesapTutariText);
        int kaydedilenHesapTutariDouble = Integer.parseInt(kaydedilenHesapTutariText);

        System.out.println("arttırılacak tutar = " + arttirilacakTutar);
        int gonderilecekTutar = kaydedilenHesapTutariDouble + arttirilacakTutar;
        System.out.println("gonderilecekTutar = " + gonderilecekTutar);

        String gonderilecekTutarText = String.valueOf(gonderilecekTutar).replace(".","");
        System.out.println("gonderilecekTutarText = " + gonderilecekTutarText);

        findElement(key).sendKeys(gonderilecekTutarText);
    }

    @Step("<key> elementine 1 ile <maxKarakter> karakter arasi rastgele sayı yaz")
    public void sendRandomNumber(String key, int maxKarakter ) {
        char[] chars = "1234567890".toCharArray();
        String stringRandom = "";
        Random random = new Random();
        int dongu = random.nextInt(maxKarakter);
        for (int i = 0; i < dongu; i++) {
            stringRandom = stringRandom + chars[random.nextInt(chars.length)];
        }
        System.out.println(stringRandom);
        findElement(key).sendKeys(stringRandom);
    }


    @Step("<key> elementine 1 ile <maxKarakter> karakter arasi rastgele text yaz")
    public void sendRandomString(String key, int maxKarakter ) {
        char[] chars = "abcçdefgğhıijklmnoöprsştuüvyzxqw".toCharArray();
        String stringRandom = "";
        Random random = new Random();
        int dongu = random.nextInt(maxKarakter);
        for (int i = 0; i < dongu; i++) {
            stringRandom = stringRandom + chars[random.nextInt(chars.length)];
        }
        System.out.println(stringRandom);
        findElement(key).sendKeys(stringRandom);
    }
}


