package org.freenono.msgpack.editor.model;

public class ModelString extends ModelBaseValue {
	private String value;

	public ModelString() {
		super(ModelValueType.STRING);
		this.value = "";
	}
	
	public ModelString(String value) {
		super(ModelValueType.STRING);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
