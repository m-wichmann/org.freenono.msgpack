package org.freenono.msgpack.editor;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

public class MsgPackDropTargetListener implements DropTargetListener {
	private Composite parent;

	public MsgPackDropTargetListener(Composite parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		
	}

	@Override
	public void drop(DropTargetEvent event) {
		
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		/*
		 * Source:
		 * https://github.com/aerofs/eclipse-platform-swt/blob/master/examples/
		 * org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet91.java
		 */

		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		if (event.item != null) {
			TreeItem item = (TreeItem) event.item;
			Point pt = event.display.map(null, this.parent, event.x, event.y);
			Rectangle bounds = item.getBounds();
			if (pt.y < bounds.y + bounds.height / 3) {
				event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
			} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
				event.feedback |= DND.FEEDBACK_INSERT_AFTER;
			} else {
				event.feedback |= DND.FEEDBACK_SELECT;
			}
		}
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		
	}
}