package br.com.cadastroit.services.xls.poi.utils;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

public class Constants {

	public static final String XLSDIRECTORYBASE = "doc";
	public static final String IMG = "img";
	public static final String DOC_PATH = ClassLoaderUtil.getClassPath() + File.separator + XLSDIRECTORYBASE;
	public static final String IMG_PATH = ClassLoaderUtil.getClassPath() + File.separator + IMG;
	public static final String EXTENSION = ".xls";
	public static final String EXTENSION_XLSX = ".xlsx";
	public static final String EXTENSION_PNG = ".png";
	public static final String EXPORT_REL = "relatorio" + System.currentTimeMillis() + EXTENSION;
	public static final String EXPORT_BOOK = "book" + System.currentTimeMillis() + EXTENSION;
	public static final String EXPORT_2007 = "export2007" + System.currentTimeMillis() + EXTENSION_XLSX;
	public static final String EXPORT_PRODUCT = "product" + System.currentTimeMillis() + EXTENSION;
	public static final String SHEETNAME = "RELATORIO_DESIF";

	public static String getBasePath(HttpServletRequest request) {
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
				+ "/";
		return basePath;
	}
}