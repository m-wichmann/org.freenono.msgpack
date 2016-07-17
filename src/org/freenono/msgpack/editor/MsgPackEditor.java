package org.freenono.msgpack.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
import org.freenono.msgpack.editor.model.ModelArray;
import org.freenono.msgpack.editor.model.ModelBaseValue;
import org.freenono.msgpack.editor.model.ModelBinary;
import org.freenono.msgpack.editor.model.ModelBoolean;
import org.freenono.msgpack.editor.model.ModelExtension;
import org.freenono.msgpack.editor.model.ModelFloat;
import org.freenono.msgpack.editor.model.ModelInteger;
import org.freenono.msgpack.editor.model.ModelMap;
import org.freenono.msgpack.editor.model.ModelNil;
import org.freenono.msgpack.editor.model.ModelString;

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
	private Text infoTextLabel;
	private MsgPackInfoTextLabelListener infoTextLabelListener;

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

		// infoTextLabel
		infoTextLabel = new Text(parent, SWT.MULTI | SWT.WRAP);
		infoTextLabel.setEditable(false);
		GridData gridData = new GridData(SWT.FILL, SWT.END, true, false);
		// TODO: fixed size isn't a great idea!
		gridData.heightHint = 100;
		infoTextLabel.setLayoutData(gridData);
		infoTextLabelListener = new MsgPackInfoTextLabelListener(infoTextLabel);
		treeViewer.addSelectionChangedListener(infoTextLabelListener);

		// In-line editing (http://ramkulkarni.com/blog/in-place-editing-in-eclipse-treeviewer/)
		// Add EditorActivationStrategy and CellEditor (column.pack() to resize correctly at start)
		TreeViewerEditor.create(treeViewer, new ColumnViewerEditorActivationStrategy(treeViewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		}, ColumnViewerEditor.DEFAULT);
		treeViewerColumn.setEditingSupport(new MsgPackCellEditingSupport(treeViewer, this));

		// traverse tree by arrow keys
		// TODO: seems like on Windows traversing already works without this, so... yeah
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				TreeItem[] selection;
				
				switch (event.keyCode) {
					case SWT.ARROW_RIGHT:
						selection = tree.getSelection();
						if (selection.length == 1) {
							selection[0].setExpanded(true);
						}
						break;
						
					case SWT.ARROW_LEFT:
						selection = tree.getSelection();
						if (selection.length == 1) {
							if (selection[0].getItems().length == 0) {
								/* Item without children detected -> jump to parent */
								selection[0].getParent().setSelection(selection[0].getParentItem());
							} else {
								/* Item with children detected */
								selection[0].setExpanded(false);
							}
						}
						break;
						
					/* TODO: HACK: This needs a constant! */
					case 127:	/* Delete key */
						selection = tree.getSelection();
						for (TreeItem treeItem : selection) {
							model.removeElement((ModelBaseValue) treeItem.getData());
						}
						setDirty();
						treeViewer.refresh();
						break;
				}
			}
		});

		// Context Menu
		createContextMenu(parent.getDisplay());
		
		// TODO: deselect tree elements
		
		// TODO: remove elements
		
//		// TODO: DND Support
//		Transfer[] transfers = new Transfer[] {org.eclipse.ui.part.PluginTransfer.getInstance()};
//		treeViewer.addDragSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDragSourceListener());
//		treeViewer.addDropSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDropTargetListener(tree));

