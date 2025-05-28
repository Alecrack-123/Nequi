package co.edu.uniquindio.poo.nequii;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

public class PDFGenerator {

    public static void generarReporteTransacciones(Usuario usuario, String rutaArchivo) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDImageXObject logo = null;
            try (InputStream is = PDFGenerator.class.getResourceAsStream("/co/edu/uniquindio/poo/nequii/images/Logo.jpg")) {
                if (is != null) {
                    BufferedImage bufferedImage = ImageIO.read(is);
                    logo = LosslessFactory.createFromImage(document, bufferedImage);
                } else {
                    System.out.println("No se encontró el logo en el classpath.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                // Logo en la primera página
                if (logo != null) {
                    int logoX = 50;
                    int logoY = 650;
                    int logoW = 120;
                    int logoH = 120;
                    contentStream.addRect(logoX, logoY, logoW, logoH);
                    contentStream.setNonStrokingColor(255, 0, 0);
                    contentStream.fill();
                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.drawImage(logo, logoX, logoY, logoW, logoH);
                }
                // Configurar fuente y tamaño
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);

                // Título
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 750);
                contentStream.showText("Reporte de Transacciones - " + usuario.getNombreCompleto());
                contentStream.endText();

                // Fecha de generación
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 730);
                contentStream.showText("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                // Información del usuario
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 710);
                contentStream.showText("Correo: " + usuario.getCorreoElectronico());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(200, 690);
                contentStream.showText("Saldo actual: $" + String.format("%.2f", usuario.getSaldoTotal()));
                contentStream.endText();

                // Encabezados de la tabla
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                float y = 700;
                String[] headers = {"Fecha", "Tipo", "Monto", "Descripción"};
                float[] columnWidths = {100, 100, 100, 200};
                float x = 50;

                for (int i = 0; i < headers.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(headers[i]);
                    contentStream.endText();
                    x += columnWidths[i];
                }

                // Datos de las transacciones
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                y -= 20;
                List<Transaccion> transacciones = usuario.getTransacciones();

