package Enviart.Enviart.util;

import freemarker.template.Template;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para generar reportes PDF usando FreeMarker y Flying Saucer
 */
@Component
public class PdfGenerator {

    private final FreeMarkerConfigurer configurer;

    public PdfGenerator(FreeMarkerConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * Genera un PDF a partir de una plantilla FreeMarker
     * 
     * @param templateName Nombre de la plantilla (sin extensión)
     * @param dataKey      Clave para la lista de datos en el modelo
     * @param datos        Lista de datos para el reporte
     * @param desde        Fecha desde (opcional)
     * @param hasta        Fecha hasta (opcional)
     * @param response     Respuesta HTTP para la descarga
     */
    public void generarPdf(String templateName, String dataKey, List<?> datos, LocalDate desde, LocalDate hasta,
            HttpServletResponse response) throws Exception {

        // 1. Crear modelo de datos para la plantilla
        Map<String, Object> model = new HashMap<>();
        model.put(dataKey, datos);
        model.put("total", datos.size());
        model.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (desde != null) {
            model.put("desde", desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (hasta != null) {
            model.put("hasta", hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // 2. Obtener y procesar la plantilla FreeMarker
        Template template = configurer.getConfiguration().getTemplate(templateName + ".ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // 3. Configurar respuesta HTTP para descarga de PDF
        response.setContentType("application/pdf");
        String filename = templateName + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        // 4. Convertir HTML a PDF usando Flying Saucer
        ITextRenderer renderer = new ITextRenderer();

        // Establecer la URL base para resolver recursos (imágenes, css)
        // Usamos file:// para que pueda resolver rutas relativas desde
        // src/main/resources/static
        try {
            String baseUrl = new java.io.File("src/main/resources/static/").toURI().toString();
            renderer.setDocumentFromString(html, baseUrl);
        } catch (Exception e) {
            // Si falla, usar sin base URL
            renderer.setDocumentFromString(html);
        }

        renderer.layout();
        renderer.createPDF(response.getOutputStream());
    }
}
