package showdependencies.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ShowDependenciesAction implements IObjectActionDelegate {

    private final class ScanJob extends Job {
	private final IFile outFile;
	private boolean canRun[] = { true };

	private ScanJob(String name, IFile outFile) {
	    super(name);
	    this.outFile = outFile;
	}

	@Override
	protected void canceling() {
	    canRun[0] = false;
	    super.canceling();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
	    // outFile.set getLocation().toFile()
	    final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    final PrintWriter output = new PrintWriter(out);
	    try {
		List<IProject> met = new LinkedList<IProject>();
		Set<IProject> all = new HashSet<IProject>();
		met.add(target);
		output.println(target.getFullPath());
		listDeps(canRun, 1, target, all, met, output);
		output.flush();
		outFile.create(new ByteArrayInputStream(out.toByteArray()),
			true, monitor);
		showBox("Dependenices list was written to "
			+ outFile.getFullPath());
		return new Status(IStatus.OK, "showDeps",
			"Completed scanning dependencies list");
	    } catch (CoreException e) {
		return new Status(IStatus.ERROR, "showDeps",
			"Can not write dependencies list to file "
				+ outFile.getFullPath(), e);
	    } finally {
		output.close(); // Just in case (no disk I/O)
	    }
	}
    }

    /**
     * 
     * @param all
     *            List of all projects gathered so far - used to not show
     *            duplicates.
     * @param met
     *            List of projects in this branch - used to detect cycles
     */
    static void listDeps(boolean[] canRun, final int indent,
	    final IProject project, final Set<IProject> all,
	    final List<IProject> met, PrintWriter output) throws CoreException {

	@SuppressWarnings("restriction")
	final IJavaProject jp = new org.eclipse.jdt.internal.core.JavaProject(
		project, null);

	jp.open(null);
	IPackageFragmentRoot[] roots = jp.getAllPackageFragmentRoots();
	for (IPackageFragmentRoot iPackageFragmentRoot : roots) {
	    if (!canRun[0]) {
		output.println("--Interrupted--");
		break;
	    }
	    IResource correspondingResource = iPackageFragmentRoot
		    .getCorrespondingResource();
	    if (null != correspondingResource) {
		final IProject project2 = correspondingResource.getProject();
		if (!all.contains(project2) && !project2.equals(project)) {
		    output.print(makeIndent(indent) + project2.getFullPath());
		    if (met.contains(project2)) {
			output.println(" << CYCLE: " + met);
		    } else {
			output.println();
			if (null != correspondingResource
				&& !project2.equals(project)) {
			    final List<IProject> initialSet = new LinkedList<IProject>(
				    met);
			    initialSet.add(project2);
			    listDeps(canRun, indent + 1, project2, all,
				    initialSet, output);
			}
		    }
		    output.flush();
		    all.add(project2);
		}
	    }
	}
    }

    static String makeIndent(int depth) {
	if (depth <= 0) {
	    return "";
	} else {
	    return makeIndent(depth - 1) + " . ";
	}
    }

    private Shell shell;

    private IProject target;

    public ShowDependenciesAction() {
    }

    private Display getDisplay() {
	Display d = Display.getCurrent();
	if (null == d) {
	    d = Display.getDefault();
	}
	if (d == null) {
	    throw new RuntimeException("No display to show message.");
	}
	return d;
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    @Override
    public void run(IAction action) {
	if (null == target) {
	    showBox("No project selected.");
	} else {
	    final IFile outFile = target.getFile("dependencies-of-"
		    + target.getName()
		    + "-"
		    + new SimpleDateFormat("yyyy-MM-dd_HHMMSS")
			    .format(new Date()) + ".txt");
	    assert null != outFile;
	    Job job = new ScanJob("Dumping dependencies of " + target, outFile);
	    job.schedule();
	}
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	shell = targetPart.getSite().getShell();
	IStructuredSelection selection = (IStructuredSelection) targetPart
		.getSite().getSelectionProvider().getSelection();
	Object selected = selection.getFirstElement();
	if (selected instanceof IProject) {
	    target = (IProject) selection;
	} else if (selected instanceof IAdaptable) {
	    target = (IProject) ((IAdaptable) selected)
		    .getAdapter(IProject.class);
	} else {
	    target = null;
	}
    }

    private void showBox(final String message) {
	getDisplay().asyncExec(new Runnable() {
	    @Override
	    public void run() {
		MessageDialog.openInformation(shell, "Project Dependency View",
			message);
	    }
	});
    }
}
