package writer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class ListWriter {
	
	public void write(int billingMonth) throws IOException{
		
		PrintWriter pw = new PrintWriter(billingMonth + ".csv",Charset.forName("MS932"));
		
	}
	
	//コンストラクタ
	public ListWriter() {
		
	}

}
