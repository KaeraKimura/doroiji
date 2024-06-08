package writer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import entity.Invoice;
import entity.SaleContent;
import model.ClientsManager;
import reader.CSVReader;

public class InvoiceWriter {
	private PrintWriter pw;

	public void print(List<Invoice> invoiceList) {
		
		for (int i = 0; i < invoiceList.size(); i++) {
			Invoice inv = invoiceList.get(i);
//			//売上がないなら出力しない
//			if(inv.getSalesRow().size() == 0) {
//				continue;
//			}
			inv.addDoroiji();
			if (inv.getIsSeparate() == true) {
				this.consSeparatePrint(inv);
			} else {
				this.print(inv);
			}
			inv.resetDoroizi();
		}
	}

	public void print(Invoice invoice) {
		List<SaleContent> sales = invoice.getSalesRow();
		int pageNum = 0;
		//売上出力用のカウンタ
		int salesPrintCnt = 0;
		//請求書のページ数　×　43行出力する
		for (int i = 1; i <= invoice.getPageCount() * 43; i++) {
			//43で割った余りが1ならヘッダーを、43行目なら金額欄を出力
			if (i % 43 == 1) {
				pageNum++;
				this.printHeader(invoice, pageNum);
			} else if (i % 43 == 0) {
				if (i == 43) {
					this.printAmounts(invoice);
				} else {
					this.printEmptyRow();
				}
				//43で割った余りが42(=空白行)
			} else if (i % 43 == 42) {
				//売上の出力が終わっていない場合は改行
				if (i <= sales.size()) {
					this.printEmptyRow();
				} else {
					//売上出力後であれば数量と金額を出力
					this.printTotalRow(invoice.getTotalVol(), invoice.getConcreteSales(), false);
				}
			} else {
				if (salesPrintCnt < sales.size()) {
					this.printSale(sales.get(salesPrintCnt));
				} else {
					this.printEmptyRow();
				}
				salesPrintCnt++;
			}
		}
		
		//道路維持管理費の合計が０なら道路維持の請求書を出さない。
		int doroijiTotal = invoice.getDoroijiTotal();
		if(doroijiTotal != 0) {
			this.printHeader(invoice, ++pageNum);
			this.printDoroiji(doroijiTotal, invoice.getBillingMonth());
		}

	}

	public void consSeparatePrint(Invoice invoice) {
		//現場Cとその売上行をMap形式にまとめる
		List<SaleContent> sales = invoice.getSalesRow();
		Map<Integer, List<SaleContent>> consMap = new HashMap<>();
		for (int i = 0; i < sales.size(); i++) {
			SaleContent sale = sales.get(i);
			if (consMap.containsKey(sale.getGenbaCode())) {
				consMap.get(sale.getGenbaCode()).add(sale);
			} else {
				List<SaleContent> list = new ArrayList<>();
				list.add(sale);
				consMap.put(sale.getGenbaCode(), list);
			}
		}
		
		ClientsManager cm = Controller.getInstance().getClientsManager();
		//マップの要素が１つ、かつ専用請求でない場合は一括で印刷する
		if(consMap.size() == 1 && cm.isOnly(invoice.getBillingNum()) == false){
			this.print(invoice);
			return;
		}

		//マップの要素ごと(=現場ごと)に請求書CSVを出力する
		int printPageCount = 0;
		for (Integer key : consMap.keySet()) {

			sales = consMap.get(key);
			//ページ用のカウンタ
			int pageCount = (int) Math.ceil(sales.size() / 40.0);
			//売上行出力のカウンタ
			int salesPrintCount = 0;
			//出力する工事名
			String consName = "";
			//事前に数量と金額を取得
			double totalVol = this.calcConsVol(sales);
			int concreteSales = this.calcConsConcreteSales(sales);
			//道路維持管理費の計算
			int doroijiSales = this.calcConsDoroijiSales(sales, invoice.getDoroijiValue());
			for (int i = 1; i <= pageCount; i++) {
				printPageCount++;
				//ヘッダー
				this.printHeader(invoice, printPageCount);
				//売上行
				for (int ii = 0; ii < 41; ii++) {
					//工事名の出力
					if (salesPrintCount == 0 && ii == 0) {
						//最も長い工事名を調べる
						for(int c = 0; c < sales.size(); c++) {
							if(consName.length() < sales.get(c).getConsName().length()) {
								consName = sales.get(c).getConsName();
							}
						}
						this.printConsName(consName);
					}else if (salesPrintCount <= sales.size()) {
						this.printSale(sales.get(salesPrintCount - 1));
					}else if(salesPrintCount == sales.size() + 1) {
						//合計行の出力
						this.printTotalRow(totalVol, concreteSales, true);
					}else {
						this.printEmptyRow();
					}
					salesPrintCount++;
				}
				//金額出力
				if (pageCount == 1) {
					int totalSales = concreteSales - doroijiSales;
					int tax = (int) (totalSales * 0.1);
					int billing = totalSales + tax;
					this.pw.print(",,," + totalSales + "," + tax + ","
							+ billing + ",,,,,,\r\n");
				} else {
					this.printEmptyRow();
				}
			}
			
			//道路維持管理
			if(doroijiSales != 0) {
				printPageCount++;
				this.printHeader(invoice, printPageCount);
				this.printDoroiji(doroijiSales, invoice.getBillingMonth(),consName);
			}
		}
	}
		

