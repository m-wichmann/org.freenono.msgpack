package org.freenono.msgpack.editor;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class AddingWizard extends Wizard implements INewWizard {

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
