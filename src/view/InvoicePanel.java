package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import entity.Invoice;

public class InvoicePanel extends JPanel {

	private Invoice invoice;
	private JCheckBox checkBox;
	private ButtonGroup btnGroup;
	private JRadioButton separateRadio;
	private JRadioButton bunchRadio;

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
		nameLabel.setFont(new Font(View.getFontname(), Font.BOLD, 18));
		nameLabel.setMaximumSize(new Dimension(260, 50));
		nameLabel.setBackground(color);
		nameLabel.setPreferredSize(nameLabel.getMaximumSize());
		this.add(nameLabel);
		//チェックボックス
		this.checkBox = new JCheckBox("", true);
		this.checkBox.setMaximumSize(new Dimension(80, 50));
		this.checkBox.setBackground(color);
		this.checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		this.checkBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean state = ((JCheckBox) e.getSource()).isSelected();
				changeState(state);
			}
		});
		this.add(this.checkBox);
		//ラジオボタン
		this.bunchRadio = new JRadioButton();
		bunchRadio.setActionCommand("bunch");
		bunchRadio.setMaximumSize(new Dimension(80, 50));
		bunchRadio.setBackground(color);
		bunchRadio.setHorizontalAlignment(JRadioButton.CENTER);
		bunchRadio.setSelected(true);
		this.add(bunchRadio);
		this.separateRadio = new JRadioButton();
		separateRadio.setMaximumSize(new Dimension(80, 50));
		separateRadio.setActionCommand("separate");
		separateRadio.setBackground(color);
		separateRadio.setHorizontalAlignment(JRadioButton.CENTER);
		this.add(separateRadio);
		this.btnGroup = new ButtonGroup();
		this.btnGroup.add(bunchRadio);
		this.btnGroup.add(separateRadio);
		
		this.setIsSeparatePrint(invoice.getIsSeparate());

	}

	boolean isPrint() {
		return this.checkBox.isSelected();
	}

	void setIsPrint(boolean boo) {
		this.checkBox.setSelected(boo);
	}

	boolean isSeparatePrint() {
		ButtonModel radio = this.btnGroup.getSelection();
		if (radio.getActionCommand().equals("separate")) {
			return true;
		}
		return false;
	}

	void setIsSeparatePrint(boolean boo) {
		if (boo == true) {
			this.separateRadio.setSelected(true);
		} else {
			this.bunchRadio.setSelected(true);
		}
	}

	Invoice getInvoice() {
		return this.invoice;
	}

	void changeState(boolean boo) {
		if (boo == false) {
			Color color = Color.decode("#EF9A9A");
			this.setBackground(color);
			this.checkBox.setBackground(color);
			this.bunchRadio.setBackground(color);
			this.bunchRadio.setEnabled(false);
			this.separateRadio.setBackground(color);
			this.separateRadio.setEnabled(false);
		} else {
			Color color = Color.decode("#F5F5F5");
			this.setBackground(color);
			this.checkBox.setBackground(color);
			this.bunchRadio.setBackground(color);
			this.bunchRadio.setEnabled(true);
			this.separateRadio.setBackground(color);
			this.separateRadio.setEnabled(true);
		}
	}
}
