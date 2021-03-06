package org.freenono.msgpack.editor.model;

public class ModelExtension extends ModelBaseValue {
	private byte[] value;
	private byte type;
	
	public ModelExtension() {
		super(ModelValueType.EXTENSION);
		this.value = new byte[0];
		this.type = 0;
	}
	
	public ModelExtension(byte[] value, byte type) {
		super(ModelValueType.EXTENSION);
		this.value = value;
		this.type = type;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	public byte getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "Ext[" + this.type + ", " + this.value.length + " bytes]";
	}
}
