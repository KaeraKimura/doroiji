package controller;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import entity.Client;
import entity.ClosingDay;
import entity.Invoice;
import model.ClientsManager;
import model.CsvCreater;
import model.UnitPriceCalculator;
import reader.CSVReader;
import view.PrintSelect;
import view.View;
import writer.InvoiceWriter;
import writer.ListWriter;

public class Controller extends MouseAdapter implements ActionListener {

	private View view;
	private PrintSelect printSelect;
	private UnitPriceCalculator calculator;
	private ClientsManager clientsManager;

	private static Controller singleton = new Controller();

	//コンストラクタ
	private Controller() {

	}

	public static Controller getInstance() {
		return singleton;
	}

	public void receiveCsvFiles(List<File> files) {

		CSVReader reader = new CSVReader();
		List<Invoice> invoiceList = new ArrayList<Invoice>();

		//clients.csvに無い業者が含まれるかのフラグ
		boolean flag = true;
		for (File f : files) {
			try {
				List<List<String[]>> csvData = reader.createInvoiceData(f.toPath());
				for (List<String[]> invoiceCsv : csvData) {
					Invoice inv = null;
					inv = new Invoice(invoiceCsv);
					//clients.csvに含まれるかを判断する
					if (this.clientsManager.contains(inv.getBillingNum()) == false) {
						//clients.csvに記載がない業者はmsgに追加
						View.getInstance().addMsg(inv.getBillingNum() + " " + inv.getCmpName());
						flag = false;
					}else {
						invoiceList.add(inv);
					}
				}
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				this.view.addMsg("csvファイル読み込みに失敗しました。");
				this.view.addMsg(e.getMessage());
				this.view.showBufferMsg();
				e.printStackTrace();
				return;
			}
		}
		//viewにInvoicePanelを追加
		view.addInvoicePanel(invoiceList);
		if (flag == false) {
			view.addMsg("<html>client.csvに記載のない業者です。<br>"
					+ "出力するにはcsvへの入力が必要です。。<html>");
		}
		view.showBufferMsg();
	}

	public void startApplication() {

		//業者一覧・スライド・割増の読み込み
		Path clientsCsvPath = Paths.get("data/clients.csv");
		Path productCsvPath = Paths.get("data/productCode.csv");
		Path warimashiCsvPath = Paths.get("data/warimashi.csv");
		
		CSVReader csvReader = new CSVReader();
		try {
			Map<Integer, Integer> products = csvReader.createProductMap(productCsvPath);
			Map<String, Integer> warimashi = csvReader.createWarimashiMap(warimashiCsvPath);
			Map<Integer, Client> clients = csvReader.createClientsMap(clientsCsvPath);
			this.calculator = new UnitPriceCalculator(products, warimashi);
			this.clientsManager = new ClientsManager(clients);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.view = View.getInstance();
	}

	public UnitPriceCalculator getCalculator() {
		return this.calculator;
	}

	public ClientsManager getClientsManager() {
		return this.clientsManager;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "CSV出力":
			List<Invoice> printInvoiceList = this.view.getSelectedInvoiceList();
			this.printDoroijiCsv(printInvoiceList);
			//CSV出力後、道路維持管理費一覧を更新するかを確認。
			if(this.view.showConfirm("道路維持管理費一覧を更新しますか？") == 0) {
				//更新する一覧の年月を取得
				String closingDate = printInvoiceList.get(0).getClosingDateStr();
				String yearStr = closingDate.substring(0,4);
				String monthStr = closingDate.substring(7,9);
				String fileName = yearStr + "." + monthStr + ".csv";
				Map<Integer,int[]> map = this.clientsManager
						.createDoroijiMap(fileName, printInvoiceList);
				try {
					new ListWriter().write(fileName,map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.view.showMessage("一覧を更新しました。");
			}
			
			break;
		case "10":
		case "15":
		case "20":
		case "末":
			ClosingDay closingDay = ClosingDay.getTypeByValue(e.getActionCommand());
			if (this.printSelect != null) {
				this.printSelect.dispose();
			}
			this.printSelect = new PrintSelect(closingDay);
			break;
		case "createRangeDecide":
			//指定された締め日のCsvを作成する
			int createStartNum = this.printSelect.getStartNum();
			int createEndNum = this.printSelect.getEndNum();
			//指定範囲を検証
			if(this.clientsManager.validateSelectedOrder(this.printSelect.clientList,
					createStartNum, createEndNum) == false) {
				View.getInstance().showMessage("終了番号より後の順番の業者は開始番号に指定できません。");
				return ;
			}
			List<Client> cliateCsvList = this.clientsManager.createCsvList(this.printSelect.clientList,
					createStartNum, createEndNum);
			this.createCsv(cliateCsvList);
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		String labelStr = ((JLabel) e.getSource()).getText();

		//ダブルクリックかつソースがClientLabelであれば
		if (e.getClickCount() == 2 && this.printSelect != null
				&& this.printSelect.isClientLabel(e.getSource())) {
			this.printSelect.setBillingNum(e.getSource());
		}

		switch (labelStr) {
		case "出力":
			if (e.getClickCount() == 2) {
				this.view.allPrintCheck(true);
			} else {
				this.view.allPrintCheck(false);
			}
			break;
		}
	}

	//
	private void printDoroijiCsv(List<Invoice> printInvoiceList) {
		
		if (printInvoiceList.size() == 0) {
			this.view.showMessage("出力する内容がないです。");
			return;
		}
		InvoiceWriter writer = null;
		try {

			writer = new InvoiceWriter(CSVReader.getCurrentInvoiceCsvName());
			writer.print(printInvoiceList);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		if (writer != null) {
			writer.close();
		}
		this.view.addMsg("CSV出力が終わりました。");
		this.view.showBufferMsg();
	}

	//CSVを自動作成
	private void createCsv(List<Client> clientList) {

		//注意
		this.view.addMsg("個別発行を画面左上にピッタリ配置して、");
		this.view.addMsg("新規ボタンをクリックした状態でOKボタンを押してください。");
		this.view.addMsg("自動操作中はマウス・キーボードは触れません。");
		this.view.addMsg("中断するときは少しの間マウスを動かしてください。");
		this.view.showBufferMsg();

		//要素ClientのbillingNumで繰り返し出力作業をする。
		try {
			CsvCreater csvCreater = new CsvCreater(
					this.clientsManager.getLatestClosingDate(this.printSelect.closingDay));
			for (Client c : clientList) {
				csvCreater.print(c.getBillingNum());
			}
			this.view.showMessage("CSV作成完了！");
			this.printSelect.dispose();
		} catch (AWTException e) {
			this.view.showMessage(e.getMessage());
		} catch (CsvCreater.UnspecifidePositionException e) {
			//CsvCreaterでマウスのクリックまえに指定座標から動いていると例外を投げる
			this.view.addMsg("自動操作を終了します。");
			//どこまで出力できているかを表示させる。
			this.view.addMsg(e.getCompletedBillingNum() + " まで完了しています。");
			this.view.showBufferMsg();
		}
	}

	//	@Override
	//	public void stateChanged(ChangeEvent e) {
	//		// TODO 自動生成されたメソッド・スタブ
	//		boolean state = ((JCheckBox) e.getSource()).isSelected();
	//		InvoicePanel = e.getSource().
	//	}
}
