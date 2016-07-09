package org.freenono.msgpack.editor.model;

public class ModelBinary extends ModelBaseValue {
	private byte[] value;
	
	public ModelBinary(byte[] value) {
		super(ModelValueType.BINARY);
		this.value = value;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "Bin[" + this.value.length + " bytes]";
	}
}
