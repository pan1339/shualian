package com.dajing.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.dajing.ocr.shualian.SLOCRService;
import com.dajing.util.FileViewer;

public class BufferedImageService {
	private static Logger logger = Logger.getLogger(BufferedImageService.class);

	public static void main(String[] args) {
		long time = System.currentTimeMillis();

		// 该文件的路劲在classes根路径下
		BufferedImage bufferedImage = readPicture("E:/shualian_images/imagelib/99.jpg");

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getWidth();

		System.out.println(width);
		System.out.println(height);

		/**
		 * 缩小32*32
		 */
		bufferedImage = scale(bufferedImage);

		List<int[]> pixelsList = new ArrayList<int[]>();
		for (int i = 0; i < 4; i++) {
			BufferedImage bufferedImageRotate = rotate(bufferedImage, i * 90);
			int[] pixels = readPixelsDeviateWeightsArray(bufferedImageRotate);
			pixelsList.add(pixels);
		}

		List<int[]> pixelsListSrc = readAllPixelsDeviateWeightsArrayFromRoot("/config/ocrlib/images", "jpg");

		time = System.currentTimeMillis() - time;
		System.out.println(time);
		time = System.currentTimeMillis();

		System.out.println(check(pixelsList, pixelsListSrc));

		time = System.currentTimeMillis() - time;
		System.out.println(time);
	}

	/**
	 * 比对图片similarity为0-1值,越接近1越相似
	 * 
	 * @param time
	 * @param pixelsList
	 * @param pixelsListSrc
	 * @param similarity
	 * @return
	 */
	public static int check(List<int[]> pixelsList, List<int[]> pixelsListSrc, double similarity) {
		int index = 0;
		for (int[] pixelsSrc : pixelsListSrc) {
			for (int i = 0; i < 4; i++) {
				int hammingDistance = getHammingDistance(pixelsList.get(i), pixelsSrc);
				double mSimilarity = calSimilarity(hammingDistance);
				if (mSimilarity >= similarity) {
					return i;
				}
			}
		}
		return index;
	}

	/**
	 * 比对图片similarity默认值为0.7,为0-1值,越接近1越相似
	 * 
	 * @param time
	 * @param pixelsList
	 * @param pixelsListSrc
	 * @param similarity
	 * @return
	 */
	public static int check(List<int[]> pixelsList, List<int[]> pixelsListSrc) {

		return check(pixelsList, pixelsListSrc, 0.7);
	}

	/**
	 * 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getHammingDistance(BufferedImage bufferedImage, BufferedImage srcBufferedImage) {

		int[] pixels = getPixels(bufferedImage);
		int[] pixelsSrc = getPixels(srcBufferedImage);

		return getHammingDistance(pixels, pixelsSrc);
	}

	/**
	 * 通过汉明距离计算相似度
	 * 
	 * @param hammingDistance
	 * @return
	 */
	public static double calSimilarity(int hammingDistance) {
		int length = 32 * 32;
		double similarity = (length - hammingDistance) / (double) length;

		// 使用指数曲线调整相似度结果
		similarity = java.lang.Math.pow(similarity, 2);
		return similarity;
	}

	/**
	 * 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getHammingDistance(int[] pixels, int[] pixelsSrc) {
		int sum = 0;
		for (int i = 0; i < pixels.length; i++) {
			sum += pixels[i] == pixelsSrc[i] ? 0 : 1;
		}
		pixels = null;
		pixelsSrc = null;
		return sum;
	}

	/**
	 * 转换成灰度图片
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toGrayscale(BufferedImage bufferedImage) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage grayBuffered = op.filter(bufferedImage, null);
		return grayBuffered;
	}

	/**
	 * 缩放至32x32像素缩略图
	 * 
	 * @param bufferedImage
	 * @return
	 */
	public static BufferedImage scale(BufferedImage bufferedImage) {
		return scale(bufferedImage, 32, 32);
	}

	/**
	 * 剪切处理
	 */
	public static BufferedImage clip(BufferedImage src, int srcX, int srcY, int width, int height) {
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(src, 0, 0, width, height, srcX, srcY, srcX + width, srcY + height, null);
		g.dispose();

		return newImg;
	}

	/**
	 * 缩放至width*height像素缩略图
	 * 
	 * @param bufferedImage
	 * @param width
	 * @param height
	 * @return
	 */

	public static BufferedImage scale(BufferedImage bufferedImage, int width, int height) {
		Image scaledImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		output.createGraphics().drawImage(scaledImage, 0, 0, null);// 画图
		return output;
	}

