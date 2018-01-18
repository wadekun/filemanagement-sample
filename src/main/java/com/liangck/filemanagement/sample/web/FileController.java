package com.liangck.filemanagement.sample.web;

import com.google.common.io.Files;
import com.liangck.filemanagement.sample.client.FastDFSClient;
import com.liangck.filemanagement.sample.domain.File;
import com.liangck.filemanagement.sample.repository.FileRepository;
import jdk.nashorn.internal.objects.NativeNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liangck
 * @version 1.0
 * @since 2018/1/16 18:10
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @RequestMapping(value = {"", "/", "/list"}, method = RequestMethod.GET)
    public ResponseEntity<List<File>> query() {
        return ResponseEntity.ok(fileRepository.findAll());
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(400).body("请选择要上传的文件！");
        }

        String fileKey = null;

        try {
            String filename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            fileKey = FastDFSClient.uploadFile(bytes, filename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(String.format("文件上传失败！%s", e.getMessage()));
        }

        File po = new File();
        po.setOriginName(file.getOriginalFilename());
        po.setFdfsKey(fileKey);
        po.setUploadTime(LocalDateTime.now());
        fileRepository.save(po);

        return ResponseEntity.ok("上传成功！");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void download(@PathVariable Long id, HttpServletResponse response) {
        String fileKey = fileRepository.getOne(id).getFdfsKey();

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.setContentType(mimeTypesMap.getContentType(fileKey));
        response.setHeader("Content-Disposition", "attachment;filename=" + Files.getNameWithoutExtension(fileKey) + "." + Files.getFileExtension(fileKey));
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            FastDFSClient.downloadFile(fileKey, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String fileKey = fileRepository.getOne(id).getFdfsKey();
        int res = FastDFSClient.deleteFile(fileKey);
        if (res == -1) {
            return ResponseEntity.status(500).body("删除文件失败！");
        }

        fileRepository.delete(id);

        return ResponseEntity.ok("删除文件成功！");
    }
}
