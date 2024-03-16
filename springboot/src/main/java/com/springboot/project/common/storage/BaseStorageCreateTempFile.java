package com.springboot.project.common.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.uuid.Generators;
import cn.hutool.core.util.ZipUtil;

@Component
public class BaseStorageCreateTempFile extends BaseStorageIsDirectory {

    /**
     * Create temporary files or folders based on the relative path of the resource
     * 
     * @param relativePathOfResource
     * @return
     */
    public File createTempFileOrFolder(String relativePathOfResource) {
        var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        var request = new MockHttpServletRequest();
        request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
        var resource = this.getResourceFromRequest(request);
        if (this.cloud.enabled()) {
            if (resource instanceof ByteArrayResource) {
                var tempFolder = this.createTempFolder();
                this.writeToFolderByRelativePath(tempFolder, relativePathOfResource);
                return tempFolder;
            } else {
                return this.createTempFile(resource);
            }
        } else {
            return new File(this.getRootPath(), relativePath);
        }
    }

    private void writeToFolderByRelativePath(File tempFolder, String relativePathOfResource) {
        var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        var request = new MockHttpServletRequest();
        request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
        try (var input = this.getResourceFromRequest(request).getInputStream()) {
            var jsonString = IOUtils.toString(input, StandardCharsets.UTF_8);
            var nameListOfChildFileAndChildFolder = this.objectMapper.readValue(jsonString,
                    new TypeReference<List<String>>() {

                    });
            for (var nameOfChildFileAndChildFolder : nameListOfChildFileAndChildFolder) {
                if (nameOfChildFileAndChildFolder.endsWith("/")) {
                    var tempFolderOfChildFileAndChildFolder = new File(tempFolder, nameOfChildFileAndChildFolder);
                    tempFolderOfChildFileAndChildFolder.mkdirs();
                    this.writeToFolderByRelativePath(tempFolderOfChildFileAndChildFolder,
                            Paths.get(relativePath, nameOfChildFileAndChildFolder).toString());
                } else {
                    var requestOfChildFileAndChildFolder = new MockHttpServletRequest();
                    requestOfChildFileAndChildFolder.setRequestURI(this.getResoureUrlFromResourcePath(
                            Paths.get(relativePath, nameOfChildFileAndChildFolder).toString()));
                    try (var inputOfChildFileAndChildFolder = this
                            .getResourceFromRequest(requestOfChildFileAndChildFolder).getInputStream()) {
                        var tempFileOfChildFileAndChildFolder = new File(tempFolder, nameOfChildFileAndChildFolder);
                        FileUtils.copyInputStreamToFile(inputOfChildFileAndChildFolder,
                                tempFileOfChildFileAndChildFolder);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File createTempFile(Resource resource) {
        try {
            String fileName = this.getFileNameFromResource(resource);
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("File name cannot be empty");
            }
            File targetFile = new File(this.createTempFolder(), fileName);
            try (InputStream input = resource.getInputStream()) {
                FileUtils.copyInputStreamToFile(input, targetFile);
            }
            return targetFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public File createTempFile(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            File targetFile = new File(this.createTempFolder(), file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(input, targetFile);
            return targetFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public File createTempFolder() {
        File tempFolder = new File(this.getRootPath(), Generators.timeBasedReorderedGenerator().generate().toString());
        tempFolder.mkdirs();
        return tempFolder;
    }

    public File createTempFolderByDecompressingZipResource(Resource resourceOfZipFile) {
        try (var input = new ZipInputStream(resourceOfZipFile.getInputStream())) {
            File tempFolder = this.createTempFolder();
            ZipUtil.unzip(input, tempFolder);
            return tempFolder;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
