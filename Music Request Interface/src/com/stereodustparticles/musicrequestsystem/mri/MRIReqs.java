package com.stereodustparticles.musicrequestsystem.mri;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MRIReqs extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3671459925839023046L;
	private JPanel contentPane;
	private ArrayList<Request> reqs;
	private JButton last;
	private JButton previous;
	private JButton next;
	private JButton first;
	private JTextArea req;
	private int currentReq;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MRIReqs frame = new MRIReqs();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MRIReqs(ArrayList<Request> r) {
		setTitle("All requests on the system");
		reqs = r;
		currentReq = 0;
		
		MRIReqs frame = this;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		req = new JTextArea();
		req.setWrapStyleWord(true);
		req.setLineWrap(true);
		req.setEditable(false);
		req.setBounds(6, 6, 422, 220);
		contentPane.add(req);
		
		first = new JButton("First");
		first.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(first.isEnabled() == true) {
					try {
						frame.first();
					} catch (OneJobException e) {
						JOptionPane.showMessageDialog(contentPane,"Something wicked just happened. Tell the MRI it only had ONE JOB and try again.\n\nDetails: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		first.setEnabled(false);
		first.setBounds(16, 228, 90, 28);
		contentPane.add(first);
		
		next = new JButton("Next");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(next.isEnabled() == true) {
					try {
						frame.next();
					} catch (OneJobException e) {
						JOptionPane.showMessageDialog(contentPane,"Something wicked just happened. Tell the MRI it only had ONE JOB and try again.\n\nDetails: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		next.setEnabled(false);
		next.setBounds(220, 228, 90, 28);
		contentPane.add(next);
		
		previous = new JButton("Previous");
		previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(previous.isEnabled() == true) {
					try {
						frame.previous();
					} catch (OneJobException e) {
						JOptionPane.showMessageDialog(contentPane,"Something wicked just happened. Tell the MRI it only had ONE JOB and try again.\n\nDetails: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		previous.setEnabled(false);
		previous.setBounds(118, 228, 90, 28);
		contentPane.add(previous);
		
		last = new JButton("Last");
		last.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(last.isEnabled() == true) {
					try {
						frame.last();
					} catch (OneJobException e) {
						JOptionPane.showMessageDialog(contentPane,"Something wicked just happened. Tell the MRI it only had ONE JOB and try again.\n\nDetails: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		last.setEnabled(false);
		last.setBounds(322, 228, 90, 28);
		contentPane.add(last);
		
		if(reqs.size() > 0) {
			req.setText(reqs.get(0).toString());
			if(reqs.size() > 1) {
				next.setEnabled(true);
				last.setEnabled(true);
			}
		}
		else {
			JOptionPane.showMessageDialog(contentPane,"There are no requests to show. This could indicate\nthat there is a problem. If there should be\nrequests, contact the software vendor.","No requests",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public MRIReqs() {
		this(new ArrayList<Request>());
	}
	
	private void first() throws OneJobException {
		try {
			currentReq = 0;
			req.setText(reqs.get(0).toString());
			if(reqs.size() > 1) {
				last.setEnabled(true);
				next.setEnabled(true);
			}
			previous.setEnabled(false);
			first.setEnabled(false);
		}
		catch(Exception e) {
			throw new OneJobException(e.getMessage());
		}
	}
	
	private void previous() throws OneJobException {
		try {
			currentReq = Math.max(0,currentReq-1);
			req.setText(reqs.get(currentReq).toString());
			if(reqs.size() > 1 && currentReq < reqs.size()-1) {
				last.setEnabled(true);
				next.setEnabled(true);
			}
			if(currentReq <= 0) {
				previous.setEnabled(false);
				first.setEnabled(false);
			}
		}
		catch(Exception e) {
			throw new OneJobException(e.getMessage());
		}
	}
	
	private void next() throws OneJobException {
		try {
			currentReq = Math.min(reqs.size()-1,currentReq+1);
			req.setText(reqs.get(currentReq).toString());
			if(reqs.size() > 1 && currentReq >= reqs.size()-1) {
				last.setEnabled(false);
				next.setEnabled(false);
			}
			if(currentReq < reqs.size()-1) {
				previous.setEnabled(true);
				first.setEnabled(true);
			}
		}
		catch(Exception e) {
			throw new OneJobException(e.getMessage());
		}
	}
	
	private void last() throws OneJobException {
		try {
			currentReq = reqs.size()-1;
			req.setText(reqs.get(reqs.size()-1).toString());
			if(reqs.size() > 1) {
				last.setEnabled(false);
				next.setEnabled(false);
			}
			previous.setEnabled(true);
			first.setEnabled(true);
		}
		catch(Exception e) {
			throw new OneJobException(e.getMessage());
		}
	}
}
