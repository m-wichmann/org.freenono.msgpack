package org.freenono.msgpack.editor;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.freenono.msgpack.editor.model.ModelArray;
import org.freenono.msgpack.editor.model.ModelBaseValue;
import org.freenono.msgpack.editor.model.ModelBinary;
import org.freenono.msgpack.editor.model.ModelBoolean;
import org.freenono.msgpack.editor.model.ModelExtension;
import org.freenono.msgpack.editor.model.ModelFloat;
import org.freenono.msgpack.editor.model.ModelInteger;
import org.freenono.msgpack.editor.model.ModelMap;
import org.freenono.msgpack.editor.model.ModelString;
import org.freenono.msgpack.editor.model.ModelBaseValue.ModelValueType;

public class MsgPackModelCellLabelProvider extends ColumnLabelProvider {

	private Display display;
	private HashMap<ModelValueType, Image> imageMap = new HashMap<>();

	public MsgPackModelCellLabelProvider(Display display) {
		super();
		this.display = display;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ModelBaseValue) {
			ModelValueType valueType = ((ModelBaseValue) element).getValueType();

			/* Get Image from map */
			Image image = imageMap.get(valueType);
			if (image != null) {
				return image;
			}

			/* Image not in map */
			String iconPath;
			switch (valueType) {
				case NIL:
					iconPath = "icons/nil.png";
					break;
				case ARRAY:
					iconPath = "icons/array.png";
					break;
				case BINARY:
					iconPath = "icons/binary.png";
					break;
				case BOOLEAN:
					iconPath = "icons/boolean.png";
					break;
				case EXTENSION:
					iconPath = "icons/extension.png";
					break;
				case FLOAT:
					iconPath = "icons/float.png";
					break;
				case INTEGER:
					iconPath = "icons/integer.png";
					break;
				case MAP:
					iconPath = "icons/map.png";
					break;
				case STRING:
					iconPath = "icons/string.png";
					break;
				default:
					iconPath = "icons/array.png";
					break;
			}

			// TODO: Let Eclipse resize the Icon!?
			URL url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path(iconPath), null);
			Image origImage = ImageDescriptor.createFromURL(url).createImage(true);
			Image resizedImage = new Image(this.display, origImage.getImageData().scaledTo(16, 16));

			/* put Image in map for later use */
			imageMap.put(valueType, resizedImage);

			return resizedImage;
		}

		return super.getImage(element);
	}
	

	@Override
	public String getText(Object element) {
		if (element instanceof ModelBaseValue) {
			ModelBaseValue value = (ModelBaseValue) element;
			ModelValueType valueType = value.getValueType();

			switch (valueType) {
				case NIL:
					return "NIL";
				case ARRAY:
					ModelArray arrayValue = (ModelArray) value;
					return "Array (" + arrayValue.getValue().size() + " elements)";
				case BINARY:
					ModelBinary binaryValue = (ModelBinary) value;
					return "Binary (" + binaryValue.getValue().length + " bytes)";
				case BOOLEAN:
					ModelBoolean booleanValue = (ModelBoolean) value;
					return booleanValue.getValue() ? "True" : "False";
				case EXTENSION:
					ModelExtension extensionValue = (ModelExtension) value;
					return "Extension (" + extensionValue.getType() + ", " + extensionValue.getValue().length + " bytes)";
				case FLOAT:
					ModelFloat floatValue = (ModelFloat) value;
					return Double.toString(floatValue.getValue());
				case INTEGER:
					ModelInteger integerValue = (ModelInteger) value;
					return Long.toString(integerValue.getValue());
				case MAP:
					ModelMap mapValue = (ModelMap) value;
					return "Map (" + mapValue.getValue().size() + " elements)";
				case STRING:
					ModelString stringValue = (ModelString) value;
					return "\"" + stringValue.getValue() + "\"";
			}
		}

		return super.getText(element);
	}

 	@Override
 	public Color getBackground(Object element) {
 		// TODO: store Color objects to dispose later
 		
		if (element instanceof ModelBaseValue) {
			ModelBaseValue value = (ModelBaseValue) element;
			ModelValueType valueType = value.getValueType();
			
			switch (valueType) {
				case ARRAY:
					return new Color(display, new RGB(255, 255, 237));
					
				case MAP:
					ModelMap modelMapValue = (ModelMap) value;
					if ((modelMapValue.getValue().size() % 2) != 0) {
						return new Color(display, new RGB(237, 97, 97));
					} else {
						return new Color(display, new RGB(255, 255, 237));	
					}
					
				default:
					break;
			}
		}
 		
 		return super.getBackground(element);
 	}

	@Override
	public void dispose() {
		super.dispose();

		for (Image image : imageMap.values()) {
			image.dispose();
		}
	}
}
