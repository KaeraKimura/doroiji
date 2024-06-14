package entity;

public class Client {

	//請求方法の定数
	public static final int DEDICATED_BILLING = 0;
	public static final int TOTALSHEET_BILLING = 1;
	public static final int SEPARATE_BILLING = 2;
	public static final int NOMAL_BILLING = 3;

	public int billingNum;
	public String name;
	ClosingDay closingDate;
	public int baseValue;
	public int doroijiValue;
	public int billingMethod;

	//コンストラクタ
	public Client(int billingNum, String name, String closingDateStr, int baseValue, int billingMethod) {
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

		this.billingMethod = billingMethod;
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

	public int getBillingMethod() {
		return this.billingMethod;
	}
}