/**
 * 
 */
package org.ecnu.smartscore.utils;

import java.io.File;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class FilePath {
	public static String getPathInUserDir(String path2) {
		return new File(System.getProperty("user.dir"), path2).getPath();
	}
}
