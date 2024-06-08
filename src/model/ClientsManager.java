package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import entity.Client;
import entity.ClosingDay;

public class ClientsManager {

	private Map<Integer, Client> clientsMap;

	public ClientsManager(Map<Integer, Client> map) {
		this.clientsMap = map;
	}

	public boolean isTarget(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return false;
		} else {
			return this.clientsMap.containsKey(billingNum);
		}
	}

	public String getName(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return "";
		} else {
			return this.clientsMap.get(billingNum).getName();
		}
	}

	public boolean isSeparate(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return false;
		} else {
			return c.isSeparate();
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

	public boolean isOnly(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if (c == null) {
			return true;
		} else {
			return this.clientsMap.get(billingNum).isOnly();
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

	//締め日に該当するClientインスタンスをコレクションにして返す
	public List<Client> narrowDownByClosingDate(ClosingDay closingDate) {
		List<Client> result = new ArrayList<>();

		Client c;
		for (int i : this.clientsMap.keySet()) {
			c = this.clientsMap.get(i);
			if (c.getClosingDate() == closingDate) {
				result.add(c);
			}
		}

		//請求先コードの昇順で並び替え
		result.sort(new Comparator<Client>() {

			@Override
			public int compare(Client o1, Client o2) {

				return o1.getBillingNum() - o2.getBillingNum();
			}

		});
		return result;
	}
}
