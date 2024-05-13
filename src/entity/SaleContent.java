package entity;

public class SaleContent {

	private String date;
	private int genbaCode;
	private String consName;
	private int recipientNum;
	private int productCode;
	private String productName;
	private String vol;
	private int unit;
	private int total;
	private String[] warimashi;
	private boolean isConcrete;
	private boolean isDoroiji;

	//コンストラクタ
	public SaleContent(String[] sale) {

		this.date = sale[0];

		//現場Cが空白（空積など）の場合は０に置き換え
		try {
			this.genbaCode = Integer.parseInt(sale[1]);
		} catch (NumberFormatException e) {
			this.genbaCode = 0;
		}

		this.consName = sale[2];
		this.recipientNum = Integer.parseInt(sale[3]);
		try {
			this.productCode = Integer.parseInt(sale[5]);
		} catch (NumberFormatException e) {
			this.productCode = 0;
		}

		this.productName = sale[6];
		this.vol = sale[7];
		this.unit = Integer.parseInt(sale[8]);
		this.total = Integer.parseInt(sale[9]);

		this.warimashi = sale[10].split(" ");

		//商品区分コードが０か１なら生コン・モルタル
		if(sale[11].equals("00") || sale[11].equals("01")) {
			this.isConcrete = true;
		}else {
			this.isConcrete = false;
		}
		this.isDoroiji = false;
	}

	public boolean isConcrete() {
		return this.isConcrete;
	}

	void subtractDoroiji(int doroijiUnit) {
		this.unit -= doroijiUnit;
		this.isDoroiji = false;
	}

	void addDoroiji(int doroijiUnit) {
		this.unit += doroijiUnit;
		this.isDoroiji = true;
	}

	void setGenbaCode(int genbaCode) {
		this.genbaCode = genbaCode;
	}

	public String getDate() {
		return date;
	}

	public int getGenbaCode() {
		return genbaCode;
	}

	public String getConsName() {
		return consName;
	}

	public int getRecipientNum() {
		return recipientNum;
	}

	public int getProductCode() {
		return productCode;
	}

	public String getProductName() {
		return productName;
	}

	public String getVol() {
		return vol;
	}

	public int getUnit() {
		return unit;
	}

	public int getTotal() {
		double vol = 0;
		try {
			vol = Double.parseDouble(this.vol);
			this.total = (int) (this.unit * vol);
		} catch (NumberFormatException e) {
			return this.total;
		}
		return this.total;
	}

	public String[] getWarimashi() {
		return this.warimashi;
	}

	public boolean isDoroiji() {
		return this.isDoroiji;
	}
}
