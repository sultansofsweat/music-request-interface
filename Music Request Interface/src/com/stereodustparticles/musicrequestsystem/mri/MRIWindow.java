package com.stereodustparticles.musicrequestsystem.mri;

import java.awt.EventQueue;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Toolkit;

public class MRIWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3363649285881555799L;
	private JPanel contentPane;
	private JTextArea req1;
	private JButton info1;
	private JButton queue1;
	private JButton decline1;
	private JButton played1;
	private JButton info2;
	private JButton queue2;
	private JButton decline2;
	private JButton played2;
	private JButton info3;
	private JButton queue3;
	private JButton decline3;
	private JButton played3;
	private JButton info4;
	private JButton queue4;
	private JButton decline4;
	private JButton played4;
	private JLabel additional;
	private JLabel time;
	private JButton refresh;
	private JButton control;
	private JButton system;
	private Timer t;
	private boolean stopped;
	private RequestList rl;
	private MRSInterface mrs;
	private TimerTask task;
	private JTextArea req2;
	private JTextArea req3;
	private JTextArea req4;
	private int refreshtime;

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
					MRIWindow frame = new MRIWindow();
					//frame.setIconImage(new ImageIcon(getClass().getResource("favicon.ico")).getImage());
					frame.getRequests();
					frame.getNumRequests();
					frame.setVisible(true);
					if(frame.mrs.systemStatus() == true) {
						frame.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MRIWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MRIWindow.class.getResource("/com/stereodustparticles/musicrequestsystem/mri/favicon.png")));
		setTitle("Music Request Interface");
		MRIWindow self = this;
		stopped = true;
		t = new Timer();
		rl = new RequestList();
		refreshtime = 60;
		String line;
		try {
			FileReader fileReader = new FileReader("config.mri");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] entry = line.split(",");
                if(entry.length == 2) {
                	switch(entry[0]) {
                	case "reftime":
                		refreshtime = Integer.valueOf(entry[1]);
                		break;
                	}
                }
            }   

            bufferedReader.close();
		}
		catch (Exception e2) {
			refreshtime = 60;
		}
		try {
			mrs = new MRSInterface();
		} catch (OneJobException e) {
			JOptionPane.showMessageDialog(contentPane,"Failed to initialize MRS link information.\nThrow a GPX clock radio at your MRS and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
			mrs = new MRSInterface("","");
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 550);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnConfiguration = new JMenu("Configuration");
		menuBar.add(mnConfiguration);
		
		JMenuItem mntmConfigureMri = new JMenuItem("Configure MRI");
		mntmConfigureMri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MRIConfig mc = new MRIConfig(mrs,self);
				mc.setVisible(true);
			}
		});
		mnConfiguration.add(mntmConfigureMri);
		
		JMenuItem mntmTestConfiguration = new JMenuItem("Test configuration");
		mntmTestConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mrs.getConfig(true) == "" || mrs.getConfig(false) == "") {
					JOptionPane.showMessageDialog(contentPane,"The MRI is unconfigured. Please configure it first.","Configure me!",JOptionPane.WARNING_MESSAGE);
				}
				else {
					boolean test = mrs.test();
					if(test == true) {
						JOptionPane.showMessageDialog(contentPane,"The MRS you attempted to connect to accepted the configuration.\nYou are good to go.","Success!",JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(contentPane,"The MRS you attempted to connect to did not accept the configuration.\nCheck the API access key and URL and try again.","GEE PEE EX!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		mnConfiguration.add(mntmTestConfiguration);
		
		JMenuItem mntmLoadConfiguration = new JMenuItem("Load configuration");
		mntmLoadConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser jfc = new JFileChooser();
				FileNameExtensionFilter f = new FileNameExtensionFilter("MRI Config Files (.mri)","mri");
				jfc.setFileFilter(f);
				String url = "";
				String key = "";
				String line;
				jfc.showOpenDialog(contentPane);
				if(jfc.getSelectedFile() != null) {
					try {
						FileReader fileReader = new FileReader(jfc.getSelectedFile().getPath());
			            BufferedReader bufferedReader = new BufferedReader(fileReader);

			            while((line = bufferedReader.readLine()) != null) {
			                String[] entry = line.split(",");
			                if(entry.length == 2) {
			                	switch(entry[0]) {
			                	case "url":
			                		url = entry[1];
			                		break;
			                	case "key":
			                		key = entry[1];
			                		break;
			                	case "reftime":
			                		refreshtime = Integer.valueOf(entry[1]);
			                	}
			                }
			            }   

			            bufferedReader.close();
			            mrs.changeConfig(url,key);
			            JOptionPane.showMessageDialog(contentPane,"The configuration in file:\n" + jfc.getSelectedFile().getPath() + "\nwas loaded. Please make sure to test it!","Configuration loaded",JOptionPane.WARNING_MESSAGE);
					}
					catch (ArrayIndexOutOfBoundsException e1) {
						JOptionPane.showMessageDialog(contentPane,"The MRI only had ONE JOB! Tell it so\nand try again.\n\nError: Attempt to access array index that doesn't exist: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception e2) {
						JOptionPane.showMessageDialog(contentPane,"The MRI only had ONE JOB! Tell it so\nand try again.\n\nError: Can't read file: " + jfc.getSelectedFile().getPath(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		mnConfiguration.add(mntmLoadConfiguration);
		
		JMenuItem mntmSaveConfiguration = new JMenuItem("Save configuration");
		mntmSaveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mrs.saveConfig();
					try {
						FileWriter fileWriter = new FileWriter("config.mri",true);
			            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			            bufferedWriter.write("\nreftime," + String.valueOf(refreshtime));
			            
			            bufferedWriter.close();
					}
					catch(Exception ex) {
						throw new OneJobException("Cannot save configuration: " + ex);
					}
					JOptionPane.showMessageDialog(contentPane,"Successfully saved configuration. It is in \"config.mri\",\nlocated in the same folder the executable is in.","Success!",JOptionPane.INFORMATION_MESSAGE);
				} catch (OneJobException e1) {
					JOptionPane.showMessageDialog(contentPane,"Failed to save configuration. Stuff your filesystem into\na swimming pool and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnConfiguration.add(mntmSaveConfiguration);
		
		JMenu mnMrs = new JMenu("MRS");
		menuBar.add(mnMrs);
		
		JMenuItem mntmConfigureApi = new JMenuItem("Configure API");
		mntmConfigureApi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
			}
		});
		mnMrs.add(mntmConfigureApi);
		
		JMenuItem mntmVersionInfo = new JMenuItem("Version info");
		mntmVersionInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
			}
		});
		mnMrs.add(mntmVersionInfo);
		
		JSeparator separator_4 = new JSeparator();
		mnMrs.add(separator_4);
		
		JMenuItem mntmArchiveRequests = new JMenuItem("Archive requests");
		mntmArchiveRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
			}
		});
		mnMrs.add(mntmArchiveRequests);
		
		JMenuItem mntmDeleteRequests = new JMenuItem("Delete requests");
		mntmDeleteRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
			}
		});
		mnMrs.add(mntmDeleteRequests);
		
		JMenuItem mntmViewAllRequests = new JMenuItem("View all requests");
		mnMrs.add(mntmViewAllRequests);
		mntmViewAllRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MRIReqs mr = new MRIReqs(mrs.getRequests());
				mr.setVisible(true);
			}
		});
		
		JSeparator separator_5 = new JSeparator();
		mnMrs.add(separator_5);
		
		JMenuItem mntmAdministration = new JMenuItem("Administration");
		mntmAdministration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
			}
		});
		mnMrs.add(mntmAdministration);
		
		JMenuItem mntmOpenclose = new JMenuItem("Open/close");
		mntmOpenclose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int code = mrs.changeSystemStatus();
					if(code == 200) {
						JOptionPane.showMessageDialog(contentPane,"Successfully opened/closed system.","Success!",JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(contentPane,"Failed to open/close system. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(contentPane,"Failed to open/close system.\nMicrowave the program and try again.\n\nError: " + ex.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
				}
				if(mrs.systemStatus() == true && stopped == true) {
					start();
				}
				else if(mrs.systemStatus() == false && stopped == false) {
					stop();
				}
			}
		});
		mnMrs.add(mntmOpenclose);
		
		JMenu mnRequests = new JMenu("MRI");
		menuBar.add(mnRequests);
		
		JMenuItem mntmClearAllRequests = new JMenuItem("Clear all requests");
		mntmClearAllRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int perform = JOptionPane.showConfirmDialog(contentPane,"This will mark all requests currently displayed as played.\nNOTE: it does NOT deal with the additional waiting requests!\n\nAre you sure you want to do this?","Confirm clearing requests",JOptionPane.YES_NO_OPTION);
				if(perform == JOptionPane.YES_OPTION) {
					try {
						if(rl.get(0) != null) {
							try {
								int code = mrs.mark(rl.get(0).getID());
								if(code == 200) {
									queue1.setEnabled(false);
									decline1.setEnabled(false);
									played1.setEnabled(false);
									info1.setEnabled(false);
									req1.setVisible(false);
									rl.remove(0);
								}
								else {
									JOptionPane.showMessageDialog(contentPane,"Failed to mark request 1 as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane,"Failed to mark request 1 as played.\nMicrowave the program and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
							}
						}
						if(rl.get(1) != null) {
							try {
								int code = mrs.mark(rl.get(1).getID());
								if(code == 200) {
									queue2.setEnabled(false);
									decline2.setEnabled(false);
									played2.setEnabled(false);
									info2.setEnabled(false);
									req2.setVisible(false);
									rl.remove(1);
								}
								else {
									JOptionPane.showMessageDialog(contentPane,"Failed to mark request 2 as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane,"Failed to mark request 2 as played.\nMicrowave the program and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
							}
						}
						if(rl.get(2) != null) {
							try {
								int code = mrs.mark(rl.get(2).getID());
								if(code == 200) {
									queue3.setEnabled(false);
									decline3.setEnabled(false);
									played3.setEnabled(false);
									info3.setEnabled(false);
									req3.setVisible(false);
									rl.remove(2);
								}
								else {
									JOptionPane.showMessageDialog(contentPane,"Failed to mark request 3 as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane,"Failed to mark request 3 as played.\nMicrowave the program and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
							}
						}
						if(rl.get(3) != null) {
							try {
								int code = mrs.mark(rl.get(3).getID());
								if(code == 200) {
									queue4.setEnabled(false);
									decline4.setEnabled(false);
									played4.setEnabled(false);
									info4.setEnabled(false);
									req4.setVisible(false);
									rl.remove(3);
								}
								else {
									JOptionPane.showMessageDialog(contentPane,"Failed to mark request 4 as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane,"Failed to mark request 4 as played.\nMicrowave the program and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
							}
						}
						JOptionPane.showMessageDialog(contentPane,"Successfully marked requests as played.","Success!",JOptionPane.INFORMATION_MESSAGE);
						update();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played.\nMicrowave the program and try again.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		JMenuItem mntmRefreshRequestList = new JMenuItem("Refresh request list");
		mntmRefreshRequestList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getRequests();
				getNumRequests();
			}
		});
		mnRequests.add(mntmRefreshRequestList);
		mnRequests.add(mntmClearAllRequests);
		
		JMenuItem mntmDumpRequestsTo = new JMenuItem("Dump requests to file");
		mntmDumpRequestsTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(contentPane,"This feature is not yet available in the MRI.","Not implemented",JOptionPane.WARNING_MESSAGE);
				String serializedreqs = "";
				for(Request r : mrs.getRequests()) {
					serializedreqs += r.toString() + "\n\n";
				}
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Choose directory to save request dump to");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jfc.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					String path = new File(jfc.getSelectedFile(),"requests.req").getPath(); 
					try {
						FileWriter fw = new FileWriter(path);
						BufferedWriter bw = new BufferedWriter(fw);
						
						bw.write(serializedreqs);
						
						bw.close();
						
						JOptionPane.showMessageDialog(contentPane,"Successfully saved file to " + path + ".","Success!",JOptionPane.INFORMATION_MESSAGE);
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(contentPane,"Failed to save requests. Stuff your filesystem into\na swimming pool and try again.\n\nError: " + ex.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		mnRequests.add(mntmDumpRequestsTo);
		
		JMenuItem mntmStopCounter = new JMenuItem("Toggle counter");
		mntmStopCounter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(stopped == true) {
					start();
				}
				else {
					stop();
				}
			}
		});
		mnRequests.add(mntmStopCounter);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmAboutMri = new JMenuItem("About MRI");
		mntmAboutMri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Icon icon = new ImageIcon(MRIWindow.class.getResource("/com/stereodustparticles/musicrequestsystem/mri/icon.png"));
				JOptionPane.showMessageDialog(contentPane,"Music Request Interface\nInterface software for use with the Music Request System\n\nVersion: 1.0 BETA, release 5\n\nCopyright 2018 Brad Hunter/CarnelProd666. All rights reserved.\nDuplication prohibited except with written permission.","All your request are belong to us",JOptionPane.QUESTION_MESSAGE,icon);
			}
		});
		mnAbout.add(mntmAboutMri);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		req1 = new JTextArea();
		req1.setWrapStyleWord(true);
		req1.setLineWrap(true);
		req1.setEditable(false);
		req1.setText("The Testers-This is a test requst, requested by TEST REQUESTER");
		req1.setBounds(10, 14, 414, 45);
		req1.setVisible(false);
		contentPane.add(req1);
		
		info1 = new JButton("View info");
		info1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(info1.isEnabled()) {
					try {
						JOptionPane.showMessageDialog(contentPane,rl.get(0),"Request Information",JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to display request information.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		info1.setBounds(20, 67, 89, 23);
		info1.setEnabled(false);
		contentPane.add(info1);
		
		queue1 = new JButton("Queue");
		queue1.setBounds(119, 67, 89, 23);
		queue1.setEnabled(false);
		queue1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(queue1.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.queue(rl.get(0).getID(),response);
						if(code == 200) {
							rl.get(0).setStatus(1);
							rl.get(0).setResponse(response);
							JOptionPane.showMessageDialog(contentPane,"Successfully queued request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue1.setEnabled(false);
							decline1.setEnabled(false);
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to queue request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to queue request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(queue1);
		
		decline1 = new JButton("Decline");
		decline1.setBounds(218, 67, 89, 23);
		decline1.setEnabled(false);
		decline1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(decline1.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.decline(rl.get(0).getID(),response);
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully declined request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue1.setEnabled(false);
							decline1.setEnabled(false);
							played1.setEnabled(false);
							info1.setEnabled(false);
							req1.setVisible(false);
							rl.remove(0);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to decline request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to decline request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(decline1);
		
		played1 = new JButton("Played");
		played1.setBounds(317, 67, 89, 23);
		played1.setEnabled(false);
		played1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(played1.isEnabled()) {
					try {
						int code = mrs.mark(rl.get(0).getID());
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully marked request as played.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue1.setEnabled(false);
							decline1.setEnabled(false);
							played1.setEnabled(false);
							info1.setEnabled(false);
							req1.setVisible(false);
							rl.remove(0);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(played1);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 101, 414, 2);
		contentPane.add(separator);
		
		req2 = new JTextArea();
		req2.setWrapStyleWord(true);
		req2.setLineWrap(true);
		req2.setEditable(false);
		req2.setVisible(false);
		req2.setText("The Testers-This is a test request, requested by TEST REQUESTER");
		req2.setBounds(10, 114, 414, 45);
		contentPane.add(req2);
		
		info2 = new JButton("View info");
		info2.setBounds(20, 170, 89, 23);
		info2.setEnabled(false);
		info2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(info2.isEnabled()) {
					try {
						JOptionPane.showMessageDialog(contentPane,rl.get(1),"Request Information",JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to display request information.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(info2);
		
		queue2 = new JButton("Queue");
		queue2.setBounds(119, 170, 89, 23);
		queue2.setEnabled(false);
		queue2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(queue2.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.queue(rl.get(1).getID(),response);
						if(code == 200) {
							rl.get(1).setStatus(1);
							rl.get(1).setResponse(response);
							JOptionPane.showMessageDialog(contentPane,"Successfully queued request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue2.setEnabled(false);
							decline2.setEnabled(false);
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to queue request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to queue request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(queue2);
		
		decline2 = new JButton("Decline");
		decline2.setBounds(218, 170, 89, 23);
		decline2.setEnabled(false);
		decline2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(decline2.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.decline(rl.get(1).getID(),response);
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully declined request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue2.setEnabled(false);
							decline2.setEnabled(false);
							played2.setEnabled(false);
							info2.setEnabled(false);
							req2.setVisible(false);
							rl.remove(1);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to decline request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to decline request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(decline2);
		
		played2 = new JButton("Played");
		played2.setBounds(317, 170, 89, 23);
		played2.setEnabled(false);
		played2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(played2.isEnabled()) {
					try {
						int code = mrs.mark(rl.get(1).getID());
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully marked request as played.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue2.setEnabled(false);
							decline2.setEnabled(false);
							played2.setEnabled(false);
							info2.setEnabled(false);
							req2.setVisible(false);
							rl.remove(1);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(played2);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 204, 414, 2);
		contentPane.add(separator_1);
		
		req3 = new JTextArea();
		req3.setText("The Testers-This is a test requst, requested by TEST REQUESTER");
		req3.setBounds(10, 217, 414, 45);
		req3.setWrapStyleWord(true);
		req3.setLineWrap(true);
		req3.setEditable(false);
		req3.setVisible(false);
		contentPane.add(req3);
		
		info3 = new JButton("View info");
		info3.setBounds(20, 273, 89, 23);
		info3.setEnabled(false);
		info3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(info3.isEnabled()) {
					try {
						JOptionPane.showMessageDialog(contentPane,rl.get(2),"Request Information",JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to display request information.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(info3);
		
		queue3 = new JButton("Queue");
		queue3.setBounds(119, 273, 89, 23);
		queue3.setEnabled(false);
		queue3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(queue3.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.queue(rl.get(2).getID(),response);
						if(code == 200) {
							rl.get(2).setStatus(1);
							rl.get(2).setResponse(response);
							JOptionPane.showMessageDialog(contentPane,"Successfully queued request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue3.setEnabled(false);
							decline3.setEnabled(false);
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to queue request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to queue request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(queue3);
		
		decline3 = new JButton("Decline");
		decline3.setBounds(218, 273, 89, 23);
		decline3.setEnabled(false);
		decline3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(decline3.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.decline(rl.get(2).getID(),response);
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully declined request.","Success!",JOptionPane.INFORMATION_MESSAGE);queue2.setEnabled(false);
							queue3.setEnabled(false);
							decline3.setEnabled(false);
							played3.setEnabled(false);
							info3.setEnabled(false);
							req3.setVisible(false);
							rl.remove(2);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to decline request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to decline request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(decline3);
		
		played3 = new JButton("Played");
		played3.setBounds(317, 273, 89, 23);
		played3.setEnabled(false);
		played3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(played3.isEnabled()) {
					try {
						int code = mrs.mark(rl.get(2).getID());
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully marked request as played.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue3.setEnabled(false);
							decline3.setEnabled(false);
							played3.setEnabled(false);
							info3.setEnabled(false);
							req3.setVisible(false);
							rl.remove(2);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(played3);
		
		req4 = new JTextArea();
		req4.setText("The Testers-This is a test requst, requested by TEST REQUESTER");
		req4.setBounds(10, 320, 414, 45);
		req4.setWrapStyleWord(true);
		req4.setLineWrap(true);
		req4.setEditable(false);
		req4.setVisible(false);
		contentPane.add(req4);
		
		info4 = new JButton("View info");
		info4.setBounds(20, 376, 89, 23);
		info4.setEnabled(false);
		info4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(info4.isEnabled()) {
					try {
						JOptionPane.showMessageDialog(contentPane,rl.get(3),"Request Information",JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to display request information.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(info4);
		
		queue4 = new JButton("Queue");
		queue4.setBounds(119, 376, 89, 23);
		queue4.setEnabled(false);
		queue4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(queue4.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.queue(rl.get(3).getID(),response);
						if(code == 200) {
							rl.get(3).setStatus(1);
							rl.get(3).setResponse(response);
							JOptionPane.showMessageDialog(contentPane,"Successfully queued request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue4.setEnabled(false);
							decline4.setEnabled(false);
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to queue request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to queue request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(queue4);
		
		decline4 = new JButton("Decline");
		decline4.setBounds(218, 376, 89, 23);
		decline4.setEnabled(false);
		decline4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(decline4.isEnabled()) {
					try {
						String response = JOptionPane.showInputDialog("Please enter your comment, or press OK for no comment.");
						int code = mrs.decline(rl.get(3).getID(),response);
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully declined request.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue4.setEnabled(false);
							decline4.setEnabled(false);
							played4.setEnabled(false);
							info4.setEnabled(false);
							req4.setVisible(false);
							rl.remove(3);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to decline request. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to decline request.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(decline4);
		
		played4 = new JButton("Played");
		played4.setBounds(317, 376, 89, 23);
		played4.setEnabled(false);
		played4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(played4.isEnabled()) {
					try {
						int code = mrs.mark(rl.get(3).getID());
						if(code == 200) {
							JOptionPane.showMessageDialog(contentPane,"Successfully marked request as played.","Success!",JOptionPane.INFORMATION_MESSAGE);
							queue4.setEnabled(false);
							decline4.setEnabled(false);
							played4.setEnabled(false);
							info4.setEnabled(false);
							req4.setVisible(false);
							rl.remove(3);
							update();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane,"Failed to mark request as played.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		contentPane.add(played4);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 410, 414, 2);
		contentPane.add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(10, 307, 414, 2);
		contentPane.add(separator_3);
		
		JLabel lblAdditionalRequests = new JLabel("Additional requests:");
		lblAdditionalRequests.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdditionalRequests.setBounds(10, 423, 128, 14);
		contentPane.add(lblAdditionalRequests);
		
		additional = new JLabel("9999999999");
		additional.setBounds(148, 423, 80, 14);
		contentPane.add(additional);
		
		JLabel lblTimeToNext = new JLabel("Time to next refresh:");
		lblTimeToNext.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTimeToNext.setBounds(223, 423, 128, 14);
		contentPane.add(lblTimeToNext);
		
		time = new JLabel("99");
		time.setBounds(361, 423, 24, 14);
		contentPane.add(time);
		
		refresh = new JButton("Refresh now");
		refresh.setBounds(10, 457, 118, 23);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getRequests();
				getNumRequests();
			}
		});
		contentPane.add(refresh);
		
		control = new JButton("Stop/start timer");
		control.setBounds(138, 457, 128, 23);
		control.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(stopped == true) {
					start();
				}
				else {
					stop();
				}
			}
		});
		contentPane.add(control);
		
		system = new JButton("Open/close system");
		system.setBounds(276, 457, 148, 23);
		system.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int code = mrs.changeSystemStatus();
					if(code == 200) {
						JOptionPane.showMessageDialog(contentPane,"Successfully opened/closed system.","Success!",JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(contentPane,"Failed to open/close system. Error code is " + code + "\nConsult the MRS and hit head on a wall to\nfind out what went wrong.","Failure!",JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(contentPane,"Failed to open/close system.\nMicrowave the program and try again.\n\nError: " + e.getMessage(),"One Job Encountered!",JOptionPane.ERROR_MESSAGE);
				}
				if(mrs.systemStatus() == true && stopped == true) {
					start();
				}
				else if(mrs.systemStatus() == false && stopped == false) {
					stop();
				}
			}
		});
		contentPane.add(system);
	}
	
	public void getRequests() {
		ArrayList<Request> reqs = mrs.getRequests();
		for(Request r : reqs) {
			rl.add(r);
		}
		try {
			rl.clean();
			update();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(contentPane,"Failed to filter requests for viewing.\nSomething bad is probably about to happen.\n\nError: " + e1.getMessage(),"One Job Encountered!",JOptionPane.WARNING_MESSAGE);
		}
	}
	public void update() throws Exception {
		if(rl.get(0) != null && req1.isVisible() == false) {
			req1.setText(rl.get(0).getRequest() + ", requested by " + rl.get(0).getUser());
			req1.setVisible(true);
			info1.setEnabled(true);
			switch(rl.get(0).getStatus()) {
			case 1:
				played1.setEnabled(true);
				break;
			default:
				queue1.setEnabled(true);
				decline1.setEnabled(true);
				played1.setEnabled(true);
				break;
			}
		}
		if(rl.get(1) != null && req2.isVisible() == false) {
			req2.setText(rl.get(1).getRequest() + ", requested by " + rl.get(1).getUser());
			req2.setVisible(true);
			info2.setEnabled(true);
			switch(rl.get(1).getStatus()) {
			case 1:
				played2.setEnabled(true);
				break;
			default:
				queue2.setEnabled(true);
				decline2.setEnabled(true);
				played2.setEnabled(true);
				break;
			}
		}
		if(rl.get(2) != null && req3.isVisible() == false) {
			req3.setText(rl.get(2).getRequest() + ", requested by " + rl.get(2).getUser());
			req3.setVisible(true);
			info3.setEnabled(true);
			switch(rl.get(2).getStatus()) {
			case 1:
				played3.setEnabled(true);
				break;
			default:
				queue3.setEnabled(true);
				decline3.setEnabled(true);
				played3.setEnabled(true);
				break;
			}
		}
		if(rl.get(3) != null && req4.isVisible() == false) {
			req4.setText(rl.get(3).getRequest() + ", requested by " + rl.get(3).getUser());
			req4.setVisible(true);
			info4.setEnabled(true);
			switch(rl.get(3).getStatus()) {
			case 1:
				played4.setEnabled(true);
				break;
			default:
				queue4.setEnabled(true);
				decline4.setEnabled(true);
				played4.setEnabled(true);
				break;
			}
		}
		getNumRequests();
	}
	
	public void getNumRequests() {
		additional.setText(String.valueOf(rl.getAdditionalRequests()));
	}
	
	public void start() {
		task = new TimerTask() {
			private int count = refreshtime;
			
			public void run() {
				time.setText(Integer.toString(count));
				count--;
				if(count < 0) {
					//Update
					getRequests();
					getNumRequests();
					count = refreshtime;
				}
			}
		};
		t.schedule(task, 0, 1000);
		stopped = false;
	}
	public void stop() {
		task.cancel();
		t.purge();
		stopped = true;
	}
	
	public int getRefTime() {
		return refreshtime;
	}
	public void setRefTime(int t) {
		refreshtime = t;
	}
}
