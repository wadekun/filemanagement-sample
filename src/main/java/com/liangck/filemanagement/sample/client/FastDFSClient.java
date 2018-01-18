package com.liangck.filemanagement.sample.client;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Description: FastDFS文件上传下载工具类 </p>
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * @author yangxin
 * @version 1.0
 * @date 2016/10/19
 */
public class FastDFSClient {

    private static final String CONFIG_FILENAME = "fastdfs-client.properties";

    private static StorageClient1 storageClient1 = null;

    // 初始化FastDFS Client
    static {
        try {
            ClientGlobal.init(CONFIG_FILENAME);
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                throw new IllegalStateException("getConnection return null");
            }

            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                throw new IllegalStateException("getStoreStorage return null");
            }

            storageClient1 = new StorageClient1(trackerServer, storageServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param fileName 文件名
     * @return
     */
    public static String uploadFile(File file, String fileName) {
        return uploadFile(file, fileName, null);
    }

    /**
     * 上传文件
     *
     * @param bytes    文件二进制内容
     * @param fileName 文件名
     * @return
     */
    public static String uploadFile(byte[] bytes, String fileName) {
        return uploadFile(bytes, fileName, null);
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param fileName 文件名
     * @param metaList 文件元数据
     * @return
     */
    public static String uploadFile(File file, String fileName, Map<String, String> metaList) {
        try {
            return uploadFile(IOUtils.toByteArray(new FileInputStream(file)), fileName, metaList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param bytes    文件二进制内容
     * @param fileName 文件名
     * @param metaList 元数据
     * @return
     */
    public static String uploadFile(byte[] bytes, String fileName, Map<String, String> metaList) {
        try {
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            return storageClient1.upload_file1(bytes, Files.getFileExtension(fileName), nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件元数据
     *
     * @param fileId 文件ID
     * @return
     */
    public static Map<String, String> getFileMetadata(String fileId) {
        try {
            NameValuePair[] metaList = storageClient1.get_metadata1(fileId);
            if (metaList != null) {
                HashMap<String, String> map = new HashMap<String, String>();
                for (NameValuePair metaItem : metaList) {
                    map.put(metaItem.getName(), metaItem.getValue());
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除失败返回-1，否则返回0
     */
    public static int deleteFile(String fileId) {
        try {
            return storageClient1.delete_file1(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 下载文件
     *
     * @param fileId  文件ID（上传文件成功后返回的ID）
     * @param outFile 文件下载保存位置
     * @return
     */
    public static int downloadFile(String fileId, File outFile) {
       OutputStream os = null;
        try {
            os = new FileOutputStream(outFile);
            return downloadFile(fileId, os);
        } catch (FileNotFoundException e) {
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

        return -1;
    }

    public static int downloadFile(String fileId, OutputStream os) {
        InputStream is = null;
        try {
            byte[] content = storageClient1.download_file1(fileId);
            is = new ByteArrayInputStream(content, 0, content.length);
            IOUtils.copy(is, os);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getToken(String fileId) {
        // unix seconds
        int ts = (int) Instant.now().getEpochSecond();
        // token
        String token = "null";
        try {
            token = ProtoCommon.getToken(Files.getNameWithoutExtension(fileId), ts, ClientGlobal.getG_secret_key());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("token=").append(token);
        sb.append("&ts=").append(ts);

        return sb.toString();
    }

    public static void main(String[] args) {
        File download_png = new File("test_download_fastdfs.png");
        FastDFSClient.downloadFile("group1/M00/00/00/wKgB51pdpHCAW4aaAAAYvtZ3D7A033.png", download_png);
    }
}