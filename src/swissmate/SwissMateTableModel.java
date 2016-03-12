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

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SwissMateTableModel extends DefaultTableModel {
	
	private ArrayList<Color> rowColors;
	
	public SwissMateTableModel(String[][] data, String[] columnNames) {
		
		super(data, columnNames);
		
		rowColors = new ArrayList<Color>(data.length);
		for(int rowIndex = 0; rowIndex < data.length; rowIndex++) {
			rowColors.add(Color.WHITE);
		}
		
	}
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public Color getRowColor(int row) {
		return rowColors.get(row);
	}
	
	public void setRowColor(int row, Color color) {
		rowColors.set(row, color);
	}

}
