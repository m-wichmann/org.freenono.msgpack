package de.ichmann.martinw.msgpackeditor.model;

public class ModelExtension extends ModelBaseValue {
	private byte[] value;
	private byte type;
	
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
