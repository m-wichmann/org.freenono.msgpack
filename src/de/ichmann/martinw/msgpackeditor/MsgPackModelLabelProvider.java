package de.ichmann.martinw.msgpackeditor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.msgpack.value.BinaryValue;

import de.ichmann.martinw.msgpackeditor.model.ModelArray;
import de.ichmann.martinw.msgpackeditor.model.ModelBaseValue;
import de.ichmann.martinw.msgpackeditor.model.ModelBinary;
import de.ichmann.martinw.msgpackeditor.model.ModelBoolean;
import de.ichmann.martinw.msgpackeditor.model.ModelExtension;
import de.ichmann.martinw.msgpackeditor.model.ModelFloat;
import de.ichmann.martinw.msgpackeditor.model.ModelInteger;
import de.ichmann.martinw.msgpackeditor.model.ModelMap;
import de.ichmann.martinw.msgpackeditor.model.ModelString;
import de.ichmann.martinw.msgpackeditor.model.ModelBaseValue.ModelValueType;

public class MsgPackModelLabelProvider extends LabelProvider {
	
	// TODO: dispose of images

	private Display display;

	public MsgPackModelLabelProvider(Display display) {
		super();
		this.display = display;
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ModelBaseValue) {
			ModelBaseValue value = (ModelBaseValue) element;
			ModelValueType valueType = value.getValueType();
			String iconPath = "icons/array.png";

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
			}

			// TODO: fix plugin ID
			URL url = FileLocator.find(Platform.getBundle("MsgPackEditor"), new Path(iconPath), null);
			Image image = ImageDescriptor.createFromURL(url).createImage(true);
			
			// TODO: Let Eclipse resize the Icon!? 
			Image resizedImage = new Image(this.display, image.getImageData().scaledTo(16, 16));
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
					// TODO: limit length
					return stringValue.getValue();
			}
		}

		return super.getText(element);
	}
}
