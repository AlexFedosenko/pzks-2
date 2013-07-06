package gui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorToolBar extends JToolBar {

    private static final long serialVersionUID = -8015443128436394471L;
    private int n = 0;

    private class SimAction extends AbstractAction {

        public SimAction() {
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = null;
            graph = GraphEditor.editor.getTaskComponent().getGraph();
            graph.repaint();
            graph.refresh();
        }
    }

    private boolean ignoreZoomChange = false;

    public EditorToolBar(final BasicGraphEditor editor, int orientation) {
        super(orientation);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);

        add(editor.bind("New", new EditorActions.NewAction(),
                "/images/new.gif"));
        add(editor.bind("Open", new EditorActions.OpenAction(),
                "/images/open.gif"));
        add(editor.bind("Save", new EditorActions.SaveAction(false),
                "/images/save.gif"));

        addSeparator();

        add(editor.bind("Print", new EditorActions.PrintAction(),
                "/images/print.gif"));

        addSeparator();

        add(editor.bind("Cut", TransferHandler.getCutAction(),
                "/images/cut.gif"));
        add(editor.bind("Copy", TransferHandler.getCopyAction(),
                "/images/copy.gif"));
        add(editor.bind("Paste", TransferHandler.getPasteAction(),
                "/images/paste.gif"));

        addSeparator();

        add(editor.bind("Delete", mxGraphActions.getDeleteAction(),
                "/images/delete.gif"));

        addSeparator();

        add(editor.bind("Undo", new EditorActions.HistoryAction(true),
                "/images/undo.gif"));
        add(editor.bind("Redo", new EditorActions.HistoryAction(false),
                "/images/redo.gif"));

        addSeparator();

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        addSeparator();


        final mxGraphView view = editor.getTaskComponent().getGraph().getView();
        final JComboBox zoomCombo = new JComboBox(new Object[]{"400%",
                    "200%", "150%", "100%", "75%", "50%", mxResources.get("page"),
                    mxResources.get("width"), mxResources.get("actualSize")});
        zoomCombo.setEditable(true);
        zoomCombo.setMinimumSize(new Dimension(75, 0));
        zoomCombo.setPreferredSize(new Dimension(75, 0));
        zoomCombo.setMaximumSize(new Dimension(75, 100));
        zoomCombo.setMaximumRowCount(9);
        add(zoomCombo);
        mxIEventListener scaleTracker = new mxIEventListener() {

            public void invoke(Object sender, mxEventObject evt) {
                ignoreZoomChange = true;

                try {
                    zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale())
                            + "%");
                } finally {
                    ignoreZoomChange = false;
                }
            }
        };
        view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
        view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE,
                scaleTracker);
        scaleTracker.invoke(null, null);

        zoomCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mxGraphComponent graphComponent = editor.getTaskComponent();
                if (!ignoreZoomChange) {
                    String zoom = zoomCombo.getSelectedItem().toString();

                    if (zoom.equals(mxResources.get("page"))) {
                        graphComponent.setPageVisible(true);
                        graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
                    } else if (zoom.equals(mxResources.get("width"))) {
                        graphComponent.setPageVisible(true);
                        graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
                    } else if (zoom.equals(mxResources.get("actualSize"))) {
                        graphComponent.zoomActual();
                    } else {
                        try {
                            zoom = zoom.replace("%", "");
                            double scale = Math.min(16, Math.max(0.01,
                                    Double.parseDouble(zoom) / 100));
                            graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(editor, ex.getMessage());
                        }
                    }
                }
            }
        });
    }
}
