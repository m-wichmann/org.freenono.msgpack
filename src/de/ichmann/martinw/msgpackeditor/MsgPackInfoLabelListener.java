package de.ichmann.martinw.msgpackeditor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Label;
import de.ichmann.martinw.msgpackeditor.model.ModelBaseValue;
import de.ichmann.martinw.msgpackeditor.model.ModelFloat;
import de.ichmann.martinw.msgpackeditor.model.ModelInteger;
import de.ichmann.martinw.msgpackeditor.model.ModelString;

public class MsgPackInfoLabelListener implements ISelectionChangedListener {
	
	private Label label;
	
	public MsgPackInfoLabelListener(Label label) {
		this.label = label;
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection().isEmpty()) {
			label.setText("");
			return;
		}
		
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		Object[] objList = selection.toArray();

		String labelText = "";
		if (objList.length != 1) {
			
		} else {
			ModelBaseValue modelValue = (ModelBaseValue) objList[0];

			switch (modelValue.getValueType()) {
				case ARRAY:
					//ModelArray modelArray = (ModelArray) modelValue;
					break;
					
				case BINARY:
					// TODO
					//ModelBinary modelBinary = (ModelBinary) modelValue;
					break;
					
				case BOOLEAN:
					//ModelBoolean modelBoolean = (ModelBoolean) modelValue;
					break;
					
				case EXTENSION:
					// TODO
					//ModelExtension modelExtension = (ModelExtension) modelValue;
					break;
					
				case FLOAT:
					ModelFloat modelFloat = (ModelFloat) modelValue;
					labelText += "Value: " + Double.toString(modelFloat.getValue()) + "\n";
					labelText += "Hex: 0x" + Double.toHexString(modelFloat.getValue()) + "\n";
					Double.toHexString(modelFloat.getValue());
					break;
					
				case INTEGER:
					ModelInteger modelInteger = (ModelInteger) modelValue;
					labelText += "Value: " + Long.toString(modelInteger.getValue()) + "\n";
					labelText += "Hex: 0x" + Long.toHexString(modelInteger.getValue()) + "\n";
					labelText += "Bin: 0b" + Long.toBinaryString(modelInteger.getValue()) + "\n";
					break;
					
				case MAP:
					//ModelMap modelMap = (ModelMap) modelValue;
					break;
					
				case NIL:
					break;
					
				case STRING:
					ModelString modelString = (ModelString) modelValue;
					labelText += modelString.getValue();
					break;
			}
		}
		label.setText(labelText);
	}
}
