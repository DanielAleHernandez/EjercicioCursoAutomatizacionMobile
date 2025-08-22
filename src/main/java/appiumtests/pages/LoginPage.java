package appiumtests.pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage
{
    private final AppiumDriver driver;

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    //Mapeo de los elementos
    private final By txtCorreo = By.xpath("//android.widget.EditText[@content-desc=\"Celular o correo electrónico,\"]");
    private final By txtPassword = By.xpath("//android.widget.EditText[@content-desc=\"Contraseña,\"]");
    private  final By btnIniciarSesion = By.xpath("//android.view.View[@content-desc=\"Iniciar sesión\"]");
    private final By dashboardPrincipalFB = By.xpath("(//android.widget.FrameLayout[@resource-id=\"android:id/content\"])[2]");
    private final By estasPensando = By.xpath("//android.view.ViewGroup[@content-desc=\"Haz una publicación en Facebook\"]");
    /**
     * Ingresa el texto de correo electronico.
     *
     * @param text Texto a buscar.
     */

    public void enterCorreoElectronico(String text)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement searchTxtCorreoWebElement = wait.until(ExpectedConditions.elementToBeClickable(txtCorreo));
        searchTxtCorreoWebElement.sendKeys(text);
    }

    /**
     * Ingresa el texto contrasenia.
     *
     * @param text Texto a buscar.
     */
    public void enterPassword(String text)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement searchTxtPasswordWebElement = wait.until(ExpectedConditions.elementToBeClickable(txtPassword));
        searchTxtPasswordWebElement.sendKeys(text);
    }

    /**
    * Hace clic en el botón de búsqueda.
    */
    public void clickButtonIniciarSesion()
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement searchBtnIniciarSesionWebElement = wait.until(ExpectedConditions.elementToBeClickable(btnIniciarSesion));
        searchBtnIniciarSesionWebElement.click();
    }

    /**
     * Carga el Dashboard principal de Facebook
     */
    public void esperarPaginaPrincipalFB()
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        //wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardPrincipalFB));
        try {
            // Espera a que el icono Home esté visible
            WebElement dashboard = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(estasPensando)
            );
            System.out.println("Dashboard principal cargado correctamente.");

            // Mantener la vista 5 segundos
            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("No se pudo cargar el dashboard principal: " + e.getMessage());
        }


    }



}
