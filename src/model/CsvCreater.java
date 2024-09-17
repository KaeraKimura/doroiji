package model;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

public class CsvCreater extends Robot {

	private Clipboard clipboard;
	private int defaultSalesAreaColorRGB;
	private int defaultDeleteBtnColorRGB;
	
	private LocalDate closingDate;
	//新規ボタンの座標
	private final int RESET_X = 25;
	private final int RESET_Y = 50;
	
	//印刷ボタンの座標
	private final int PRINT_X = 235;
	private final int PRINT_Y = 50;
	
	//テキスト出力の座標
	private final int CSV_PRINT_X = 820;
	private final int CSV_PRINT_Y = 470;
	
	//印刷終了の座標
	private final int PRINT_END_X = 925;
	private final int PRINT_END_Y = 470;
	
	private int completedBillingNum;
	
	public CsvCreater(LocalDate closingDate) throws AWTException, UnspecifidePositionException {
		super();
		//入力する〆日
		this.closingDate = closingDate;
		//クリップボートオブジェクト生成
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		//開始まえに少しまつ
		this.delay(2500);

		//売上表示欄・削除ボタンの色のRGB
		this.pushReset();
		this.delay(500);
		this.defaultSalesAreaColorRGB = this.getSalesAreaColorRGB();
		this.defaultDeleteBtnColorRGB = this.getDeleteBtnColorRGB();
	}

	//自動作業のルーチン
	public void print(int billingNum) throws UnspecifidePositionException {

		//新規クリック
		this.pushReset();

		this.delay(500);

		//請求先C入力
		this.inputNumber(billingNum);
		this.pressEnter(this.RESET_X,this.RESET_Y);
		this.delay(100);

		//締め日　年数
		this.inputNumber(this.closingDate.getYear());
		this.pressEnter(this.RESET_X,this.RESET_Y);
		this.delay(100);

		//月数
		this.inputNumber(this.closingDate.getMonthValue());
		this.pressEnter(this.RESET_X,this.RESET_Y);
		this.delay(100);

		//日数
		this.inputNumber(this.closingDate.getDayOfMonth());
		this.pressEnter(this.RESET_X,this.RESET_Y);
		this.delay(100);

		//集計
		this.pressEnter(this.RESET_X,this.RESET_Y);

		//集計が完了したかを画面の色から判断する。
		for (int i = 0; ; i++) {
			System.out.println(billingNum + ":" + i);
			this.delay(500);
			//売上行の色が変わる＝集計完了なのでbreakして出力
			if (this.defaultSalesAreaColorRGB != this.getSalesAreaColorRGB()) {
				break;
			}
			//1秒経過しても削除ボタンの色に変化が無い＝売上なしなので処理を終了
			if(i == 2 && this.defaultDeleteBtnColorRGB == this.getDeleteBtnColorRGB()) {
				return;
			}
			//3秒経過して何も変化が無いときも終了
			if(i == 6) {
				return;
			}
		}

		//印刷クリック
		this.pushPrint();
		this.delay(300);

		//テキスト出力クリック
		this.pushCsvPrint();
		this.delay(200);

		//確認メッセージOk
		this.pressEnter(this.CSV_PRINT_X,this.CSV_PRINT_Y);
		this.delay(200);

		//完了メッセージ
		this.pressEnter(this.CSV_PRINT_X,this.CSV_PRINT_Y);
		this.delay(200);

		//印刷終了ボタンのクリック
		this.pushPrintEnd();
		this.delay(200);
		
		this.completedBillingNum = billingNum;
	}

	private int getSalesAreaColorRGB() {
		return this.getPixelColor(485, 335).getRGB();
	}
	
	private int getDeleteBtnColorRGB() {
		return this.getPixelColor(175, 45).getRGB();
	}

	//数字の入力
	public void inputNumber(int Num) {

		//StringSelectionとかいうよくわからんオブジェクト
		StringSelection ss = new StringSelection(String.valueOf(Num));
		clipboard.setContents(ss, null);

		//貼り付け Ctrl + v
		this.keyPress(KeyEvent.VK_CONTROL);
		this.keyPress(KeyEvent.VK_V);
		this.keyRelease(KeyEvent.VK_CONTROL);
		this.keyRelease(KeyEvent.VK_V);
	}

	//エンターキー入力
	private void pressEnter(int x, int y) throws UnspecifidePositionException {
		this.validateMousePosition(x, y);
		this.keyPress(KeyEvent.VK_ENTER);
		this.keyRelease(KeyEvent.VK_ENTER);
	}
	
	//新規ボタンのクリック
	private void pushReset() throws UnspecifidePositionException {
		this.mouseMove(this.RESET_X, this.RESET_Y);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		//クリック後に指定した座標のプラマイ５ｐｘかを判定
		this.validateMousePosition(this.RESET_X, this.RESET_Y);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//印刷ボタンのクリック
	private void pushPrint() throws UnspecifidePositionException {
		this.mouseMove(this.PRINT_X, this.PRINT_Y);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.validateMousePosition(this.PRINT_X, this.PRINT_Y);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//テキスト出力のクリック
	private void pushCsvPrint() throws UnspecifidePositionException {
		this.mouseMove(this.CSV_PRINT_X, this.CSV_PRINT_Y);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.validateMousePosition(CSV_PRINT_X, CSV_PRINT_Y);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//印刷終了のクリック
	private void pushPrintEnd() throws UnspecifidePositionException {
		this.mouseMove(this.PRINT_END_X, this.PRINT_END_Y);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.validateMousePosition(PRINT_END_X, PRINT_END_Y);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	//マウス座標の確認
	private boolean validateMousePosition(int x, int y) throws UnspecifidePositionException {
		//今の座標
		int currentX = MouseInfo.getPointerInfo().getLocation().x;
		int currentY = MouseInfo.getPointerInfo().getLocation().y;

		//渡された座標（Robotが操作した座標）とプラマイ５以内ならTrue。そうでなければFalse。
		if(currentX <= x + 5 && currentX >= x - 5) {
			if(currentY <= y + 5 && currentY >= y -5) {
				return true;
			}
		}
		throw new UnspecifidePositionException("マウスが動きました。");
	}
	
	public class UnspecifidePositionException extends Exception{
		
		public UnspecifidePositionException(String msg) {
			super(msg);
		}
		public int getCompletedBillingNum() {
			//外側クラス（CsvCreaterのフィールド）の印刷中の請求先Cを返す。
			return completedBillingNum;
		}
	}
}
