package gui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.*;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import core.CellInfo;
import core.ControlPanel;
import core.Gantt;
import core.MatrixHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasicGraphEditor extends JPanel {

    private static final long serialVersionUID = -6561623072112577140L;

    static {
        try {
            mxResources.add("resources/editor");
        } catch (Exception e) {
        }
    }
    static protected mxGraphComponent taskComponent;
    static protected mxGraphComponent sysComponent;
    protected mxGraphOutline graphOutline;
    protected JTabbedPane libraryPane;
    protected GenerationPanel generationPanel;
    JTabbedPane componentPane;
    protected mxUndoManager undoManager;
    protected mxUndoManager sysManager;
    protected String appTitle;
    protected JLabel statusBar;
    protected JScrollPane queuePane;
    static protected JTextArea queueArea;
    protected File currentFile;
    protected boolean modified = false;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    static JSplitPane tables = null;
    public static ControlPanel controlPanel = new ControlPanel();
    public static Gantt gantt = new Gantt();
    protected mxIEventListener undoHandler = new mxIEventListener() {

        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };
    protected mxIEventListener sysHandler = new mxIEventListener() {

        public void invoke(Object source, mxEventObject evt) {
            sysManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };
    protected mxIEventListener changeTracker = new mxIEventListener() {

        public void invoke(Object source, mxEventObject evt) {
            setModified(true);
        }
    };

    public BasicGraphEditor(String appTitle, mxGraphComponent component1, mxGraphComponent component2) {
        this.appTitle = appTitle;

        taskComponent = component1;
        sysComponent = component2;
        final mxGraph graph = taskComponent.getGraph();
        undoManager = createUndoManager();
        graph.setResetViewOnRootChange(false);
        graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);
        mxIEventListener undoHandler = new mxIEventListener() {

            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);
        final mxGraph sysGraph = sysComponent.getGraph();
        sysManager = createUndoManager();
        sysGraph.setResetViewOnRootChange(false);
        sysGraph.getModel().addListener(mxEvent.CHANGE, changeTracker);
        sysGraph.getModel().addListener(mxEvent.UNDO, sysHandler);
        sysGraph.getView().addListener(mxEvent.UNDO, sysHandler);
        mxIEventListener sysHandler = new mxIEventListener() {

            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                sysGraph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
            }
        };
        sysManager.addListener(mxEvent.UNDO, undoHandler);
        sysManager.addListener(mxEvent.REDO, undoHandler);
        graphOutline = new mxGraphOutline(taskComponent);
        libraryPane = new JTabbedPane();
        generationPanel = new GenerationPanel(this, libraryPane.getWidth(), libraryPane.getHeight());
        libraryPane.add("Generation", generationPanel);
        installDefaultFormat();
        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                libraryPane, null);
        inner.setDividerLocation(320);
        inner.setResizeWeight(1);
        inner.setDividerSize(6);
        inner.setBorder(null);
        queuePane = new JScrollPane();
        queueArea = new JTextArea();
        componentPane = new JTabbedPane();
        componentPane.add("Task", taskComponent);
        componentPane.add("System", sysComponent);
        componentPane.add("Queues", queuePane);

        componentPane.add("Planning", gantt.myPane);
        queuePane.setViewportView(queueArea);
//        queueArea.setPreferredSize(taskComponent.getPreferredSize());
        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
                componentPane);
        outer.setOneTouchExpandable(true);
        outer.setDividerLocation(200);
        outer.setDividerSize(6);
        outer.setBorder(null);
//        tables = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outer,
//                controlPanel);
//        tables.setOneTouchExpandable(true);
//        tables.setDividerLocation(200);
//        tables.setDividerSize(6);
//        tables.setBorder(null);
        statusBar = createStatusBar();
        installRepaintListener();
        setLayout(new BorderLayout());
        outer.setDividerLocation(200);
