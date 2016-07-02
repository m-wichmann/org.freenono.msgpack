package de.ichmann.martinw.msgpackeditor;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

class MsgPackTreeDecoLabelProvider extends DecoratingLabelProvider {
	public MsgPackTreeDecoLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	@Override
	public Color getBackground(Object element) {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return new Color(display, 100, 20, 50);
	}
}