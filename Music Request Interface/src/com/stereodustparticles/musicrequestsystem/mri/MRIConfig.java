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
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class MRIConfig extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7026887374037228189L;
	private final JPanel contentPanel = new JPanel();
	private final String[] mrsVersions = {"2.3","2.4"};
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
			MRIConfig dialog = new MRIConfig(new MRSInterface("","",""),new MRIWindow());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	//Get rid of nonsense errors that no one needs to care about, ever...
	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	public MRIConfig(MRSInterface mrs,MRIWindow mri) {
		setTitle("Configuration");
		setBounds(100, 100, 450, 223);
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
		url.setText(mrs.getConfig("URL"));
		url.setBounds(120, 8, 304, 28);
		contentPanel.add(url);
		url.setColumns(10);
		
		key = new JPasswordField();
		key.setText(mrs.getConfig("Key"));
		key.setColumns(10);
		key.setBounds(120, 41, 304, 30);
		contentPanel.add(key);
		
		JLabel lblRefreshAfter = new JLabel("Refresh after:");
		lblRefreshAfter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRefreshAfter.setBounds(10, 120, 100, 14);
		contentPanel.add(lblRefreshAfter);
		
		JSpinner reftime = new JSpinner();
		reftime.setModel(new SpinnerNumberModel(mri.getRefTime(), 5, 600, 5));
		reftime.setBounds(120, 113, 60, 28);
		contentPanel.add(reftime);
		
		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setHorizontalAlignment(SwingConstants.LEFT);
		lblSeconds.setBounds(192, 120, 100, 14);
		contentPanel.add(lblSeconds);
		
		JLabel lblMrsVersion = new JLabel("MRS version:");
		lblMrsVersion.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMrsVersion.setBounds(10, 87, 100, 14);
		contentPanel.add(lblMrsVersion);
		
		JComboBox mrsVersionSelect = new JComboBox();
		mrsVersionSelect.setModel(new DefaultComboBoxModel(mrsVersions));
		int indexOf = 0;
		for(int i = 0; i < mrsVersions.length; i++) {
			indexOf = i;
			if(mrsVersions[i] == mrs.getConfig("Version")) {
				break;
			}
		}
		mrsVersionSelect.setSelectedIndex(indexOf);
		mrsVersionSelect.setBounds(120, 83, 56, 26);
		contentPanel.add(mrsVersionSelect);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						mrs.changeConfig(url.getText(),String.valueOf(key.getPassword()),mrsVersionSelect.getSelectedItem().toString());
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
