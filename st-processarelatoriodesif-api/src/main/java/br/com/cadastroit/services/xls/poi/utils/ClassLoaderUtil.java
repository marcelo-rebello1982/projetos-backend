package br.com.cadastroit.services.xls.poi.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ClassLoaderUtil {

	private static String classPath = "";
	protected static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderUtil.class);
	private static ClassLoader loader = Thread.currentThread().getContextClassLoader();

	static {
		if (loader == null) {
			LOGGER.info("using system class loader!");
			loader = ClassLoader.getSystemClassLoader();
		}
		try {
			URL url = loader.getResource("");
			File f = new File(url.toURI());
			classPath = f.getAbsolutePath();
			classPath = URLDecoder.decode(classPath, "utf-8");
			if (classPath.contains(".jar!")) {
				LOGGER.warn("using config file inline jar!" + classPath);
				classPath = System.getProperty("user.dir");
				addCurrentWorkingDirClasspath(classPath);
			}
		} catch (Exception e) {
			LOGGER.warn("cannot get classpath using getResource(), now using user.dir");
			classPath = System.getProperty("user.dir");
			addCurrentWorkingDirClasspath(classPath);
		}
		LOGGER.info("classpath: {}", classPath);
	}

	private static void addCurrentWorkingDirClasspath(String pathAdded) {
		URLClassLoader urlClassLoader;
		try {
			urlClassLoader = new URLClassLoader(new URL[] { new File(pathAdded).toURI().toURL() }, loader);
			Thread.currentThread().setContextClassLoader(urlClassLoader);
		} catch (Exception e) {
			LOGGER.warn(e.toString());
		}
	}

	public static String getClassPath() {
		return classPath;
	}

	public static void setClassPath(String classPath) {
		ClassLoaderUtil.classPath = classPath;
	}
}