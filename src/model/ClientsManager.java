package model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Client;
import entity.ClosingDay;
import entity.Invoice;
import reader.CSVReader;

public class ClientsManager {

	private Map<Integer, Client> clientsMap;

	public ClientsManager(Map<Integer, Client> map) {
		this.clientsMap = map;
	}
	
	public Client getClient(int billingNum) {
		return this.clientsMap.get(billingNum);
	}
	public boolean contains(int billingNum) {
		return this.clientsMap.containsKey(billingNum);
	}

	public String getName(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return "";
		} else {
			return this.clientsMap.get(billingNum).getName();
		}
	}

	public int getDoroijiValue(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return 0;
		} else {
			return this.clientsMap.get(billingNum).getDoroijiValue();
		}
	}

	public int getBaseValue(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return 0;
		} else {
			return this.clientsMap.get(billingNum).getBaseValue();
		}
	}

	public int getBillingMethod(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return ３;
		} else {
			return this.clientsMap.get(billingNum).getBillingMethod();
		}
	}

	//締め日
	public LocalDate getLatestClosingDate(ClosingDay closingDate) {
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
			//現在＋3日から遡って月数が変わる日付を調べる
			long tommorowEpoch = LocalDate.now().toEpochDay() + 3;
			int targetMonth = LocalDate.ofEpochDay(tommorowEpoch).getMonthValue();
			for (long c = tommorowEpoch;; c--) {
				if (targetMonth != LocalDate.ofEpochDay(c).getMonthValue()) {
					return LocalDate.ofEpochDay(c);
				}
			}
		}

	}
	//PrintSelectで指定された範囲の業者リストを作成
	public List<Client> createCsvList(List<Client> clientList, int startNum, int endNum){
		
		List<Client> result = new ArrayList<>();
		
		boolean flag = false;
		Client c;
		for (int i = 0; i < clientList.size(); i++) {
			c = clientList.get(i);
			if (c.getBillingNum() == startNum) {
				flag = true;
			}
			if (flag == true) {
				result.add(c);
			}
			if (c.getBillingNum() == endNum) {
				break;
			}
		}
		return result;
	}
	
	//PrintSelectで指定された開始番号が、終了番号の業者より順番がうしろかどうかを検証
	public boolean validateSelectedOrder(List<Client> clientList, int startNum, int endNum) {
		int startNumIndex = 0;
		int endNumIndex = 0;
		Client c;
		for(int i = 0; i < clientList.size(); i++) {
			c = clientList.get(i);
			if(c.getBillingNum() == startNum) {
				startNumIndex = i;
			}else if(c.getBillingNum() == endNum){
				endNumIndex = i;
			}
		}
		return endNumIndex >= startNumIndex;
	}

	//締め日に該当するClientインスタンスをコレクションにして返す
	public List<Client> narrowDownByClosingDate(ClosingDay closingDate) {
		List<Client> result = new ArrayList<>();

		Client client;
		//引数の締め日のClientインスタンスをMapから抽出
		for (int i : this.clientsMap.keySet()) {
			client = this.clientsMap.get(i);
			if (client.getClosingDate() == closingDate) {
				result.add(client);
			}
		}

		//先方→総括→現場の順でかつ請求先Cの昇順
		result.sort(new Comparator<Client>() {

			@Override
			public int compare(Client o1, Client o2) {
				int result = 0;
				//同じ請求方法あれば請求先Cを比べる
				int o1Method = o1.getBillingMethod();
				int o2Method = o2.getBillingMethod();
				if (o1Method == o2Method) {
					return o1.getBillingNum() - o2.getBillingNum();
				} else {
					//請求方法が違うClientなら先方→総括→現場→ノーマルの順
					return o1Method - o2Method;
				}
			}

		});
		return result;
	}
	
	//出力したCSVの内容で一覧を作成
	public Map<Integer,int[]> createDoroijiMap(String fileName, List<Invoice> list) {
		
		//作成した請求書の年月日に合致するcsvファイルを取得・コレクション化する
		Map<Integer,int[]> result = null;
		try {
			result = new CSVReader().createDoroijiSaleMap(fileName);
		}catch(IOException err) {
			//csvファイルが存在しない場合は新しいインスタンスを生成
			result = new HashMap<>();
		}
		//listの内容でMapを書き換え
		for(Invoice inv: list) {
			
			//道路維持管理費の対象業者でなければスキップ
			if(this.clientsMap.get(inv.getBillingNum()).isDoroijiCmp() == false) {
				continue;
			}
			
			//道路維持管理費合計と締め日の配列
			int[] arr = {inv.getDoroijiTotal(),inv.getClosingDay().getDateNum()};
			//Mapに同じKey＝請求先Cが存在するか確かめる。
			if(result.containsKey(inv.getBillingNum())){
				
				int csvClosingDay = result.get(inv.getBillingNum())[1]; //csvの締め日
				int invClosingDay = inv.getClosingDay().getDateNum();//請求書の締め日
				//締め日が同じであれば上書き、別日であれば加算する。
				if(csvClosingDay != invClosingDay) {
					arr[0] = result.get(inv.getBillingNum())[0] + inv.getDoroijiTotal();
				}
				result.put(inv.getBillingNum(), arr);
			}else {
				result.put(inv.getBillingNum(), arr);
			}
			
			inv.resetDoroiji();
		}
		
		return result;
	}
}
