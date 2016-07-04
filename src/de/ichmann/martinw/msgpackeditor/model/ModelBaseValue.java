package de.ichmann.martinw.msgpackeditor.model;

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

public class ModelBaseValue {
	public enum ModelValueType {
		INTEGER,
		NIL,
		BOOLEAN,
		FLOAT,
		STRING,
		BINARY,
		ARRAY,
		MAP,
		EXTENSION
	}
	
	private ModelValueType valueType;
	
	public ModelBaseValue(ModelValueType valueType) {
		this.valueType = valueType;
	}
	
	public ModelValueType getValueType() {
		return valueType;
	}
	
	@Override
	public String toString() {
		switch (valueType) {
			case ARRAY:
				return "ARRAY";
			case BINARY:
				return "BINARY";
			case BOOLEAN:
				return "BOOLEAN";
			case EXTENSION:
				return "EXTENSION";
			case FLOAT:
				return "FLOAT";
			case INTEGER:
				return "INTEGER";
			case MAP:
				return "MAP";
			case NIL:
				return "NIL";
			case STRING:
				return "STRING";
			default:
				return super.toString();
		}
	}
}
