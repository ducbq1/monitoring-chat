package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Controller
public class VideoController {

    private static final String VIDEO_DIR = "src/main/resources/static/video/";

    // Trang HTML phát video
    @GetMapping("/video")
    public String videoPage(Model model) {
        ViewHelper.setView(model, "view/video-player", "Video Player");
        return "layout"; // Load layout.html
    }

    // Stream video hỗ trợ Range (tua, phát mượt)
    @GetMapping("/videos/{filename:.+}")
    public ResponseEntity<Resource> getVideo(
            @PathVariable String filename,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Path videoPath = Path.of(VIDEO_DIR + filename);
        Resource video = new UrlResource(videoPath.toUri());

        if (!video.exists()) {
            return ResponseEntity.notFound().build();
        }

        String range = request.getHeader(HttpHeaders.RANGE);
        long fileLength = video.contentLength();

        if (range == null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                    .body(video);
        }

        List<HttpRange> httpRanges = HttpRange.parseRanges(range);
        HttpRange httpRange = httpRanges.get(0);
        long start = httpRange.getRangeStart(fileLength);
        long end = httpRange.getRangeEnd(fileLength);
        long rangeLength = end - start + 1;

        return ResponseEntity
                .status(206)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength))
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength)
                .body(new UrlResource(videoPath.toUri()));
    }
}
