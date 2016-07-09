package org.freenono.msgpack.editor.model;

public class ModelFloat extends ModelBaseValue {
	
	// TODO: maybe also add float to prevent changing the value during cast?
	
	private double value;
	
	public ModelFloat(double value) {
		super(ModelValueType.FLOAT);
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Double.toString(this.value);
	}
}
