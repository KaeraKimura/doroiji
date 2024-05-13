import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import controller.Controller;
import entity.Invoice;
import reader.CSVReader;
import writer.InvoiceWriter;

public class Main {

	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Controller.getInstance().startApplication();
			}
		});
	}
	
	private static void test() {
		
	}
	
	private static void problem() {
		Path path = Paths.get("20240331.csv");
		CSVReader r = new CSVReader();
		List<Invoice> invoiceList = new ArrayList<>();
		InvoiceWriter w = null;
		try {
			List<List<String[]>> list = r.createInvoiceData(path);
			List<String[]> invoiceCSV = list.get(0);
			Invoice inv = new Invoice(invoiceCSV);
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			
		}
		System.out.println("finish!");
	}

	
	private static void separateTest() {
		Path path = Paths.get("20240331.csv");
		CSVReader r = new CSVReader();
		List<Invoice> invoiceList = new ArrayList<>();
		InvoiceWriter w = null;
		try {
			for (List<String[]> list : r.createInvoiceData(path)) {
				invoiceList.add(new Invoice(list));
			}
			w = new InvoiceWriter(Paths.get("").toAbsolutePath().toString());
			for (Invoice inv : invoiceList) {
				inv.addDoroiji();
				w.consSeparatePrint(inv);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			w.close();
		}
		System.out.println("finish!");
	}
	
	private static void viewTest() {
		Path path = Paths.get("20240331.csv");
		Controller c = Controller.getInstance();
		c.startApplication();
		List<File> files = new ArrayList<>();
		files.add(path.toFile());
		c.receiveCsvFiles(files);

	}

	private static void createCsv() {

		Path path = Paths.get("20240331.csv");
		CSVReader r = new CSVReader();
		List<Invoice> invoiceList = new ArrayList<>();
		InvoiceWriter w = null;
		try {
			for (List<String[]> list : r.createInvoiceData(path)) {
				invoiceList.add(new Invoice(list));
			}
			w = new InvoiceWriter(Paths.get("").toAbsolutePath().toString());
			for (Invoice inv : invoiceList) {
				inv.addDoroiji();
				w.print(inv);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			w.close();
		}
		System.out.println("finish!");
	}
}
