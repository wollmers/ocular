package edu.berkeley.cs.nlp.ocular.data;

import fileio.f;
import edu.berkeley.cs.nlp.ocular.image.ImageUtils;
import edu.berkeley.cs.nlp.ocular.image.ImageUtils.PixelType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import edu.berkeley.cs.nlp.ocular.preprocessing.Binarizer;
import edu.berkeley.cs.nlp.ocular.preprocessing.Cropper;
import edu.berkeley.cs.nlp.ocular.preprocessing.LineExtractor;
import edu.berkeley.cs.nlp.ocular.preprocessing.Straightener;
import threading.BetterThreader;

public class RawImageLoader implements ImageLoader {

	public static class RawImageDocument implements ImageLoader.Document {
		private final String baseName;
		final PixelType[][][] observations;
		
		public RawImageDocument(String inputPath, String baseName, int lineHeight, double binarizeThreshold) {
			this.baseName = baseName;
			double[][] levels = ImageUtils.getLevels(f.readImage(inputPath+"/"+baseName));
			double[][] rotLevels = Straightener.straighten(levels);
			double[][] cropLevels = Cropper.crop(rotLevels, binarizeThreshold);
			Binarizer.binarizeGlobal(binarizeThreshold, cropLevels);
			List<double[][]> lines = LineExtractor.extractLines(cropLevels);
			observations = new PixelType[lines.size()][][];
			for (int i=0; i<lines.size(); ++i) {
				if (lineHeight >= 0) {
					observations[i] = ImageUtils.getPixelTypes(ImageUtils.resampleImage(ImageUtils.makeImage(lines.get(i)), lineHeight));
				} else {
					observations[i] = ImageUtils.getPixelTypes(ImageUtils.makeImage(lines.get(i)));
				}
			}
		}

		public PixelType[][][] loadLineImages() {
			return observations;
		}

		public String[][] loadLineText() {
			return null;
		}
		
		public String baseName() {
			return baseName;
		}

	}
	
	private final String inputPath;
	private final int lineHeight;
	private final int numThreads;
	private final double binarizeThreshold;

	public RawImageLoader(String inputPath, int lineHeight, double binarizeThreshold, int numThreads) {
		this.inputPath = inputPath;
		this.lineHeight = lineHeight;
		this.binarizeThreshold = binarizeThreshold;
		this.numThreads = numThreads;
	}
	
	public List<Document> readDataset() {
		System.out.println("Extracting text line images from dataset "+inputPath);
		File dir = new File(inputPath);
		final String[] dirList = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith("."); // ignore hidden files
			}
		});
		final Document[] docs = new Document[dirList.length]; 
		BetterThreader.Function<Integer,Object> func = new BetterThreader.Function<Integer,Object>(){public void call(Integer i, Object ignore){
			String baseName = dirList[i];
			docs[i] = new RawImageDocument(inputPath, baseName, lineHeight, binarizeThreshold);
		}};
		BetterThreader<Integer,Object> threader = new BetterThreader<Integer,Object>(func, numThreads);
		for (int i=0; i<dirList.length; ++i) threader.addFunctionArgument(i);
		threader.run();
		return Arrays.asList(docs);
	}

}
