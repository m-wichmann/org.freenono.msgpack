package de.ichmann.martinw.msgpackeditor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.BinaryValue;
import org.msgpack.value.BooleanValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.FloatValue;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.StringValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;

class MsgPackTreeLabelProvider extends LabelProvider {

	// TODO: dispose of images

	private Display display;

	public MsgPackTreeLabelProvider(Display display) {
		super();
		this.display = display;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Value) {
			Value value = (Value) element;
			ValueType type = value.getValueType();
			String iconPath = "icons/array.png";

			switch (type) {
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
		if (element instanceof Value) {
			Value value = (Value) element;
			ValueType type = value.getValueType();

			switch (type) {
				case NIL:
					return "NIL";
				case ARRAY:
					ArrayValue arrayValue = value.asArrayValue();
					return "Array (" + arrayValue.size() + " elements)";
				case BINARY:
					BinaryValue binaryValue = value.asBinaryValue();
					// TODO: better use asByteBuffer, but how to get length then?
					return "Binary (" + binaryValue.asByteArray().length + " bytes)";
				case BOOLEAN:
					BooleanValue booleanValue = value.asBooleanValue();
					return booleanValue.getBoolean() ? "True" : "False";
				case EXTENSION:
					ExtensionValue extensionValue = value.asExtensionValue();
					return "Extension (" + extensionValue.getType() + ", " + extensionValue.getData().length + " bytes)";
				case FLOAT:
					FloatValue floatValue = value.asFloatValue();
					return Double.toString(floatValue.toDouble());
				case INTEGER:
					IntegerValue integerValue = value.asIntegerValue();
					return Long.toString(integerValue.asLong());
				case MAP:
					MapValue mapValue = value.asMapValue();
					// TODO: are number of pairs or elements return by size()?
					return "Map (" + mapValue.size() + " pairs)";
				case STRING:
					StringValue stringValue = value.asStringValue();
					// TODO: limit length
					return stringValue.asString();
			}
		}

		return super.getText(element);
	}
}
