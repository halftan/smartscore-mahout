/**
 * 
 */
package org.ecnu.smartscore.utils;

import java.io.File;

import org.ecnu.smartscore.configs.ServerConfig;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class FilePath {
	public static String getPathInUserDir(String path2) {
		return new File(System.getProperty("user.dir"), path2).getPath();
	}

	public static String getFullInputPath(String inputPath) {
		return new File(ServerConfig.getString("sc.data.path.input"), inputPath)
				.getPath();
	}

	public static String getFullOutputPath(String outputPath) {
		return new File(ServerConfig.getString("sc.data.path.output"),
				outputPath).getPath();
	}
}
