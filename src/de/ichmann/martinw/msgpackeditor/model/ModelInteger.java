package de.ichmann.martinw.msgpackeditor.model;

public class ModelInteger extends ModelBaseValue {
	private long value;
	
	public ModelInteger(long value) {
		super(ModelValueType.INTEGER);
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Long.toString(this.value);
	}
}
