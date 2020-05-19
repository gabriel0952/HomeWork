package com.example.demotest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.List;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AndroidContactsTest {
    private static AppiumDriver<AndroidElement> driver;

    @BeforeClass
    public static void setUp() throws Exception {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File("D:\\AndroidStudioProjects\\HomeWork\\app\\build\\outputs\\apk\\debug");
        File app = new File(appDir, "app-debug.apk");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "TestDevice");
        capabilities.setCapability("platformVersion", "8.1");               // 配置模擬器系統版本
        capabilities.setCapability("app", app.getAbsolutePath());           // 設置 app 的路径
        capabilities.setCapability("appPackage", "com.example.homework");  // 設置 app 的 package
        capabilities.setCapability("appActivity", ".MainActivity");         // 設置 app 的初始 activity

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Thread.sleep(1000);
        driver.quit();
    }

    @Test
    public void test_1_loginTest() throws Exception {
        AndroidElement account = driver.findElement(By.id("com.example.homework:id/user_account_editText"));
        AndroidElement password = driver.findElement(By.id("com.example.homework:id/user_password_editText"));
        AndroidElement button = driver.findElement(By.id("com.example.homework:id/login_button"));

        account.sendKeys("Ray001");
        password.sendKeys("123456");
        button.click();

        Thread.sleep(4000);
    }

    @Test
    public void test_2_selectCity() throws Exception {
        AndroidElement spinner = driver.findElementById("com.example.homework:id/spinner");
        spinner.click();

        Thread.sleep(1000);

        List<AndroidElement> listItem2 = driver.findElementsByClassName("android.widget.TextView");
        for (int i = 0; i < listItem2.size(); i++) {
            System.out.println(listItem2.get(i).getText());
        }
        listItem2.get(2).click();

        Thread.sleep(4000);
    }

    @Test
    public void test_3_refreshList() throws Exception {
        AndroidElement refreshButton = driver.findElementById("com.example.homework:id/reload_imageButton");
        refreshButton.click();

        Thread.sleep(4000);
    }

    @Test
    public void test_4_logout() throws Exception {
        AndroidElement logoutButton = driver.findElementById("com.example.homework:id/action_logout");
        logoutButton.click();

        AndroidElement listButton = driver.findElementByClassName("android.app.AlertDialog");
//        for (int i = 0; i < listButton.size(); i++) {
//            System.out.println(listButton.get(i).getText());
//        }
        listButton.sendKeys("YES");
        Thread.sleep(4000);
    }

    @Test
    public void test_5_checkClean() throws Exception {
        AndroidElement account = driver.findElement(By.id("com.example.homework:id/user_account_editText"));
        AndroidElement password = driver.findElement(By.id("com.example.homework:id/user_password_editText"));

        Assert.assertEquals(account.getText(), "Ray001");
        Assert.assertEquals(password.getText(), "123456");
    }
}