//        tables.setDividerLocation(10000);
        add(outer, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        installToolBar();
        installHandlers();
        installListeners();
        updateTitle();
    }

    protected mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    protected void installHandlers() {
        rubberband = new mxRubberband(taskComponent);
        keyboardHandler = new EditorKeyboardHandler(taskComponent);
    }

    protected void installToolBar() {
        add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
    }

    protected JLabel createStatusBar() {
        JLabel statusBar = new JLabel(mxResources.get("ready"));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        return statusBar;
    }

    protected void installDefaultFormat() {
        PageFormat pf = taskComponent.getPageFormat();
        pf.setOrientation(pf.LANDSCAPE);
        taskComponent.setPageFormat(pf);
    }

    protected void installRepaintListener() {
        taskComponent.getGraph().addListener(mxEvent.REPAINT,
                new mxIEventListener() {

                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (taskComponent.getTripleBuffer() != null) ? ""
                                : " (unbuffered)";
                        mxRectangle dirty = (mxRectangle) evt.getProperty("region");

                        if (dirty == null) {
                            status("Repaint all" + buffer);
                        } else {
                            status("Repaint: x=" + (int) (dirty.getX()) + " y="
                                    + (int) (dirty.getY()) + " w="
                                    + (int) (dirty.getWidth()) + " h="
                                    + (int) (dirty.getHeight()) + buffer);
                        }
                    }
                });

        sysComponent.getGraph().addListener(mxEvent.REPAINT,
                new mxIEventListener() {

                    public void invoke(Object source, mxEventObject evt) {
                        String buffer = (sysComponent.getTripleBuffer() != null) ? ""
                                : " (unbuffered)";
                        mxRectangle dirty = (mxRectangle) evt.getProperty("region");

                        if (dirty == null) {
                            status("Repaint all" + buffer);
                        } else {
                            status("Repaint: x=" + (int) (dirty.getX()) + " y="
                                    + (int) (dirty.getY()) + " w="
                                    + (int) (dirty.getWidth()) + " h="
                                    + (int) (dirty.getHeight()) + buffer);
                        }
                    }
                });
    }


    public EditorPalette insertPalette(String title) {
        final EditorPalette palette = new EditorPalette();
        final JScrollPane scrollPane = new JScrollPane(palette);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryPane.add(title, scrollPane);
        libraryPane.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                int w = scrollPane.getWidth()
                        - scrollPane.getVerticalScrollBar().getWidth();
                palette.setPreferredWidth(w);
            }
        });

        return palette;
    }

    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            taskComponent.zoomIn();
        } else {
            taskComponent.zoomOut();
        }

        status(mxResources.get("scale") + ": "
                + (int) (100 * taskComponent.getGraph().getView().getScale())
                + "%");
    }

    protected void showOutlinePopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                taskComponent);
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(
                mxResources.get("magnifyPage"));
        item.setSelected(graphOutline.isFitPage());

        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                graphOutline.setFitPage(!graphOutline.isFitPage());
                graphOutline.repaint();
            }
        });

        JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(
                mxResources.get("showLabels"));
        item2.setSelected(graphOutline.isDrawLabels());

        item2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
                graphOutline.repaint();
            }
        });

        JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(
                mxResources.get("buffering"));
        item3.setSelected(graphOutline.isTripleBuffered());

        item3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
                graphOutline.repaint();
            }
        });

        JPopupMenu menu = new JPopupMenu();
        menu.add(item);
        menu.add(item2);
        menu.add(item3);
        menu.show(taskComponent, pt.x, pt.y);

        e.consume();
    }

    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                taskComponent);
        EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
        menu.show(taskComponent, pt.x, pt.y);

        e.consume();
    }

    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", " + e.getY());
    }

    protected void installListeners() {
        MouseWheelListener wheelTracker = new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    BasicGraphEditor.this.mouseWheelMoved(e);
                }
            }
        };

        graphOutline.addMouseWheelListener(wheelTracker);
        taskComponent.addMouseWheelListener(wheelTracker);
        graphOutline.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showOutlinePopupMenu(e);
                }
            }
        });
        taskComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }
        });

        taskComponent.getGraphControl().addMouseMotionListener(
                new MouseMotionListener() {

                    public void mouseDragged(MouseEvent e) {
                        mouseLocationChanged(e);
                    }

                    public void mouseMoved(MouseEvent e) {
                        mouseDragged(e);
                    }
                });
    }

    public void setCurrentFile(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);

        if (oldValue != file) {
            updateTitle();
        }
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;

        firePropertyChange("modified", oldValue, modified);

        if (oldValue != modified) {
            updateTitle();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public static mxGraphComponent getTaskComponent() {
        return taskComponent;
    }

    public static mxGraphComponent getSysComponent() {
        return sysComponent;
    }

    public static void setTaskComponent(mxGraphComponent component) {
        taskComponent = component;
    }

    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    public JTabbedPane getLibraryPane() {
        return libraryPane;
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public mxUndoManager getSysManager() {
        return sysManager;
    }

    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }

    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl) {
        return new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                BasicGraphEditor.class.getResource(iconUrl)) : null) {

            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(componentPane.getSelectedComponent(), e.getID(), e.getActionCommand()));
            }
        };
    }

    public void status(String msg) {
        statusBar.setText(msg);
    }

    public void updateTitle() {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            String title = (currentFile != null) ? currentFile.getAbsolutePath() : mxResources.get("newDiagram");

            if (modified) {
                title += "*";
            }

            frame.setTitle(title + " - " + appTitle);
        }
    }

    public void about() {
//        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//        if (frame != null) {
//            EditorAboutFrame about = new EditorAboutFrame(frame);
//            about.setModal(true);
//            int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
//            int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
//            about.setLocation(x, y);
//            about.setVisible(true);
//        }
    }

    public void exit() {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            frame.dispose();
        }
    }

    public void setLookAndFeel(String clazz) {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            try {
                UIManager.setLookAndFeel(clazz);
                SwingUtilities.updateComponentTreeUI(frame);
                keyboardHandler = new EditorKeyboardHandler(taskComponent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public JFrame createFrame(JMenuBar menuBar) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.setSize(/*870, 640*/1000, 850);
        updateTitle();

        return frame;
    }

    @SuppressWarnings("serial")
    public Action graphLayout(final String key, boolean animate) {
        final mxIGraphLayout layout = createLayout(key, animate);

        if (layout != null) {
            return new AbstractAction(mxResources.get(key)) {

                public void actionPerformed(ActionEvent e) {
                    final mxGraph graph = taskComponent.getGraph();
                    Object cell = graph.getSelectionCell();

                    if (cell == null
                            || graph.getModel().getChildCount(cell) == 0) {
                        cell = graph.getDefaultParent();
                    }

                    graph.getModel().beginUpdate();
                    try {
                        long t0 = System.currentTimeMillis();
                        layout.execute(cell);
                        status("Layout: " + (System.currentTimeMillis() - t0)
                                + " ms");
                    } finally {
                        mxMorphing morph = new mxMorphing(taskComponent, 20,
                                1.2, 20);

                        morph.addListener(mxEvent.DONE, new mxIEventListener() {

                            public void invoke(Object sender, mxEventObject evt) {
                                graph.getModel().endUpdate();
                            }
                        });

                        morph.startAnimation();
                    }

                }
            };
        } else {
            return new AbstractAction(mxResources.get(key)) {

                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(taskComponent,
                            mxResources.get("noLayout"));
                }
            };
        }
    }

    protected mxIGraphLayout createLayout(String ident, boolean animate) {
        mxIGraphLayout layout = null;

        if (ident != null) {
            mxGraph graph = taskComponent.getGraph();

            if (ident.equals("verticalHierarchical")) {
                layout = new mxHierarchicalLayout(graph);
            } else if (ident.equals("horizontalHierarchical")) {
                layout = new mxHierarchicalLayout(graph, JLabel.WEST);
            } else if (ident.equals("verticalTree")) {
                layout = new mxCompactTreeLayout(graph, false);
            } else if (ident.equals("horizontalTree")) {
                layout = new mxCompactTreeLayout(graph, true);
            } else if (ident.equals("parallelEdges")) {
                layout = new mxParallelEdgeLayout(graph);
            } else if (ident.equals("placeEdgeLabels")) {
                layout = new mxEdgeLabelLayout(graph);
            } else if (ident.equals("organicLayout")) {
                layout = new mxOrganicLayout(graph);
            }
            if (ident.equals("verticalPartition")) {
                layout = new mxPartitionLayout(graph, false) {

                    public mxRectangle getContainerSize() {
                        return taskComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("horizontalPartition")) {
                layout = new mxPartitionLayout(graph, true) {

                    public mxRectangle getContainerSize() {
                        return taskComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("verticalStack")) {
                layout = new mxStackLayout(graph, false) {

                    public mxRectangle getContainerSize() {
                        return taskComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("horizontalStack")) {
                layout = new mxStackLayout(graph, true) {

                    public mxRectangle getContainerSize() {
                        return taskComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("circleLayout")) {
                layout = new mxCircleLayout(graph);
            }
        }

        return layout;
    }

    public mxGraphComponent getCurrentComponent() {
        return (mxGraphComponent)componentPane.getSelectedComponent();
    }

    public void generateGraph() {
        mxGraph graph = taskComponent.getGraph();
        graph.removeCells(graph.getChildCells(graph.getDefaultParent()));
        ArrayList<Object> nodes = new ArrayList<>();
        int w = 0;
        int l = 0;
        ArrayList<Object> links = new ArrayList<>();
        int[][] linkMatrix;
        Random r = new Random();

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            for (int i = 0; i < generationPanel.getNodesAmount(); i++) {
                mxCell cell = (mxCell)graph.insertVertex(parent, null,
                        Integer.toString(r.nextInt(generationPanel.getNodesMax() + generationPanel.getNodesMin() - 1) - generationPanel.getNodesMin() + 2),
                        0, 0, 50, 50, "shape=ellipse;fillColor=white;perimeter=ellipsePerimeter");
                nodes.add(cell);
                CellInfo info = new CellInfo(Integer.parseInt((String)cell.getValue()));
                w += Integer.parseInt((String)cell.getValue());
                info.setName("P" + (MatrixHolder.getLastPlaceName() + 1));
                cell.setValue(info);
                mxGeometry geom = new mxGeometry(0, 0, cell.getGeometry().getWidth(), 10);
                geom.setOffset(new mxPoint(0, cell.getGeometry().getHeight() + 5));
                geom.setRelative(true);
                mxCell label = new mxCell(i+1, geom, "shape=none");
                label.setVertex(true);
                label.setConnectable(false);
                cell.insert(label);
            }

            l = (int)(w / generationPanel.getCorrelation() - w);

            linkMatrix = new int[generationPanel.getNodesAmount()][generationPanel.getNodesAmount()];

            for (int i = 0; i < linkMatrix.length; i++) {
                for (int j = 0; j < linkMatrix[i].length; j++) {
                    linkMatrix[i][j] = 0;
                }
            }

            while (true) {
                int i = r.nextInt(linkMatrix.length);
                int j = r.nextInt(linkMatrix.length);
                int k = 0;
                if (i == j) continue;
//                if ((linkMatrix[i][j] >= generationPanel.getLinksMax())) continue;
                k = r.nextInt(generationPanel.getLinksMax() + generationPanel.getLinksMin() - 1) - generationPanel.getLinksMin() + 2;
                if (l >= k && i < j) {
                    int delta = (generationPanel.getLinksMax() - linkMatrix[i][j] >= k) ? k : (generationPanel.getLinksMax() - linkMatrix[i][j]);
                    linkMatrix[i][j] += k;
                    l -= k;
                    if (l < generationPanel.getLinksMin()) {
                        break;
                    }
                } else {
                    continue;
                }
            }

            do {
                for (int i = 0; i < linkMatrix.length; i++) {
                    for (int j = 0; j < linkMatrix[i].length; j++) {
                        if (linkMatrix[i][j] != 0) {
                            if (l > 0 && linkMatrix[i][j] < generationPanel.getLinksMax()) {
                                linkMatrix[i][j] += ((generationPanel.getLinksMax() - linkMatrix[i][j]) > l) ? l : (generationPanel.getLinksMax() - linkMatrix[i][j]);
                                l -= ((generationPanel.getLinksMax() - linkMatrix[i][j]) > l) ? l : (generationPanel.getLinksMax() - linkMatrix[i][j]);
                            }
                            CellInfo info = new CellInfo((byte) linkMatrix[i][j]);
                            mxCell cell = (mxCell)graph.insertEdge(parent, null, info.getWeight(), nodes.get(i), nodes.get(j), "rounded=1");
                            links.add(cell);
                            cell.setValue(info);
                            if (MatrixHolder.checkCircles()) {
                                System.out.println("sdf");
                                graph.removeCells(new Object[]{links.get(links.size() - 1)});
                                l += info.getWeight();
                                linkMatrix[i][j] = 0;
                                links.remove(links.size() - 1);
                            }
                        }

                    }
                }
            } while (l != 0);


        } finally {
            graph.getModel().endUpdate();
        }
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
//        layout.execute(parent);

        taskComponent.setGraph(graph);
    }
}
