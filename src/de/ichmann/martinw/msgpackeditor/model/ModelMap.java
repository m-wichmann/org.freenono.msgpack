package de.ichmann.martinw.msgpackeditor.model;

import java.util.ArrayList;

public class ModelMap extends ModelBaseValue {
	private ArrayList<ModelBaseValue> value;
	
	public ModelMap(ArrayList<ModelBaseValue> value) {
		super(ModelValueType.MAP);
		this.value = value;
	}
	
	public ArrayList<ModelBaseValue> getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		// TODO: fix
		
		String ret = "{";
		for (ModelBaseValue modelBaseValue : value) {
			ret += modelBaseValue.toString();
			ret += ", ";
		}
		ret += "}";
		return ret;
	}
}
