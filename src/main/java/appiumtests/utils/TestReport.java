package appiumtests.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReport {

    private static final String BASE_SCREENSHOT_PATH = "screenshots/";
    private static final String PDF_PATH = "screenshots/";

    private String testName;
    private List<String> screenshotPaths = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    private int cont;

    public TestReport(String testName) {
        this.testName = testName;
        cont = 1;
    }

    /**
     * Toma una captura de pantalla y la guarda en un archivo.
     *
     * @param driver      WebDriver para tomar la captura.
     * @param description Descripción para la captura.
     */
    @Attachment(value = "Screenshot: {1}", type = "image/png")
    public byte[] takeScreenshot(WebDriver driver, String description) {
        // Tomar la captura de pantalla y guardarla en un archivo
        String screenshotPath = saveScreenshotToFile(driver, description);
        screenshotPaths.add(screenshotPath);
        descriptions.add(description);

        // Adjuntar la captura de pantalla al reporte de Allure
        try {
            return Files.readAllBytes(Paths.get(screenshotPath));
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Guarda la captura de pantalla en un archivo.
     *
     * @param driver      WebDriver para tomar la captura.
     * @param description Descripción para la captura.
     * @return Ruta del archivo de la captura.
     */
    private String saveScreenshotToFile(WebDriver driver, String description) {
        // Crear una subcarpeta con el nombre del test dentro de "screenshots"
        String testFolder = BASE_SCREENSHOT_PATH + testName + "/";
        Path screenshotDir = Paths.get(testFolder);
        if (!Files.exists(screenshotDir)) {
            try {
                Files.createDirectories(screenshotDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Nombre del archivo de la captura
        String fileName = testName + "_" + PdfUtil.dateTime() + "_step" + (cont++) + "_" + description + ".png";
        String fullPath = screenshotDir.resolve(fileName).toString();

        // Guardar la captura de pantalla
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshotFile.toPath(), Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullPath;
    }

    /**
     * Genera el PDF con las capturas tomadas durante la prueba.
     *
     * @return Ruta del archivo PDF generado.
     */
    public String generateTestReport() throws IOException {
        // Crear una subcarpeta con el nombre del test dentro de "screenshots"
        String testFolder = PDF_PATH;
        Path pdfDir = Paths.get(testFolder);
        if (!Files.exists(pdfDir)) {
            try {
                Files.createDirectories(pdfDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Generar el PDF en la subcarpeta del test
        String pdfFilePath = testFolder + testName + "_" + PdfUtil.dateTime() + "_report.pdf";
        PdfUtil.generatePdfFromImages(screenshotPaths, descriptions, pdfFilePath);
        return pdfFilePath;
    }

    /**
     * Clase interna para generar el PDF a partir de las capturas.
     */
    public static class PdfUtil {
        public static void generatePdfFromImages(List<String> images, List<String> descriptions, String outputPdf)
                throws IOException {
            if (Files.exists(Paths.get(outputPdf))) {
                Files.delete(Paths.get(outputPdf));
            }

            if (images.size() != descriptions.size()) {
                throw new IllegalArgumentException("El número de imágenes debe coincidir con el número de descripciones.");
            }

            // Crear un escritor de PDF
            PdfWriter writer = new PdfWriter(outputPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Agregar imágenes y descripciones al PDF
            for (int i = 0; i < images.size(); i++) {
                // Agregar descripción como un párrafo
                document.add(new Paragraph("Paso " + (i + 1) + ": " + descriptions.get(i)));

                // Agregar imagen
                ImageData imageData = ImageDataFactory.create(images.get(i));
                Image img = new Image(imageData);
                img.scaleToFit(500, 500); // Escalar la imagen para que no supere el tamaño de la página
                document.add(img);

                // Agregar un salto de línea
                document.add(new Paragraph("\n"));
            }

            // Cerrar el documento
            document.close();
            System.out.println("PDF generado en: " + outputPdf);
        }

        public static String dateTime() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            return dateFormat.format(date);
        }
    }
}