	public void printConsName(String consName) {
		
		//半角カッコを付け加える
		consName = "【" + consName + "】";
		
		double strCnt = 0.0; //出力文字数用のカウンタ
		//各欄に出力する文字列
		StringBuilder consColStr = new StringBuilder();
		StringBuilder productColStr = new StringBuilder();
		StringBuilder volColStr = new StringBuilder();
		StringBuilder valueColStr = new StringBuilder();
		//文字ごとの配列を作る
		char[] arr = consName.toCharArray();
		String[] consStrArr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			consStrArr[i] = String.valueOf(arr[i]);
		}
		
		for (int i = 0; i < consStrArr.length; i++) {
			//バイトの長さが2を超える＝全角なのでカウンタを1.0増やす
			if (consStrArr[i].matches("^[ｦ-ﾟ]+$")) {//半角カタカナは３バイトになるので事前に判定しておく
				strCnt += 0.5;
			}else if(consStrArr[i].getBytes().length > 2) {
				strCnt += 1.0;
			}else {
				strCnt += 0.5; //そうでない場合は半角なので０.5増やす
			}
			//カウンタからどの欄に出力するかを判断
			if (strCnt <= 17) {
				consColStr.append(consStrArr[i]);
			}else if (strCnt <= 17 + 9.5) {
				productColStr.append(consStrArr[i]);
			}else if(strCnt <= 17 + 9.5 + 4.5) {
				volColStr.append(consStrArr[i]);
			}else if(strCnt <= 17 + 9.5 + 4.5 + 4.5) {
				valueColStr.append(consStrArr[i]);
			}
		}
		
