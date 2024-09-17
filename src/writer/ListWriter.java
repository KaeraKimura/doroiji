package writer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import controller.Controller;
import model.ClientsManager;

public class ListWriter {
	
	public void write(String fileName, Map<Integer,int[]> doroijiMap) throws IOException{
		
		PrintWriter pw = new PrintWriter(fileName,Charset.forName("MS932"));
		List<Integer> billingNumList = new ArrayList<>();
		ClientsManager cm = Controller.getInstance().getClientsManager();
		
		//billingNumのListを作って昇順で並べ替え
		for(Integer i: doroijiMap.keySet()) {
			billingNumList.add(i);
		}
		Collections.sort(billingNumList);
		
		int billingNum;
		String billingName;
		int doroijiSale;
		int closingDateNum;
		for(int i = 0; i < billingNumList.size(); i++) {
			billingNum = billingNumList.get(i);
			//請求先C・請求先名・道路維持管理費金額の順で出力
			billingName = cm.getName(billingNum);
			doroijiSale = doroijiMap.get(billingNum)[0];
			closingDateNum = doroijiMap.get(billingNum)[1];
			//道路維持管理費がゼロ円なら出力しない
			if(doroijiSale == 0) {
				continue;
			}
			pw.print(billingNum + "," + billingName + "," + doroijiSale + "," + closingDateNum + "\n");
		}
		pw.close();
	}
	
	//コンストラクタ
	public ListWriter() {
		
	}
	

	
}
