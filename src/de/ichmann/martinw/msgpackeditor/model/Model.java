package de.ichmann.martinw.msgpackeditor.model;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.BinaryValue;
import org.msgpack.value.BooleanValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.FloatValue;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.StringValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;

public class Model {
	private ArrayList<ModelBaseValue> topLevel = new ArrayList<>();
	
	public Model(ArrayList<Value> values) {
		// TODO
	}

	public Model(IFile inputFile) throws CoreException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputFile.getContents());
		ArrayList<Value> valueList = new ArrayList<Value>();
	
		try {
			while (true) {
				Value val = unpacker.unpackValue();
				valueList.add(val);
			}
		} catch (MessageInsufficientBufferException | IOException e) {
			/* end loop */
		}
		
		for (Value value : valueList) {
			topLevel.add(convValue(value));
		}
	}
	
	public ArrayList<Value> toMsgpackValue() {
		// TODO
		return new ArrayList<>();
	}
	
	public void toMsgpackValue(IFile outputFile) {
		
	}
	
	public Object[] getTopLevel() {
		return topLevel.toArray();
	}
	
	private ModelBaseValue convValue(Value value) {
		ValueType tempValueType = value.getValueType();
		
		switch (tempValueType) {
			case NIL:
				return new ModelNil();
			case BINARY:
				return new ModelBinary(value.asBinaryValue().asByteArray());
			case BOOLEAN:
				return new ModelBoolean(value.asBooleanValue().getBoolean());
			case EXTENSION:
				ExtensionValue extensionValue = value.asExtensionValue();
				return new ModelExtension(extensionValue.getData(), extensionValue.getType());
			case FLOAT:
				return new ModelFloat(value.asFloatValue().toDouble());
			case INTEGER:
				return new ModelInteger(value.asIntegerValue().toLong());
			case STRING:
				return new ModelString(value.asStringValue().asString());
				
			case ARRAY:
				ArrayValue arrayValue = value.asArrayValue();
				ArrayList<ModelBaseValue> newArrayValue = new ArrayList<ModelBaseValue>();
				for (Value arrayElement : arrayValue) {
					newArrayValue.add(convValue(arrayElement));
				}
				return new ModelArray(newArrayValue);
				
			case MAP:
				MapValue mapValue = value.asMapValue();
				ArrayList<ModelBaseValue> newMapValue = new ArrayList<ModelBaseValue>();
				
				for (Value mapElement : mapValue.getKeyValueArray()) {
					newMapValue.add(convValue(mapElement));
				}

				return new ModelMap(newMapValue);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		for (ModelBaseValue modelBaseValue : topLevel) {
			ret += modelBaseValue.toString();
			ret += "\n";
		}
		
		return ret;
	}
}
