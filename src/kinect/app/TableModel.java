package kinect.app;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class TableModel extends AbstractTableModel {
	
	// field definitions
	private ArrayList<MyTableData> td; // holds table data
	// table column headings
	private String columnName[] = { "Body Part", "xPos", "yPos" };
	
	/* the constructor creates an array list 
	 * ready to hold data.*/
	public TableModel() {
		td = new ArrayList<>(20);
	}
	
	/* used to add data to the TableModel */ 
	public void addData(MyTableData tableData) {
		td.add(tableData);
		fireTableRowsInserted(td.size() - 1, td.size() - 1);
	}
	
	/* deletes data from table model */
	public void deleteData() {
		int rows = getRowCount();
		
		if (rows == 0) {
			return;
		}
		
		td.clear();
	}
	
	/* get the number of columns 
	 * from columnName array
	 */
	@Override
	public int getColumnCount() {
		return columnName.length;
	}
	
	/* get the names of the columns
	 * from columnName array
	 */
	@Override
	public String getColumnName(int column) {
		return columnName[column];
	}

	/* get size of rows 
	 * row size = size of array list
	 */
	@Override
	public int getRowCount() {
		return td.size();
	}
	
	/* gets the arrayIndex of array list and set to row 
	 * index. For each item in array list. assign each 
	 * field to a field in the table. 
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MyTableData tableData = td.get(rowIndex);
		Object value = null;
		switch (columnIndex) {
		case 0:
			value = tableData.getBodyPart();
			break;
		case 1:
			value = tableData.getXPos();
			break;
		case 2:
			value = tableData.getYPos();
		}
		return value;
	}
	
	/* used to return the table data. */
	public ArrayList<MyTableData> getTableData() {
		return td;
	}

}
