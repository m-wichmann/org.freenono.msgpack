package org.freenono.msgpack.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.freenono.msgpack.editor.model.ModelBaseValue;
import org.freenono.msgpack.editor.model.ModelFloat;
import org.freenono.msgpack.editor.model.ModelInteger;
import org.freenono.msgpack.editor.model.ModelString;
import org.freenono.msgpack.editor.model.ModelBaseValue.ModelValueType;
import org.freenono.msgpack.editor.model.ModelBoolean;

public class MsgPackCellEditingSupport extends EditingSupport {

	// TODO: dispose of CellEditors
	
    public MsgPackCellEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
    protected void setValue(Object element, Object rawEditValue) {
		// TODO: Long etc. constructor throws NumberFormatException
		
        if (element instanceof ModelBaseValue)
        {
        	ModelBaseValue modelValue = (ModelBaseValue) element;
        	ModelValueType valueType = modelValue.getValueType();
        	String editValue = (String) rawEditValue;
        	
			switch (valueType) {
				case BOOLEAN:
					ModelBoolean booleanValue = (ModelBoolean) modelValue;
					Boolean rawBooleanValue = new Boolean(editValue);
					if (rawBooleanValue != null) {
						booleanValue.setValue(rawBooleanValue);
					}
					break;

				case FLOAT:
					ModelFloat floatValue = (ModelFloat) modelValue;
					Double rawFloatValue = new Double(editValue);
					if (rawFloatValue != null) {
						floatValue.setValue(rawFloatValue);
					}
					break;

				case INTEGER:
					ModelInteger integerValue = (ModelInteger) modelValue;
					Long rawIntegerValue = new Long(editValue);
					if (rawIntegerValue != null) {
						integerValue.setValue(rawIntegerValue);
					}
					break;

				case STRING:
					ModelString stringValue = (ModelString) modelValue;
					if (editValue != null) {
						stringValue.setValue(editValue);
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
        
        this.getViewer().update(element, null);
        // TODO: set dirty flag in editor and fire event
    }

    @Override
    protected Object getValue(Object element) {
        return element.toString();
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
    	// TODO: store Editors to dispose them later
    	
//    	if (element instanceof ModelBaseValue) {
//    		ModelValueType valueType = ((ModelBaseValue) element).getValueType();
//    		
//    		switch (valueType) {
//				case BOOLEAN:
//					String[] comboElements = new String[] {"true", "false"};
//					return new ComboBoxCellEditor(((TreeViewer) this.getViewer()).getTree(), comboElements);
//					
//				case FLOAT:
//				case INTEGER:
//				case STRING:
//					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());
//
//				case BINARY:
//				case EXTENSION:
//					// TODO: implement editing
//					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());
//
//				case NIL:
//				case MAP:
//				case ARRAY:
//				default:
//					return new TextCellEditor(((TreeViewer) this.getViewer()).getTree());
//    		}
//    	}
    	
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
