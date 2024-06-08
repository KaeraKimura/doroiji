package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import entity.Client;
import entity.ClosingDate;

public class ClientsManager {

	private Map<Integer, Client> clientsMap;

	public ClientsManager(Map<Integer, Client> map) {
		this.clientsMap = map;
	}

	public boolean isTarget(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return false;
		}else {
			return this.clientsMap.containsKey(billingNum);
		}
	}
	
	public String getName(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return "";
		}else {
			return this.clientsMap.get(billingNum).getName();
		}
	}

	public boolean isSeparate(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return false;
		}else {
			return c.isSeparate();
		}
	}
	
	public int getDoroijiValue(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return 0;
		}else {
			return this.clientsMap.get(billingNum).getDoroijiValue();
		}
	}
	
	public int getBaseValue(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return 0;
		}else {
			return this.clientsMap.get(billingNum).getBaseValue();
		}
	}
	
	public boolean isOnly(int billingNum) {
		Client c = this.clientsMap.get(billingNum);
		if(c == null) {
			return true;
		}else {
			return this.clientsMap.get(billingNum).isOnly();
		}
	}
	
	//締め日に該当するClientインスタンスをコレクションにして返す
	public List<Client> narrowDownByClosingDate(ClosingDate closingDate){
		List<Client> result = new ArrayList<>();
		
		Client c;
		for(int i: this.clientsMap.keySet()) {
			c = this.clientsMap.get(i);
			if(c.getClosingDate() == closingDate) {
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
