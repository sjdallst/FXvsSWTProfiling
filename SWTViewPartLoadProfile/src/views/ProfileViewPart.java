package views;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.util.time.TimeDuration.ofHertz;



import java.util.ArrayList;
import java.util.List;



import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;



import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ProfileViewPart extends ViewPart {
	private List<Label> labelsList = new ArrayList<Label>();
	List<PV<Object, Object>> pvList = new ArrayList<PV<Object, Object>>();
	
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(10, true);
		parent.setLayout(layout);
		createLists(1, parent);
		parent.layout();
	}
	
	public void createLists(int length, Composite parent) {
		labelsList.clear();
		closePVs();
		for(int i = 0; i < length; i++) {
			labelsList.add(new Label(parent, SWT.NONE));
			labelsList.get(i).setText("" + i);
		}
		for(int i = 0; i < length; i++) {
			pvList.add(PVManager.readAndWrite(channel("sim://noise"))
                    .timeout(TimeDuration.ofSeconds(5))
                    .readListener((PVReaderEvent<Object> event1) -> {
                    	Display.getDefault().asyncExec(()->{
                    		if(pvList.size() > 0) {
		                    	int randNum = (int)(Math.random()*pvList.size());
		                    	
		                    	((Label)(labelsList.get(randNum)))
		                    		.setText(((VNumber)(pvList.get(randNum).getValue())).getValue().doubleValue() + "");
		                    	parent.layout();
                    		}
                    	});
                    })
                    .writeListener((PVWriterEvent<Object> event1) -> {

                    })
                    .asynchWriteAndMaxReadRate(ofHertz(10)));
		}
	}

	private void closePVs() {
		for(int i = 0; i < pvList.size(); i++) {
			if(pvList.get(i) != null) {
				pvList.get(i).close();
			}
		}
		pvList.clear();
	}
	
	@Override
	public void dispose() {
		closePVs();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
