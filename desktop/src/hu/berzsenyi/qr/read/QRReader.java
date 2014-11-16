package hu.berzsenyi.qr.read;

import java.util.List;

public class QRReader {
	public static class Options {
		public float finderPatternFinderRange = 0.4F;
		public float finderPatternFinderRange2 = 0.25F;
		public float finderPatternFinderRange3 = 0.33F;
		public float aligmentPatternFinderRange = 0.25F;
		public float aligmentPatternFinderRange2 = 0.2F;
		public float aligmentPatternFinderRange3 = 0.25F;
		public int gausRadius = 1;
		public float gausThreshold = 1F;
		
		public Options(Configuration config) {
			this.finderPatternFinderRange = Float.parseFloat(config.getValue("finderPatternFinderRange", ""+this.finderPatternFinderRange));
			this.finderPatternFinderRange2 = Float.parseFloat(config.getValue("finderPatternFinderRange2", ""+this.finderPatternFinderRange2));
			this.finderPatternFinderRange3 = Float.parseFloat(config.getValue("finderPatternFinderRange3", ""+this.finderPatternFinderRange3));
			this.aligmentPatternFinderRange = Float.parseFloat(config.getValue("aligmentPatternFinderRange", ""+this.aligmentPatternFinderRange));
			this.aligmentPatternFinderRange2 = Float.parseFloat(config.getValue("aligmentPatternFinderRange2", ""+this.aligmentPatternFinderRange2));
			this.aligmentPatternFinderRange3 = Float.parseFloat(config.getValue("aligmentPatternFinderRange3", ""+this.aligmentPatternFinderRange3));
			this.gausRadius = Integer.parseInt(config.getValue("gausRadius", ""+this.gausRadius));
			this.gausThreshold = Float.parseFloat(config.getValue("gausThreshold", ""+this.gausThreshold));
		}
	}
	
	public Options options;
	
	private int width, height;
	private int[] grayScale;
	private GaussianBlur blur;
//	private EdgeDetector edgeDetector;
//	private boolean[][] edgeMap;
	private BitExtractor bitExtractor;
	private boolean[][] bitmap;
	private FinderPatternFinder finderPatternFinder;
	private List<Vector2F> finderPatterns;
	private AligmentPatternFinder aligmentPatternFinder;
	private List<Vector2F> aligmentPatterns;
	private QRParser parser;
	private List<QRBitmap> codes;
	
	public QRReader(Options options) {
		this.options = options;
	}
	
	public void read(int width, int height, int[] grayScale) {
		this.width = width;
		this.height = height;
		this.grayScale = grayScale;
		
		if(this.options.gausRadius != 0) {
			this.blur = new GaussianBlur(this.options.gausRadius, this.options.gausThreshold);
			this.grayScale = this.blur.blur(this.width, this.height, this.grayScale);
		}
		
//		this.edgeDetector = new EdgeDetector();
//		this.edgeMap = this.edgeDetector.detect(this.width, this.height, this.grayScale);
		
		this.bitExtractor = new BitExtractor();
		this.bitmap = this.bitExtractor.extract(this.width, this.height, this.grayScale);
		
		this.finderPatternFinder = new FinderPatternFinder(this.options.finderPatternFinderRange, this.options.finderPatternFinderRange2, this.options.finderPatternFinderRange3);
		this.finderPatterns = this.finderPatternFinder.findPatterns(this.width, this.height, this.bitmap);
		
		this.aligmentPatternFinder = new AligmentPatternFinder(this.options.aligmentPatternFinderRange, this.options.aligmentPatternFinderRange2, this.options.aligmentPatternFinderRange3);
		this.aligmentPatterns = this.aligmentPatternFinder.findPatterns(this.width, this.height, this.bitmap);
		
		this.parser = new QRParser();
		this.codes = this.parser.parse(this.width, this.height, this.bitmap, this.finderPatterns, this.aligmentPatterns);
	}
	
//	public boolean[][] getEdgeMap() {
//		return this.edgeMap;
//	}
	
	public List<Vector2F> getFinderPatterns() {
		return this.finderPatterns;
	}
	
	public List<Vector2F> getAligmentPatterns() {
		return this.aligmentPatterns;
	}
	
	public List<QRBitmap> getCodes() {
		return this.codes;
	}
	
	public GaussianBlur getBlur() {
		return this.blur;
	}
	
//	public EdgeDetector getEdgeDetector() {
//		return this.edgeDetector;
//	}
	
	public FinderPatternFinder getFinderPatternFinder() {
		return this.finderPatternFinder;
	}
	
	public AligmentPatternFinder getAligmentPatternFinder() {
		return this.aligmentPatternFinder;
	}
	
	public QRParser getQRParser() {
		return this.parser;
	}
	
	public BitExtractor getBitExtractor() {
		return this.bitExtractor;
	}
}
