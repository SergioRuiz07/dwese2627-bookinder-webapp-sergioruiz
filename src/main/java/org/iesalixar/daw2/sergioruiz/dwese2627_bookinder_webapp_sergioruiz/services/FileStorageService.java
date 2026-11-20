package org.iesalixar.daw2.sergioruiz.dwese2627_bookinder_webapp_sergioruiz.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Servicio para guardar y borrar los ficheros que sube el usuario (las portadas de los libros).
 *
 * Los archivos se guardan en una carpeta externa al proyecto que se indica en la propiedad
 * app.upload-root. Dentro de esa carpeta se usa la subcarpeta "uploads".
 *
 * Ejemplo de configuración:
 * - application.properties: app.upload-root=${APP_UPLOAD_PATH}
 * - .env: APP_UPLOAD_PATH=/home/sergio/dwese/bookinder-images
 *
 * Spring Boot sirve esa carpeta con
 * spring.web.resources.static-locations=classpath:/static/,file:${app.upload-root}/
 * Así, un archivo guardado en ${app.upload-root}/uploads/loquesea.png
 * se puede ver en el navegador con la ruta /uploads/loquesea.png.
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    /**
     * Carpeta raíz donde se guardan los ficheros. Se lee de la propiedad app.upload-root.
     */
    @Value("${app.upload-root}")
    private String uploadRootPath;

    /**
     * Subcarpeta para los ficheros subidos dentro de uploadRootPath.
     * No lleva barra al principio para que Path.resolve la junte bien con la carpeta raíz.
     */
    private static final String UPLOADS_SUBDIR = "uploads";

    /**
     * Guarda un archivo en disco y devuelve la ruta web para verlo (por ejemplo /uploads/uuid.png).
     *
     * @param file el archivo que se quiere guardar
     * @return la ruta web del archivo guardado, o null si ha habido un error
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Intento de guardar un archivo nulo o vacío.");
            return null;
        }
        try {
            // Nombre original y extensión
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            // Generar un nombre único
            String uniqueFileName = UUID.randomUUID().toString();
            if (!fileExtension.isBlank()) {
                uniqueFileName += "." + fileExtension;
            }
            // Carpeta de uploads: <uploadRootPath>/uploads/
            Path uploadsDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);
            // Crear las carpetas si no existen
            Files.createDirectories(uploadsDir);
            // Ruta completa del archivo
            Path filePath = uploadsDir.resolve(uniqueFileName);
            // Guardar el contenido
            Files.write(filePath, file.getBytes());
            logger.info("Archivo {} guardado con éxito en {}", uniqueFileName, filePath);
            // Devolvemos la ruta web que usará la vista: /uploads/<nombre>
            return "/" + UPLOADS_SUBDIR + "/" + uniqueFileName;
        } catch (IOException e) {
            logger.error("Error al guardar el archivo: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Borra un archivo del disco.
     * Acepta tanto el nombre del fichero (abc.png) como la ruta web (/uploads/abc.png).
     *
     * @param filePathOrWebPath nombre del archivo o ruta web
     */
    public void deleteFile(String filePathOrWebPath) {
        if (filePathOrWebPath == null || filePathOrWebPath.isBlank()) {
            logger.warn("Se ha intentado eliminar un archivo con nombre/ruta vacío.");
            return;
        }
        try {
            // Si viene como /uploads/xxxx, nos quedamos solo con el nombre del fichero
            String fileName = normalizeFileName(filePathOrWebPath);

            Path uploadsDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);
            Path filePath = uploadsDir.resolve(fileName).normalize();

            // Seguridad: no se permite borrar nada fuera de la carpeta de uploads
            if (!filePath.startsWith(uploadsDir.normalize())) {
                logger.warn("Ruta fuera del directorio de uploads, no se elimina: {}", filePath);
                return;
            }

            Files.deleteIfExists(filePath);
            logger.info("Archivo {} eliminado con éxito ({})", fileName, filePath);
        } catch (IOException e) {
            logger.error("Error al eliminar el archivo {}: {}", filePathOrWebPath, e.getMessage(), e);
        }
    }

    /**
     * Devuelve la extensión del archivo (sin el punto).
     *
     * @param fileName nombre del archivo
     * @return la extensión en minúsculas, o un texto vacío si no tiene extensión
     */
    private String getFileExtension(String fileName) {
        if (fileName != null) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0 && lastDot < fileName.length() - 1) {
                return fileName.substring(lastDot + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * Convierte una ruta web como /uploads/abc.png en el nombre del fichero abc.png.
     *
     * @param filePathOrWebPath nombre del archivo o ruta web
     * @return el nombre del fichero
     */
    private String normalizeFileName(String filePathOrWebPath) {
        String prefix = "/" + UPLOADS_SUBDIR + "/";
        if (filePathOrWebPath.startsWith(prefix)) {
            return filePathOrWebPath.substring(prefix.length());
        }
        return filePathOrWebPath;
    }
}