	/**
	 * 读取跟路径下的所有imageType格式图片像素数组
	 * 
	 * @param fileName
	 * @param imageType
	 * @return
	 * 
	 * 
	 */
	public static List<int[]> readAllPixelsFromRoot(String fileName, String imageType) {
		List<int[]> pixelsList = new ArrayList<int[]>();
		List<BufferedImage> bufferedImages = readAllPictureFromRoot(fileName, imageType);

		for (BufferedImage bufferedImage : bufferedImages) {
			pixelsList.add(getPixels(bufferedImage));
		}

		return pixelsList;
	}

	/**
	 * * // 获取灰度像素数组
	 * 
	 * toGrayscale int[] pixels = getPixels(image); // 获取平均灰度颜色 int averageColor
	 * = getAverageOfPixelArray(pixels);
	 * 
	 * 
	 * // 转换至灰度 image = toGrayscale(image); // 缩小成32x32的缩略图 image =
	 * scale(image); // 获取灰度像素数组 int[] pixels = getPixels(image); // 获取平均灰度颜色
	 * int averageColor = getAverageOfPixelArray(pixels); //
	 * 获取灰度像素的比较数组（即图像指纹序列） pixels = getPixelDeviateWeightsArray(pixels,
	 * averageColor);
	 * 
	 * @param fileName
	 * @param imageType
	 * @return
	 */
	public static List<int[]> readAllPixelsDeviateWeightsArrayFromRoot(String fileName, String imageType) {
		List<int[]> pixelsList = new ArrayList<int[]>();
		List<BufferedImage> bufferedImages = readAllPictureFromRoot(fileName, imageType);
		for (BufferedImage bufferedImage : bufferedImages) {
			bufferedImage = scale(bufferedImage);

			// 转换至灰度
			bufferedImage = toGrayscale(bufferedImage);
			// 获取灰度像素数组
			int[] pixels = getPixels(bufferedImage);
			// 获取平均灰度颜色
			int averageColor = getAverageOfPixelArray(pixels);
			// 获取灰度像素的比较数组（即图像指纹序列）
			pixels = getPixelDeviateWeightsArray(pixels, averageColor);
			pixelsList.add(pixels);
		}
		return pixelsList;
	}

	public static int[] readPixelsDeviateWeightsArray(BufferedImage bufferedImage) {

		// 转换至灰度
		bufferedImage = toGrayscale(bufferedImage);
		// 获取灰度像素数组
		int[] pixels = getPixels(bufferedImage);
		// 获取平均灰度颜色
		int averageColor = getAverageOfPixelArray(pixels);
		// 获取灰度像素的比较数组（即图像指纹序列）
		pixels = getPixelDeviateWeightsArray(pixels, averageColor);

		return pixels;
	}

	/**
	 * 读取该路径下的所有imageType格式图片
	 * 
	 * @param path
	 * @param imageType
	 * @return
	 */
	public static List<BufferedImage> readAllPicture(String path, String imageType) {

		List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
		List<String> imagePaths = FileViewer.getListFiles(path, "." + imageType);
		for (String imagePath : imagePaths) {
			BufferedImage bufferedImage = readPicture(imagePath);
			if (bufferedImage != null) {
				bufferedImages.add(scale(bufferedImage));
			}
		}
		return bufferedImages;
	}

	/**
	 * 读取跟路径下的所有imageType格式图片
	 * 
	 * @param fileName
	 * @param imageType
	 * @return
	 */
	public static List<BufferedImage> readAllPictureFromRoot(String fileName, String imageType) {
		// 该文件的路劲在classes根路径下
		String ccrlibPath = SLOCRService.class.getResource(fileName).getPath();

		return readAllPicture(ccrlibPath, imageType);
	}

	/**
	 * 生成图片
	 * 
	 * @param bufferedImage
	 * @param disposedImage
	 * @return
	 */
	public static boolean createPic(BufferedImage bufferedImage, File disposedImage) {
		boolean flag = false;
		String imageType = getFileType(disposedImage.getName());

		String parentDirectory = disposedImage.getParent();
		checkDirectoryExist(parentDirectory);

		try {
			flag = ImageIO.write(bufferedImage, imageType, disposedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 生成图片
	 * 
	 * @param bufferedImage
	 * @param disposedImage
	 * @return
	 */
	public static boolean createPic(BufferedImage bufferedImage, String disposedImage) {
		return createPic(bufferedImage, new File(disposedImage));
	}

	private static void checkDirectoryExist(String path) {
		File parentPath = new File(path);

		if (!parentPath.exists() && !parentPath.isDirectory()) {
			System.out.println("//不存在");
			parentPath.mkdir();
		}
	}

	/**
	 * 获取灰度图的平均像素颜色值
	 * 
	 * @param pixels
	 * @return
	 */
	public static int getAverageOfPixelArray(int[] pixels) {
		Color color;
		long sumRed = 0;
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			sumRed += color.getRed();
		}
		int averageRed = (int) (sumRed / pixels.length);
		return averageRed;
	}

	/**
	 * 获取灰度图的像素比较数组（平均值的离差）
	 * 
	 * @param pixels
	 * @param averageColor
	 * @return
	 */
	public static int[] getPixelDeviateWeightsArray(int[] pixels, int averageColor) {
		Color color;
		int[] dest = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
		}
		return dest;
	}

	/**
	 * 获取图片类型
	 * 
	 * @param imageName
	 * @return
	 */
	private static String getFileType(String imageName) {
		String imageType = "jpg";
		int index = imageName.lastIndexOf(".");
		if (index != -1 && index != imageName.length()) {
			imageType = imageName.substring(index + 1);
		}
		return imageType;
	}

	/**
	 * 读取指定图片
	 */
	public static BufferedImage readPicture(String path) {
		BufferedImage bi = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			bi = ImageIO.read(file);
		} catch (Exception e) {
			logger.error("读取图片错误 path:" + path + ", " + e.toString());
		}
		return bi;
	}

