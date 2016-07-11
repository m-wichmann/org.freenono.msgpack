package org.freenono.msgpack.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.EditorPart;
import org.freenono.msgpack.editor.model.ModelBaseValue;
import org.freenono.msgpack.editor.model.ModelFloat;
import org.freenono.msgpack.editor.model.ModelInteger;
import org.freenono.msgpack.editor.model.ModelString;
import org.freenono.msgpack.editor.model.ModelBaseValue.ModelValueType;
import org.freenono.msgpack.editor.model.ModelBoolean;

public class MsgPackCellEditingSupport extends EditingSupport {

	// TODO: dispose of CellEditors
	
	private String[] booleanComboElements = new String[] {"True", "False"};
	private MsgPackEditor editor;
	
    public MsgPackCellEditingSupport(ColumnViewer viewer, MsgPackEditor editor) {
		super(viewer);
		this.editor = editor;
	}

	@Override
    protected void setValue(Object element, Object rawEditValue) {
		String editValueString;
		Integer editValueInteger;
		boolean valueChanged = false;
		
        if (element instanceof ModelBaseValue)
        {
        	ModelBaseValue modelValue = (ModelBaseValue) element;
        	ModelValueType valueType = modelValue.getValueType();
        	
			switch (valueType) {
				case BOOLEAN:
					editValueInteger = (Integer) rawEditValue;
					ModelBoolean booleanValue = (ModelBoolean) modelValue;
					Boolean rawBooleanValue = new Boolean((editValueInteger == 0) ? true : false);
					if (rawBooleanValue != null) {
						if (!rawBooleanValue.equals(booleanValue.getValue())) {
							valueChanged = true;	
						}
						booleanValue.setValue(rawBooleanValue);
					}
					break;

				case FLOAT:
					editValueString = (String) rawEditValue;
					ModelFloat floatValue = (ModelFloat) modelValue;
					try {
						Double rawFloatValue = new Double(editValueString);
						if (rawFloatValue != null) {
							if (!rawFloatValue.equals(floatValue.getValue())) {
								valueChanged = true;	
							}
							floatValue.setValue(rawFloatValue);
						}
					} catch (NumberFormatException e) {
						/* Just don't set the value */
					}
					break;

				case INTEGER:
					editValueString = (String) rawEditValue;
					ModelInteger integerValue = (ModelInteger) modelValue;
					try {
						// TODO: parse hex and binary radix (0x, 0b)
						Long rawIntegerValue = new Long(editValueString);
						if (rawIntegerValue != null) {
							if (!rawIntegerValue.equals(integerValue.getValue())) {
								valueChanged = true;	
							}
							integerValue.setValue(rawIntegerValue);
						}
					} catch (NumberFormatException e) {
						/* Just don't set the value */
					}
					break;

				case STRING:
					editValueString = (String) rawEditValue;
					ModelString stringValue = (ModelString) modelValue;
					if (editValueString != null) {
						if (!editValueString.equals(stringValue.getValue())) {
							valueChanged = true;	
						}
						stringValue.setValue(editValueString);
					}
					break;

				case BINARY:
				case EXTENSION:
					// TODO: implement editing
					break;

				case NIL:
				case MAP:
				case ARRAY:
				default:
					break;
			}
		}
        
        if (valueChanged) {
        	editor.setDirty();
        }
        
        this.getViewer().update(element, null);
    }

    @Override
    protected Object getValue(Object element) {
    	if (element instanceof ModelBaseValue) {
    		ModelBaseValue modelValue = (ModelBaseValue) element;
    		ModelValueType valueType = modelValue.getValueType();
    		
    		switch (valueType) {
				case BOOLEAN:
					ModelBoolean booleanValue = (ModelBoolean) modelValue;
					return booleanValue.getValue() ? 0 : 1;

				case FLOAT:
				case INTEGER:
				case STRING:
					return element.toString();

				case ARRAY:
				case BINARY:
				case EXTENSION:
				case MAP:
				case NIL:
				default:
					return null;
    		}
    	}
    	
    	return null;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
    	// TODO: store Editors to dispose them later
    	
    	if (element instanceof ModelBaseValue) {
    		ModelValueType valueType = ((ModelBaseValue) element).getValueType();
    		
    		switch (valueType) {
				case BOOLEAN:
					return new ComboBoxCellEditor(((TreeViewer) this.getViewer()).getTree(), booleanComboElements);
					
				case FLOAT:
				case INTEGER:
				case STRING:
					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());

				case BINARY:
				case EXTENSION:
					// TODO: implement editing
					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());

				case NIL:
				case MAP:
				case ARRAY:
				default:
					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());
    		}
    	}
    	
        return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
    	if (element instanceof ModelBaseValue) {
    		ModelValueType valueType = ((ModelBaseValue) element).getValueType();
    		
    		switch (valueType) {
				case BOOLEAN:
				case FLOAT:
				case INTEGER:
				case STRING:
					return true;

				case BINARY:
				case EXTENSION:
					// TODO: implement editing
					return false;

				case NIL:
				case MAP:
				case ARRAY:
				default:
					return false;
    		}
    	}
    	
    	return false;
    }
}
