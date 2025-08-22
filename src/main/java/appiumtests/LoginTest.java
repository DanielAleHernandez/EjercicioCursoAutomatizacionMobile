package appiumtests;


import appiumtests.pages.LoginPage;
import appiumtests.utils.TestReport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class LoginTest {
    private AppiumDriver driver; //Driver de Appium
    private TestReport report; //Se utiliza para la generación de los reportes

    //Establecemos las capabilities
    @BeforeMethod
    public void setUp() throws MalformedURLException {
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("deviceName", "Redmi9A");
        cap.setCapability("udid", "QCN7HQFIUSRS9XLZ");
        cap.setCapability("platformName", "Android");
        cap.setCapability("platformVersion", "10");
        cap.setCapability("automationName", "UiAutomator2");
        //capabilities de la App de Facebook
        cap.setCapability("appPackage", "com.facebook.katana");
        cap.setCapability("appActivity", "com.facebook.katana.LoginActivity");
        cap.setCapability("adbExecTimeout", 60000); // <-- Aumenta a 60s

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), cap);
        report = new TestReport("LoginTest");
    }


    @Test(description = "Ingresar a Facebok")
    public void loginFacebook() throws InterruptedException
    {
        ingresarTelefonoUCorreo("7731570013");
        ingresarContrasena("quimica11");
        darClickEnIniciarSesion();
        esperarDashboardPrincipal();
    }

    @Step("Ingresar correo")
    public void ingresarTelefonoUCorreo(String telefonoUCorreo)
    {
        LoginPage homePage = new LoginPage(driver);
        homePage.enterCorreoElectronico(telefonoUCorreo);
        report.takeScreenshot(driver, "Ingresar telefono o correo electronico del usuario");
    }

    @Step("Ingresar Contraseña")
    public void ingresarContrasena(String password)
    {
        LoginPage homePage = new LoginPage(driver);
        homePage.enterPassword(password);
        report.takeScreenshot(driver,"Ingresar la contraseña del usuario");
    }

    @Step("Dar click en el boton iniciar sesion")
    public void darClickEnIniciarSesion()
    {
        LoginPage homePage = new LoginPage(driver);
        homePage.clickButtonIniciarSesion();
        report.takeScreenshot(driver, "Dar Click en el boton iniciar sesion");
    }

    @Step("Esperar a que cargue la pagina principal de Facebook")
    public void esperarDashboardPrincipal()
    {
        LoginPage homePage = new LoginPage(driver);
        homePage.esperarPaginaPrincipalFB();
        report.takeScreenshot(driver,"Dasboard principal de la pagina de Facebook");
    }


    @AfterMethod
    public void tearDown() throws IOException {
        // Generar el PDF
        String pdfFilePath = report.generateTestReport();

        // Adjuntar el PDF al reporte de Allure
        attachPdfToAllure(pdfFilePath);

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Adjunta el PDF generado al reporte de Allure.
     *
     * @param pdfFilePath Ruta del archivo PDF.
     */
    @Attachment(value = "Test Report PDF", type = "application/pdf")
    private byte[] attachPdfToAllure(String pdfFilePath) throws IOException {
        return Files.readAllBytes(Paths.get(pdfFilePath));
    }

    // Métodos para manejar la alerta y el botón de "Atrás"
    private boolean isAlertVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            WebElement alertCancelButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("com.alibaba.aliexpresshd:id/tv_dialog_cancel")
            ));
            return alertCancelButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void dismissAlert() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alertCancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("com.alibaba.aliexpresshd:id/tv_dialog_cancel")
        ));
        alertCancelButton.click();
        System.out.println("Alerta descartada.");
    }

    private void pressBackButton() {
        try {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            System.out.println("Botón de 'Atrás' presionado.");
        } catch (Exception e) {
            ((AndroidDriver) driver).executeScript("mobile: shell", ImmutableMap.of(
                    "command", "input",
                    "args", ImmutableList.of("keyevent", "KEYCODE_BACK")
            ));
            System.out.println("Botón de 'Atrás' simulado con ADB.");
        }
    }


}