	/**
	 * 读取指定图片
	 */
	public static BufferedImage readPicture(byte[] imageBytes) {
		BufferedImage bi = null;
		
		ByteArrayInputStream in = new ByteArrayInputStream(imageBytes); // 将b作为输入流；
		try {
			bi = ImageIO.read(in); // 将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bi;
	}

	/**
	 * 获取像素数组
	 * 
	 * @param bufferedImage
	 * @return
	 */
	public static int[] getPixels(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth(null);
		int height = bufferedImage.getHeight(null);
		int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
		return pixels;
	}

	/**
	 * 像素数组画出图片
	 * 
	 * @param rbg
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage paintPixels(int[] rbg, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int color = rbg[w + width * h];
				Color c = new Color(color);
				g2.setColor(c);
				g2.drawLine(w, h, w + 1, h + 1);
			}
		}
		return bi;
	}

	// /**
	// *
	// * @param src
	// * @param angel
	// * @return
	// */
	// public static BufferedImage rotate(Image src, int angel) {
	// int src_width = src.getWidth(null);
	// int src_height = src.getHeight(null);
	// // calculate the new image size
	// Rectangle rect_des = CalcRotatedSize(new Rectangle(new
	// Dimension(src_width, src_height)), angel);
	//
	// BufferedImage res = null;
	// res = new BufferedImage(rect_des.width, rect_des.height,
	// BufferedImage.TYPE_INT_RGB);
	// Graphics2D g2 = res.createGraphics();
	// // transform
	// g2.translate((rect_des.width - src_width) / 2, (rect_des.height -
	// src_height) / 2);
	// g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
	//
	// g2.drawImage(src, null, null);
	// return res;
	// }

	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		// if angel is greater than 90 degree, we need to do some conversion
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}

		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}

	// angle：角度，图片旋转角度
	public static BufferedImage rotate(BufferedImage bufferedImage, int angle) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int new_w = 0, new_h = 0;
		int new_radian = angle;
		if (angle <= 90) {
			new_w = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
		} else if (angle <= 180) {
			new_radian = angle - 90;
			new_w = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
		} else if (angle <= 270) {
			new_radian = angle - 180;
			new_w = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
		} else {
			new_radian = angle - 270;
			new_w = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
		}
		BufferedImage toStore = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = toStore.createGraphics();
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(angle), width / 2, height / 2);
		if (angle != 180) {
			AffineTransform translationTransform = findTranslation(affineTransform, bufferedImage, angle);
			affineTransform.preConcatenate(translationTransform);
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, new_w, new_h);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawRenderedImage(bufferedImage, affineTransform);
		g.dispose();
		bufferedImage = toStore;

		return bufferedImage;
	}

	private static AffineTransform findTranslation(AffineTransform at, BufferedImage bi, int angle) {// 45
		Point2D p2din, p2dout;
		double ytrans = 0.0, xtrans = 0.0;
		if (angle <= 90) {
			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();
		}
		/*
		 * else if(angle<=135){ p2din = new Point2D.Double(0.0, bi.getHeight());
		 * p2dout = at.transform(p2din, null); ytrans = p2dout.getY();
		 * 
		 * p2din = new Point2D.Double(bi.getWidth(),bi.getHeight()); p2dout =
		 * at.transform(p2din, null); xtrans = p2dout.getX();
		 * 
		 * }
		 */
		else if (angle <= 180) {
			p2din = new Point2D.Double(0.0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		/*
		 * else if(angle<=225){ p2din = new Point2D.Double(bi.getWidth(),
		 * bi.getHeight()); p2dout = at.transform(p2din, null); ytrans =
		 * p2dout.getY();
		 * 
		 * p2din = new Point2D.Double(bi.getWidth(),0.0); p2dout =
		 * at.transform(p2din, null); xtrans = p2dout.getX();
		 * 
		 * }
		 */
		else if (angle <= 270) {
			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		} else {
			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;
	}

}