                for (Transaccion transaccion : transacciones) {
                    if (y < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        // Logo en cada nueva página
                        if (logo != null) {
                            contentStream.drawImage(logo, 50, 770, 120, 120);
                        }
                        y = 750;
                    }
                    x = 50;

                    // Fecha
                    LocalDate fecha = LocalDate.parse(transaccion.getFecha());
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    contentStream.endText();
                    x += columnWidths[0];

                    // Tipo
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(transaccion.getTipo().toString());
                    contentStream.endText();
                    x += columnWidths[1];

                    // Monto
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText("$" + String.format("%,.2f", transaccion.getMonto()));
                    contentStream.endText();
                    x += columnWidths[2];

                    // Descripción
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(transaccion.getDescripcion());
                    contentStream.endText();

                    y -= 15;
                }
            } finally {
                contentStream.close();
            }
            // Guardar el documento
            document.save(rutaArchivo);
        }
    }

    public static void generarReportePresupuestos(Usuario usuario, String rutaArchivo) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDImageXObject logo = null;
            try (InputStream is = PDFGenerator.class.getResourceAsStream("/co/edu/uniquindio/poo/nequii/images/Logo.jpg")) {
                if (is != null) {
                    BufferedImage bufferedImage = ImageIO.read(is);
                    logo = LosslessFactory.createFromImage(document, bufferedImage);
                } else {
                    System.out.println("No se encontró el logo en el classpath.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                // Logo en la primera página
                if (logo != null) {
                    contentStream.drawImage(logo, 50, 770, 120, 120);
                }
                // Configurar fuente y tamaño
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);

                // Título
                contentStream.beginText();
                contentStream.newLineAtOffset(120, 800);
                contentStream.showText("Reporte de Presupuestos - " + usuario.getNombreCompleto());
                contentStream.endText();

                // Fecha de generación
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(120, 780);
                contentStream.showText("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                // Encabezados de la tabla
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                float y = 740;
                String[] headers = {"Nombre", "Monto", "Estado"};
                float[] columnWidths = {200, 150, 150};
                float x = 50;

                for (int i = 0; i < headers.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(headers[i]);
                    contentStream.endText();
                    x += columnWidths[i];
                }

                // Datos de los presupuestos
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                y -= 20;
                List<Presupuesto> presupuestos = usuario.getPresupuestos();

                for (Presupuesto presupuesto : presupuestos) {
                    if (y < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        // Logo en cada nueva página
                        if (logo != null) {
                            contentStream.drawImage(logo, 50, 770, 120, 120);
                        }
                        y = 750;
                    }

                    x = 50;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(presupuesto.getNombre());
                    contentStream.endText();
                    x += columnWidths[0];

                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText("$" + String.format("%.2f", presupuesto.getMontoTotal()));
                    contentStream.endText();
                    x += columnWidths[1];

                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(presupuesto.getEstado().toString());
                    contentStream.endText();

                    y -= 15;
                }
            } finally {
                contentStream.close();
            }
            // Guardar el documento
            document.save(rutaArchivo);
        }
    }

    public static void generarReporteTransaccionesCuenta(Usuario usuario, Cuenta cuenta, String rutaArchivo) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDImageXObject logo = null;
            try (InputStream is = PDFGenerator.class.getResourceAsStream("/co/edu/uniquindio/poo/nequii/images/Logo.jpg")) {
                if (is != null) {
                    BufferedImage bufferedImage = ImageIO.read(is);
                    logo = LosslessFactory.createFromImage(document, bufferedImage);
                } else {
                    System.out.println("No se encontró el logo en el classpath.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                // Fondo morado en el encabezado
                contentStream.setNonStrokingColor(110, 10, 214); // Morado Nequi
                contentStream.addRect(0, 700, 600, 100);
                contentStream.fill();
                // Logo
                if (logo != null) {
                    contentStream.drawImage(logo, 50, 700, 100, 100);
                }
                // Título en blanco
                contentStream.setNonStrokingColor(255, 255, 255);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(250, 770);
                contentStream.showText("Reporte de Transacciones - " + usuario.getNombreCompleto());
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 13);
                float y = 740;
                contentStream.beginText();
                contentStream.newLineAtOffset(250, y);
                contentStream.showText("Cuenta: " + cuenta.getNumeroCuenta() + " - " + cuenta.getTipoCuenta());
                contentStream.endText();

                y -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(250, y);
                contentStream.showText("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

                y -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(250, y);
                contentStream.showText("Saldo actual: $" + String.format("%.2f", cuenta.getSaldo()));
                contentStream.endText();

                // Encabezados de la tabla
                y = 650;
                float x = 50;
                float[] columnWidths = {80, 80, 200, 80, 100};
                String[] headers = {"Tipo", "Monto", "Descripción", "Fecha", "Categoría"};
                contentStream.setNonStrokingColor(110, 10, 214); // Morado Nequi
                for (int i = 0; i < headers.length; i++) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(headers[i]);
                    contentStream.endText();
                    x += columnWidths[i];
                }
                contentStream.setNonStrokingColor(0, 0, 0); // Negro para el resto
                y -= 25;
                // Filtrar transacciones por la cuenta seleccionada y el usuario
                List<Transaccion> transacciones = Transaccion.getTransacciones().stream()
                        .filter(t ->
                                ((t.getCuentaOrigen() != null && t.getCuentaOrigen().getNumeroCuenta().equals(cuenta.getNumeroCuenta())) ||
                                        (t.getCuentaDestino() != null && t.getCuentaDestino().getNumeroCuenta().equals(cuenta.getNumeroCuenta()))) &&
                                        (t.getUsuario() != null && t.getUsuario().getCorreoElectronico().equals(usuario.getCorreoElectronico()))
                        )
                        .toList();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Transaccion transaccion : transacciones) {
                    x = 50;
                    contentStream.setNonStrokingColor(0, 0, 0); // Asegura texto negro
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(transaccion.getTipo().getDescripcion());
                    contentStream.endText();
                    x += columnWidths[0];
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(String.format("$%,.2f", transaccion.getMonto()));
                    contentStream.endText();
                    x += columnWidths[1];
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(transaccion.getDescripcion());
                    contentStream.endText();
                    x += columnWidths[2];
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(LocalDate.parse(transaccion.getFecha()).format(formatter));
                    contentStream.endText();
                    x += columnWidths[3];
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(transaccion.getCategoria() != null ? transaccion.getCategoria().getNombre() : "");
                    contentStream.endText();
                    y -= 20;
                    if (y < 60) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        y = 750;
                    }
                }
            } finally {
                contentStream.close();
            }
            document.save(rutaArchivo);
        }
    }
}
