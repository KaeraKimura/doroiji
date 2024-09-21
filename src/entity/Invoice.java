package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controller.Controller;
import model.ClientsManager;

public class Invoice {

	//郵便番号
	private String postCode;
	//住所
	private String address;
	//業者名
	private String cmpName;
	//請求先C
	private int billingNum;

	private ClosingDay closingDay;
	//〆日 日付情報
	private LocalDate closingDate;
	//〆日文字列
	private String closingDateStr;
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
	//売上金額
	private final int initialSale;
	//売上のList<Map>
	private List<SaleContent> salesRow;
	//総量
	private BigDecimal totalVol;
	//請求月
	private int billingMonth;
	//道路維持管理費
	private int doroijiValue;

	public int getPageCount() {
		return (int) Math.ceil((double) this.salesRow.size() / 40);
	}

	//道路維持管理費を適用前に戻す
	public void resetDoroiji() {
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
				result = result - new BigDecimal(sale.getVol()).multiply(new BigDecimal(this.doroijiValue)).intValue();
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

		this.closingDateStr = header[7];
		//日付文字列をLocalDateに変換
		String dateStr = this.closingDateStr;
		int year = Integer.parseInt(dateStr.substring(0, 4));
		int month = Integer.parseInt(dateStr.substring(7, 9));
		int day = Integer.parseInt(dateStr.substring(12, 14));
		this.closingDate = LocalDate.of(year, month, day);

		if (day > 25) {
			this.closingDay = ClosingDay.D_LAST;
		} else {
			this.closingDay = ClosingDay.getTypeByValue(String.valueOf(day));
		}
		this.billingMonth = Integer.parseInt(this.closingDateStr.substring(7, 9));

		String[] amounts = list.get(42);
		this.lastBilling = Integer.parseInt(amounts[0]);
		this.peyment = Integer.parseInt(amounts[1]);
		this.caryyOver = Integer.parseInt(amounts[2]);
		this.initialSale = Integer.parseInt(amounts[3]);
		this.offset = amounts[7];
		this.transfer = amounts[8];

		//売上行を生成 請求書を表すクラスにすべき！！Z
		this.salesRow = new ArrayList<SaleContent>();
		for (int i = 1; i < list.size(); i++) {
			//スラッシュを含む＝日付なので売上行
			if (list.get(i).length != 0 && list.get(i)[0].contains("/")) {
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
			this.totalVol = new BigDecimal("0.0");
		} else {
			this.totalVol = new BigDecimal(volStr);
		}

		//道路維持管理費
		ClientsManager clientsManager = Controller.getInstance().getClientsManager();
		this.doroijiValue = clientsManager.getDoroijiValue(this.billingNum);
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
		result = result.replace("合資会社", "(資)");
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

	public ClosingDay getClosingDay() {
		return this.closingDay;
	}

	public LocalDate getClosingDate() {
		return this.closingDate;
	}

	public String getClosingDateStr() {
		return closingDateStr;
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

	public int getInitialSale() {
		return this.initialSale;
	}

	public int getConcreteSales() {
		int sales = 0;
		for (SaleContent s : this.salesRow) {
			sales += s.getTotal();
		}
		return sales;
	}

	public int getTax() {
		int tax = (int) ((this.getConcreteSales() + this.getDoroijiTotal()) * 0.1);
		return tax;
	}

	public int getBilling() {
		int concreteSales = this.getConcreteSales();
		int doroijiSales = this.getDoroijiTotal();
		int tax = (int) ((concreteSales + doroijiSales) * 0.1);
		return concreteSales + doroijiSales + tax;
	}

	public int getTotalBilling() {
		return this.caryyOver + this.getBilling();
	}

	public List<SaleContent> getSalesRow() {
		return salesRow;
	}

	public BigDecimal getTotalVol() {
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
