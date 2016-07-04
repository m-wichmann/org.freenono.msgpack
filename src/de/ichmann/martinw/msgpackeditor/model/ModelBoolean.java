package de.ichmann.martinw.msgpackeditor.model;

public class ModelBoolean extends ModelBaseValue {
	private boolean value;
	
	public ModelBoolean(boolean value) {
		super(ModelValueType.BOOLEAN);
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.value ? "true" : "false";
	}
}
