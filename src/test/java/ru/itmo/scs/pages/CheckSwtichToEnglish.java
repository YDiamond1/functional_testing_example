package ru.itmo.scs.pages;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.scs.Constants;
import ru.itmo.scs.Context;
import ru.itmo.scs.exceptions.InvalidPropertiesException;
import ru.itmo.scs.utils.Properties;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckSwtichToEnglish {
    private static final Logger logger = Logger.getLogger(CheckSwtichToEnglish.class);

    public Context context;
    public List<WebDriver> driverList;
    static List<JavascriptExecutor> jsList;

    @BeforeEach
    public void setUp() {
        context = new Context();
        driverList = new ArrayList<>();
        jsList = new ArrayList<>();
        try {
            Properties.getInstance().reading(context);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }

//        if (context.getGeckodriver() != null) {
//            System.setProperty(Constants.WEBDRIVER_FIREFOX_DRIVER, context.getGeckodriver());
//            FirefoxProfile firefoxProfile = new FirefoxProfile(new File("cmyxm934.TestFirefox"));
//            FirefoxOptions firefoxOptions = new FirefoxOptions();
//            firefoxOptions.setProfile(firefoxProfile);
//            driverList.add(new FirefoxDriver(firefoxOptions));
//        }
        if (context.getChromedriver() != null) {
            System.setProperty(Constants.WEBDRIVER_CHROME_DRIVER, context.getChromedriver());
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--user-data-dir=C:\\Users\\YDiamond\\AppData\\Local\\Google\\Chrome\\User Data\\","C:\\Users\\YDiamond\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 1\\");
            driverList.add(new ChromeDriver(options));
        }
        if (driverList.isEmpty()) throw new InvalidPropertiesException();
        driverList.forEach(driver->{
            jsList.add((JavascriptExecutor) driver);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
        });

    }

    @AfterEach
    public  void tearDown(){
        driverList.forEach(WebDriver::quit);
    }

    @Test
    public void checkSwitch(){
        driverList.forEach(driver ->{
            driver.get("https://hh.ru/");
            driver.manage().window().maximize();
            JavascriptExecutor js = (JavascriptExecutor)driver;

            WebElement element = driver.findElement(By.xpath("//a[contains(text(), 'Switch to English')]"));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            js.executeScript("arguments[0].click();" , element);

            {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300).toMillis());
                wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[contains(text(), 'Мои резюме')]"), 0));
            }

//            driver.findElement(By.xpath("//a[contains(text(), 'Switch to English')]")).click();
            Assertions.assertTrue(driver.findElement(By.xpath("//div[contains(text(), 'My Resumes')]")) != null);

            element = driver.findElement(By.xpath("//a[contains(text(), 'Russian version')]"));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            js.executeScript("arguments[0].click();" , element);
        });
    }
}
