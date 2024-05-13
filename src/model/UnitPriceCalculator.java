package model;

import java.util.Map;

import controller.Controller;
import entity.SaleContent;
import view.View;

public class UnitPriceCalculator {

	private ClientsManager clientsManager;
	private Map<Integer, Integer> productMap;
	private Map<String, Integer> warimashiMap;

	//コンストラクタ
	public UnitPriceCalculator(
			Map<Integer, Integer> productMap,
			Map<String, Integer> warimashiMap) {
		this.clientsManager = Controller.getInstance().getClientsManager();
		this.productMap = productMap;
		this.warimashiMap = warimashiMap;
	}

	public int calcBaseValue(SaleContent sale) throws NullPointerException {
		//スライドの判定
		int result;

		int slide = this.productMap.get(sale.getProductCode());
		result = sale.getUnit() - this.productMap.get(sale.getProductCode());

		//割増の計算
		for (String warimashiStr : sale.getWarimashi()) {
			result -= this.warimashiMap.get(warimashiStr);
		}
		return result;
	}

	public boolean isNewValue(SaleContent sale, int billingNum) {

		//スライドの判定
		int baseValue;
		try {
			int slide = this.productMap.get(sale.getProductCode());
			baseValue = sale.getUnit() - this.productMap.get(sale.getProductCode());
		} catch (NullPointerException e) {
			View.getInstance().addMsg("請求先C" + billingNum + " " +
					sale.getDate() + " " +
					sale.getConsName() + "のベース単価を算定できません。");
			return false;
		}

		//割増の計算
		try {
			//割増の計算
			for (String warimashiStr : sale.getWarimashi()) {
				baseValue -= this.warimashiMap.get(warimashiStr);
			}
		} catch (NullPointerException e) {
			View.getInstance().addMsg("請求先C" + billingNum + " " +
					sale.getDate() + " " +
					sale.getConsName() + "に変な割増があります");
		}
		if (this.clientsManager.getBaseValue(billingNum) == baseValue) {
			return true;
		}

		return false;
	}
}
