package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import controller.Controller;
import controller.FileTransferHandler;
import entity.ClosingDate;
import entity.Invoice;

public class View {

	private JFrame frame;
	private Color color;
	private JScrollPane scrollPane;
	private JOptionPane msgPane;
	private ListPanel listPanel;
	private Controller controller;
	private static View singleton = new View();
	private StringBuffer msgBuffer;

	private View() {
		this.controller = Controller.getInstance();
		this.msgBuffer = new StringBuffer();

		this.color = Color.decode("#E3F2FD");
		this.frame = new JFrame("道路維持管理請求書");
		ImageIcon icon = new ImageIcon("data/icon.png");
		this.frame.setIconImage(icon.getImage());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS));
		this.frame.setSize(600, 600);
		this.frame.setLocationRelativeTo(null);
		this.frame.getContentPane().setBackground(color);
		frame.setResizable(false);

		this.frame.add(new HeaderPanel());

		this.listPanel = new ListPanel();
		JScrollPane scrlPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrlPane.setMaximumSize(new Dimension(500, 500));
		scrlPane.setViewportView(this.listPanel);
		this.frame.add(scrlPane);
		this.frame.add(Box.createRigidArea(new Dimension(10, 10)));

		this.frame.add(new FooterPanel());
		this.frame.setVisible(true);
	}

	public static View getInstance() {
		return singleton;
	}

	public void addMsg(String msg) {
		this.msgBuffer.append(msg + "\n");
	}

	public void showBufferMsg() {
		if (!this.msgBuffer.toString().equals("")) {
			JOptionPane.showMessageDialog(this.frame, this.msgBuffer);
			this.msgBuffer = new StringBuffer();
		}
	}

	public void allPrintCheck(boolean boo) {
		List<InvoicePanel> panels = this.listPanel.getPanels();
		for (InvoicePanel panel : panels) {
			panel.setIsPrint(boo);
		}
	}

	public void setAllSeparatePrint(boolean boo) {
		List<InvoicePanel> panels = this.listPanel.getPanels();
		for (InvoicePanel panel : panels) {
			panel.setIsSeparatePrint(boo);
		}
	}
	
	public void setNotPrint(List notPrintList) {
		for(InvoicePanel panel: this.listPanel.getPanels()) {
			if(notPrintList.contains(panel.getInvoice().getBillingNum())) {
				panel.setIsPrint(false);
			}
		}
	}

	public void addInvoicePanel(List<Invoice> list) {
		this.listPanel.addInvoicePanel(list);
		this.frame.validate();
	}

	public List<Invoice> getSelectedInvoiceList() {
		List<InvoicePanel> panels = this.listPanel.getPanels();
		List<Invoice> result = new ArrayList<>();
		for (int i = 0; i < panels.size(); i++) {
			if (panels.get(i).isPrint() == true) {
				Invoice inv = panels.get(i).getInvoice();
				inv.setIsSeparate(panels.get(i).isSeparatePrint());
				result.add(inv);

			}
		}
		return result;
	}

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this.frame, msg);
	}

	class HeaderPanel extends JPanel {
		HeaderPanel() {
			//			this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.setBackground(color);
			//請求先名ラベル
			JLabel nameLabel = new JLabel("請求先名");
			nameLabel.setFont(new Font(View.getFontname(), Font.PLAIN, 18));
			nameLabel.setMaximumSize(new Dimension(260, 20));
			nameLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(nameLabel);
			//出力選択ラベル
			JLabel printLabel = new JLabel("出力");
			printLabel.setFont(new Font(View.getFontname(), Font.PLAIN, 18));
			printLabel.setMaximumSize(new Dimension(80, 50));
			printLabel.setHorizontalAlignment(JLabel.CENTER);
			printLabel.addMouseListener(Controller.getInstance());

			this.add(printLabel);
			//一括か現場別化の選択
			JLabel radioLabel = new JLabel("一括");
			radioLabel.setFont(new Font(View.getFontname(), Font.PLAIN, 18));
			radioLabel.setMaximumSize(new Dimension(80, 20));
			radioLabel.setHorizontalAlignment(JLabel.CENTER);
			radioLabel.addMouseListener(Controller.getInstance());
			this.add(radioLabel);
			radioLabel = new JLabel("別");
			radioLabel.setFont(new Font(View.getFontname(), Font.PLAIN, 18));
			radioLabel.setMaximumSize(new Dimension(80, 20));
			radioLabel.setHorizontalAlignment(JLabel.CENTER);
			radioLabel.addMouseListener(Controller.getInstance());
			this.add(radioLabel);
		}
	}

	class FooterPanel extends JPanel {
		FooterPanel() {
			this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			//CSV出力ボタン
			JButton printBtn = new JButton("CSV出力");
			printBtn.setActionCommand("print");
			printBtn.addActionListener(controller);
			this.add(printBtn);
			
			//CSV作成ボタン
			JButton D20Btn = new JButton(ClosingDate.D_20.toString());
			D20Btn.setActionCommand(ClosingDate.D_20.toString());
			D20Btn.addActionListener(controller);
			this.add(D20Btn);
		}
	}

	class ListPanel extends JPanel {
		//CSVインポートした業者のパネル群
		List<InvoicePanel> panels;

		ListPanel() {

			this.panels = new ArrayList<InvoicePanel>();
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setAlignmentX(CENTER_ALIGNMENT);

			this.setBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.gray));
			this.setBackground(Color.LIGHT_GRAY);
			//ドロップ操作の有効化
			this.setTransferHandler(new FileTransferHandler());
		}

		List<InvoicePanel> getPanels() {
			return this.panels;
		}

		void addInvoicePanel(List<Invoice> list) {
			InvoicePanel invoicePanel;
			for (int i = 0; i < list.size(); i++) {
				invoicePanel = new InvoicePanel(list.get(i));
				panels.add(invoicePanel);
				this.add(invoicePanel);
			}
		}
	}

	public static String getFontname() {
		return "Meiryo UI";
	}
}
