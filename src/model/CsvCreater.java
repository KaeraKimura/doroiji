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
	private LocalDate closingDate;

	public CsvCreater(LocalDate closingDate) throws AWTException {
		super();
		//入力する〆日
		this.closingDate = closingDate;
		//クリップボートオブジェクト生成
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		//売上表示欄の色のRGB
		this.pushReset();
		this.delay(1000);
		this.defaultSalesAreaColorRGB = this.getSalesAreaColorRGB();
	}

	//自動作業のルーチン
	public void print(int billingNum) {

		//新規クリック
		this.pushReset();

		this.delay(500);

		//請求先C入力
		this.inputNumber(billingNum);
		this.pressEnter();
		this.delay(100);

		//締め日　年数
		this.inputNumber(this.closingDate.getYear());
		this.pressEnter();
		this.delay(100);

		//月数
		this.inputNumber(this.closingDate.getMonthValue());
		this.pressEnter();
		this.delay(100);

		//日数
		this.inputNumber(this.closingDate.getDayOfMonth());
		this.pressEnter();
		this.delay(100);

		//集計
		this.pressEnter();

		//集計が完了したかを画面の色から判断する。
		for (int i = 0; i < 7; i++) {
			if (i == 6) {
				//6回(約3秒経っても）変化が無い場合は売上無しとして以降の処理をスキップ
				return;
			}
			this.delay(500);
			if (this.defaultSalesAreaColorRGB != this.getSalesAreaColorRGB()) {
				break;
			}
		}

		//印刷クリック
		this.pushPrint();
		this.delay(300);

		//テキスト出力クリック
		this.pushCsvPrint();
		this.delay(200);

		//確認メッセージOk
		this.pressEnter();
		this.delay(200);

		//完了メッセージ
		this.pressEnter();
		this.delay(200);

		//印刷終了ボタンのクリック
		this.pushPrintEnd();
		this.delay(200);
	}

	private int getSalesAreaColorRGB() {
		return this.getPixelColor(485, 335).getRGB();
	}

	//新規ボタンのクリック
	private void pushReset() {
		this.mouseMove(25, 50);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
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
	private void pressEnter() {
		this.keyPress(KeyEvent.VK_ENTER);
		this.keyRelease(KeyEvent.VK_ENTER);
	}

	//印刷ボタンのクリック
	private void pushPrint() {
		this.mouseMove(235, 50);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//テキスト出力のクリック
	private void pushCsvPrint() {
		this.mouseMove(820, 470);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//印刷終了のクリック
	private void pushPrintEnd() {
		this.mouseMove(925, 470);
		this.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	//マウス座標の確認
	private boolean validateMousePosition(int x, int y) {
		//今の座標
		int currentX = MouseInfo.getPointerInfo().getLocation().x;
		int currentY = MouseInfo.getPointerInfo().getLocation().y;

	}
}
