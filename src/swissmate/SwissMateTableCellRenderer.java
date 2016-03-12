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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class SwissMateTableCellRenderer extends DefaultTableCellRenderer {
	
	public SwissMateTableCellRenderer(boolean centerAligned) {
		super();
		if(centerAligned) {
			setHorizontalAlignment(JLabel.CENTER);
		}
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
        SwissMateTableModel model = (SwissMateTableModel)table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(!isSelected) {
        	c.setBackground(model.getRowColor(row));
        }
        return c;
        
    }

}
