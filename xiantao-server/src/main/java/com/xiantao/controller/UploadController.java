package com.xiantao.controller;

import com.xiantao.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads/}")
    private String urlPrefix;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        if (files.length > 9) {
            return Result.error("最多上传9张图片");
        }

        List<String> urls = new ArrayList<>();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // 将配置的上传根目录解析为绝对路径：MultipartFile.transferTo 对相对路径会按容器临时目录解析，
        // 导致与 mkdirs 创建的目录不一致而写入失败；此处统一转绝对路径，兼顾机器无关配置与写入正确性
        String fullPath = new File(uploadPath, datePath).getAbsolutePath();

        File dir = new File(fullPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建上传目录失败: {}", fullPath);
            return Result.error("服务器存储目录创建失败");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 文件大小校验
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("文件「" + file.getOriginalFilename() + "」超过5MB限制");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            // 文件类型白名单校验
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return Result.error("不支持的文件类型: " + extension + "，仅支持 jpg/jpeg/png/gif/webp/bmp");
            }

            // Content-Type 校验
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                return Result.error("文件内容类型不被允许");
            }

            // 使用 UUID 生成安全文件名，防止路径遍历攻击
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            File destFile = new File(fullPath + File.separator + newFilename);

            // 确保目标路径在上传目录内
            try {
                String destCanonicalPath = destFile.getCanonicalPath();
                String dirCanonicalPath = dir.getCanonicalPath();
                if (!destCanonicalPath.startsWith(dirCanonicalPath)) {
                    log.error("检测到路径遍历攻击: {}", originalFilename);
                    return Result.error("文件名不合法");
                }
            } catch (IOException e) {
                return Result.error("文件路径验证失败");
            }

            try {
                file.transferTo(destFile);
                String url = urlPrefix + datePath + "/" + newFilename;
                urls.add(url);
                log.info("文件上传成功: {} -> {}", originalFilename, url);
            } catch (IOException e) {
                log.error("文件上传失败: {}", e.getMessage());
                return Result.error("文件上传失败: " + e.getMessage());
            }
        }

        return Result.success("上传成功", urls);
    }

}
