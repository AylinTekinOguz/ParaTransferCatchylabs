package base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.ElementInfo;
import org.apache.logging.log4j.LogManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    protected static WebDriver driver;
    protected static Actions actions;
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    DesiredCapabilities capabilities;
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;
    EdgeOptions edgeOptions;

    String browserName = "chrome";

    String testURL = "https://catchylabs-webclient.testinium.com/";


    public static final String DEFAULT_DIRECTORY_PATH = "elementValues";
    ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();



    @BeforeScenario
    public void setUp() {
        logger.info("************************************  BeforeScenario  ************************************");
        if (StringUtils.isEmpty(System.getenv("key"))) {
            logger.info("Local cihazda " + browserName + " browserında test ayağa kalkacak");

                if ("chrome".equalsIgnoreCase(browserName)) {
                    driver = new ChromeDriver(chromeOptions());
                    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    driver.get(testURL);
                } else if ("firefox".equalsIgnoreCase(browserName)) {
                    driver = new FirefoxDriver(firefoxOptions());
                    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    driver.get(testURL);
                } else if ("edge".equalsIgnoreCase(browserName)) {
                    driver = new EdgeDriver(edgeOptions());
                    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    driver.get(testURL);
                }
                actions = new Actions(driver);

            }

        }

    @AfterScenario
    public void tearDown() {
        driver.quit();
    }

    public void initMap(File[] fileList) {
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                elementInfoList = gson
                        .fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream()
                        .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
            } catch (FileNotFoundException e) {
                logger.warn("{} not found", e);
            }
        }
    }


    public File[] getFileList() {
        File[] fileList = new File("src/test/resources/" + DEFAULT_DIRECTORY_PATH).listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
        if (fileList == null) {
            logger.warn(
                    "File Directory Is Not Found! Please Check Directory Location. Default Directory Path = {}",
                    DEFAULT_DIRECTORY_PATH);
            throw new NullPointerException();
        }
        return fileList;
    }

    /**
     * Set Chrome options
     *
     * @return the chrome options
     */
    public ChromeOptions chromeOptions() {
        chromeOptions = new ChromeOptions();
        capabilities = DesiredCapabilities.chrome();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("--kiosk");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--start-fullscreen");
        System.setProperty("webdriver.chrome.driver", "web_driver/chromedriver.exe");
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    /**
     * Set Microsoft Edge options
     *
     * @return the edge options
     */
    public EdgeOptions edgeOptions() {
        edgeOptions = new EdgeOptions();
        Map<String, Object> map = new HashMap<>();
        List<String> args = Arrays.asList("--start-fullscreen", "--disable-blink-features=AutomationControlled"
                //,"-inprivate"
                , "--ignore-certificate-errors");
        map.put("args", args);
        edgeOptions.setCapability("ms:edgeOptions", map);
        System.setProperty("webdriver.edge.driver", "web_driver/msedgedriver.exe");
        edgeOptions.merge(capabilities);
        edgeOptions.setCapability("--start-fullscreen", true);
        return edgeOptions;
    }

    /**
     * Set Firefox options
     *
     * @return the firefox options
     */
    public FirefoxOptions firefoxOptions() {
        firefoxOptions = new FirefoxOptions();
        capabilities = DesiredCapabilities.firefox();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        firefoxOptions.addArguments("--kiosk");
        firefoxOptions.addArguments("--disable-notifications");
        firefoxOptions.addArguments("--start-fullscreen");
        FirefoxProfile profile = new FirefoxProfile();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setCapability("marionette", true);
        firefoxOptions.merge(capabilities);
        System.setProperty("webdriver.gecko.driver", "web_driver/geckodriver");
        return firefoxOptions;
    }

    public ElementInfo findElementInfoByKey(String key) {
        return (ElementInfo) elementMapList.get(key);
    }

    public void saveValue(String key, String value) {
        elementMapList.put(key, value);
    }

    public String getValue(String key) {
        return elementMapList.get(key).toString();
    }


}