		//右詰めになる欄は最大文字数に満たない場合アンダーバーで埋める
		if(volColStr.isEmpty() == false) {
			int spaceCnt = (int)((17.0 + 9.5 + 4.5 - strCnt) * 2);//半角アンダーバーを出力する回数
			for(int i = 0; i < spaceCnt; i++) {
				volColStr.append("_");
			}
		}
		if(valueColStr.isEmpty() == false) {
			int underBarCnt = (int)((17.0 + 9.5 + 4.5 + 4.5- strCnt) * 2);//半角スペースを出力する回数
			for(int i = 0; i < underBarCnt; i++) {
				valueColStr.append("_");
			}
		}
		this.pw.print(",," + consColStr + ",,,," + productColStr + "," + volColStr + "," + valueColStr + ",,,\r\n");
	}

	//現場別の数量を合計する
	private double calcConsVol(List<SaleContent> sales) {
		double result = 0.0;
		for (SaleContent sale : sales) {
			if (sale.isConcrete()) {
				result += Double.parseDouble(sale.getVol());
			}
		}
		
		result += 0.00;
		return result;
	}

	//現場別の金額を計算する
	private int calcConsConcreteSales(List<SaleContent> sales) {
		int result = 0;
		for (SaleContent sale : sales) {
			result += sale.getTotal();
		}
		return result;
	}

	//現場別の道路維持管理費を計算
	private int calcConsDoroijiSales(List<SaleContent> sales, int doroijiValue) {
		int result = 0;
		for (SaleContent sale : sales) {
			if (sale.isDoroiji()) {
				result += (int) (Double.parseDouble(sale.getVol()) * doroijiValue);
			}
		}

		return result;
	}

	private void printTotalRow(double totalVol, int concreteSales, boolean isSeparate) {
		String vol = String.valueOf(totalVol);
		//小数点第2位に０追加
		if(vol.length() - vol.indexOf(".") == 2) {
			vol = vol + "0";
		}
		String volTitle;
		if(isSeparate == true) {
			volTitle = "現場計";
		}else {
			volTitle = "合　計";
		}
		this.pw.print(",,,,,,【" + volTitle + "】," + vol + ",,"
				+ concreteSales + ",\r\n");
	}
	
	

	private void printHeader(Invoice invoice, int pageNum) {
		//郵便番号
		this.pw.print(invoice.getPostCode() + ",");
		//住所
		this.pw.print(invoice.getAddress() + ",");
		this.pw.print(",");
		this.pw.print(",");
		//社名
		this.pw.print(invoice.getCmpName() + ",");
		//請求先C
		this.pw.print(invoice.getBillingNum() + ",");
		//ページNo
		this.pw.print(pageNum + ",");
		//〆日
		this.pw.print(invoice.getClosingDate() + ",\r\n");
	}

	private void printAmounts(Invoice invoice) {
		//前回請求額
		this.pw.print(invoice.getLastBilling() + ",");
		//今回入金額
		this.pw.print(invoice.getPeyment() + ",");
		//繰越額
		this.pw.print(invoice.getCaryyOver() + ",");
		//今回売上
		this.pw.print(invoice.getConcreteSales() - invoice.getDoroijiTotal() + ",");
		//消費税
		this.pw.print(invoice.getTax() + ",");
		//今回請求額
		this.pw.print(invoice.getBilling() + ",");
		//合計請求額
		this.pw.print(invoice.getTotalBilling() + ",");
		//相殺
		this.pw.print(invoice.getOffset() + ",");
		//振込
		this.pw.print(invoice.getTransfer() + "\r\n");
	}

	private void printSale(SaleContent sale) {
		//日付
		this.pw.print(sale.getDate() + ",");
		//現場C
		this.pw.print(sale.getGenbaCode() + ",");
		//工事名
		this.pw.print(sale.getConsName() + ",");
		//請求先C
		this.pw.print(sale.getRecipientNum() + ",");
		this.pw.print(",");
		//製品C
		this.pw.print(sale.getProductCode() + ",");
		//製品名
		this.pw.print(sale.getProductName() + ",");
		//数量
		this.pw.print(sale.getVol() + ",");
		//単価
		this.pw.print(sale.getUnit() + ",");
		//金額
		this.pw.print(sale.getTotal() + ",");
		//備考
		String comment = "";
		for (String el : sale.getWarimashi()) {
			comment += " " + el;
		}
		this.pw.print(comment + ",\r\n");
	}

	private void printDoroiji(int doroijiSales, int billingMonth) {
		this.pw.print(",,,,,,道路維持管理費,,," + doroijiSales + "," + billingMonth + "月分,\r\n");
		this.pw.print(",,,,,,【現場計】,,," + doroijiSales + ",,\r\n");
		for (int i = 1; i <= 39; i++) {
			this.printEmptyRow();
		}
		this.printEmptyRow();
	}
	
	//現場別出力用にオーバーライド
	private void printDoroiji(int doroijiSales, int billingMonth, String consName) {
		this.printConsName(consName);
		this.pw.print(",,,,,,道路維持管理費,,," + doroijiSales + "," + billingMonth + "月分,\r\n");
		this.pw.print(",,,,,,【現場計】,,," + doroijiSales + ",,\r\n");
		for (int i = 1; i <= 38; i++) {
			this.printEmptyRow();
		}
		this.printEmptyRow();
	}

	public void close() {
		this.pw.close();
	}
	
	private void printEmptyRow() {
		this.pw.print(",,,,,,,,,,,\r\n");
	}

	//コンストラクタ
	public InvoiceWriter(String readCsvName) throws IOException {
		//日付情報を読み取る
		int year = Integer.parseInt(readCsvName.substring(0, 4));
		int month = Integer.parseInt(readCsvName.substring(4, 6));
		int day = Integer.parseInt(readCsvName.substring(6, 8));
		LocalDate ldt = LocalDate.of(year, month, day);

		//読み取った日付の前日を取得
		ldt = LocalDate.ofEpochDay(ldt.toEpochDay() - 1);
		year = ldt.getYear();
		month = ldt.getMonthValue();
		day = ldt.getDayOfMonth();

		//ファイル名用に文字列を整える
		String yearStr = String.valueOf(year);
		String monthStr = String.valueOf(month).format("%02d", month);
		String dayStr = String.valueOf(day).format("%02d", day);

		String fileName = "\\" + yearStr + monthStr + dayStr + ".csv";
		String pathStr = CSVReader.getCurrentInvoiceCsvPath().toString() + fileName;
		this.pw = new PrintWriter(pathStr, Charset.forName("MS932"));

	}
}
