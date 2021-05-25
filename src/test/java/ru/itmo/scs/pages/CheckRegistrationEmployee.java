package ru.itmo.scs.pages;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.scs.Constants;
import ru.itmo.scs.Context;
import ru.itmo.scs.exceptions.InvalidPropertiesException;
import ru.itmo.scs.utils.Properties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckRegistrationEmployee {
    private static final Logger logger = Logger.getLogger(CheckRegistrationEmployee.class);

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

        if (context.getGeckodriver() != null) {
            System.setProperty(Constants.WEBDRIVER_FIREFOX_DRIVER, context.getGeckodriver());
            driverList.add(new FirefoxDriver());
        }
        if (context.getChromedriver() != null) {
            System.setProperty(Constants.WEBDRIVER_CHROME_DRIVER, context.getChromedriver());
            driverList.add(new ChromeDriver());
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
    public void checkRegistration(){
        driverList.forEach(driver ->{
            driver.get("https://hh.ru/account/login?backurl=%2F");
            driver.manage().window().maximize();
            driver.findElement(By.xpath("//span[contains(@data-qa,'expand-login-by-password')]")).click();
            driver.findElement(By.xpath("//input[contains(@placeholder,'Email или телефон')]")).click();
            driver.findElement(By.xpath("//input[contains(@placeholder,'Email или телефон')]")).sendKeys("dima.vyatkin.00@mail.ru");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
            driver.findElement(By.xpath("//input[contains(@placeholder,'Пароль')]")).click();
            driver.findElement(By.xpath("//input[contains(@placeholder,'Пароль')]")).sendKeys("test12345");
            driver.findElement(By.xpath("//span[contains(text(),'Войти')]")).click();
            {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300).toMillis());
                wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//iframe[contains(@title, 'recaptcha')]"), 0));
            }
            driver.findElement(By.xpath("//span[contains(text(),'Войти')]")).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(300).toMillis(), TimeUnit.MILLISECONDS);
            //Assertions.assertTrue(driver.findElement(By.xpath("//h1[contains(text(),'Найдено')]")).isDisplayed());
        });
    }
}
