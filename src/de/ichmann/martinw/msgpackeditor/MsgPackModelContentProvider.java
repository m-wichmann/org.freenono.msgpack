package de.ichmann.martinw.msgpackeditor;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.ichmann.martinw.msgpackeditor.model.Model;
import de.ichmann.martinw.msgpackeditor.model.ModelArray;
import de.ichmann.martinw.msgpackeditor.model.ModelBaseValue;
import de.ichmann.martinw.msgpackeditor.model.ModelMap;

public class MsgPackModelContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object element) {
		return ((Model) element).getTopLevel();
	}
	
	@Override
	public Object[] getChildren(Object element) {
		ModelBaseValue value = (ModelBaseValue) element;
		
		switch (value.getValueType()) {
			case ARRAY:
				return ((ModelArray) value).getValue().toArray();
			case MAP:
				return ((ModelMap) value).getValue().toArray();
			default:
				return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		ModelBaseValue value = (ModelBaseValue) element;
		
		switch (value.getValueType()) {
			case ARRAY:
				return !(((ModelArray) value).getValue().isEmpty());
			case MAP:
				return !(((ModelMap) value).getValue().isEmpty());
			default:
				return false;
		}
	}

}
