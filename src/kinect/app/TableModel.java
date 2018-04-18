package kinect.app;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class TableModel extends AbstractTableModel {

	private ArrayList<MyTableData> td;
	private String columnName[] = { "Body Part", "xPos", "yPos" };

	public TableModel() {
		td = new ArrayList<>(20);
	}

	public void addData(MyTableData tableData) {
		td.add(tableData);
		fireTableRowsInserted(td.size() - 1, td.size() - 1);
	}
	
	public void deleteData() {
		int rows = getRowCount();
		
		if (rows == 0) {
			return;
		}
		
		td.clear();
	}

	@Override
	public int getColumnCount() {
		return columnName.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnName[column];
	}

	@Override
	public int getRowCount() {
		return td.size();
	}

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
	
	public ArrayList<MyTableData> getTableData() {
		return td;
	}

}
