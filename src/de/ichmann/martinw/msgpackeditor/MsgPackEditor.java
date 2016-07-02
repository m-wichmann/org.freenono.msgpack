package de.ichmann.martinw.msgpackeditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

// TODO: do this dispose stuff!

public class MsgPackEditor extends EditorPart {

	private boolean dirty = false;
	private IFile input_file;
	private Tree tree;
	private TreeViewer treeViewer;
	private ArrayList<Value> model = new ArrayList<Value>();
	private Label infoLabel;

	public MsgPackEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			saveFile(this.input_file, this.model);
			dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (IOException | CoreException e) {
			monitor.setCanceled(true);
		}
	}

	@Override
	public void doSaveAs() {
		FileDialog dialog = new FileDialog(getSite().getShell().getDisplay().getActiveShell(), SWT.SAVE);
		dialog.setOverwrite(true);
		String outFilepath = dialog.open();

		if (outFilepath != null) {
			// TODO: get IFile or something?
			//saveFile()

			dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);	// dirty state has changed
		}
	}


	private void saveFile(IFile file, ArrayList<Value> valueList) throws IOException, CoreException {
		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
		for (Value value : valueList) {
			packer.packValue(value);
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(packer.toByteArray());

		file.setContents(stream, false, false, null);
	}


	private ArrayList<Value> loadFile(IFile file) throws CoreException, IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(file.getContents());
		ArrayList<Value> valueList = new ArrayList<Value>();

		try {
			while (true) {
				Value val = unpacker.unpackValue();
				valueList.add(val);
			}
		} catch (MessageInsufficientBufferException e) {
			/* end loop */
		}

		return valueList;
	}




	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		setPartName(input.getName());

		input_file = input.getAdapter(IFile.class);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}


	@Override
	public void createPartControl(Composite parent) {


		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);




		tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Menu menu = new Menu(tree);
		tree.setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuHidden(MenuEvent e) {
				System.out.println("hidden");
				System.out.println(e.data);
			}

			@Override
			public void menuShown(MenuEvent e) {
				System.out.println("shown");

				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}
				MenuItem newItem = new MenuItem(menu, SWT.NONE);
				newItem.setText("Menu for " + tree.getSelection()[0].getText());
			}
		});


		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new MsgPackTreeContentProvider());
		treeViewer.setLabelProvider(new MsgPackTreeLabelProvider(parent.getDisplay()));
		//treeViewer.setLabelProvider(new DecoLabelProvider(new ViewLabelProvider(), new MyLabelDecorator()));
		treeViewer.setInput(model);
		treeViewer.expandAll();

		try {
			ArrayList<Value> valueList = loadFile(input_file);
			model.addAll(valueList);
			treeViewer.refresh();
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}

		Transfer[] transfers = new Transfer[] {org.eclipse.ui.part.PluginTransfer.getInstance()};
		// TODO: maybe drop DROP_COPY?!
		treeViewer.addDragSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDragSourceListener());
		treeViewer.addDropSupport((DND.DROP_COPY | DND.DROP_MOVE), transfers, new MsgPackDropTargetListener(tree));

		// InfoLabel
		Label infoLabel = new Label(parent, 0);
		GridData gridData = new GridData(SWT.FILL, SWT.END, true, false);
		// TODO: fixed size isn't a great idea!
		gridData.heightHint = 100;
		infoLabel.setLayoutData(gridData);
		//tree.addListener(SWT.Selection, new MsgPackEditorInfoLabelListener());
		
		treeViewer.addSelectionChangedListener(new MsgPackInfoLabelListener(infoLabel));
		
//		infoLabel = new Label(parent, 0);
//		GridData gridData = new GridData(SWT.FILL, SWT.END, true, false);
//		// TODO: fixed size isn't a great idea!
//		gridData.heightHint = 200;
//		infoLabel.setLayoutData(gridData);
//
//		tree.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				if (event.type == SWT.SELECTED) {
//					Value value = null;
//					MsgPackEditorInfoLabelProvider.generateInfoString(infoLabel, value);
//				}
//			}
//		});
		
		// TODO: add cut and paste support
//		Clipboard clipboard = new Clipboard(getSite().getShell().getDisplay());
//		IActionBars bars = getEditorSite().getActionBars();
//		bars.setGlobalActionHandler("Copy", new Action() {
//			public void run() {
//				System.out.println("run()");
//			}
//		});
	}

	@Override
	public void setFocus() {
		tree.setFocus();
	}
}
