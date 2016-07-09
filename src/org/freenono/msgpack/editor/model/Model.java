package org.freenono.msgpack.editor.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.freenono.msgpack.editor.model.ModelBaseValue.ModelValueType;
import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.ValueType;

public class Model {
	private ArrayList<ModelBaseValue> topLevel = new ArrayList<>();

	public Model() {

	}

	public Model(IFile inputFile) throws IllegalArgumentException, CoreException {
		if (inputFile == null) {
			throw new IllegalArgumentException("Could not open file!");
		}

		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputFile.getContents());
		ArrayList<Value> valueList = new ArrayList<Value>();

		try {
			while (true) {
				Value val = unpacker.unpackValue();
				valueList.add(val);
			}
		} catch (MessageInsufficientBufferException | IOException e) {
			/* end loop */
		}

		for (Value value : valueList) {
			topLevel.add(convToModel(value));
		}
	}

	public Model(File inputFile) throws IllegalArgumentException, FileNotFoundException {
		if (inputFile == null) {
			throw new IllegalArgumentException("Could not open file!");
		}

		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(inputFile));
		ArrayList<Value> valueList = new ArrayList<Value>();

		/* TODO: remove duplicated code */
		try {
			while (true) {
				Value val = unpacker.unpackValue();
				valueList.add(val);
			}
		} catch (MessageInsufficientBufferException | IOException e) {
			/* end loop */
		}

		for (Value value : valueList) {
			topLevel.add(convToModel(value));
		}
	}

	public ArrayList<Value> toMsgpackValue() {
		ArrayList<Value> valueList = new ArrayList<Value>();
		for (ModelBaseValue modelBaseValue : topLevel) {
			valueList.add(convToValue(modelBaseValue));
		}
		return valueList;
	}

	public Object[] getTopLevel() {
		return topLevel.toArray();
	}

	private ModelBaseValue convToModel(Value value) {
		ValueType valueType = value.getValueType();

		switch (valueType) {
			case NIL:
				return new ModelNil();
			case BINARY:
				return new ModelBinary(value.asBinaryValue().asByteArray());
			case BOOLEAN:
				return new ModelBoolean(value.asBooleanValue().getBoolean());
			case EXTENSION:
				ExtensionValue extensionValue = value.asExtensionValue();
				return new ModelExtension(extensionValue.getData(), extensionValue.getType());
			case FLOAT:
				return new ModelFloat(value.asFloatValue().toDouble());
			case INTEGER:
				return new ModelInteger(value.asIntegerValue().toLong());
			case STRING:
				return new ModelString(value.asStringValue().asString());

			case ARRAY:
				ArrayValue arrayValue = value.asArrayValue();
				ArrayList<ModelBaseValue> newArrayValue = new ArrayList<ModelBaseValue>();
				for (Value arrayElement : arrayValue) {
					newArrayValue.add(convToModel(arrayElement));
				}
				return new ModelArray(newArrayValue);

			case MAP:
				MapValue mapValue = value.asMapValue();
				ArrayList<ModelBaseValue> newMapValue = new ArrayList<ModelBaseValue>();

				for (Value mapElement : mapValue.getKeyValueArray()) {
					newMapValue.add(convToModel(mapElement));
				}

				return new ModelMap(newMapValue);
		}

		return null;
	}

	private Value convToValue(ModelBaseValue value) {
		ModelValueType valueType = value.getValueType();

		switch (valueType) {
			case ARRAY:
				ArrayList<Value> newArray = new ArrayList<>();
				ModelArray modelArray = (ModelArray) value;
				for (ModelBaseValue modelBaseValue : modelArray.getValue()) {
					newArray.add(convToValue(modelBaseValue));
				}
				return ValueFactory.newArray(newArray);

			case BINARY:
				ModelBinary modelBinary = (ModelBinary) value;
				return ValueFactory.newBinary(modelBinary.getValue());

			case BOOLEAN:
				ModelBoolean modelBoolean = (ModelBoolean) value;
				return ValueFactory.newBoolean(modelBoolean.getValue());

			case EXTENSION:
				ModelExtension modelExtension = (ModelExtension) value;
				return ValueFactory.newExtension(modelExtension.getType(), modelExtension.getValue());

			case FLOAT:
				ModelFloat modelFloat = (ModelFloat) value;
				return ValueFactory.newFloat(modelFloat.getValue());

			case INTEGER:
				ModelInteger modelInteger = (ModelInteger) value;
				return ValueFactory.newInteger(modelInteger.getValue());

			case MAP:
				ArrayList<Value> newMap = new ArrayList<>();
				ModelMap modelMap = (ModelMap) value;
				for (ModelBaseValue modelBaseValue : modelMap.getValue()) {
					newMap.add(convToValue(modelBaseValue));
				}
				return ValueFactory.newMap((Value[]) newMap.toArray(), false);

			case NIL:
				return ValueFactory.newNil();

			case STRING:
				ModelString modelString = (ModelString) value;
				return ValueFactory.newString(modelString.getValue());
		}

		return null;
	}

	@Override
	public String toString() {
		String ret = "";

		for (ModelBaseValue modelBaseValue : topLevel) {
			ret += modelBaseValue.toString();
			ret += "\n";
		}

		return ret;
	}
}
