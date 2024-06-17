package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import controller.Controller;
import entity.Client;
import entity.ClosingDay;

public class PrintSelect extends JFrame {

	private JLabel startNumLabel;
	private JLabel endNumLabel;

	private JLabel currentSelectLabel;
	private ClientLabel currentOverLabel;
	public final ClosingDay closingDay;

	//コンストラクタ
	public PrintSelect(ClosingDay closingDay) {
		super("請求先C選択");
		this.setSize(400, 600);
		this.setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		//指定された締め日のClientインスタンスを抽出・並び変えたコレクションを取得
		List<Client> clientList = Controller.getInstance()
				.getClientsManager().narrowDownByClosingDate(closingDay);

		JLabel label = new JLabel("<html>CSVを作成する請求先Cを指定してください。</html>");
		label.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		layout.putConstraint(SpringLayout.NORTH, label, 20, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, panel);

		//開始番号のラベル
		this.startNumLabel = this.createLabel(String.valueOf(clientList.get(0).getBillingNum()));
		layout.putConstraint(SpringLayout.NORTH, startNumLabel, 20, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, startNumLabel, 20, SpringLayout.WEST, panel);

		//終了番号のラベル
		this.endNumLabel = this.createLabel(String.valueOf(clientList.get(clientList.size() - 1).getBillingNum()));
		layout.putConstraint(SpringLayout.NORTH, endNumLabel, 20, SpringLayout.SOUTH, startNumLabel);
		layout.putConstraint(SpringLayout.WEST, endNumLabel, 20, SpringLayout.WEST, panel);

		//CSVを作成する業者一覧のパネル
		JPanel clientsListPanel = new JPanel();
		clientsListPanel.setLayout(new BoxLayout(clientsListPanel, BoxLayout.Y_AXIS));
		JScrollPane scr = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scr.setViewportView(clientsListPanel);
		scr.setPreferredSize(new Dimension(250, 440));
		layout.putConstraint(SpringLayout.WEST, scr, 20, SpringLayout.EAST, startNumLabel);
		layout.putConstraint(SpringLayout.NORTH, scr, 10, SpringLayout.SOUTH, label);
		for (int i = 0; i < clientList.size(); i++) {
			clientsListPanel.add(new ClientLabel(clientList.get(i)));
		}

		//決定ボタン
		JButton rangeDecideButton = new JButton("決定");
		rangeDecideButton.setPreferredSize(new Dimension(100, 40));
		rangeDecideButton.setFont(new Font(View.getFontName(), Font.BOLD, 20));
		rangeDecideButton.setActionCommand("createRangeDecide");
		rangeDecideButton.addActionListener(Controller.getInstance());
		layout.putConstraint(SpringLayout.NORTH, rangeDecideButton, 20, SpringLayout.SOUTH, endNumLabel);
		layout.putConstraint(SpringLayout.WEST, rangeDecideButton, 20, SpringLayout.WEST, panel);

		panel.add(label);
		panel.add(startNumLabel);
		panel.add(endNumLabel);
		panel.add(scr);
		panel.add(rangeDecideButton);
		this.add(panel);
		this.setVisible(true);

		//現在選択中のラベル（初期値はstartNumLabel)
		this.currentSelectLabel = startNumLabel;
		currentSelectLabel.setBorder(new LineBorder(Color.black, 2));

		this.closingDay = closingDay;
	}

	private JLabel createLabel(String str) {
		JLabel l = new JLabel(str);
		l.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		l.setPreferredSize(new Dimension(100, 30));
		l.setBorder(new LineBorder(Color.black));
		l.setHorizontalAlignment(JLabel.RIGHT);
		l.setBackground(Color.white);
		l.setOpaque(true);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				currentSelectLabel.setBorder(new LineBorder(Color.black, 1));
				currentSelectLabel = l;
				currentSelectLabel.setBorder(new LineBorder(Color.black, 2));
			}
		});
		return l;
	}

	public int getStartNum() {
		return Integer.parseInt(this.startNumLabel.getText());
	}

	public int getEndNum() {
		return Integer.parseInt(this.endNumLabel.getText());
	}

	public boolean isClientLabel(Object o) {
		return o instanceof ClientLabel;
	}

	public void setBillingNum(Object o) {
		if (o instanceof ClientLabel) {
			int billingNum = ((ClientLabel) o).getBillingNum();
			this.currentSelectLabel.setText(String.valueOf(billingNum));
		}
	}

	private class ClientLabel extends JLabel {
		private int billingNum;
		private Color defaultColor;

		ClientLabel(Client client) {
			super(String.format("%05d", client.getBillingNum()) + " " + client.getName());
			this.billingNum = client.getBillingNum();
			this.setFont(new Font(View.getFontName(), Font.PLAIN, 12));
			this.setMaximumSize(new Dimension(250, 30));
			this.setPreferredSize(new Dimension(250, 30));
			defaultColor = Color.white;
			//請求方法によって背景色を変更
			switch (client.billingMethod) {
			case Client.DEDICATED_BILLING:
				defaultColor = Color.orange;
				break;
			case Client.TOTALSHEET_BILLING:
				defaultColor = Color.decode("#E3F2FD");
				break;
			case Client.SEPARATE_BILLING:
				defaultColor = Color.decode("#9acd32");
			}
			this.setBackground(defaultColor);
			this.setOpaque(true);
			this.addMouseListener(Controller.getInstance());
			this.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					if (currentOverLabel == null) {
						currentOverLabel = ((ClientLabel) e.getSource());
					}
					swichCurrentOverLabel(e);
				}
			});
		}

		public int getBillingNum() {
			return this.billingNum;
		}

		private void swichCurrentOverLabel(MouseEvent e) {
			currentOverLabel.setBackground(currentOverLabel.defaultColor);
			currentOverLabel = ((ClientLabel) e.getSource());
			currentOverLabel.setBackground(Color.decode("#ffe4e1"));
		}
	}
}
