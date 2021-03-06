package org.freenono.msgpack.editor.model;

public class ModelInteger extends ModelBaseValue {
	private long value;
	
	public ModelInteger() {
		super(ModelValueType.INTEGER);
		this.value = 0;
	}
	
	public ModelInteger(long value) {
		super(ModelValueType.INTEGER);
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return Long.toString(this.value);
	}
}
