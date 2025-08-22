package appiumtests.utils;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Attachment(value = "Screenshot of {0}", type = "image/png")
    public byte[] saveScreenshot(String name, AppiumDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        AppiumDriver driver = (AppiumDriver) result.getTestContext().getAttribute("driver");
        if (driver != null) {
            saveScreenshot(result.getName(), driver);
        }
    }
}