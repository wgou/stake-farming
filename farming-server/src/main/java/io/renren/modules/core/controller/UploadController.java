package io.renren.modules.core.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.constants.Constants;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Api
@RestController
@RequestMapping("/upload")
public class UploadController {

	@Value("${stake.upload}")
	String upload;
	
	@PostMapping("image")
	public R uploadImage(@RequestParam("file") MultipartFile file) {
	    if (file.isEmpty()) {
	        return R.error("file is null");
	    }
	    try {
	    	String day = DateUtils.format(new Date(),DateUtils.DATEPATTERN);
	        String uploadDir = Constants.filePath + day;
	        Path directoryPath = Paths.get(uploadDir);
	        if (!Files.exists(directoryPath)) {  // 检查目录是否存在，如果不存在则创建
	            Files.createDirectories(directoryPath); // 创建多层目录
	        }
	        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	        Path filePath = Paths.get(uploadDir, fileName);
	        Files.write(filePath, file.getBytes());
	        String imageUrl = new StringBuffer(upload).append("/files/").append(day).append("/").append(fileName).toString();
	        return  R.ok().put("url", imageUrl);
	    } catch (IOException e) {
	       log.error("文件上传失败.",e.getLocalizedMessage());
	       throw new RRException("file uplod fail.");
	    }
	}

}
