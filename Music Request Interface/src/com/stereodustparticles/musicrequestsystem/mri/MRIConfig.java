package com.stereodustparticles.musicrequestsystem.mri;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MRIConfig extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7026887374037228189L;
	private final JPanel contentPanel = new JPanel();
	private JTextField url;
	private JPasswordField key;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			MRIConfig dialog = new MRIConfig(new MRSInterface("",""),new MRIWindow());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public MRIConfig(MRSInterface mrs,MRIWindow mri) {
		setTitle("Configuration");
		setBounds(100, 100, 450, 180);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblMrsPath = new JLabel("MRS path:");
			lblMrsPath.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMrsPath.setBounds(10, 15, 100, 14);
			contentPanel.add(lblMrsPath);
		}
		{
			JLabel lblApiAccessKey = new JLabel("API access key:");
			lblApiAccessKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblApiAccessKey.setBounds(10, 49, 100, 14);
			contentPanel.add(lblApiAccessKey);
		}
		
		url = new JTextField();
		url.setText(mrs.getConfig(true));
		url.setBounds(120, 8, 304, 28);
		contentPanel.add(url);
		url.setColumns(10);
		
		key = new JPasswordField();
		key.setText(mrs.getConfig(false));
		key.setColumns(10);
		key.setBounds(120, 41, 304, 30);
		contentPanel.add(key);
		
		JLabel lblRefreshAfter = new JLabel("Refresh after:");
		lblRefreshAfter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRefreshAfter.setBounds(10, 83, 100, 14);
		contentPanel.add(lblRefreshAfter);
		
		JSpinner reftime = new JSpinner();
		reftime.setModel(new SpinnerNumberModel(mri.getRefTime(), 5, 600, 5));
		reftime.setBounds(120, 76, 60, 28);
		contentPanel.add(reftime);
		
		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setHorizontalAlignment(SwingConstants.LEFT);
		lblSeconds.setBounds(192, 83, 100, 14);
		contentPanel.add(lblSeconds);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						mrs.changeConfig(url.getText(),String.valueOf(key.getPassword()));
						mri.setRefTime((int)reftime.getValue());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}
}