//		// TODO: add cut and paste support
//		Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
//		IActionBars bars = getEditorSite().getActionBars();
//		bars.setGlobalActionHandler("Copy", new Action() {
//			public void run() {
//				System.out.println("run()");
//			}
//		});
		
		// TODO: add undo/redo
	}

	@Override
	public void dispose() {
		super.dispose();
		this.tree.dispose();
		this.infoTextLabel.dispose();
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
	
	public void setDirty() {
		dirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	public void createContextMenu(Display display) {
		// TODO: HACK: This whole context menu stuff is _ugly_ and has to be replaced! Sadly I don't know how...
		
		class MenuListener extends SelectionAdapter implements MenuDetectListener {
			
			private ModelBaseValue currValue;

			@Override
			public void widgetSelected(SelectionEvent event) {
				Object obj = event.widget.getData();

				ModelBaseValue newValue = null;

				if (obj instanceof ModelArray) {
					newValue = new ModelArray(new ArrayList<>());
				} else if (obj instanceof ModelBinary) {
					newValue = new ModelBinary(new byte[0]);
				} else if (obj instanceof ModelBoolean) {
					newValue = new ModelBoolean(false);
				} else if (obj instanceof ModelExtension) {
					newValue = new ModelExtension(new byte[0], (byte) 0);
				} else if (obj instanceof ModelFloat) {
					newValue = new ModelFloat(0);
				} else if (obj instanceof ModelInteger) {
					newValue = new ModelInteger(0);
				} else if (obj instanceof ModelMap) {
					newValue = new ModelMap(new ArrayList<>());
				} else if (obj instanceof ModelNil) {
					newValue = new ModelNil();
				} else if (obj instanceof ModelString) {
					newValue = new ModelString("");
				}
				
				if (newValue != null) {
					if (currValue instanceof ModelArray) {
						((ModelArray) currValue).getValue().add(newValue);
						MsgPackEditor.this.treeViewer.refresh();
						MsgPackEditor.this.setDirty();
					} else if (currValue instanceof ModelMap) {
						((ModelMap) currValue).getValue().add(newValue);
						MsgPackEditor.this.treeViewer.refresh();
						MsgPackEditor.this.setDirty();
					}
				}
			}

			@Override
			public void menuDetected(MenuDetectEvent event) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				Object[] objList = selection.toArray();
				
				if (objList.length == 1) {
					if (   (objList[0] instanceof ModelArray)
						|| (objList[0] instanceof ModelMap)) {
						
						Tree tempTree = (Tree) event.widget;
						TreeItem[] treeSelection = tempTree.getSelection();
						if (treeSelection.length == 1) {
							ModelBaseValue treeElement = (ModelBaseValue) treeSelection[0].getData();
							currValue = treeElement;
						}
						
						return;
					}
				}

				event.doit = false;	
			}
		};
		
		Menu menu = new Menu(tree);
		tree.setMenu(menu);
		MenuListener menuListener = new MenuListener();
		tree.addMenuDetectListener(menuListener);

		MenuItem menuItemArray = new MenuItem(menu, SWT.NONE);
		menuItemArray.setText("Add Array");
		menuItemArray.setData(new ModelArray(new ArrayList<>()));
		menuItemArray.addSelectionListener(menuListener);
		MenuItem menuItemBinary = new MenuItem(menu, SWT.NONE);
		menuItemBinary.setText("Add Binary");
		menuItemBinary.setData(new ModelBinary(new byte[0]));
		menuItemBinary.addSelectionListener(menuListener);
		MenuItem menuItemBoolean = new MenuItem(menu, SWT.NONE);
		menuItemBoolean.setText("Add Boolean");
		menuItemBoolean.setData(new ModelBoolean(false));
		menuItemBoolean.addSelectionListener(menuListener);
		MenuItem menuItemExtension = new MenuItem(menu, SWT.NONE);
		menuItemExtension.setText("Add Extension");
		menuItemExtension.setData(new ModelExtension(new byte[0], (byte) 0));
		menuItemExtension.addSelectionListener(menuListener);
		MenuItem menuItemFloat = new MenuItem(menu, SWT.NONE);
		menuItemFloat.setText("Add Float");
		menuItemFloat.setData(new ModelFloat(0));
		menuItemFloat.addSelectionListener(menuListener);
		MenuItem menuItemInteger = new MenuItem(menu, SWT.NONE);
		menuItemInteger.setText("Add Integer");
		menuItemInteger.setData(new ModelInteger(0));
		menuItemInteger.addSelectionListener(menuListener);
		MenuItem menuItemMap = new MenuItem(menu, SWT.NONE);
		menuItemMap.setText("Add Map");
		menuItemMap.setData(new ModelMap(new ArrayList<>()));
		menuItemMap.addSelectionListener(menuListener);
		MenuItem menuItemNil = new MenuItem(menu, SWT.NONE);
		menuItemNil.setText("Add Nil");
		menuItemNil.setData(new ModelNil());
		menuItemNil.addSelectionListener(menuListener);
		MenuItem menuItemString = new MenuItem(menu, SWT.NONE);
		menuItemString.setText("Add String");
		menuItemString.setData(new ModelString(""));
		menuItemString.addSelectionListener(menuListener);
	
		Image origImage;
		Image resizedImage;
		URL url;
		
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/array.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemArray.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/binary.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemBinary.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/boolean.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemBoolean.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/extension.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemExtension.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/float.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemFloat.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/integer.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemInteger.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/map.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemMap.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/nil.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemNil.setImage(resizedImage);
		url = FileLocator.find(Platform.getBundle(MsgPackEditor.PLUGIN_ID), new Path("icons/string.png"), null);
		origImage = ImageDescriptor.createFromURL(url).createImage(true);
		resizedImage = new Image(display, origImage.getImageData().scaledTo(16, 16));
		menuItemString.setImage(resizedImage);		
	}
}
