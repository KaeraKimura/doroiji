package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PrintSelect extends JFrame{
	
	private JTextField printStartNumText;
	private JTextField printEndNumText;
	
	public static void main(String[] args) {
		new PrintSelect();
	}
	
	//コンストラクタ
	PrintSelect(){
		super("請求先C選択");
		this.setSize(300,300);
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JLabel label = new JLabel("<html>CSVを作成する請求先Cを<br>指定してください。</html>");
		label.setFont(new Font(View.getFontName(),Font.BOLD,18));
		this.printStartNumText = this.createTextField();
		layout.putConstraint(SpringLayout.NORTH, label, 20, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, this.printStartNumText,10 ,SpringLayout.SOUTH, label);
		
		panel.add(label);
		panel.add(this.printStartNumText);
		this.add(panel);
		this.setVisible(true);
	}
	
	//TextField生成
	private JTextField createTextField() {
		JTextField t = new JTextField();
		t.setFont(new Font(View.getFontName(),Font.BOLD,18));
		t.setPreferredSize(new Dimension(100,50));
		return t;
	}
}
