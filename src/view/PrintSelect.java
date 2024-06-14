package view;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import controller.Controller;
import entity.Client;

public class PrintSelect extends JFrame {

	private JTextField createStartNumText;
	private JTextField createEndNumText;

	private JButton rangeDecideButton;
	private JButton cancelButton;

	//コンストラクタ
	public PrintSelect(List<Client> clientList) {
		super("請求先C選択");
		this.setSize(400, 600);
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel label = new JLabel("<html>CSVを作成する請求先Cを<br>指定してください。</html>");
		label.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		layout.putConstraint(SpringLayout.NORTH, label, 20, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, panel);

		//開始番号のラベル
		JLabel startNumLabel = this.createLabel("");
		layout.putConstraint(SpringLayout.NORTH, startNumLabel, 20, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, startNumLabel, 10, SpringLayout.WEST, panel);

		//		//開始番号のTextField
		//		this.createStartNumText = this.createTextField();
		//		layout.putConstraint(SpringLayout.WEST, this.createStartNumText, 150, SpringLayout.WEST, panel);
		//		layout.putConstraint(SpringLayout.NORTH, this.createStartNumText, 10, SpringLayout.SOUTH, label);

		//終了番号のラベル
		JLabel endNumLabel = this.createLabel("");
		layout.putConstraint(SpringLayout.NORTH, endNumLabel, 20, SpringLayout.SOUTH, startNumLabel);
		layout.putConstraint(SpringLayout.WEST, endNumLabel, 10, SpringLayout.WEST, panel);

		//CSVを作成する業者一覧のパネル
		JPanel clientsListPanel = new JPanel();
		clientsListPanel.setLayout(new BoxLayout(clientsListPanel, BoxLayout.Y_AXIS));
		JScrollPane scr = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scr.setViewportView(clientsListPanel);
		scr.setPreferredSize(new Dimension(200, 500));
		layout.putConstraint(SpringLayout.WEST, scr, 10, SpringLayout.EAST, startNumLabel);
		layout.putConstraint(SpringLayout.NORTH, scr, 10, SpringLayout.SOUTH, label);
		for (int i = 0; i < clientList.size(); i++) {
			clientsListPanel.add(this.createClientLabel(clientList.get(i)));
		}
		//		//終了番号のTextField
		//		this.createEndNumText = this.createTextField();
		//		layout.putConstraint(SpringLayout.WEST, this.createEndNumText, 150, SpringLayout.WEST, panel);
		//		layout.putConstraint(SpringLayout.NORTH, this.createEndNumText, 10, SpringLayout.SOUTH,
		//				this.createStartNumText);

		//決定ボタン
		this.rangeDecideButton = new JButton("決定");
		this.rangeDecideButton.setPreferredSize(new Dimension(150, 40));
		this.rangeDecideButton.setFont(new Font(View.getFontName(), Font.BOLD, 20));
		this.rangeDecideButton.setActionCommand("createRangeDecide");
		this.rangeDecideButton.addActionListener(Controller.getInstance());
		layout.putConstraint(SpringLayout.NORTH, this.rangeDecideButton, 20, SpringLayout.SOUTH, clientsListPanel);
		layout.putConstraint(SpringLayout.WEST, this.rangeDecideButton, 20, SpringLayout.WEST, panel);
		//キャンセルボタン
		this.cancelButton = new JButton("キャンセル");
		this.cancelButton.setPreferredSize(new Dimension(150, 40));
		this.cancelButton.setFont(new Font(View.getFontName(), Font.BOLD, 12));
		this.rangeDecideButton.setActionCommand("createRangeDecideCancel");
		this.cancelButton.addActionListener(Controller.getInstance());
		layout.putConstraint(SpringLayout.NORTH, this.cancelButton, 20, SpringLayout.SOUTH, clientsListPanel);
		layout.putConstraint(SpringLayout.WEST, this.cancelButton, 20, SpringLayout.EAST, this.rangeDecideButton);

		panel.add(label);
		panel.add(startNumLabel);
		panel.add(endNumLabel);
		panel.add(scr);
		panel.add(this.rangeDecideButton);
		panel.add(this.cancelButton);
		this.add(panel);
		this.setVisible(true);
	}

	//TextField生成
	private JTextField createTextField() {
		JTextField t = new JTextField();
		t.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		t.setPreferredSize(new Dimension(100, 30));
		return t;
	}

	private JLabel createLabel(String str) {
		JLabel l = new JLabel(str);
		l.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		l.setPreferredSize(new Dimension(100, 30));
		return l;
	}

	private JLabel createClientLabel(Client client) {
		String billingNumStr = String.format("%05d", client.getBillingNum());
		JLabel label = new JLabel(billingNumStr + " " + client.getName());
		return label;
	}
}
