package model;

import java.util.Map;

import entity.Client;

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
}
