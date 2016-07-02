package de.ichmann.martinw.msgpackeditor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

class MsgPackTreeContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ArrayList<Value>) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Value value = (Value) parentElement;
		ArrayValue arrValue = value.asArrayValue();
		return arrValue.list().toArray();

		// File file = (File) parentElement;
		// System.out.println("getChildren(" + file + ")");
		// return file.listFiles();
	}

	@Override
	public Object getParent(Object element) {
		// File file = (File) element;
		// System.out.println("getParent(" + file + ")");
		// return file.getParentFile();

		/* TODO: parent not available */
		System.out.println("getParent() not yet implemented");
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Value value = (Value) element;
		if (value.isArrayValue()) {
			return true;
		}
		return false;

		// System.out.println("hasChildren()");
		// File file = (File) element;
		// if (file.isDirectory()) {
		// return true;
		// }
		// return false;
	}
}
