package com.example.demo.jar;

import java.io.*;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.*;

public class JarHandler {

    // Giải nén JAR
    public static void extractJar(String jarFilePath, String destDir) throws IOException {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Files.createDirectories(Paths.get(destDir));
            jarFile.stream().forEach(entry -> {
                try {
                    File file = new File(destDir, entry.getName());
                    if (entry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        try (InputStream in = jarFile.getInputStream(entry);
                             OutputStream out = new FileOutputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Đóng gói lại các file vào JAR
    public static void repackageJar(String sourceDir, String jarFilePath) throws IOException {
        try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarFilePath))) {
            Files.walk(Paths.get(sourceDir))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        // Tạo một entry cho mỗi file
                        JarEntry entry = new JarEntry(file.toString().substring(sourceDir.length() + 1));
                        jarOut.putNextEntry(entry);
                        
                        // Ghi nội dung file vào JAR
                        Files.copy(file, jarOut);
                        jarOut.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    // Ghi các file từ danh sách vào JAR (giữ nguyên đường dẫn)
    public static void addFilesToJar(String jarFilePath, String[] filePaths) throws IOException {

        String buildSource = "F:/CODING/JAVA/MONITORING_CHAT/target/classes";
        String svnLink = "/Teller/branches/Rel_2.0_maint/TPTeller/";

        // Giải nén JAR ban đầu vào thư mục tạm
        String tempDir = "temp_extract";
        extractJar(jarFilePath, tempDir);

        // Thêm các file mới vào thư mục tạm
        for (String filePath : filePaths) {
            if (!filePath.endsWith(".java")) continue;

            String classFile = filePath.replace(svnLink, "").replace(".java", ".class");

            Path sourcePath = Paths.get(buildSource, classFile);
            Path targetPath = Paths.get(tempDir, "/BOOT-INF/classes", classFile);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Đóng gói lại JAR
        repackageJar(tempDir, jarFilePath);

        // Xóa thư mục tạm sau khi xong
        Files.walk(Paths.get(tempDir))
            .map(Path::toFile)
            .forEach(File::delete);
    }

    private static void addFileToJar(JarOutputStream jarOut, File file, String entryName) throws IOException {
        JarEntry entry = new JarEntry(entryName);
        jarOut.putNextEntry(entry);

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) >= 0) {
            jarOut.write(buffer, 0, length);
        }
        fis.close();
        jarOut.closeEntry();
    }
}
