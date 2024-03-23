package com.springboot.project.common.SpannerUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class SpannerUtil {

    private static Boolean isSpannerEmulator = null;

    public static boolean getIsSpannerEmulator() {
        if (isSpannerEmulator == null) {
            synchronized (SpannerUtil.class) {
                if (isSpannerEmulator == null) {
                    var databaseSourceUrlOfENV = System.getenv("SPRING_DATASOURCE_URL");
                    if (StringUtils.isNotBlank(databaseSourceUrlOfENV)) {
                        isSpannerEmulator = databaseSourceUrlOfENV.toLowerCase()
                                .contains("autoConfigEmulator=true".toLowerCase());
                    } else {
                        try (var input = new ClassPathResource("application.yml").getInputStream()) {
                            var urlOfDatasource = new YAMLMapper()
                                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8)).get("spring")
                                    .get("datasource").get("url").asText();
                            isSpannerEmulator = urlOfDatasource.toLowerCase()
                                    .contains("autoConfigEmulator=true".toLowerCase());
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return isSpannerEmulator;
    }
}
