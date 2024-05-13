package model;

import java.io.File;
import java.util.List;

import view.View;

public class ImportFileValidator {

	//コンストラクタ
	public ImportFileValidator() {

	}

	public List<File> validateCSV(List<File> files) {

		for (int i = 0; i < files.size(); i++) {
			File f = files.get(i);
			if (f.isDirectory() == true) {
				View.getInstance().showMessage("フォルダは受け取れません。");
				files.remove(i);
				i--;
			} else if (!f.getName().substring(f.getName().lastIndexOf(".")).equals(".csv")) {
				View.getInstance().showMessage("CSVファイル以外は受け取れません。");
				files.remove(i);
				i--;
			}
		}
		return files;
	}
}
