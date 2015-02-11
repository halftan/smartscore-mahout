/**
 * 
 */
package org.ecnu.smartscore.configs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.ecnu.smartscore.utils.FilePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class ServerConfig {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ServerConfig.class);
	private static Properties p = null;

	public static synchronized void load() {
		if (p == null) {
			LOGGER.info("Loading configs...");
			p = initProperties();
			LOGGER.info("Configurations loaded.");
		}
	}

	static protected Properties initProperties() {
		Properties p = new Properties();
		try {
			LOGGER.info("Load config file: {}",
					FilePath.getPathInUserDir("configs.properties"));
			InputStream inputStream = new FileInputStream(
					FilePath.getPathInUserDir("configs.properties"));
			p.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("Cannot open config file!");
			LOGGER.error(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			LOGGER.error("Cannot load config file!");
			LOGGER.error(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Illegal config file!");
			LOGGER.error(e.toString());
			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
		if (!checkProperties(p)) {
			LOGGER.error("Config file check failed!");
		}
		return p;
	}

	private static boolean checkProperties(Properties p) {
		return true;
	}

	public static String getProperty(String key) {
		return p.getProperty(key);
	}

	public static int getInteger(String key) {
		return Integer.parseInt(p.getProperty(key));
	}

	public static long getLong(String key) {
		return Long.parseLong(p.getProperty(key));
	}

	public static double getDouble(String key) {
		return Double.parseDouble(p.getProperty(key));
	}

	public static String getString(String key) {
		return p.getProperty(key);
	}

    public static List<String> getStringList(String key) {
        return getStringList(key, ",");
    }

    public static List<String> getStringList(String key, String splitter) {
        return Arrays.asList(getString(key).split(splitter));
    }

	public static boolean getBoolean(String key) {
		return p.getProperty(key).toLowerCase().equals("true") ? true : false;
	}
}