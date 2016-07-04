package de.ichmann.martinw.msgpackeditor.model;

public class ModelNil extends ModelBaseValue {

	public ModelNil() {
		super(ModelValueType.NIL);
	}
	
	@Override
	public String toString() {
		return "NIL";
	}
}
