package org.freenono.msgpack.editor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.freenono.msgpack.editor.model.Model;
import org.freenono.msgpack.editor.model.ModelArray;
import org.freenono.msgpack.editor.model.ModelBaseValue;
import org.freenono.msgpack.editor.model.ModelMap;

public class MsgPackModelContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof Model) {
			return ((Model) element).getTopLevel();
		}

		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof ModelBaseValue) {
			ModelBaseValue value = (ModelBaseValue) element;

			switch (value.getValueType()) {
				case ARRAY:
					return ((ModelArray) value).getValue().toArray();
				case MAP:
					return ((ModelMap) value).getValue().toArray();
				default:
					return new Object[0];
			}
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		/* getParent does not have to be implemented */
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ModelBaseValue) {
			ModelBaseValue value = (ModelBaseValue) element;

			switch (value.getValueType()) {
				case ARRAY:
					return !(((ModelArray) value).getValue().isEmpty());
				case MAP:
					return !(((ModelMap) value).getValue().isEmpty());
				default:
					return false;
			}
		}

		return false;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		/*
		 * Implementation needed, since Eclipse Neon defines these as default
		 * functions in IContentProvider, but older versions don't. So this
		 * throws a java.lang.AbstractMethodError on older versions.
		 */
	}

	@Override
	public void dispose() {
		/*
		 * Implementation needed, since Eclipse Neon defines these as default
		 * functions in IContentProvider, but older versions don't. So this
		 * throws a java.lang.AbstractMethodError on older versions.
		 */
	}
}
