package org.freenono.msgpack.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.value.Value;
import org.eclipse.ui.part.FileEditorInput;
import org.freenono.msgpack.editor.model.Model;

public class MsgPackEditor extends EditorPart {

	public final static String PLUGIN_ID = "org.freenono.msgpack";

	private boolean dirty = false;
	private IFile inputIFile = null;
	private File inputFile = null;
	private boolean isReadOnly = false; // TODO: don't allow save with read only files

	private Model model;

	private Tree tree;
	private TreeViewer treeViewer;
	private TreeViewerColumn treeViewerColumn;
	private Label infoLabel;
	private MsgPackInfoLabelListener infoLabelListener;

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void setFocus() {
		tree.setFocus();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		firePropertyChange(IWorkbenchPartConstants.PROP_INPUT);
		this.setPartName(input.getName());

		/* TODO: improve error handling */

		/*
		 * Code based on:
		 * http://ehep.cvs.sourceforge.net/viewvc/ehep/net.sourceforge.ehep/src/
		 * net/sourceforge/ehep/editors/HexEditor.java?view=markup
		 */
		if (input instanceof FileEditorInput) {
			/* Input file found in Eclipse Workspace - good */
			inputIFile = ((FileEditorInput) input).getFile();
			this.isReadOnly = inputIFile.isReadOnly();
		} else if (input instanceof IPathEditorInput) {
			/* Input file is outside the Eclipse Workspace */
			IPathEditorInput pathEditorInput = (IPathEditorInput) input;
			IPath path = pathEditorInput.getPath();
			inputFile = path.toFile();
			this.isReadOnly = !inputFile.canWrite();
		} else if (input instanceof IURIEditorInput) {
			/* Input file is outside the Eclipse Workspace */
			IURIEditorInput uriEditorInput = (IURIEditorInput) input;
			inputFile = new File(uriEditorInput.getURI());
			this.isReadOnly = !inputFile.canWrite();
		} else {
			/* Unhandled input type */
		}

		try {
			if (inputIFile != null) {
				model = new Model(inputIFile);
			} else if (inputFile != null) {
				model = new Model(inputFile);
			} else {
				/* Could not open file */
			}
		} catch (IllegalArgumentException | CoreException | FileNotFoundException e) {
			model = new Model();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// Layout
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		// Tree
		tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// TreeViewer
		treeViewer = new TreeViewer(tree);
		treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewer.setContentProvider(new MsgPackModelContentProvider());
		treeViewerColumn.setLabelProvider(new MsgPackModelCellLabelProvider(parent.getDisplay()));
		treeViewer.setInput(model);
		treeViewer.expandAll();
		treeViewerColumn.getColumn().pack();

		// InfoLabel
		infoLabel = new Label(parent, 0);
		GridData gridData = new GridData(SWT.FILL, SWT.END, true, false);
		// TODO: fixed size isn't a great idea!
		gridData.heightHint = 100;
		infoLabel.setLayoutData(gridData);
		infoLabelListener = new MsgPackInfoLabelListener(infoLabel);
		treeViewer.addSelectionChangedListener(infoLabelListener);

		// In-line editing (http://ramkulkarni.com/blog/in-place-editing-in-eclipse-treeviewer/)
		// Add EditorActivationStrategy and CellEditor (column.pack() to resize correctly at start)
		TreeViewerEditor.create(treeViewer, new ColumnViewerEditorActivationStrategy(treeViewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		}, ColumnViewerEditor.DEFAULT);
		treeViewerColumn.setEditingSupport(new MsgPackCellEditingSupport(treeViewer));

		
		
		
		
//		// DND Support
//		Transfer[] transfers = new Transfer[] {org.eclipse.ui.part.PluginTransfer.getInstance()};
//		// TODO: maybe drop DROP_COPY?!
//		treeViewer.addDragSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDragSourceListener());
//		treeViewer.addDropSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDropTargetListener(tree));
//		
//		// Context Menu
//		Menu menu = new Menu(tree);
//		tree.setMenu(menu);
//		menu.addMenuListener(new MenuAdapter() {
//			@Override
//			public void menuHidden(MenuEvent e) {
//				MenuItem[] items = menu.getItems();
//				for (int i = 0; i < items.length; i++) {
//					items[i].dispose();
//				}
//			}
//
//			@Override
//			public void menuShown(MenuEvent e) {
//				// Add new items
//				MenuItem newItem = new MenuItem(menu, SWT.NONE);
//				newItem.setText("Menuitem");
//				
////				System.out.println("====");
////				System.out.println(e.display);
////				System.out.println(e.widget);
////				System.out.println(e.getSource());
//				
//				newItem.addSelectionListener(new SelectionAdapter() {
//					@Override
//					public void widgetSelected(SelectionEvent event) {
//						System.out.println(event);
//					}
//				});
//			}
//		});
		
//		tree.addListener(SWT.MenuDetect, event -> {
//			System.out.println("====");
//			System.out.println(event.data);
//			System.out.println(event.index);
//			System.out.println(event.item);
//			Point pt = getSite().getShell().getDisplay().map(null, tree, new Point(event.x, event.y));
//			Rectangle clientArea = tree.getClientArea();
//			boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + tree.getHeaderHeight());
//			//tree.setMenu(header ? headerMenu : treeMenu);
//			System.out.println(event.x);
//			System.out.println(event.y);
//		});
		
//		// TODO: add cut and paste support
//		Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
//		IActionBars bars = getEditorSite().getActionBars();
//		bars.setGlobalActionHandler("Copy", new Action() {
//			public void run() {
//				System.out.println("run()");
//			}
//		});
	}

	@Override
	public void dispose() {
		super.dispose();
		this.tree.dispose();
		this.infoLabel.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			if (inputIFile != null) {
				saveFile(this.inputIFile, this.model);
			} else if (inputFile != null) {
				saveFile(this.inputFile, this.model);
			}

			dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (IOException | CoreException e) {
			monitor.setCanceled(true);
		}
	}

	@Override
	public void doSaveAs() {
		// TODO: maybe use this?!
		// SaveAsDialog dialog = new SaveAsDialog(getSite().getShell().getDisplay().getActiveShell());

		FileDialog dialog = new FileDialog(getSite().getShell().getDisplay().getActiveShell(), SWT.SAVE);
		dialog.setOverwrite(true);
		String outFilepath = dialog.open();

		if (outFilepath != null) {
			try {
				saveFile(new File(outFilepath), this.model);
			} catch (IOException | CoreException e) {
				e.printStackTrace();
			}

			dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	private void saveFile(IFile file, Model model) throws IOException, CoreException {
		byte[] data = saveFilePackData(model);
		file.setContents(new ByteArrayInputStream(data), false, false, null);
	}

	private void saveFile(File file, Model model) throws IOException, CoreException {
		byte[] data = saveFilePackData(model);
		OutputStream os = new FileOutputStream(file);
		os.write(data);
		os.close();
	}

	private byte[] saveFilePackData(Model model) throws IOException {
		ArrayList<Value> valueList = model.toMsgpackValue();

		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
		for (Value value : valueList) {
			packer.packValue(value);
		}

		return packer.toByteArray();
	}
}
