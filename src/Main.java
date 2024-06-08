import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import controller.Controller;
import entity.ClosingDay;
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

	private static LocalDate test(ClosingDay closingDate) {
		if (closingDate != ClosingDay.D_LAST) {
			int closingDayNum = Integer.parseInt(closingDate.toString());
			//現在から遡って最短で日数が一致する日を調べる
			long currentEpoch = LocalDate.now().toEpochDay();
			for (long c = currentEpoch;; c--) {
				if (LocalDate.ofEpochDay(c).getDayOfMonth() == closingDayNum) {
					return LocalDate.ofEpochDay(c);
				}
			}
		} else {
			//末の場合
			//現在＋１日から遡って月数が変わる日付を調べる
			long tommorowEpoch = LocalDate.now().toEpochDay() + 1;
			int targetMonth = LocalDate.ofEpochDay(tommorowEpoch).getMonthValue();
			for (long c = tommorowEpoch;; c--) {
				if (targetMonth != LocalDate.ofEpochDay(c).getMonthValue()) {
					return LocalDate.ofEpochDay(c);
				}
			}
		}
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
