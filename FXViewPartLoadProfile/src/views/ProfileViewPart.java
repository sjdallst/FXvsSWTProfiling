package views;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.util.time.TimeDuration.ofHertz;




import java.util.ArrayList;
import java.util.List;




import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import org.hyperic.sigar.*;




import javafx.scene.text.Text;




import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import java.lang.management.*;

public class ProfileViewPart extends ViewPart {
	
	public static final String ID = "views.ProfileViewPart";
    private FXCanvas canvas;
    private TreeTableView table;
    private final GridPane grid = new GridPane();
    private Scene scene;
    List<PV<Object, Object>> pvList = new ArrayList<PV<Object, Object>>();

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent, SWT.NONE);
        canvas.setScene(createFxScene());
        //new Thread(new SleeperThread()).start();
	}
	
	private Scene createFxScene() {
        BorderPane pane = new BorderPane();
        createLists(1);
        pane.setCenter(grid);
        return new Scene(pane);
    }

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void createLists(int length) {
		grid.getChildren().clear();
		closePVs();
		for(int i = 0; i < length; i++) {
			for(int j = 0;j < length; j++) {
				grid.add(new Text("" + i), i, j);
			}
		}
		for(int i = 0; i < length; i++) {
			pvList.add(PVManager.readAndWrite(channel("sim://noise"))
                    .timeout(TimeDuration.ofSeconds(5))
                    .readListener((PVReaderEvent<Object> event1) -> {
                    	Platform.runLater(()->{
	                    	int randNum = (int)(Math.random()*pvList.size());
	                    	
	                    	((Text)(grid.getChildren().get(randNum)))
	                    		.setText(((VNumber)(pvList.get(randNum).getValue())).getValue().doubleValue() + "");
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
	
}
