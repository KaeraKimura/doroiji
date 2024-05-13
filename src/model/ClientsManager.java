package model;

import java.util.Map;

import controller.Controller;
import entity.Client;
import entity.Invoice;
import entity.SaleContent;
import view.View;

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

	public int getDoroijiValue(Invoice inv) {
		Client c = this.clientsMap.get(inv.getBillingNum());
		if (c == null) {
			int baseValue = 0;
			//適当な生コンのsaleを取得してCalcuratorに渡す
			SaleContent concreteSale = null;
			for (SaleContent sale : inv.getSalesRow()) {
				if (sale.isConcrete() == true && sale.getProductCode() < 1000) {
					concreteSale = sale;
					break;
				}
			}
			try {
				UnitPriceCalculator up = Controller.getInstance().getCalculator();
				baseValue = up.calcBaseValue(concreteSale);
				//ベース単価の下三桁によって地区内か地区外かを判断する
				String baseValueStr = String.valueOf(baseValue);
				int last3Digits = Integer.parseInt(baseValueStr.substring(baseValueStr.length() - 3));
				if (last3Digits == 300 || last3Digits == 500) {
					return 1500;
				} else {
					return 1000;
				}
			} catch (NullPointerException e) {
				//concreteSaleがnullの時は1500で戻すが、確認用メッセージを出す。
				View.getInstance().addMsg(inv.getCmpName() + "：要確認");
				return 1500;
			}
		} else {
			return this.clientsMap.get(inv.getBillingNum()).getDoroijiValue();
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
			return false;
		} else {
			return this.clientsMap.get(billingNum).isOnly();
		}
	}
}
