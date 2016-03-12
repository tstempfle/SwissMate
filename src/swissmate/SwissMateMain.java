/*
 * Copyright 2015 Tobias Stempfle <tobias.stempfle@gmx.net>
 * 
 * This file is part of SwissMate.
 * 
 * SwissMate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwissMate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwissMate.  If not, see <http://www.gnu.org/licenses/>.
 */

package swissmate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.simontuffs.onejar.Boot;

import swtlib.swt.SwtFileFilter;
import swtlib.swt.SwtParser;
import swtlib.swt.SwtWriter;
import swtlib.tournament.Pairing;
import swtlib.tournament.Round;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

import swissmate.javafo.JaVaFoPairingParser;
import swissmate.trfx.TrfxWriter;

public class SwissMateMain {
	
	private static JFrame frame;
	private static JTabbedPane tabbedPane;
	
	private static File openTournamentFile;
	private static Tournament openTournament;
	
	private static JMenuItem saveMenuItem;
	private static JMenuItem saveAsMenuItem;
	
	private static JButton refreshButton;
	private static JButton saveButton;
	private static JButton removeButton;
	private static JButton nextRoundButton;
	
	private static final String openPathPreference = "openPath";
	private static final String defaultOpenPath = "C:/WinSwiss-8/";
	
	public static void main(String [] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SwissMateMain.initGui();
            }
        });  
		
	}
	
	private static void initGui() {
		
		// set off Direct3D as this may cause a rendering bug
		Properties props = System.getProperties();
		props.setProperty("sun.java2d.d3d", "false");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
		
		frame = new JFrame("SwissMate");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		ActionListener loadListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSwt();
			}
		};
		
		ActionListener refreshListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshSwt();
			}
		};
		
		ActionListener saveListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSwt();
			}
		};
		
		ActionListener saveAsListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSwtAs();
			}
		};
		
		ActionListener removeListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeLastRound();
			}
		};
		
		ActionListener nextRoundListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pairNextRound();
			}
		};
		
		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		};
		
		ActionListener aboutListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showAboutMessage();
			}
		};
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu ("?");
		
		JMenuItem openMenuItem = new JMenuItem("Open file");
		openMenuItem.addActionListener(loadListener);
		fileMenu.add(openMenuItem);
		
		fileMenu.addSeparator();
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(saveListener);
		saveMenuItem.setEnabled(false);
		fileMenu.add(saveMenuItem);
		
		saveAsMenuItem = new JMenuItem("Save as");
		saveAsMenuItem.addActionListener(saveAsListener);
		saveAsMenuItem.setEnabled(false);
		fileMenu.add(saveAsMenuItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(exitListener);
		fileMenu.add(exitMenuItem);
		
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(aboutListener);
		helpMenu.add(aboutItem);
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		JButton loadButton = new JButton(new ImageIcon(SwissMateMain.class.getResource("/icons/open.png")));
		loadButton.setToolTipText("Open file");
		loadButton.addActionListener(loadListener);
		loadButton.setFocusable(false);
		toolBar.add(loadButton);
		
		refreshButton = new JButton(new ImageIcon(SwissMateMain.class.getResource("/icons/refresh.png")));
		refreshButton.setToolTipText("Refresh file");
		refreshButton.addActionListener(refreshListener);
		refreshButton.setFocusable(false);
		refreshButton.setEnabled(false);
		toolBar.add(refreshButton);
		
		saveButton = new JButton(new ImageIcon(SwissMateMain.class.getResource("/icons/save.png")));
		saveButton.setToolTipText("Save file");
		saveButton.addActionListener(saveListener);
		saveButton.setFocusable(false);
		saveButton.setEnabled(false);
		toolBar.add(saveButton);
		
		toolBar.addSeparator();
		
		removeButton = new JButton(new ImageIcon(SwissMateMain.class.getResource("/icons/remove.png")));
		removeButton.setToolTipText("Remove the last round");
		removeButton.addActionListener(removeListener);
		removeButton.setFocusable(false);
		removeButton.setEnabled(false);
		toolBar.add(removeButton);
		
		nextRoundButton = new JButton(new ImageIcon(SwissMateMain.class.getResource("/icons/add.png")));
		nextRoundButton.setToolTipText("Add the pairings of the next round");
		nextRoundButton.addActionListener(nextRoundListener);
		nextRoundButton.setFocusable(false);
		nextRoundButton.setEnabled(false);
		toolBar.add(nextRoundButton);
		
		tabbedPane = new JTabbedPane();
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 1));
		topPanel.add(menuBar);
		topPanel.add(toolBar);
		
		frame.add(topPanel, BorderLayout.PAGE_START);
		frame.add(tabbedPane, BorderLayout.CENTER);
		
		frame.setSize(571, 689);
		frame.setResizable(false);
		frame.setMaximumSize(new Dimension(frame.getSize().width, Integer.MAX_VALUE));
		frame.setMinimumSize(frame.getSize());
		frame.setVisible(true);
		
	}
	
	private static void showAboutMessage() {
		
		String text = "SwissMate Version 1.0\n\n";
		text += "Copyright 2015 Tobias Stempfle <tobias.stempfle@gmx.net>\n";
		text += "This program comes with ABSOLUTELY NO WARRANTY.\n";
		text += "This is free software, and you are welcome to redistribute it under certain conditions.\n";
		text += "See https://www.gnu.org/licenses/gpl-3.0.html for details.\n\n";
		text += "Pairing Engine:\nJaVaFo Version 1.4 by Roberto Ricca (http://www.rrweb.org/javafo)\n\n";
		text += "Icons created by VisualPharm (http://www.visualpharm.com)";
		
		JOptionPane.showMessageDialog(frame, text);
		
	}
	
	private static void clearGui() {
		
		tabbedPane.removeAll();
		
	}
	
	private static void loadSwt() {
		
		Preferences prefs = Preferences.userNodeForPackage(SwissMateMain.class);
		
		JFileChooser fileChooser = new JFileChooser(prefs.get(openPathPreference, defaultOpenPath));
		fileChooser.setFileFilter(new SwtFileFilter());
		
		if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			openSwt(fileChooser.getSelectedFile(), true);
			prefs.put(openPathPreference, fileChooser.getSelectedFile().getParent());
		}

	}
	
	private static void refreshSwt() {
		
		openSwt(openTournamentFile, false);
		
	}
	
	private static void openSwt(File swtFile, boolean selectLastRoundPane) {
		
		Tournament tournament = null;
		try {
			tournament = SwtParser.parse(swtFile);
		} catch (IllegalStateException | IOException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "File parsing error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		clearGui();
		
		tabbedPane.add("Part.", new ParticipantsTable(tournament.getPlayers()));
		
		int roundIndex = 0;
		for(Round round : tournament.getRounds()) {
			
			if(round.getPairings().isEmpty()) {
				break;
			}
			
			tabbedPane.add("R" + (roundIndex + 1), new RoundTable(round, roundIndex));			
			roundIndex++;
			
		}
		
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		
		openTournamentFile = swtFile;
		openTournament = tournament;
		
		frame.setTitle("SwissMate - " + swtFile.getAbsolutePath());
		
		saveMenuItem.setEnabled(true);
		saveAsMenuItem.setEnabled(true);
		refreshButton.setEnabled(true);
		saveButton.setEnabled(true);
		removeButton.setEnabled(true);
		nextRoundButton.setEnabled(tabbedPane.getTabCount() - 1 < tournament.getRounds().size());
		
	}
	
	private static void saveSwt() {
		
		try {
			SwtWriter.write(openTournamentFile, openTournament);
		}
		catch(IOException | IllegalStateException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void saveSwtAs() {
		
		JFileChooser fileChooser = new JFileChooser(openTournamentFile.getAbsolutePath());
		fileChooser.setFileFilter(new SwtFileFilter());
		
		if(fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		// make sure the file has the desired .SWT extension
		File swtFile = fileChooser.getSelectedFile();
		if(!swtFile.getAbsolutePath().endsWith(".SWT")) {
			swtFile = new File(fileChooser.getSelectedFile().getAbsoluteFile() + ".SWT");
		}
		
		if(swtFile.exists()) {
			if(JOptionPane.showConfirmDialog(frame, "Overwrite file " + swtFile.getPath() + "?", "Save", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
				return;
			}
		}
		
		try {
			Files.copy(openTournamentFile.toPath(), fileChooser.getSelectedFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
			SwtWriter.write(fileChooser.getSelectedFile(), openTournament);
		}
		catch(IOException | IllegalStateException e) {
			e.printStackTrace();
			return;
		}
		
		openTournamentFile = fileChooser.getSelectedFile();
		
		frame.setTitle("SwissMate - " + swtFile.getAbsolutePath());
		
	}
	
	private static void removeLastRound() {
		
		int roundIndex = tabbedPane.getTabCount() - 2;
		
		String text = "Remove round " + (roundIndex + 1) + "?\n";
		text += "The pairings and results will get lost!";
		
		if(JOptionPane.showConfirmDialog(frame, text, "New round", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}
		
		openTournament.removePairings(roundIndex);
		tabbedPane.remove(roundIndex + 1);
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		
		int numPairedRounds = tabbedPane.getTabCount() - 1;
		if(numPairedRounds == 0) {
			removeButton.setEnabled(false);
		}
		nextRoundButton.setEnabled(true);
		
	}
	
	private static void pairNextRound() {
		
		int newRoundIndex = tabbedPane.getTabCount() - 1;
		if(newRoundIndex >= openTournament.getRounds().size()) {
			return;
		}
		
		String text = "Determine pairings of round " + (newRoundIndex + 1) + "?"; 
		if(JOptionPane.showConfirmDialog(frame, text, "New round", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}
		
		pairRound(newRoundIndex);
		
	}
	
	private static void pairRound(int newRound) {
		
		// check if there is a result for every pairing in every past round
		for(int roundIndex = 0; roundIndex < newRound; roundIndex++) {
			for(Pairing pairing : openTournament.getRounds().get(roundIndex).getPairings()) {
				if(pairing.getResult().getWhiteResult() == SingleResult.NONE && pairing.getResult().getBlackResult() == SingleResult.NONE) {
					JOptionPane.showMessageDialog(frame, "There are missing results!", "New round", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}
		
		// create a TRFX file out of the tournament
		File trfxFile = null;
		File javafoPairings = null;
		try {
			trfxFile = File.createTempFile("test", ".trfx");
			TrfxWriter.write(trfxFile, openTournament, newRound - 1);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "TRFX writer error", JOptionPane.ERROR_MESSAGE);
			if(trfxFile != null) {
				trfxFile.delete();
			}
			return;
		}
		
		try {
			javafoPairings = File.createTempFile("javafoPairings", "");
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "JaVaFo error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		try {
			callJaVaFo(trfxFile, javafoPairings, false);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "JaVaFo error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		finally {
			// clean up temporary TRFX file
			if(trfxFile != null) {
				trfxFile.delete();
			}
		}
		
		ArrayList<Pairing> pairings;
		try {
			pairings = JaVaFoPairingParser.parse(javafoPairings, openTournament);
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "JaVaFo pairings file parsing error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		finally {
			if(javafoPairings != null) {
				javafoPairings.delete();
			}
		}
		
		openTournament.updatePairings(newRound, pairings);
		
		tabbedPane.add("R" + (newRound + 1),  new RoundTable(openTournament.getRounds().get(newRound), newRound));
		tabbedPane.setSelectedIndex(newRound + 1);
		
		nextRoundButton.setEnabled(tabbedPane.getTabCount() - 1 < openTournament.getRounds().size());
		removeButton.setEnabled(true);
		
	}
	
	private static void callJaVaFo(File trfxFile, File javafoPairings, boolean weightedMatchingAlgorithm) throws IOException, InterruptedException {
		
		// build JaVaFo call
		String[] javafoArgs;
		if(weightedMatchingAlgorithm) {
			javafoArgs = new String[8];
			javafoArgs[7] = "-w";
		}
		else {
			javafoArgs = new String[9];
			javafoArgs[7] = "-q";
			javafoArgs[8] = "10000";
		}
		javafoArgs[0] = System.getProperty("java.home") + "/bin/java";
		javafoArgs[1] = "-cp";
		javafoArgs[2] = System.getProperty("java.class.path");
		javafoArgs[3] = Boot.class.getCanonicalName();
		javafoArgs[4] = trfxFile.getAbsolutePath();
		javafoArgs[5] = "-p";
		javafoArgs[6] = javafoPairings.getAbsolutePath();
		
		File processOut = null;
		RandomAccessFile processOutRaFile = null;
		
		try {
			
			// build the process for JaVaFo
			ProcessBuilder pb = new ProcessBuilder(javafoArgs);
			pb.redirectErrorStream(true);
			processOut = File.createTempFile("processOut", "");
			pb.redirectOutput(processOut);
	
			// start the process and wait for it to finish
			Process process = pb.start();
			process.waitFor();
	
			// check if there was an error
			processOutRaFile = new RandomAccessFile(processOut, "r");
			if(processOutRaFile.length() > 0) {				
				byte[] errorMessageBytes = new byte[(int)processOutRaFile.length()];
				processOutRaFile.readFully(errorMessageBytes);
				throw new IllegalStateException(new String(errorMessageBytes));
			}
			
			// if pairing was not successful, call JaVaFo again with use of the weighted matching algorithm
			if(!weightedMatchingAlgorithm && javafoPairings.length() < 4) {
				callJaVaFo(trfxFile, javafoPairings, true);
			}
			
		}
		catch(Exception e) {
			// simply pass the exception to the caller
			throw e;
		}
		finally {
			// clean up temporary process out file
			if(processOutRaFile != null) {
				try {
					processOutRaFile.close();
				}
				catch(IOException e) {}
			}
			if(processOut != null) {
				processOut.delete();
			}
		}
		
	}
	
}
