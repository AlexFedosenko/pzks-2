package gui;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxConnectionConstraint;
import com.mxgraph.view.mxGraph;
import core.Algorithms;
import core.CellInfo;
import core.MatrixHolder;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

public class GraphEditor extends BasicGraphEditor {

    private static final long serialVersionUID = -4601740824088314699L;
    public static final GraphEditor editor = new GraphEditor();
    public static final NumberFormat numberFormat = NumberFormat.getInstance();
    public static URL url = null;

    public GraphEditor() {
        this("PZKS-2 ©", new CustomGraphComponent(new CustomGraph()), new CustomGraphComponent(new CustomGraph()));
    }

    public GraphEditor(String appTitle, mxGraphComponent component1, mxGraphComponent component2) {
        super(appTitle, component1, component2);
        final mxGraph graph = taskComponent.getGraph();

        EditorPalette shapesPalette = insertPalette(mxResources.get("shape"));
        shapesPalette.setBackground(Color.GRAY);
        shapesPalette.setGradientColor(Color.LIGHT_GRAY);
        shapesPalette.addListener(mxEvent.SELECT, new mxIEventListener() {

            public void invoke(Object sender, mxEventObject evt) {
                Object tmp = evt.getProperty("transferable");

                if (tmp instanceof mxGraphTransferable) {
                    mxGraphTransferable t = (mxGraphTransferable) tmp;
                    Object cell = t.getCells()[0];

                    if (graph.getModel().isEdge(cell)) {
                        ((CustomGraph) graph).setEdgeTemplate(cell);
                    }
                }
            }
        });

//        shapesPalette.addTemplate(
//                "Container",
//                new ImageIcon(
//                GraphEditor.class.getResource("/images/swimlane.png")),
//                "swimlane", 280, 280, new CellInfo("Container"));
//        shapesPalette.addTemplate(
//                "Transition",
//                new ImageIcon(
//                GraphEditor.class.getResource("/images/rectangle.png")),
//                null, 30, 70, new CellInfo(1.0));
//        shapesPalette.addTemplate(
//                "Immediate transition",
//                new ImageIcon(
//                GraphEditor.class.getResource("/images/rounded.png")),
//                "shape=rectangle;"
//                + "perimeter=rectanglePerimeter;"
//                + "fillColor=black;"
//                + "strokeColor=black", 15, 70, new CellInfo(0.0));
        shapesPalette.addTemplate(
                "Vertex",
                new ImageIcon(
                GraphEditor.class.getResource("/images/ellipse.png")),
                "ellipse", 50, 50, new CellInfo(0));
        shapesPalette.addEdgeTemplate(
                "Straight",
                new ImageIcon(
                GraphEditor.class.getResource("/images/straight.png")),
                "straight", 120, 120, new CellInfo());
        shapesPalette.addEdgeTemplate(
                "Horizontal Connector",
                new ImageIcon(
                GraphEditor.class.getResource("/images/connect.png")),
                null, 100, 100, new CellInfo());
        shapesPalette.addEdgeTemplate(
                "Vertical Connector",
                new ImageIcon(
                GraphEditor.class.getResource("/images/vertical.png")),
                "vertical", 100, 100, new CellInfo());
//        shapesPalette.addEdgeTemplate(
//                "Entity Relation",
//                new ImageIcon(
//                GraphEditor.class.getResource("/images/entity.png")),
//                "entity", 100, 100, new CellInfo());
    }

    public static class CustomGraphComponent extends mxGraphComponent {

        private static final long serialVersionUID = -6833603133512882012L;

