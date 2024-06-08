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

	public int getBaseValue(SaleContent sale, int billingNum) {

		//スライドの判定
		int baseValue = 0;
		try {
			int slide = this.productMap.get(sale.getProductCode());
			baseValue = sale.getUnit() - this.productMap.get(sale.getProductCode());
		} catch (NullPointerException e) {
			View.getInstance().addMsg("請求先C" + billingNum + " " +
					sale.getDate() + " " +
					sale.getConsName() + "のベース単価を算定できません。");
			return 0;
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

		return baseValue;
	}
}
