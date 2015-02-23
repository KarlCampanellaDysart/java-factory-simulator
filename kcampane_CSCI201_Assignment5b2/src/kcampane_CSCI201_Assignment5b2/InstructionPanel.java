package kcampane_CSCI201_Assignment5b2;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InstructionPanel extends JPanel{		
	JTextField numUse[] = new JTextField[2];
	JComboBox toolboxes[] = new JComboBox[2];
	JComboBox machineBoxes; 
	JTextField seconds = new JTextField();
	OrderForm of;
	public InstructionPanel(OrderForm of){
		this.of = of;
		setPreferredSize(new Dimension(500,100));
		setMaximumSize(new Dimension(500,75));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setVisible(true);
		for(int i = 0;i<2;i++){
			numUse[i] = new JTextField();
			toolboxes[i] = new JComboBox(of.tools);
		}
		machineBoxes = new JComboBox(of.machines);
		
		JPanel usePanel = new JPanel();
		usePanel.setLayout(new BoxLayout(usePanel, BoxLayout.Y_AXIS));
		usePanel.add(numUse[0]);
		usePanel.add(numUse[1]);
		add(usePanel);
		
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
		toolPanel.add(toolboxes[0]);
		toolPanel.add(toolboxes[1]);
		add(toolPanel);
		
		add(new JLabel("At"));
		add(machineBoxes);
		add(new JLabel("For"));
		add(seconds);
		add(new JLabel("s"));
	}
}