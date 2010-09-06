package showdependencies.popup.actions;

import java.io.FileNotFoundException;
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
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ShowDependenciesAction implements IObjectActionDelegate
{

	static void listDeps(boolean[] canRun, final int indent, final IProject project, final Set<IProject> all,
			final List<IProject> met, PrintWriter output) throws CoreException
	{
		IJavaProject jp = new JavaProject(project, null);
		jp.open(null);
		IPackageFragmentRoot[] roots = jp.getAllPackageFragmentRoots();
		for(IPackageFragmentRoot iPackageFragmentRoot : roots)
		{
			if(!canRun[0])
			{
				output.println("--Interrupted--");
				break;
			}
			IResource correspondingResource = iPackageFragmentRoot.getCorrespondingResource();
			if(null != correspondingResource)
			{
				final IProject project2 = correspondingResource.getProject();
				if(!all.contains(project2) && !project2.equals(project))
				{
					output.print(makeIndent(indent) + project2.getFullPath());
					if(met.contains(project2))
					{
						output.println(" << CYCLE: " + met);
					}
					else
					{
						output.println();
						if(null != correspondingResource && !project2.equals(project))
						{
							listDeps(canRun, indent + 1, project2, all, new LinkedList<IProject>(met)
							{
								{
									add(project2);
								}
							}, //
									output);
						}
					}
					output.flush();
					all.add(project2);
				}
			}
		}
	}

	static String makeIndent(int depth)
	{
		if(depth <= 0)
			return "";
		else return makeIndent(depth - 1) + " . ";
	}

	private Shell		shell;

	private IProject	target;

	public ShowDependenciesAction()
	{
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action)
	{
		String message;
		if(null == target)
		{
			message = "No project selected.";
		}
		else
		{
			final IFile outFile = target.getFile("dependencies-"
					+ new SimpleDateFormat("yyyy-MM-dd_HHMMSS").format(new Date()) + ".txt");
			assert null != outFile;
			Job job = new Job("Dumping dependencies of " + target)
			{
				private boolean	canRun[]	= {true};

				@Override
				protected void canceling()
				{
					canRun[0] = false;
					super.canceling();
				}

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					final PrintWriter output;
					try
					{
						output = new PrintWriter(outFile.getLocation().toFile());
					}
					catch(FileNotFoundException e)
					{
						throw new RuntimeException(e);
					}
					try
					{
						List<IProject> met = new LinkedList<IProject>();
						Set<IProject> all = new HashSet<IProject>();
						output.println("Dependencies \n" + target.getFullPath());
						listDeps(canRun, 1, target, all, met, output);
					}
					catch(CoreException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						output.close();
					}
					return new Status(IStatus.OK, "showDeps", "completed scanning dependencies");
				}

			};
			job.schedule();
			message = "Dependenices are being written into " + outFile;
		}
		MessageDialog.openInformation(shell, "Project Dependency View", message);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
		IStructuredSelection selection = (IStructuredSelection)targetPart.getSite().getSelectionProvider()
				.getSelection();
		Object selected = selection.getFirstElement();
		if(selected instanceof IProject)
			target = (IProject)selection;
		else if(selected instanceof IAdaptable)
			target = (IProject)((IAdaptable)selected).getAdapter(IProject.class);
		else target = null;
	}

}
