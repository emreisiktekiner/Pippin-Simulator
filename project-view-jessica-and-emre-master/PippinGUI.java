package project.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import project.model.CodeAccessException;
import project.model.Data;
import project.model.DivideByZeroException;
import project.model.Job;
import project.model.JobListener;
import project.model.MemoryAccessException;
import project.model.Model;

public class PippinGUI implements JobListener{

	private Model model;
	private FilesMgr filesMgr;
	private TimerControl stepControl;
	private JFrame frame;	
	private CodeViewPanel codeViewPanel;
	private DataViewPanel dataViewPanel1;
	private DataViewPanel dataViewPanel2;
	private DataViewPanel dataViewPanel3;
	private ControlPanel controlPanel;
	private ProcessorViewPanel processorPanel;
	private MenuBarBuilder menuBuilder;
	private ViewStates currentViewState;
	
	public Model getModel() {
		return model;
	}
	public void setModel(Model m) {
		model=m;
	}
	public JFrame getFrame() { 
		return frame;
	}
	public void changeToJob(int i) { 
		model.changeToJob(i);
		notifyObservers("Switch to job" + i);
	}
	private void notifyObservers(String str) { 
		codeViewPanel.update(str);
		menuBuilder.update(str);
		int job=model.getCurrentJob().getId();
		if (job==0) dataViewPanel1.update(str);
		else if (job==1) dataViewPanel2.update(str);
		else dataViewPanel3.update(str);
		processorPanel.update(str);
		controlPanel.update(str);
		menuBuilder.update(str);
	}
	public void clearJob() { 
		int codeSize=model.getCurrentJob().getCodeSize();
		model.clearJob();
		model.setCurrentState(Job.State.NOTHING_LOADED);
		notifyObservers("Clear " + codeSize);
	}
	public void makeReady(String s) { 
		stepControl.setAutoStepOn(false);
		model.setCurrentState(Job.State.PROGRAM_LOADED);
		notifyObservers(s);
	}
	
	public void setCurrentState(ViewStates value, String notifyMessage) {
		if (value == ViewStates.PROGRAM_HALTED) {
			stepControl.setAutoStepOn(false);
		}
		currentViewState = value;
		currentViewState.enter();
		notifyObservers(notifyMessage);
		
	}
	public ViewStates getCurrentState () { 
		return currentViewState;
	}
	
	public void toggleAutoStep() { 
		stepControl.toggleAutoStep();
		if (stepControl.isAutoStepOn() == true) {
			setCurrentState(ViewStates.AUTO_STEPPING,"");
		}
		else {
			setCurrentState(ViewStates.PROGRAM_LOADED_NOT_AUTOSTEPPING,"");
		}
		
	}
	
	public void reload() { 
		stepControl.setAutoStepOn(false);
		clearJob();
		filesMgr.finalLoad_Reload(model.getCurrentJob());
	}
	
	public void assembleFile() { 
		filesMgr.assembleFile();
	}
	
	public void loadFile() { 
		filesMgr.loadFile(model.getCurrentJob());
	}
	
	public void setPeriod(int value) { 
		stepControl.setPeriod(value);
	}
	public void step() { 
		if (model.getCurrentState() == Job.State.PROGRAM_LOADED) {
			try {
				model.step();
			} catch (MemoryAccessException e) {
				JOptionPane.showMessageDialog(frame, 
				"Illegal access to data from line " + model.getInstrPtr() + "\n"
				+ "Exception message: " + e.getMessage(),
				"Run time error",
				JOptionPane.OK_OPTION);
			} catch (CodeAccessException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal access to code from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame, 
						"NullPointerException from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, 
						"Program Error from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame, 
						"Divide by zero from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			}
			notifyObservers("");
		}
	}
	public void execute() {
		while (model.getCurrentState() == Job.State.PROGRAM_LOADED) {
			try {
				model.step();
			} catch (MemoryAccessException e) {
				JOptionPane.showMessageDialog(frame, 
				"Illegal access to data from line " + model.getInstrPtr() + "\n"
				+ "Exception message: " + e.getMessage(),
				"Run time error",
				JOptionPane.OK_OPTION);
			} catch (CodeAccessException e) {
				JOptionPane.showMessageDialog(frame, 
						"Illegal access to code from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame, 
						"NullPointerException from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, 
						"Program Error from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame, 
						"Divide by zero from line " + model.getInstrPtr() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error",
						JOptionPane.OK_OPTION);
			}
		}
		notifyObservers("");
	}
	  
	// some complete methods:
	private void createAndShowGUI() {
		stepControl = new TimerControl(this);
		filesMgr = new FilesMgr(this);
		filesMgr.initialize();
		codeViewPanel = new CodeViewPanel(model);
		dataViewPanel1 = new DataViewPanel(model, 0, 512);
		dataViewPanel2 = new DataViewPanel(model, 512, Data.DATA_SIZE/2);
		dataViewPanel3 = new DataViewPanel(model, Data.DATA_SIZE/2, Data.DATA_SIZE);
		controlPanel = new ControlPanel(this);
		processorPanel = new ProcessorViewPanel(model.getCpu());
		menuBuilder = new MenuBarBuilder(this);
		frame = new JFrame("Pippin Simulator");

		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		bar.add(menuBuilder.createFileMenu());
		bar.add(menuBuilder.createExecuteMenu());
		bar.add(menuBuilder.createJobsMenu());

		Container content = frame.getContentPane(); 
		content.setLayout(new BorderLayout(1,1));
		content.setBackground(Color.BLACK);
		frame.setSize(1200,600);
		frame.add(codeViewPanel, BorderLayout.LINE_START);
		frame.add(processorPanel,BorderLayout.PAGE_START);
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,3));
		center.add(dataViewPanel1);
		center.add(dataViewPanel2);
		center.add(dataViewPanel3);
		frame.add(center, BorderLayout.CENTER);
		frame.add(controlPanel, BorderLayout.PAGE_END);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
		frame.setLocationRelativeTo(null);
		model.addJobListener(this);
		model.setCurrentState(Job.State.NOTHING_LOADED);
		currentViewState = ViewStates.NOTHING_LOADED;
		stepControl.start();
		notifyObservers("");
		frame.setVisible(true);
	}

	public void exit() { // method executed when user exits the program
	  int decision = JOptionPane.showConfirmDialog(
	    frame, "Do you really wish to exit?",
	    "Confirmation", JOptionPane.YES_NO_OPTION);
	  if (decision == JOptionPane.YES_OPTION) {
	    System.exit(0);
	  }
	}
	
	public void updateJob(Job currentJob) {
		// Need to react to job changes
	
		Job.State current = currentJob.getCurrentState();
		if (current== Job.State.NOTHING_LOADED) {
			setCurrentState(ViewStates.NOTHING_LOADED,"");
		}
		else if (current == Job.State.PROGRAM_LOADED) {
			setCurrentState(ViewStates.PROGRAM_LOADED_NOT_AUTOSTEPPING,"");
		}
		else{
			setCurrentState(ViewStates.PROGRAM_HALTED,"");
		}
		
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(()->
			{
				PippinGUI organizer = new PippinGUI();
				Model model = new Model(()->{});
				// Note... need to override the default halt callback, which exits
				// with a null lambda expression... don't do anything when a job halts
				organizer.setModel(model);
				organizer.createAndShowGUI();
			}
		);
	}
	
	
}
