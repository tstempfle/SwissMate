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

import javax.swing.JScrollPane;
import javax.swing.JTable;

import swtlib.tournament.Pairing;
import swtlib.tournament.PairingResult;
import swtlib.tournament.Round;
import swtlib.tournament.SingleResult;

@SuppressWarnings("serial")
public class RoundTable extends JScrollPane {

	private static final String[] pairingsColumnNames = {"Pair", "No", "Name", "Pts", "", "No", "Name", "Pts", "Result"};
	
	public RoundTable(Round round, int roundIndex) {
		
		JTable table = new JTable();
		SwissMateTableModel tableModel = new SwissMateTableModel(getPairingsData(round, roundIndex), pairingsColumnNames);
		
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
		
	}
	
	private String[][] getPairingsData(Round round, int roundIndex) {
		
		String[][] swtPairingsData = new String[round.getPairings().size()][9];
		
		for(int pairingIndex = 0; pairingIndex < round.getPairings().size(); pairingIndex++) {
			
			Pairing pairing = round.getPairings().get(pairingIndex);
			
			swtPairingsData[pairingIndex][0] = String.format("%d", pairingIndex + 1);
			swtPairingsData[pairingIndex][1] = String.format("%d", pairing.getWhitePlayer().getStartRank());
			swtPairingsData[pairingIndex][2] = pairing.getWhitePlayer().getName();
			swtPairingsData[pairingIndex][3] = pairing.getWhitePlayer().getPoints(roundIndex);
			swtPairingsData[pairingIndex][4] = "-";
			swtPairingsData[pairingIndex][5] = String.format("%d", pairing.getBlackPlayer().getStartRank());
			swtPairingsData[pairingIndex][6] = pairing.getBlackPlayer().getName();
			swtPairingsData[pairingIndex][7] = pairing.getBlackPlayer().getPoints(roundIndex);
			PairingResult result = pairing.getResult();
			if(result.getWhiteResult() != SingleResult.NONE || result.getBlackResult() != SingleResult.NONE) {
				swtPairingsData[pairingIndex][8] = result.toString();
			}
			
		}
		
		return swtPairingsData;
		
	}
	
}
