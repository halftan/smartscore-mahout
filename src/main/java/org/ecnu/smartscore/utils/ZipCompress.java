package org.ecnu.smartscore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Microdog <dlangu0393@gmail.com>
 *
 */
public class ZipCompress {

	public static void main(String[] args) throws Exception {
		String compressDirectory = "C:/Users/Kainan/Documents/ZipTest/";
		String decompressFile = "C:/Users/Kainan/Documents/ZipTest.zip";

		// Uncomment or comment the following lines to test the program
		compressDir(compressDirectory);
		// decompressZip(decompressFile);

	}

	public static void compressDir(String path) throws Exception {
		// The directory to be compressed
		File compressDir = new File(path);
		// The output compressed file
		File outputZip = new File(compressDir.getPath() + ".zip");

		// Compressed file(.zip) output file stream
		FileOutputStream zipFileOutputStream = new FileOutputStream(outputZip);

		// ZipOutputStream used for compressing
		ZipOutputStream zipOutputStream = new ZipOutputStream(
				zipFileOutputStream);

		// Do compress recursively
		recursionZip(compressDir, zipOutputStream, compressDir.getParent()
				+ "\\");

		// Close ZipOutputStream
		zipOutputStream.close();
		// Close FileOutputStream
		zipFileOutputStream.close();
	}

	/**
	 * @param directory
	 *            The directory to be processed
	 * @param zip
	 *            ZipOutputStream
	 * @param baseDir
	 *            The base path used in entry path truncating
	 * @throws Exception
	 *             Just throw all exception
	 */
	protected static void recursionZip(File directory, ZipOutputStream zip,
			String baseDir) throws Exception {
		// FileInputStream used for reading input file
		FileInputStream fileInputStream;
		// Read buffer
		byte[] buffer = new byte[8192];
		// Number of bytes read used in file reading
		int bytesRead = 0;

		// Get all files(subdirectory included) of current directory
		File[] files = directory.listFiles();
		if (files.length == 0) { // Empty dir
			// Create directory entry
			zip.putNextEntry(new ZipEntry(directory.getPath().replace(baseDir,
					"")
					+ "/"));
			zip.closeEntry();
		} else {
			for (File file : files) {
				if (file.isDirectory()) {
					// New directory found
					recursionZip(file, zip, baseDir);
				} else {
					// Create file entry
					zip.putNextEntry(new ZipEntry(file.getPath().replace(
							baseDir, "")));
					// Open input file
					fileInputStream = new FileInputStream(file);

					// Read input file and write to zip output stream
					while ((bytesRead = fileInputStream.read(buffer)) > 0) {
						zip.write(buffer, 0, bytesRead);
					}

					// Close input file stream
					fileInputStream.close();
					// Close zip file entry
					zip.closeEntry();
				}
			}
		}
	}

	/**
	 * @param zipPath
	 * @throws Exception
	 */
	public static void decompressZip(String zipPath) throws Exception {
		// Compressed file
		File zipFile = new File(zipPath);
		// FileInputStream for compressed file(.zip)
		FileInputStream zipFileInputStream = new FileInputStream(zipFile);
		// ZipInputStream
		ZipInputStream zipInputStream = new ZipInputStream(zipFileInputStream);

		// Path of parent directory of compressed file
		String basePath = zipFile.getParent() + "\\";

		// Output file read and parent file of it
		File outputFile, parentFile;
		// Decompressed FileOutputStream
		FileOutputStream fileOutputStream;
		// Read buffer
		byte[] buffer = new byte[8192];
		int bytesRead = 0;

		// ZipEntry read
		ZipEntry entry;
		// Loop until all had been processed
		while ((entry = zipInputStream.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				// Directory found, create directory
				new File(basePath + entry.getName() + "\\").mkdir();
			} else {
				// Create output file
				outputFile = new File(basePath + entry.getName());

				// Make parent dirs
				parentFile = outputFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}

				fileOutputStream = new FileOutputStream(outputFile);

				// Read zip input stream and write output file
				while ((bytesRead = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, bytesRead);
				}

				// Close output stream
				fileOutputStream.close();
			}
			zipInputStream.closeEntry();
		}

		zipInputStream.close();
	}
}
