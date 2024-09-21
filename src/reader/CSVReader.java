package reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import entity.Client;
import model.ClientsManager;

public class CSVReader {

	//読み込んだCSVファイル名
	private static String currentInvoiceCsvName;
	//読み込まれたCSVファイルパス
	private static Path currentInvoiceCsvPath;

	//コンストラクタ
	public CSVReader() {
	}

	//1件ごとの請求書データをList<String[]>にし、それらをまとめてListにして返す
	public List<List<String[]>> createInvoiceData(Path csvPath)
			throws IOException {

		String pathStr = csvPath.toString();
		currentInvoiceCsvPath = Paths.get(pathStr.substring(0, pathStr.length() - 12));
		//ファイル名の取得
		int startIndex = pathStr.length() - 12;
		currentInvoiceCsvName = pathStr.substring(startIndex, pathStr.length());

		List<String> lines;
		lines = Files.readAllLines(csvPath, Charset.forName("MS932"));

		List<List<String[]>> result = new ArrayList<List<String[]>>();
		List<String[]> data = null;
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i).split(",");
			System.out.println(i + " " + line.length);
			for (int ii = 0; ii < line.length; ii++) {
				line[ii] = line[ii].replaceAll("\"", "");
			}

			if (line.length > 6 && line[6].equals("1")) {
				result.add(data);
				data = new ArrayList<String[]>();
			}
			data.add(line);
		}
		result.add(data);
		result.remove(0);
		return result;
	}

	//配合CとスライドをMapにする
	public Map<Integer, Integer> createProductMap(Path csvPath)
			throws IOException {

		Map<Integer, Integer> result = new HashMap<>();

		List<String> lines;
		lines = Files.readAllLines(csvPath, Charset.forName("UTF-8"));
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i).split(",");
			if (line[5].equals("") == false) {
				int productCode = Integer.parseInt(line[4]);
				int slide = Integer.parseInt(line[5]);
				result.put(productCode, slide);
			}
		}

		return result;
	}

	//割増文字列と割増額をMapにする
	public Map<String, Integer> createWarimashiMap(Path csvPath)
			throws IOException {

		Map<String, Integer> result = new HashMap<>();
		String BOM = "\uFEFF";

		List<String> lines;
		lines = Files.readAllLines(csvPath, Charset.forName("UTF-8"));
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i).split(",");
			if (line[0].startsWith(BOM)) {
				line[0] = line[0].substring(1);
			}
			result.put(line[0], Integer.parseInt(line[1]));
		}

		return result;
	}

	//業者情報をMapにする
	public Map<Integer, Client> createClientsMap(Path csvPath)
			throws IOException {
		Map<Integer, Client> result = new HashMap<>();
		List<String> lines;
		lines = Files.readAllLines(csvPath, Charset.forName("MS932"));
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i).split(",");
			//請求先C
			int billingNum = Integer.parseInt(line[0]);
			//社名
			String name = line[1];
			//締め日
			String closingDateStr = line[2];
			//対象業者か？
			int isDoroijiCmpNum = Integer.parseInt(line[3]);
			//生コンベース単価
			int baseValue = Integer.parseInt(line[4]);
			//請求方法
			int billingMethodNum = Integer.parseInt(line[5]);
			//キーを請求先CにしてMapに追加
			result.put(billingNum,
					new Client(billingNum, name, closingDateStr, baseValue, isDoroijiCmpNum, billingMethodNum));
		}
		return result;
	}

	//道路維持管理費一覧
	public Map<Integer, int[]> createDoroijiSaleMap(String csvPath) throws IOException {
		Map<Integer, int[]> result = new HashMap<>();
		Path path = Paths.get(csvPath);
		List<String> lines = Files.readAllLines(path, Charset.forName("MS932"));
		ClientsManager cm = Controller.getInstance().getClientsManager();
		String[] line;
		int billingNum;
		int doroijiSale;
		int closingDateNum;
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i).split(",");
			billingNum = Integer.parseInt(line[0]);
			doroijiSale = Integer.parseInt(line[2]);
			closingDateNum = Integer.parseInt(line[3]);
			int[] arr = { doroijiSale, closingDateNum };
			//請求先コードからClientインスタンスを取得してmapに詰める
			result.put(billingNum, arr);
		}
		return result;
	}

	public static String getCurrentInvoiceCsvName() {
		return currentInvoiceCsvName;
	}

	public static Path getCurrentInvoiceCsvPath() {
		return currentInvoiceCsvPath;
	}
}
