package entity;

public class Client {

	private int billingNum;
	private String name;
	ClosingDay closingDate;
	private int baseValue;
	private int doroijiValue;
	private boolean isSeparate;
	private boolean isOnly;

	//コンストラクタ
	public Client(int billingNum, String name, String closingDateStr, int baseValue, int isSeparateNum, int isOnlyNum) {
		this.billingNum = billingNum;
		this.name = name;
		this.baseValue = baseValue;
		this.closingDate = ClosingDay.getTypeByValue(closingDateStr);

		//20500か20300なら地区内なので1500。それいがいは地区外なので1000
		if (baseValue == 20500 || baseValue == 20300) {
			this.doroijiValue = 1500;
		} else {
			this.doroijiValue = 1000;
		}

		//separateNumが１なら現場別なのでtrue
		if (isSeparateNum == 1) {
			this.isSeparate = true;
		} else {
			this.isSeparate = false;
		}
		//isOnlyNumが１なら専用請求書なのでtrue
		if (isOnlyNum == 1) {
			this.isOnly = true;
		} else {
			this.isOnly = false;
		}
	}

	public ClosingDay getClosingDate() {
		return this.closingDate;
	}

	public int getDoroijiValue() {
		return this.doroijiValue;
	}

	public int getBillingNum() {
		return billingNum;
	}

	public String getName() {
		return name;
	}

	public int getBaseValue() {
		return baseValue;
	}

	public boolean isSeparate() {
		return isSeparate;
	}

	public boolean isOnly() {
		return this.isOnly;
	}
}