        public CustomGraphComponent(mxGraph graph) {
            super(graph);

            setPageVisible(false);
            setGridVisible(true);
            setToolTips(true);
            mxCodec codec = new mxCodec();
            Document doc = mxUtils.loadDocument(GraphEditor.class.getResource(
                    "/default-style.xml").toString());
            codec.decode(doc.getDocumentElement(), graph.getStylesheet());
            getViewport().setOpaque(false);
            setBackground(Color.WHITE);
            getGraphControl().addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    Object object = getCellAt(e.getX(), e.getY());
                    getGraph().setCellsEditable(false);
                    if (object != null && e.getClickCount() == 2) {
                        mxCell cell = (mxCell) object;

                        if (!cell.isEdge()) {
                            CellInfo info = (CellInfo) cell.getValue();
                            if (info.isVertex()) {
                                String marks = JOptionPane.showInputDialog(
                                        "Input new amount of marks");
                                if (marks != null) {
                                    info.setMark(Integer.parseInt(marks));

                                }
                                MatrixHolder.updateMatrix();
                                BasicGraphEditor.queueArea.setText(MatrixHolder.printQueues());
                                refresh();
                            }
                            if (info.isTransition()) {
                                String variance = JOptionPane.showInputDialog(
                                        "Input new variance");
                                if (variance != null) {
                                    double var = Double.parseDouble(variance);
                                    info.setVariance(var);
                                    if (var == 0) {
                                        cell.setStyle("fillColor=#000000");
                                        cell.getGeometry().setWidth(20);
                                    } else {
                                        cell.getGeometry().setWidth(30);
                                    }
                                }

                                String prob = JOptionPane.showInputDialog(
                                        "Input new probability");
                                if (prob != null) {
                                    double p = Double.parseDouble(prob);
                                    info.setProbability(p);
                                }
                                cell.getChildAt(1).setValue("g: " + info.getVariance());
                                String lambda = JOptionPane.showInputDialog(
                                        "Input new intensity");
                                if (lambda != null) {
                                    double l = Double.parseDouble(lambda);
                                    info.setLambda(l);
                                }
                                cell.getChildAt(2).setValue("λ: " + info.getLambda());
                                refresh();
                            }
                            if (info.isContainer()) {
                                String name = "     ";
                                name += JOptionPane.showInputDialog(
                                        "Input new name");
                                info.setName(name);

                                refresh();
                            }
                        } else {
//                            CellInfo info = (CellInfo) cell.getValue();
                            String weight = JOptionPane.showInputDialog(
                                    "Input new weight");
//                            if (weight != null) {
//                                info.setWeight((byte) Integer.parseInt(weight));
//                            }
                            cell.setValue(weight);
                            MatrixHolder.updateMatrix();
                            BasicGraphEditor.queueArea.setText(MatrixHolder.printQueues());
                            refresh();
                        }
                    }
//                    super.mouseClicked(e);
                }
            });
        }

        @Override
        public ImageIcon getFoldingIcon(mxCellState state) {
            return null;
        }

        @Override
        public Object[] importCells(Object[] cells, double dx, double dy,
                Object target, Point location) {
            if (target == null && cells.length == 1 && location != null) {
                target = getCellAt(location.x, location.y);

                if (target instanceof mxICell && cells[0] instanceof mxICell) {
                    mxICell targetCell = (mxICell) target;
                    mxICell dropCell = (mxICell) cells[0];

                    if (targetCell.isVertex() == dropCell.isVertex()
                            || targetCell.isEdge() == dropCell.isEdge()) {
                        mxIGraphModel model = graph.getModel();
                        model.setStyle(target, model.getStyle(cells[0]));
                        graph.setSelectionCell(target);

                        return null;
                    }
                }
            }

            return super.importCells(cells, dx, dy, target, location);
        }
    }

    public static class CustomGraph extends mxGraph {

        protected Object edgeTemplate;
        public static ArrayList<Object> places = new ArrayList<Object>();
        public static ArrayList<Object> transitions = new ArrayList<Object>();
        public static ArrayList<Integer> marks = new ArrayList<Integer>();
        public static ArrayList<Double> times = new ArrayList<Double>();

        public CustomGraph() {
            setAllowLoops(false);
            setCellsCloneable(false);
            setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        }

        public static String getType(mxCell cell) {
            String type = null;
            String str = null;
            if (cell instanceof mxCell) {
                str = (String) cell.getValue();
                try {
                    int in = Integer.parseInt(str);
                    type = "vertex";
                } catch (Exception e) {
                    if (str.equals(" ")) {
                        type = "immediate";
                    }
                    try {
                        double d = Double.parseDouble(str);
                        type = "transition";
                    } catch (Exception q) {
                    }
                }
                if (type == null) {
                    type = "edge";
                }
            }
            return type;
        }

        @Override
        public void cellsAdded(Object[] os, Object o, Integer intgr, Object o1, Object o2, boolean bln) {
            super.cellsAdded(os, o, intgr, o1, o2, bln);
            for (Object object : os) {
                if (object instanceof mxCell) {
                    mxCell cell = (mxCell) object;
                    if (cell.getValue() instanceof CellInfo) {
                        CellInfo info = (CellInfo) cell.getValue();
                        if (info.isVertex()) {
                            if (editor.getCurrentComponent() == editor.getTaskComponent()) {
                                info.setName("P" + (MatrixHolder.getLastPlaceName() + 1));
                            } else {
                                info.setName("P" + (MatrixHolder.getLastSysName() + 1));
                            }
                        }
                        if (info.isTransition()) {
//                            info.setName("T" + (MatrixHolder.getLastTransitionName() + 1));
                        }
                        getModel().beginUpdate();
                        {
                            mxGeometry geom = new mxGeometry(0, 0, cell.getGeometry().getWidth(), 10);
                            geom.setOffset(new mxPoint(0, cell.getGeometry().getHeight() + 5));
                            geom.setRelative(true);
                            mxCell label = new mxCell(info.getName(), geom, "shape=none");
                            label.setVertex(true);
                            label.setConnectable(false);
                            cell.insert(label);
                            if (info.isTransition() && !info.isImmediate()) {
                                geom = new mxGeometry(0, 0, cell.getGeometry().getWidth(), 10);
                                geom.setOffset(new mxPoint(0, -10));
                                geom.setRelative(true);
                                label = new mxCell("g: " + info.getVariance(), geom, "shape=none");
                                label.setVertex(true);
                                label.setConnectable(false);
                                cell.insert(label);
                                geom = new mxGeometry(0, 0, cell.getGeometry().getWidth(), 10);
                                geom.setOffset(new mxPoint(0, -20));
                                geom.setRelative(true);
                                label = new mxCell("λ: " + info.getLambda(), geom, "shape=none");
                                label.setVertex(true);
                                label.setConnectable(false);
                                cell.insert(label);
                            }
                        }
                        getModel().endUpdate();
                        MatrixHolder.updateMatrix();
                        BasicGraphEditor.queueArea.setText(MatrixHolder.printQueues());
                        refresh();
                        refresh();

                    }
                }
            }
            MatrixHolder.updateMatrix();
        }

        @Override
        public void cellsRemoved(Object[] os) {
            super.cellsRemoved(os);
            for (Object object : os) {
                if (object instanceof mxCell) {
                    refresh();
                }
            }
            MatrixHolder.updateMatrix();
            BasicGraphEditor.queueArea.setText(MatrixHolder.printQueues());
        }

        @Override
        public void cellConnected(Object edge, Object terminal, boolean source, mxConnectionConstraint constraint) {
            super.cellConnected(edge, terminal, source, constraint);
            mxParallelEdgeLayout layout = new mxParallelEdgeLayout(this);
            mxCell edgeSource = (mxCell) ((mxCell) edge).getSource();
            mxCell edgeTarget = (mxCell) ((mxCell) edge).getTarget();
            CellInfo infoSourse = (CellInfo) edgeSource.getValue();

            mxCell cell = (mxCell) edge;
            if (edgeSource != null && edgeTarget != null) {
                CellInfo infoTarget = (CellInfo) edgeTarget.getValue();
//                if (infoSourse.getType().equalsIgnoreCase(infoTarget.getType())) {
//                    cell.removeFromParent();
//                }
            }
            mxICell parent = cell.getParent();
            layout.execute(getDefaultParent());
            MatrixHolder.updateMatrix();
            BasicGraphEditor.queueArea.setText(MatrixHolder.printQueues());
            refresh();
        }

        public void setEdgeTemplate(Object template) {
            edgeTemplate = template;
        }

        @Override
        public Object createEdge(Object parent, String id, Object value,
                Object source, Object target, String style) {
            if (edgeTemplate != null) {
                mxCell edge = (mxCell) cloneCells(new Object[]{edgeTemplate})[0];
                edge.setId(id);
                return edge;
            }
            return super.createEdge(parent, id, "1", source, target, style);
        }
    }

    public static void main(String[] args) {
        mxCodecRegistry.register(new mxObjectCodec(new CellInfo()));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
        }

        mxConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        editor.createFrame(new EditorMenuBar(editor)).setVisible(true);

    }
}
