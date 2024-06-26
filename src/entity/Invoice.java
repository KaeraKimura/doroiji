package entity;

import java.util.ArrayList;
import java.util.List;

import controller.Controller;
import model.ClientsManager;
import model.UnitPriceCalculator;

public class Invoice {

	//郵便番号
	private String postCode;
	//住所
	private String address;
	//業者名
	private String cmpName;
	//請求先C
	private int billingNum;
	//〆日
	private String closingDate;
	//前回請求額
	private int lastBilling;
	//今回入金額
	private int peyment;
	//繰越額
	private int caryyOver;
	//振込金額
	private String transfer;
	//相殺
	private String offset;
	//売上のList<Map>
	private List<SaleContent> salesRow;
	//総量
	private double totalVol;
	//請求月
	private int billingMonth;
	//道路維持管理費
	private int doroijiValue;

	public int getPageCount() {
		return (int) Math.ceil((double) this.salesRow.size() / 40);
	}

	//道路維持管理費を適用前に戻す
	public void resetDoroizi() {
		for (SaleContent sale : this.salesRow) {
			if (sale.isDoroiji() == true) {
				sale.subtractDoroiji(this.doroijiValue);
			}
		}
	}

	//道路維持管理をプラスする
	public void addDoroiji() {

		for (SaleContent sale : this.salesRow) {
			if (sale.isConcrete()) {
				sale.addDoroiji(this.doroijiValue);
			}
		}
	}

	public int getBillingMonth() {
		return billingMonth;
	}

	public int getDoroijiTotal() {
		int result = 0;
		for (SaleContent sale : this.salesRow) {
			if (sale.isDoroiji() == true) {
				result += Double.parseDouble(sale.getVol()) * this.doroijiValue;
			}
		}
		return result;
	}

	//コンストラクタ
	public Invoice(List<String[]> list) {

		//業者情報
		String[] header = list.get(0);
		this.postCode = header[0];
		this.address = header[1];
		this.cmpName = header[4];
		this.billingNum = Integer.parseInt(header[5]);
		this.closingDate = header[7];
		this.billingMonth = Integer.parseInt(this.closingDate.substring(7, 9));

		String[] amounts = list.get(42);
		this.lastBilling = Integer.parseInt(amounts[0]);
		this.peyment = Integer.parseInt(amounts[1]);
		this.caryyOver = Integer.parseInt(amounts[2]);
		this.offset = amounts[7];
		this.transfer = amounts[8];

		//売上行を生成 請求書を表すクラスにすべき！！Z
		this.salesRow = new ArrayList<SaleContent>();
		for (int i = 1; i < list.size(); i++) {
			//スラッシュを含む＝日付なので売上行
			if (list.get(i)[0].contains("/")) {
				SaleContent row = new SaleContent(list.get(i));
				this.salesRow.add(row);
			}
		}

		//空積料の現場コードを入力
		for (int i = 0; i < this.salesRow.size(); i++) {
			SaleContent sale = this.salesRow.get(i);
			if (sale.getGenbaCode() == 0) {
				sale.setGenbaCode(this.salesRow.get(i - 1).getGenbaCode());
			}
		}

		//合計数量
		String volStr = list.get(list.size() - 2)[7];
		if (volStr.equals("")) {
			this.totalVol = 0.0;
		} else {
			this.totalVol = Double.parseDouble(volStr);
		}

		//道路維持管理費
		ClientsManager clientsManager = Controller.getInstance().getClientsManager();
		this.doroijiValue = clientsManager.getDoroijiValue(this.billingNum);

		//doroijiValue = 0 のときはclients.csvに記載のない業者なので売上の単価から道路維持管理費単価を計算
		if (this.doroijiValue == 0) {
			UnitPriceCalculator calculator = Controller.getInstance().getCalculator();
			int baseValue = 0;
			//売上の中から生コン・モルタルを見つけ、ベース単価を計算
			for (SaleContent sale : this.salesRow) {
				if (sale.isConcrete() == true) {
					baseValue = calculator.getBaseValue(sale, this.billingNum);
					break;
				}
			}
			//計算したベース単価の下3桁から維持管理費を算定
			String s = String.valueOf(baseValue).substring(2, 5);
			if (s.equals("000") || s.equals("800")) {
				this.doroijiValue = 1000;
			} else {
				this.doroijiValue = 1500;
			}
		}
	}

	public String getPostCode() {
		return postCode;
	}

	public String getAddress() {
		return address;
	}

	public String getCmpName() {
		return cmpName;
	}

	public String getShapeCmpName() {
		String result;
		//株式会社を㈱に
		result = this.cmpName.replace("株式会社", "㈱");
		//有限会社を㈲に
		result = result.replace("有限会社", "㈲");
		//合同会社を(同)に
		result = result.replace("合同会社", "(同)");
		//合資会社を㈾に
		result = result.replace("合資会社", "㈾");
		//建設工事共同企業体をJVに
		result = result.replace("建設工事共同企業体", "JV");
		//スペースを除去
		result = result.replace("　", "");
		result = result.replace(" ", "");

		return result;

	}

	public int getBillingNum() {
		return billingNum;
	}

	public String getClosingDate() {
		return closingDate;
	}

	public int getLastBilling() {
		return lastBilling;
	}

	public int getPeyment() {
		return peyment;
	}

	public int getCaryyOver() {
		return caryyOver;
	}

	public int getConcreteSales() {
		int sales = 0;
		for (SaleContent s : this.salesRow) {
			sales += s.getTotal();
		}
		return sales;
	}

	public int getTax() {
		int tax = (int) ((this.getConcreteSales() - this.getDoroijiTotal()) * 0.1);
		return tax;
	}

	public int getBilling() {
		int concreteSales = this.getConcreteSales();
		int doroijiSales = this.getDoroijiTotal();
		int tax = (int) ((concreteSales - doroijiSales) * 0.1);
		return concreteSales - doroijiSales + tax;
	}

	public int getTotalBilling() {
		return this.caryyOver + this.getBilling();
	}

	public List<SaleContent> getSalesRow() {
		return salesRow;
	}

	public double getTotalVol() {
		return totalVol;
	}

	public String getTransfer() {
		return transfer;
	}

	public String getOffset() {
		return offset;
	}

	public int getDoroijiValue() {
		return this.doroijiValue;
	}
}
