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
import view.View;
import writer.InvoiceWriter;

public class Controller extends MouseAdapter implements ActionListener {

	private View view;
	private UnitPriceCalculator calculator;
	private ClientsManager clientsManager;
	private CsvCreater csvCreater;
	
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

		//JVなどclients.csvに無い業者のInvoicePanelのisPrintをfalseにするためのList
		List<Integer> notPrintList = new ArrayList<Integer>();
		for (File f : files) {
			try {
				List<List<String[]>> csvData = reader.createInvoiceData(f.toPath());
				for (List<String[]> invoiceCsv : csvData) {
					Invoice inv = null;
					inv = new Invoice(invoiceCsv);

					invoiceList.add(inv);
					inv.setIsSeparate(this.clientsManager.isSeparate(inv.getBillingNum()));

					//道路維持管理の対象か=clients.csvに含まれるかを判断する
					if (this.clientsManager.isTarget(inv.getBillingNum()) == false) {
						//対象外＝clients.csvに記載がない業者はmsgに追加
						View.getInstance().addMsg(inv.getBillingNum() + " " + inv.getCmpName());
						notPrintList.add(inv.getBillingNum());
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
		view.addInvoicePanel(invoiceList);
		if (notPrintList.size() > 0) {
			view.addMsg("の出力方法を設定してください。");
			view.setNotPrint(notPrintList);
		}
		view.showBufferMsg();
	}

	public void startApplication() {

		//スライド・割増の読み込み
		Path productCsvPath = Paths.get("data/productCode.csv");
		Path warimashiCsvPath = Paths.get("data/warimashi.csv");
		Path clientsCsvPath = Paths.get("data/clients.csv");
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
			this.printDoroijiCsv();
			break;
		case "10":
			this.createCsv(ClosingDay.D_10);
			break;
		case "15":
			this.createCsv(ClosingDay.D_15);
			break;
		case "20":
			this.createCsv(ClosingDay.D_20);
			break;
		case "末":
			this.createCsv(ClosingDay.D_LAST);
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		String labelStr = ((JLabel) e.getSource()).getText();

		switch (labelStr) {
		case "出力":
			if (e.getClickCount() == 2) {
				this.view.allPrintCheck(true);
			} else {
				this.view.allPrintCheck(false);
			}
			break;
		case "一括":
			this.view.setAllSeparatePrint(false);
			break;
		case "別":
			this.view.setAllSeparatePrint(true);
			break;
		}
	}

	//
	private void printDoroijiCsv() {
		List<Invoice> printInvoiceList = this.view.getSelectedInvoiceList();
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
		this.view.addMsg("出力が終わりました。");
		this.view.showBufferMsg();
	}

	//CSVを自動作成
	private void createCsv(ClosingDay closingDate) {

		//確認
		if (this.view.showInputDialog(closingDate.toString() + "日締の請求書でOK？") != 0) {
			return;
		}
		
		//注意
		this.view.addMsg("個別発行を画面左上にピッタリ配置して、");
		this.view.addMsg("新規ボタンをクリックした状態でOKボタンを押してください。");
		this.view.addMsg("自動操作中はマウス・キーボードは触れません。");
		this.view.addMsg("中断するときは少しの間マウスを動かしてください。");
		this.view.showBufferMsg();

		List<Client> clientList = this.clientsManager.narrowDownByClosingDate(closingDate);

		//要素ClientのbillingNumで繰り返し出力作業をする。
		try {
			csvCreater = new CsvCreater(this.clientsManager.getLatestClosingDate(closingDate));
			for (Client c : clientList) {
				csvCreater.print(c.getBillingNum());
			}
			this.view.showMessage("CSV作成完了！");
		} catch (AWTException e) {
			this.view.showMessage(e.getMessage());
		}catch(CsvCreater.UnspecifidePositionException e) {
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
