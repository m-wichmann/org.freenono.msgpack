package org.freenono.msgpack.editor.model;

public class ModelString extends ModelBaseValue {
	private String value;

	public ModelString(String value) {
		super(ModelValueType.STRING);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
