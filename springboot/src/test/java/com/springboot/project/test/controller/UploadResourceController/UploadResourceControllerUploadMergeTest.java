package com.springboot.project.test.controller.UploadResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import com.springboot.project.common.storage.RangeUrlResource;
import com.springboot.project.test.BaseTest;
import io.reactivex.rxjava3.core.Observable;

public class UploadResourceControllerUploadMergeTest extends BaseTest {
    private List<String> urlList;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/upload/merge").build();
        var response = this.testRestTemplate.postForEntity(url, urlList, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("/resource/"));
        var result = this.testRestTemplate.getForEntity(new URIBuilder(response.getBody()).build(), byte[].class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, result.getHeaders().getContentType());
        assertTrue(result.getHeaders().getContentDisposition().isInline());
        assertEquals("default.jpg", result.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, result.getHeaders().getContentDisposition().getCharset());
        assertEquals(9287, result.getBody().length);
        assertNotNull(result.getHeaders().getETag());
        assertTrue(result.getHeaders().getETag().startsWith("\""));
        assertEquals("max-age=604800, no-transform, public", result.getHeaders().getCacheControl());
        assertEquals(9287, result.getHeaders().getContentLength());
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        var imageResource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
        var everySize = 100;
        this.urlList = Observable
                .range(0,
                        new BigDecimal(imageResource.contentLength()).divide(new BigDecimal(everySize))
                                .setScale(0, RoundingMode.CEILING).intValue())
                .map(startIndex -> {
                    var url = new URIBuilder("/upload/resource").build();
                    var body = new LinkedMultiValueMap<Object, Object>();
                    var rangeLength = everySize;
                    if (imageResource.contentLength() < startIndex * everySize + everySize) {
                        rangeLength = Long.valueOf(imageResource.contentLength() - startIndex * everySize).intValue();
                    }
                    body.set("file", new RangeUrlResource(ClassLoader.getSystemResource("image/default.jpg"),
                            startIndex * everySize, rangeLength));
                    var response = this.testRestTemplate.postForEntity(url, body, String.class);
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    return response.getBody();
                }).toList().blockingGet();
    }
}