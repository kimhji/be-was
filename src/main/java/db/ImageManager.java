package db;

import common.Config;
import customException.DBExceptionConverter;
import customException.WebStatusConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageManager {
    static String basePath = Config.IMAGE_BASE_PATH;
    static String baseProfilePath = Config.IMAGE_PROFILE_BASE_PATH;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    public static String saveImagePost(byte[] imageBytes) {
        return saveImage(basePath, imageBytes);
    }

    public static String saveImageProfile(byte[] imageBytes) {
        return saveImage(baseProfilePath, imageBytes);
    }

    private static String saveImage(String inputBasePath, byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw DBExceptionConverter.failToLoadImage();
        }

        File dir = new File(inputBasePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 생성
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String fileName = timestamp + ".png";

        File file = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
            fos.flush();
        } catch (IOException e) {
            throw WebStatusConverter.fileWriteError();
        }
        return fileName;
    }

    public static byte[] readImagePost(String path){
        return readImage(basePath, path);
    }

    public static byte[] readImageProfile(String path){
        return readImage(baseProfilePath, path);
    }

    private static byte[] readImage(String inputBasePath, String path){
        File file = new File(inputBasePath+"/"+path);
        if (!file.exists() || !file.isFile()) {
            throw DBExceptionConverter.failToLoadImage();
        }

        byte[] image = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int read = 0;
            while (read < image.length) {
                int bytesRead = fis.read(image, read, image.length - read);
                if (bytesRead == -1) break;
                read += bytesRead;
            }
        } catch (IOException e) {
            throw DBExceptionConverter.failToLoadImage();
        }

        return image;
    }
}
