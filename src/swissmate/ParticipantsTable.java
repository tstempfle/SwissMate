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

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import swtlib.tournament.Player;

@SuppressWarnings("serial")
public class ParticipantsTable extends JScrollPane {
	
	private static final String[] participantsColumnNames = {"No", "Name", "Title", "Elo", "Nat", "Club"};
	
	ParticipantsTable(ArrayList<Player> players) {
		
		JTable table = new JTable();
		SwissMateTableModel tableModel = getParticipantsData(players);
		
		table.setModel(tableModel);
		
		SwissMateTableCellRenderer leftAlignedRenderer = new SwissMateTableCellRenderer(false);
		SwissMateTableCellRenderer centerAlignedRenderer = new SwissMateTableCellRenderer(true);
		
		table.getColumnModel().getColumn(0).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(leftAlignedRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(centerAlignedRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(leftAlignedRenderer);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(230);
		table.getColumnModel().getColumn(2).setPreferredWidth(40);
		table.getColumnModel().getColumn(3).setPreferredWidth(40);
		table.getColumnModel().getColumn(4).setPreferredWidth(40);
		table.getColumnModel().getColumn(5).setPreferredWidth(230);
		
		table.setFillsViewportHeight(true);
		
		setViewportView(table);
		
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setPreferredSize(table.getPreferredScrollableViewportSize());
		
	}
	
	private SwissMateTableModel getParticipantsData(ArrayList<Player> players) {
		
		String[][] participantsData = new String[players.size()][6];
		
		for(int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			
			Player player = players.get(playerIndex);
			
			participantsData[playerIndex][0] = String.valueOf(player.getStartRank());
			participantsData[playerIndex][1] = player.getName();
			participantsData[playerIndex][2] = player.getTitle();
			participantsData[playerIndex][3] = Integer.toString(player.getElo());
			participantsData[playerIndex][4] = Integer.toString(player.getNationalRating());
			participantsData[playerIndex][5] = player.getClub();
			
		}
		
		SwissMateTableModel model = new SwissMateTableModel(participantsData, participantsColumnNames);
		
		for(int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			
			if(!players.get(playerIndex).isActive()) {
				model.setRowColor(playerIndex, Color.LIGHT_GRAY);
			}
			
		}
		
		return model;
		
	}

}
