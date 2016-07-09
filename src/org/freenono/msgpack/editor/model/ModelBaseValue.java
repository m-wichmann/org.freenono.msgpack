package org.freenono.msgpack.editor.model;

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
