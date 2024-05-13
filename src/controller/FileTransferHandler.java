package controller;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler;

import model.ImportFileValidator;

public class FileTransferHandler extends TransferHandler {
	//ドロップされたのがファイルかどうかを判定するメソッド
	@Override
	public boolean canImport(TransferSupport support) {

		if (support.isDrop() == false) {
			//ドロップ操作でないなら受け取らない
			return false;
		}
		if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
			//ドロップされたものがファイルでないなら受け取らない
			return false;
		}
		return true;
	}

	//ドロップされたファイルを受け取る
	@Override
	public boolean importData(TransferSupport support) {
		//受け取ってよいかの判断
		if (this.canImport(support) == false) {
			return false;
		}

		//ドロップ処理
		Transferable t = support.getTransferable();
		List<File> files;
		try {
			//ファイルの検証　CSV以外は除外
			files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
			List<File> variableFiles = new ArrayList<File>();
			for (File f : files) {
				variableFiles.add(f);
			}
			files = new ImportFileValidator().validateCSV(variableFiles);
			Controller.getInstance().receiveCsvFiles(files);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
