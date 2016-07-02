package de.ichmann.martinw.msgpackeditor;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Label;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.BinaryValue;
import org.msgpack.value.BooleanValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.FloatValue;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.StringValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.ValueType;

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
		System.out.println(objList.length);
		
//		Value[] valueList = (Value[]) selection.toArray();
//		for (Value value : valueList) {
//			System.out.println(value);
//		}
		
//        for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//            Value value = (Value) iterator.next();
//            System.out.println(value);
//        }
		
		// TODO: get value
		Value value = ValueFactory.newInteger(42);
		ValueType type = value.getValueType();

		String labelText = "";
		
		switch (type) {
			case NIL:
				break;
				
			case ARRAY:
				ArrayValue arrayValue = value.asArrayValue();
				break;

			case BINARY:
				BinaryValue binaryValue = value.asBinaryValue();
				break;

			case BOOLEAN:
				BooleanValue booleanValue = value.asBooleanValue();
				break;

			case EXTENSION:
				ExtensionValue extensionValue = value.asExtensionValue();
				break;

			case FLOAT:
				FloatValue floatValue = value.asFloatValue();
				break;

			case INTEGER:
				IntegerValue integerValue = value.asIntegerValue();
				labelText += "Value: " + Long.toString(integerValue.asLong()) + "\n";
				labelText += "Hex: 0x" + Long.toHexString(integerValue.asLong()) + "\n";
				labelText += "Bin: 0b" + Long.toBinaryString(integerValue.asLong()) + "\n";
				break;

			case MAP:
				MapValue mapValue = value.asMapValue();
				break;

			case STRING:
				StringValue stringValue = value.asStringValue();
				labelText += stringValue.asString();
				break;
		}
	
		label.setText(labelText);
	}
}
