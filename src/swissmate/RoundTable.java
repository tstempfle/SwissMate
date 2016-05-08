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

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

import swtlib.tournament.Pairing;
import swtlib.tournament.PairingResult;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

@SuppressWarnings("serial")
public class RoundTable extends JScrollPane {

	private JTable table;
	private Tournament tournament;
	private int roundIndex;
	private static final String[] pairingsColumnNames = {"Pair", "No", "Name", "Pts", "", "No", "Name", "Pts", "Result"};
	
	public RoundTable(Tournament tournament, int roundIndex) {
		
		if(roundIndex >= tournament.getRounds().size()) {
			return;
		}
		
		this.tournament = tournament;
		this.roundIndex = roundIndex;
		
		table = new JTable();
		SwissMateTableModel tableModel = new SwissMateTableModel(getPairingsData(), pairingsColumnNames);
		
		table.setModel(tableModel);
		
		SwissMateTableCellRenderer leftAlignedRenderer = new SwissMateTableCellRenderer(false);
		SwissMateTableCellRenderer centerAlignedRenderer = new SwissMateTableCellRenderer(true);
		
		table.getColumnModel().getColumn(0).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(leftAlignedRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(leftAlignedRenderer);
		table.getColumnModel().getColumn(7).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(8).setCellRenderer(centerAlignedRenderer);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.getColumnModel().getColumn(2).setPreferredWidth(230);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		table.getColumnModel().getColumn(4).setPreferredWidth(15);
		table.getColumnModel().getColumn(5).setPreferredWidth(30);
		table.getColumnModel().getColumn(6).setPreferredWidth(230);
		table.getColumnModel().getColumn(7).setPreferredWidth(30);
		table.getColumnModel().getColumn(8).setPreferredWidth(60);
		
		table.setFillsViewportHeight(true);
		
		setViewportView(table);
		
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setPreferredSize(table.getPreferredScrollableViewportSize());
		
		table.getInputMap().put(KeyStroke.getKeyStroke('1'), "whiteWins");
		table.getInputMap().put(KeyStroke.getKeyStroke('+'), "whiteWinsDefault");
		table.getInputMap().put(KeyStroke.getKeyStroke('2'), "draw");
		table.getInputMap().put(KeyStroke.getKeyStroke('5'), "draw");
		table.getInputMap().put(KeyStroke.getKeyStroke('r'), "draw");
		table.getInputMap().put(KeyStroke.getKeyStroke('0'), "blackWins");
		table.getInputMap().put(KeyStroke.getKeyStroke('-'), "blackWinsDefault");
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteResult");
		
		table.getActionMap().put("whiteWins",  new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(SingleResult.WIN, SingleResult.LOSS, false), true);
			}
		});
		table.getActionMap().put("whiteWinsDefault", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(SingleResult.WIN, SingleResult.LOSS, true), true);
			}
		});
		table.getActionMap().put("draw", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(SingleResult.DRAW, SingleResult.DRAW, false), true);
			}
		});
		table.getActionMap().put("blackWins", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(SingleResult.LOSS, SingleResult.WIN, false), true);
			}
		});
		table.getActionMap().put("blackWinsDefault", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(SingleResult.LOSS, SingleResult.WIN, true), true);
			}
		});
		table.getActionMap().put("deleteResult",  new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setResult(new PairingResult(), false);
			}
		});
	}
	
	private String[][] getPairingsData() {
		
		ArrayList<Pairing> pairings = tournament.getRounds().get(roundIndex).getPairings();
		String[][] swtPairingsData = new String[pairings.size()][9];
		
		for(int pairingIndex = 0; pairingIndex < pairings.size(); pairingIndex++) {
			
			Pairing pairing = pairings.get(pairingIndex);
			
			swtPairingsData[pairingIndex][0] = String.format("%d", pairingIndex + 1);
			swtPairingsData[pairingIndex][1] = String.format("%d", pairing.getWhitePlayer().getStartRank());
			swtPairingsData[pairingIndex][2] = pairing.getWhitePlayer().getName();
			swtPairingsData[pairingIndex][3] = pairing.getWhitePlayer().getPoints(roundIndex);
			swtPairingsData[pairingIndex][4] = "-";
			swtPairingsData[pairingIndex][5] = String.format("%d", pairing.getBlackPlayer().getStartRank());
			swtPairingsData[pairingIndex][6] = pairing.getBlackPlayer().getName();
			swtPairingsData[pairingIndex][7] = pairing.getBlackPlayer().getPoints(roundIndex);
			swtPairingsData[pairingIndex][8] = getResultString(pairing.getResult());
			
		}
		
		return swtPairingsData;
		
	}
	
	private void setResult(PairingResult result, boolean askBeforeOverwriting) {
		
		ArrayList<Pairing> pairings = tournament.getRounds().get(roundIndex).getPairings();
		int row = table.getSelectedRow();
		if(row < 0 || row >= pairings.size()) {
			return;
		}
		Pairing pairing = pairings.get(row);
		
		if(askBeforeOverwriting && (pairing.getResult().getWhiteResult() != SingleResult.NONE || pairing.getResult().getBlackResult() != SingleResult.NONE)) {
			if(JOptionPane.showConfirmDialog(table, "Overwrite result?", "Enter result", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
				return;
			}
		}
		
		pairing.setResult(result);
		tournament.updatePairings(roundIndex, pairings);
		
		table.getModel().setValueAt(getResultString(result), row, 8);
		
		if(row != pairings.size() - 1) {
			table.setRowSelectionInterval(row + 1, row + 1);
		}
		
	}
	
	private static String getResultString(PairingResult result) {
		
		if(result.getWhiteResult() != SingleResult.NONE || result.getBlackResult() != SingleResult.NONE) {
			return result.toString();
		}
		else {
			return new String();
		}
		
	}
	
}
