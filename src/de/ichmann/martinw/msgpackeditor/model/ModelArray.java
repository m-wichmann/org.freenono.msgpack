package de.ichmann.martinw.msgpackeditor.model;

import java.util.ArrayList;

public class ModelArray extends ModelBaseValue {
	private ArrayList<ModelBaseValue> value;
	
	public ModelArray(ArrayList<ModelBaseValue> value) {
		super(ModelValueType.ARRAY);
		this.value = value;
	}	
	
	public ArrayList<ModelBaseValue> getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String ret = "[";
		for (ModelBaseValue modelBaseValue : value) {
			ret += modelBaseValue.toString();
			// TODO: fix last element
			ret += ", ";
		}
		ret += "]";
		return ret;
	}
}
