package de.ichmann.martinw.msgpackeditor;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class MsgPackDragSourceListener implements DragSourceListener {
	@Override
	public void dragStart(DragSourceEvent event) {
		/* Nothing to do, dragging of tree elements is always valid */
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		System.out.println("dragSetData");
		System.out.println(event.data);
		System.out.println(event.detail);
		System.out.println(event.doit);
		System.out.println(event.dataType);
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("dragFinished");
	}
}