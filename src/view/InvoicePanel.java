package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import entity.Invoice;

public class InvoicePanel extends JPanel {

	private Invoice invoice;
	private JCheckBox printCheck;
	private ButtonGroup btnGroup;
//	private JRadioButton separateRadio;
//	private JRadioButton bunchRadio;

	//コンストラクタ
	InvoicePanel(Invoice invoice) {
		this.invoice = invoice;
		Color color = Color.decode("#F5F5F5");
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setMaximumSize(new Dimension(500, 50));
		this.setPreferredSize(new Dimension(500, 50));
		this.setBackground(color);

		//ラベル
		JLabel nameLabel = new JLabel(invoice.getShapeCmpName());
		nameLabel.setFont(new Font(View.getFontName(), Font.BOLD, 18));
		nameLabel.setMaximumSize(new Dimension(350, 50));
		nameLabel.setBackground(color);
		nameLabel.setPreferredSize(nameLabel.getMaximumSize());
		this.add(nameLabel);
		//チェックボックス
		this.printCheck = new JCheckBox("", true);
		this.printCheck.setMaximumSize(new Dimension(80, 50));
		this.printCheck.setBackground(color);
		this.printCheck.setHorizontalAlignment(JCheckBox.CENTER);
		this.printCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean state = ((JCheckBox) e.getSource()).isSelected();
				changeState(state);
			}
		});
		this.add(this.printCheck);
		//ラジオボタン
//		this.bunchRadio = new JRadioButton();
//		bunchRadio.setActionCommand("bunch");
//		bunchRadio.setMaximumSize(new Dimension(80, 50));
//		bunchRadio.setBackground(color);
//		bunchRadio.setHorizontalAlignment(JRadioButton.CENTER);
//		bunchRadio.setSelected(true);
//		this.add(bunchRadio);
//		this.separateRadio = new JRadioButton();
//		separateRadio.setMaximumSize(new Dimension(80, 50));
//		separateRadio.setActionCommand("separate");
//		separateRadio.setBackground(color);
//		separateRadio.setHorizontalAlignment(JRadioButton.CENTER);
//		this.add(separateRadio);
//		this.btnGroup = new ButtonGroup();
//		this.btnGroup.add(bunchRadio);
//		this.btnGroup.add(separateRadio);

	}

	boolean isPrint() {
		return this.printCheck.isSelected();
	}

	void setIsPrint(boolean boo) {
		this.printCheck.setSelected(boo);
	}

	Invoice getInvoice() {
		return this.invoice;
	}

	void changeState(boolean boo) {
		if (boo == false) {
			Color color = Color.decode("#EF9A9A");
			this.setBackground(color);
			this.printCheck.setBackground(color);

		} else {
			Color color = Color.decode("#F5F5F5");
			this.setBackground(color);
			this.printCheck.setBackground(color);

		}
	}
	
	void setPrintMethod(int printMethod) {
		
	}
}
