package org.freenono.msgpack.editor.model;

import java.util.ArrayList;

public class ModelMap extends ModelBaseValue {
	private ArrayList<ModelBaseValue> value;

	public ModelMap() {
		super(ModelValueType.MAP);
		this.value = new ArrayList<ModelBaseValue>();
	}
	
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
	
	@Override
	public void removeElement(ModelBaseValue element) {
		/* Don't use foreach, since remove needs index */
		for (int i = 0; i < value.size(); i++) {
			if (value.get(i) == element) {
				value.remove(i);
				return;
			}
		}
		
		for (ModelBaseValue modelBaseValue : value) {
			modelBaseValue.removeElement(element);
		}
	}
}
