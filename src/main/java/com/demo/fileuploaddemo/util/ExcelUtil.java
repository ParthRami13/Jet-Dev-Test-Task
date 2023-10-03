package com.demo.fileuploaddemo.util;

import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {
	
	private static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }
}
