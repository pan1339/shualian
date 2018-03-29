package com.dajing.aa;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dajing.image.BufferedImageService;
import com.dajing.image.CheckImage;
import com.dajing.image.PsImage;
import com.dajing.ocr.OCRUtil;
import com.dajing.util.FileUtil;
import com.dajing.util.FileViewer;

public class CheckTest {

	static String basePath = "E:/shualian_images/imagelib/";
	static String imagesPath = "E:/shualian_images/images/";
	static String newImagesPath = "E:/javacode/workspaces/ShuaLian/src/config/ocrlib/images";
	static List<int[]> pixelsListSrc;

	public static void main(String[] args) {

		// double similarityResult =
		// CheckImage.check("e:/main1496170462128_3t.jpg",
		// "e:/main1496170462128_3.jpg");
		// System.out.println(similarityResult);

		pixelsListSrc = BufferedImageService.readAllPixelsDeviateWeightsArrayFromRoot("/config/ocrlib/images", "jpg");

		for (;;) {

			dowmImage();

			clipImages();

			/**
			 * 去重复
			 */

			copyLimit();

			/**
			 * 删除文件
			 */

			deleteFile();
		}

	}

	private static void deleteFile() {
		List<String> imagePaths = FileViewer.getListFiles(basePath, ".jpg", false);
		imagePaths.addAll(FileViewer.getListFiles(imagesPath, ".jpg", false));

		for (String imagePath : imagePaths) {
			FileUtil.deleteFile(imagePath);
		}
	}

	/**
	 * 去重复
	 */

	private static void copyLimit() {
		List<String> imagesPaths = FileViewer.getListFiles(imagesPath, ".jpg");
		double similarity = 0.7;

		for (String imagePath : imagesPaths) {
			BufferedImage bufferedImage = BufferedImageService.readPicture(imagePath);
			// 缩放至32*32图片
			BufferedImage bi = BufferedImageService.scale(bufferedImage);

			boolean flag = true;

			for (int j = 0; j < 4; j++) {
				BufferedImage bufferedImageRotate = BufferedImageService.rotate(bi, j * 90);
				int[] pixels = BufferedImageService.readPixelsDeviateWeightsArray(bufferedImageRotate);
				for (int[] pixelsSrc : pixelsListSrc) {
					int hammingDistance = BufferedImageService.getHammingDistance(pixels, pixelsSrc);
					double mSimilarity = BufferedImageService.calSimilarity(hammingDistance);

					if (mSimilarity > similarity) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					break;
				}
			}

			if (flag) {
				String newImage = newImagesPath + imagePath.substring(imagePath.lastIndexOf("\\"), imagePath.length());
				FileUtil.copyFile(imagePath, newImage);
				System.out.println("copy----" + imagePath + "====>" + newImage);
				pixelsListSrc.add(BufferedImageService.readPixelsDeviateWeightsArray(bi));
			}
		}

	}

	/**
	 * 分割图片
	 */
	private static void clipImages() {
		List<String> imagePaths = FileViewer.getListFiles(basePath, ".jpg", false);
		for (String imagePath : imagePaths) {
			File imageFile = new File(imagePath);
			try {
				PsImage psImage = new PsImage(imageFile);
				BufferedImage bufferedImage = psImage.getBufferedImage();

				int width = bufferedImage.getWidth();
				int height = bufferedImage.getHeight();

				int num = width / 125;
				System.out.println(width + "*" + height + ", " + num);

				if (height != 125 && width != 500 && num != 4) {
					continue;
				}

				for (int i = 0; i < num; i++) {
					PsImage mpsImage = new PsImage(imageFile);
					mpsImage = mpsImage.clip(i * 125, 0, 125, 125);
					File file = new File("E:/shualian_images/images/" + System.currentTimeMillis() + ".jpg");
					mpsImage.createPic(file);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载图片
	 */
	private static void dowmImage() {
		for (int i = 0; i < 100; i++) {
			OCRUtil.checkOcrImage("http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/any/littleplugin/verifyImage.htm",
					null, "E:/shualian_images/imagelib/" + System.currentTimeMillis() + ".jpg");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(i);
		}
	}

}
