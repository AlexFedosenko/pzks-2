package gui;

import com.mxgraph.analysis.mxDistanceCostFunction;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.io.gd.mxGdDocument;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.io.mxVdxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.png.mxPNGzTXtDecoder;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import core.MatrixHolder;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;

public class EditorActions {

    public static final BasicGraphEditor getEditor(ActionEvent e) {
        if (e.getSource() instanceof Component) {
            Component component = (Component) e.getSource();

            while (component != null
                    && !(component instanceof BasicGraphEditor)) {
                component = component.getParent();
            }

            return (BasicGraphEditor) component;
        }

        return null;
    }

    @SuppressWarnings("serial")
    public static class ToggleRulersItem extends JCheckBoxMenuItem {

        public ToggleRulersItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(editor.getTaskComponent().getColumnHeader() != null);

            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    mxGraphComponent graphComponent = editor.getTaskComponent();

                    if (graphComponent.getColumnHeader() != null) {
                        graphComponent.setColumnHeader(null);
                        graphComponent.setRowHeader(null);
                    } else {
                        graphComponent.setColumnHeaderView(new EditorRuler(
                                graphComponent,
                                EditorRuler.ORIENTATION_HORIZONTAL));
                        graphComponent.setRowHeaderView(new EditorRuler(
                                graphComponent,
                                EditorRuler.ORIENTATION_VERTICAL));
                    }
                }
            });
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleGridItem extends JCheckBoxMenuItem {

        public ToggleGridItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(true);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    mxGraphComponent graphComponent = editor.getTaskComponent();
                    mxGraph graph = graphComponent.getGraph();
                    boolean enabled = !graph.isGridEnabled();

                    graph.setGridEnabled(enabled);
                    graphComponent.setGridVisible(enabled);
                    graphComponent.repaint();
                    setSelected(enabled);
                }
            });
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleOutlineItem extends JCheckBoxMenuItem {

        public ToggleOutlineItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(true);

            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    final mxGraphOutline outline = editor.getGraphOutline();
                    outline.setVisible(!outline.isVisible());
                    outline.revalidate();

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (outline.getParent() instanceof JSplitPane) {
                                if (outline.isVisible()) {
                                    ((JSplitPane) outline.getParent()).setDividerLocation(editor.getHeight() - 300);
                                    ((JSplitPane) outline.getParent()).setDividerSize(6);
                                } else {
                                    ((JSplitPane) outline.getParent()).setDividerSize(0);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @SuppressWarnings("serial")
    public static class ExitAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                editor.exit();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class StylesheetAction extends AbstractAction {

        protected String stylesheet;

        public StylesheetAction(String stylesheet) {
            this.stylesheet = stylesheet;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxGraph graph = graphComponent.getGraph();
                mxCodec codec = new mxCodec();
                Document doc = mxUtils.loadDocument(EditorActions.class.getResource(stylesheet).toString());

                if (doc != null) {
                    codec.decode(doc.getDocumentElement(),
                            graph.getStylesheet());
                    graph.refresh();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ZoomPolicyAction extends AbstractAction {

        protected int zoomPolicy;

        public ZoomPolicyAction(int zoomPolicy) {
            this.zoomPolicy = zoomPolicy;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                graphComponent.setPageVisible(true);
                graphComponent.setZoomPolicy(zoomPolicy);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class GridStyleAction extends AbstractAction {

        protected int style;

        public GridStyleAction(int style) {
            this.style = style;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                graphComponent.setGridStyle(style);
                graphComponent.repaint();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class GridColorAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                Color newColor = JColorChooser.showDialog(graphComponent,
                        mxResources.get("gridColor"),
                        graphComponent.getGridColor());

                if (newColor != null) {
                    graphComponent.setGridColor(newColor);
                    graphComponent.repaint();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ScaleAction extends AbstractAction {

        protected double scale;

        public ScaleAction(double scale) {
            this.scale = scale;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                double scale = this.scale;

                if (scale == 0) {
                    String value = (String) JOptionPane.showInputDialog(
                            graphComponent, mxResources.get("value"),
                            mxResources.get("scale") + " (%)",
                            JOptionPane.PLAIN_MESSAGE, null, null, "");

                    if (value != null) {
                        scale = Double.parseDouble(value.replace("%", "")) / 100;
                    }
                }

                if (scale > 0) {
                    graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class PageSetupAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                PrinterJob pj = PrinterJob.getPrinterJob();
                PageFormat format = pj.pageDialog(graphComponent.getPageFormat());

                if (format != null) {
                    graphComponent.setPageFormat(format);
                    graphComponent.zoomAndCenter();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class PrintAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                PrinterJob pj = PrinterJob.getPrinterJob();

                if (pj.printDialog()) {
                    PageFormat pf = graphComponent.getPageFormat();
                    Paper paper = new Paper();
                    double margin = 36;
                    paper.setImageableArea(margin, margin, paper.getWidth()
                            - margin * 2, paper.getHeight() - margin * 2);
                    pf.setPaper(paper);
                    pj.setPrintable(graphComponent, pf);

                    try {
                        pj.print();
                    } catch (PrinterException e2) {
                        System.out.println(e2);
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SaveAction extends AbstractAction {

        protected boolean showDialog;
        protected String lastDir = null;

        public SaveAction(boolean showDialog) {
            this.showDialog = showDialog;
        }

        protected void saveXmlPng(BasicGraphEditor editor, String filename,
                Color bg) throws IOException {

            mxGraphComponent graphComponent = editor.getTaskComponent();
            mxGraph graph = graphComponent.getGraph();
            BufferedImage image = mxCellRenderer.createBufferedImage(graph,
                    null, 1, bg, graphComponent.isAntiAlias(), null,
                    graphComponent.getCanvas());
            mxCodec codec = new mxCodec();
            String xml = URLEncoder.encode(
                    mxUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
            mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
            param.setCompressedText(new String[]{"mxGraphModel", xml});
            FileOutputStream outputStream = new FileOutputStream(new File(
                    filename.split(".png")[0] + "-task" + ".png"));
            try {
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                        param);

                if (image != null) {
                    encoder.encode(image);

                    editor.setModified(false);
                    editor.setCurrentFile(new File(filename));
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            mxResources.get("noImageData"));
                }
            } finally {
                outputStream.close();
                MatrixHolder.updateMatrix();
            }

            graphComponent = editor.getSysComponent();
            graph = graphComponent.getGraph();
            image = mxCellRenderer.createBufferedImage(graph,
                    null, 1, bg, graphComponent.isAntiAlias(), null,
                    graphComponent.getCanvas());
            codec = new mxCodec();
            xml = URLEncoder.encode(
                    mxUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
            param = mxPngEncodeParam.getDefaultEncodeParam(image);
            param.setCompressedText(new String[]{"mxGraphModel", xml});
            outputStream = new FileOutputStream(new File(
                    filename.split(".png")[0] + "-sys" + ".png"));
            try {
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                        param);

                if (image != null) {
                    encoder.encode(image);

                    editor.setModified(false);
                    editor.setCurrentFile(new File(filename));
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            mxResources.get("noImageData"));
                }
            } finally {
                outputStream.close();
                MatrixHolder.updateMatrix();
            }
        }

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);
            if (editor != null) {
                mxGraphComponent graphComponent = editor.getTaskComponent();
                mxGraph graph = graphComponent.getGraph();
                FileFilter selectedFilter = null;
                DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
                        "PNG+XML " + mxResources.get("file") + " (.png)");
                FileFilter vmlFileFilter = new DefaultFileFilter(".html",
                        "VML " + mxResources.get("file") + " (.html)");
                String filename = null;
                boolean dialogShown = false;

                if (showDialog || editor.getCurrentFile() == null) {
                    String wd;

                    if (lastDir != null) {
                        wd = lastDir;
                    } else if (editor.getCurrentFile() != null) {
                        wd = editor.getCurrentFile().getParent();
                    } else {
                        wd = System.getProperty("user.dir");
                    }

                    JFileChooser fc = new JFileChooser(wd);
                    FileFilter defaultFilter = xmlPngFilter;
                    fc.addChoosableFileFilter(defaultFilter);
                    fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                            "mxGraph Editor " + mxResources.get("file")
                            + " (.mxe)"));
                    fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
                            "Graph Drawing " + mxResources.get("file")
                            + " (.txt)"));
                    fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
                            "SVG " + mxResources.get("file") + " (.svg)"));
                    fc.addChoosableFileFilter(vmlFileFilter);
                    fc.addChoosableFileFilter(new DefaultFileFilter(".html",
                            "HTML " + mxResources.get("file") + " (.html)"));
                    Object[] imageFormats = ImageIO.getReaderFormatNames();
                    HashSet<String> formats = new HashSet<String>();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString().toLowerCase();
                        formats.add(ext);
                    }

                    imageFormats = formats.toArray();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString();
                        fc.addChoosableFileFilter(new DefaultFileFilter("."
                                + ext, ext.toUpperCase() + " "
                                + mxResources.get("file") + " (." + ext + ")"));
                    }
                    fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
                            mxResources.get("allImages")));
                    fc.setFileFilter(defaultFilter);
                    int rc = fc.showDialog(null, mxResources.get("save"));
                    dialogShown = true;

                    if (rc != JFileChooser.APPROVE_OPTION) {
                        return;
                    } else {
                        lastDir = fc.getSelectedFile().getParent();
                    }

                    filename = fc.getSelectedFile().getAbsolutePath();
                    selectedFilter = fc.getFileFilter();

                    if (selectedFilter instanceof DefaultFileFilter) {
                        String ext = ((DefaultFileFilter) selectedFilter).getExtension();

                        if (!filename.toLowerCase().endsWith(ext)) {
                            filename += ext;
                        }
                    }

                    if (new File(filename).exists()
                            && JOptionPane.showConfirmDialog(graphComponent,
                            mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    filename = editor.getCurrentFile().getAbsolutePath();
                }

                try {
                    String ext = filename.substring(filename.lastIndexOf('.') + 1);

                    if (ext.equalsIgnoreCase("svg")) {
                        mxUtils.writeFile(mxUtils.getXml(mxCellRenderer.createSvgDocument(graph, null, 1, null, null).getDocumentElement()), filename);
                    } else if (selectedFilter == vmlFileFilter) {
                        mxUtils.writeFile(mxUtils.getXml(mxCellRenderer.createVmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
                    } else if (ext.equalsIgnoreCase("html")) {
                        mxUtils.writeFile(mxUtils.getXml(mxCellRenderer.createHtmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
                    } else if (ext.equalsIgnoreCase("mxe")
                            || ext.equalsIgnoreCase("xml")) {
                        mxCodec codec = new mxCodec();
                        String xml = mxUtils.getXml(codec.encode(graph.getModel()));

                        mxUtils.writeFile(xml, filename);

                        editor.setModified(false);
                        editor.setCurrentFile(new File(filename));
                    } else if (ext.equalsIgnoreCase("txt")) {
                        String content = mxGdCodec.encode(graph).getDocumentString();

                        mxUtils.writeFile(content, filename);
                    } else {
                        Color bg = null;

                        if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png"))
                                || JOptionPane.showConfirmDialog(
                                graphComponent, mxResources.get("transparentBackground")) != JOptionPane.YES_OPTION) {
                            bg = graphComponent.getBackground();
                        }

                        if (selectedFilter == xmlPngFilter
                                || (editor.getCurrentFile() != null
                                && ext.equalsIgnoreCase("png") && !dialogShown)) {
                            saveXmlPng(editor, filename, bg);
                        } else {
                            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, bg,
                                    graphComponent.isAntiAlias(), null,
                                    graphComponent.getCanvas());

                            if (image != null) {
                                ImageIO.write(image, ext, new File(filename));
                            } else {
                                JOptionPane.showMessageDialog(graphComponent,
                                        mxResources.get("noImageData"));
                            }
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(graphComponent,
                            ex.toString(), mxResources.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SelectShortestPathAction extends AbstractAction {

        protected boolean directed;

        public SelectShortestPathAction(boolean directed) {
            this.directed = directed;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxGraph graph = graphComponent.getGraph();
                mxIGraphModel model = graph.getModel();

                Object source = null;
                Object target = null;

                Object[] cells = graph.getSelectionCells();

                for (int i = 0; i < cells.length; i++) {
                    if (model.isVertex(cells[i])) {
                        if (source == null) {
                            source = cells[i];
                        } else if (target == null) {
                            target = cells[i];
                        }
                    }

                    if (source != null && target != null) {
                        break;
                    }
                }

                if (source != null && target != null) {
                    int steps = graph.getChildEdges(graph.getDefaultParent()).length;
                    Object[] path = mxGraphAnalysis.getInstance().getShortestPath(graph, source, target,
                            new mxDistanceCostFunction(), steps,
                            directed);
                    graph.setSelectionCells(path);
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            mxResources.get("noSourceAndTargetSelected"));
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SelectSpanningTreeAction extends AbstractAction {

        protected boolean directed;

        public SelectSpanningTreeAction(boolean directed) {
            this.directed = directed;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxGraph graph = graphComponent.getGraph();
                mxIGraphModel model = graph.getModel();

                Object parent = graph.getDefaultParent();
                Object[] cells = graph.getSelectionCells();

                for (int i = 0; i < cells.length; i++) {
                    if (model.getChildCount(cells[i]) > 0) {
                        parent = cells[i];
                        break;
                    }
                }

                Object[] v = graph.getChildVertices(parent);
                Object[] mst = mxGraphAnalysis.getInstance().getMinimumSpanningTree(graph, v,
                        new mxDistanceCostFunction(), directed);
                graph.setSelectionCells(mst);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleDirtyAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                graphComponent.showDirtyRectangle = !graphComponent.showDirtyRectangle;
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleConnectModeAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxConnectionHandler handler = graphComponent.getConnectionHandler();
                handler.setHandleEnabled(!handler.isHandleEnabled());
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleCreateTargetItem extends JCheckBoxMenuItem {

        public ToggleCreateTargetItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(true);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    mxGraphComponent graphComponent = editor.getTaskComponent();

                    if (graphComponent != null) {
                        mxConnectionHandler handler = graphComponent.getConnectionHandler();
                        handler.setCreateTarget(!handler.isCreateTarget());
                        setSelected(handler.isCreateTarget());
                    }
                }
            });
        }
    }

    @SuppressWarnings("serial")
    public static class PromptPropertyAction extends AbstractAction {

        protected Object target;
        protected String fieldname, message;

        public PromptPropertyAction(Object target, String message) {
            this(target, message, message);
        }

        public PromptPropertyAction(Object target, String message,
                String fieldname) {
            this.target = target;
            this.message = message;
            this.fieldname = fieldname;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component) {
                try {
                    Method getter = target.getClass().getMethod(
                            "get" + fieldname);
                    Object current = getter.invoke(target);
                    if (current instanceof Integer) {
                        Method setter = target.getClass().getMethod(
                                "set" + fieldname, new Class[]{int.class});

                        String value = (String) JOptionPane.showInputDialog(
                                (Component) e.getSource(), "Value", message,
                                JOptionPane.PLAIN_MESSAGE, null, null, current);

                        if (value != null) {
                            setter.invoke(target, Integer.parseInt(value));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                graphComponent.repaint();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class TogglePropertyItem extends JCheckBoxMenuItem {

        public TogglePropertyItem(Object target, String name, String fieldname) {
            this(target, name, fieldname, false);
        }

        public TogglePropertyItem(Object target, String name, String fieldname,
                boolean refresh) {
            this(target, name, fieldname, refresh, null);
        }

        public TogglePropertyItem(final Object target, String name,
                final String fieldname, final boolean refresh,
                ActionListener listener) {
            super(name);
            if (listener != null) {
                addActionListener(listener);
            }
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    execute(target, fieldname, refresh);
                }
            });
            PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equalsIgnoreCase(fieldname)) {
                        update(target, fieldname);
                    }
                }
            };

            if (target instanceof mxGraphComponent) {
                ((mxGraphComponent) target).addPropertyChangeListener(propertyChangeListener);
            } else if (target instanceof mxGraph) {
                ((mxGraph) target).addPropertyChangeListener(propertyChangeListener);
            }
            update(target, fieldname);
        }

        public void update(Object target, String fieldname) {
            if (target != null && fieldname != null) {
                try {
                    Method getter = target.getClass().getMethod(
                            "is" + fieldname);

                    if (getter != null) {
                        Object current = getter.invoke(target);

                        if (current instanceof Boolean) {
                            setSelected(((Boolean) current).booleanValue());
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        public void execute(Object target, String fieldname, boolean refresh) {
            if (target != null && fieldname != null) {
                try {
                    Method getter = target.getClass().getMethod(
                            "is" + fieldname);
                    Method setter = target.getClass().getMethod(
                            "set" + fieldname, new Class[]{boolean.class});

                    Object current = getter.invoke(target);

                    if (current instanceof Boolean) {
                        boolean value = !((Boolean) current).booleanValue();
                        setter.invoke(target, value);
                        setSelected(value);
                    }

                    if (refresh) {
                        mxGraph graph = null;

                        if (target instanceof mxGraph) {
                            graph = (mxGraph) target;
                        } else if (target instanceof mxGraphComponent) {
                            graph = ((mxGraphComponent) target).getGraph();
                        }

                        graph.refresh();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class HistoryAction extends AbstractAction {

        protected boolean undo;

        public HistoryAction(boolean undo) {
            this.undo = undo;
        }

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                if (undo) {
                    editor.getUndoManager().undo();
                    MatrixHolder.updateMatrix();
                } else {
                    editor.getUndoManager().redo();
                    MatrixHolder.updateMatrix();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class FontStyleAction extends AbstractAction {

        protected boolean bold;

        public FontStyleAction(boolean bold) {
            this.bold = bold;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                Component editorComponent = null;

                if (graphComponent.getCellEditor() instanceof mxCellEditor) {
                    editorComponent = ((mxCellEditor) graphComponent.getCellEditor()).getEditor();
                }

                if (editorComponent instanceof JEditorPane) {
                    JEditorPane editorPane = (JEditorPane) editorComponent;
                    int start = editorPane.getSelectionStart();
                    int ende = editorPane.getSelectionEnd();
                    String text = editorPane.getSelectedText();

                    if (text == null) {
                        text = "";
                    }
                    try {
                        HTMLEditorKit editorKit = new HTMLEditorKit();
                        HTMLDocument document = (HTMLDocument) editorPane.getDocument();
                        document.remove(start, (ende - start));
                        editorKit.insertHTML(document, start, ((bold) ? "<b>"
                                : "<i>") + text + ((bold) ? "</b>" : "</i>"),
                                0, 0, (bold) ? HTML.Tag.B : HTML.Tag.I);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    editorPane.requestFocus();
                    editorPane.select(start, ende);
                } else {
                    mxIGraphModel model = graphComponent.getGraph().getModel();
                    model.beginUpdate();
                    try {
                        graphComponent.stopEditing(false);
                        graphComponent.getGraph().toggleCellStyleFlags(
                                mxConstants.STYLE_FONTSTYLE,
                                (bold) ? mxConstants.FONT_BOLD
                                : mxConstants.FONT_ITALIC);
                    } finally {
                        model.endUpdate();
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class WarningAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                Object[] cells = graphComponent.getGraph().getSelectionCells();

                if (cells != null && cells.length > 0) {
                    String warning = JOptionPane.showInputDialog(mxResources.get("enterWarningMessage"));

                    for (int i = 0; i < cells.length; i++) {
                        graphComponent.setCellWarning(cells[i], warning);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            mxResources.get("noCellSelected"));
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class NewAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                if (!editor.isModified()
                        || JOptionPane.showConfirmDialog(editor,
                        mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {
                    mxGraph graph = editor.getTaskComponent().getGraph();
                    mxCell root = new mxCell();
                    root.insert(new mxCell());
                    graph.getModel().setRoot(root);

                    editor.setModified(false);
                    editor.setCurrentFile(null);
                    editor.getTaskComponent().zoomAndCenter();

                    graph = editor.getSysComponent().getGraph();
                    mxCell root2 = new mxCell();
                    root.insert(new mxCell());
                    graph.getModel().setRoot(root2);

                    editor.setModified(false);
                    editor.setCurrentFile(null);
                    editor.getSysComponent().zoomAndCenter();
                    MatrixHolder.updateMatrix();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ImportAction extends AbstractAction {

        protected String lastDir;

        public static String addStencilShape(EditorPalette palette, String nodeXml,
                String path) {
            int lessthanIndex = nodeXml.indexOf("<");
            nodeXml = nodeXml.substring(lessthanIndex);
            mxStencilShape newShape = new mxStencilShape(nodeXml);
            String name = newShape.getName();
            ImageIcon icon = null;
            if (path != null) {
                String iconPath = path + newShape.getIconPath();
                icon = new ImageIcon(iconPath);
            }
            mxGraphics2DCanvas.putShape(name, newShape);

            if (palette != null && icon != null) {
                palette.addTemplate(name, icon, "shape=" + name, 80, 80, "");
            }

            return name;
        }

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");
                JFileChooser fc = new JFileChooser(wd);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.addChoosableFileFilter(new DefaultFileFilter(".shape",
                        "Dia Shape " + mxResources.get("file") + " (.shape)"));
                int rc = fc.showDialog(null, mxResources.get("importStencil"));
                if (rc == JFileChooser.APPROVE_OPTION) {
                    lastDir = fc.getSelectedFile().getParent();
                    try {
                        if (fc.getSelectedFile().isDirectory()) {
                            EditorPalette palette = editor.insertPalette(fc.getSelectedFile().getName());

                            for (File f : fc.getSelectedFile().listFiles(
                                    new FilenameFilter() {

                                        public boolean accept(File dir,
                                                String name) {
                                            return name.toLowerCase().endsWith(
                                                    ".shape");
                                        }
                                    })) {
                                String nodeXml = mxUtils.readFile(f.getAbsolutePath());
                                addStencilShape(palette, nodeXml, f.getParent() + File.separator);
                            }

                            JComponent scrollPane = (JComponent) palette.getParent().getParent();
                            editor.getLibraryPane().setSelectedComponent(
                                    scrollPane);
                        } else {
                            String nodeXml = mxUtils.readFile(fc.getSelectedFile().getAbsolutePath());
                            String name = addStencilShape(null, nodeXml, null);

                            JOptionPane.showMessageDialog(editor, mxResources.get("stencilImported",
                                    new String[]{name}));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class OpenAction extends AbstractAction {

        protected String lastDir;

        protected void resetEditor(BasicGraphEditor editor) {
            editor.setModified(false);
            editor.getUndoManager().clear();
            editor.getSysManager().clear();
            editor.getTaskComponent().zoomAndCenter();
        }

        protected void openXmlPng(BasicGraphEditor editor, File file)
                throws IOException {
            Map<String, String> text = mxPNGzTXtDecoder.decodezTXt(new FileInputStream(file));
            if (text != null) {
                String value = text.get("mxGraphModel");
                if (value != null) {
                    Document document = mxUtils.parseXml(URLDecoder.decode(
                            value, "UTF-8"));
                    mxCodec codec = new mxCodec(document);

                    if (file.getCanonicalPath().endsWith("-task.png")) {
                        codec.decode(document.getDocumentElement(), editor.getTaskComponent().getGraph().getModel());
                        editor.setCurrentFile(new File(file.getCanonicalPath()));
                        resetEditor(editor);
                    }
                    if (file.getCanonicalPath().endsWith("-sys.png")) {
                        codec.decode(document.getDocumentElement(), editor.getSysComponent().getGraph().getModel());
                        editor.setCurrentFile(new File(file.getCanonicalPath()));
                        resetEditor(editor);
                    }
//                    mxGraph graph = editor.getTaskComponent().getGraph();
                    try {
                        MatrixHolder.updateMatrix();
                    } catch(Exception e) {}
//                    Object[] array = graph.getChildCells(graph.getDefaultParent());

                    return;
                }
            }

            MatrixHolder.updateMatrix();

            JOptionPane.showMessageDialog(editor,
                    mxResources.get("imageContainsNoDiagramData"));
        }

        protected void openVdx(BasicGraphEditor editor, File file,
                Document document) {
            mxGraph graph = editor.getTaskComponent().getGraph();
            String filename = file.getName();
            filename = filename.substring(0, filename.length() - 4) + ".mxe";

            if (new File(filename).exists()
                    && JOptionPane.showConfirmDialog(editor,
                    mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                return;
            }

            ((mxGraphModel) graph.getModel()).clear();
            mxVdxCodec.decode(document, graph);
            editor.getTaskComponent().zoomAndCenter();
            editor.setCurrentFile(new File(lastDir + "/" + filename));
        }

        protected void openGD(BasicGraphEditor editor, File file,
                mxGdDocument document) {
            mxGraph graph = editor.getTaskComponent().getGraph();
            String filename = file.getName();
            filename = filename.substring(0, filename.length() - 4) + ".mxe";

            if (new File(filename).exists()
                    && JOptionPane.showConfirmDialog(editor,
                    mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                return;
            }

            ((mxGraphModel) graph.getModel()).clear();
            mxGdCodec.decode(document, graph);
            editor.getTaskComponent().zoomAndCenter();
            editor.setCurrentFile(new File(lastDir + "/" + filename));
        }

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);
            if (editor != null) {
                if (!editor.isModified()
                        || JOptionPane.showConfirmDialog(editor,
                        mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {
                    mxGraph graph = editor.getTaskComponent().getGraph();

                    if (graph != null) {
                        String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");

                        JFileChooser fc = new JFileChooser(wd);
                        DefaultFileFilter defaultFilter = new DefaultFileFilter(
                                ".mxe", mxResources.get("allSupportedFormats")
                                + " (.mxe, .png, .vdx)") {

                            public boolean accept(File file) {
                                String lcase = file.getName().toLowerCase();

                                return super.accept(file)
                                        || lcase.endsWith(".png")
                                        || lcase.endsWith(".vdx");
                            }
                        };
                        fc.addChoosableFileFilter(defaultFilter);

                        fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                                "mxGraph Editor " + mxResources.get("file")
                                + " (.mxe)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".png",
                                "PNG+XML  " + mxResources.get("file")
                                + " (.png)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
                                "XML Drawing  " + mxResources.get("file")
                                + " (.vdx)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
                                "Graph Drawing  " + mxResources.get("file")
                                + " (.txt)"));
                        fc.setFileFilter(defaultFilter);
                        int rc = fc.showDialog(null,
                                mxResources.get("openFile"));

                        if (rc == JFileChooser.APPROVE_OPTION) {
                            lastDir = fc.getSelectedFile().getParent();
                            try {
                                if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png")) {
                                    openXmlPng(editor, fc.getSelectedFile());
                                } else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".txt")) {
                                    mxGdDocument document = new mxGdDocument();
                                    document.parse(mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));
                                    openGD(editor, fc.getSelectedFile(),
                                            document);
                                } else {
                                    Document document = mxUtils.parseXml(mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));

                                    if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".vdx")) {
                                        openVdx(editor, fc.getSelectedFile(),
                                                document);
                                    } else {
                                        mxCodec codec = new mxCodec(document);
                                        codec.decode(
                                                document.getDocumentElement(),
                                                graph.getModel());
                                        editor.setCurrentFile(fc.getSelectedFile());
                                    }

                                    resetEditor(editor);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                        editor.getTaskComponent(),
                                        ex.toString(),
                                        mxResources.get("error"),
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ToggleAction extends AbstractAction {

        protected String key;
        protected boolean defaultValue;

        public ToggleAction(String key) {
            this(key, false);
        }

        public ToggleAction(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null) {
                graph.toggleCellStyles(key, defaultValue);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SetLabelPositionAction extends AbstractAction {

        protected String labelPosition, alignment;

        public SetLabelPositionAction(String labelPosition, String alignment) {
            this.labelPosition = labelPosition;
            this.alignment = alignment;
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.getModel().beginUpdate();
                try {
                    if (labelPosition.equals(mxConstants.ALIGN_LEFT)
                            || labelPosition.equals(mxConstants.ALIGN_CENTER)
                            || labelPosition.equals(mxConstants.ALIGN_RIGHT)) {
                        graph.setCellStyles(mxConstants.STYLE_LABEL_POSITION,
                                labelPosition);
                        graph.setCellStyles(mxConstants.STYLE_ALIGN, alignment);
                    } else {
                        graph.setCellStyles(
                                mxConstants.STYLE_VERTICAL_LABEL_POSITION,
                                labelPosition);
                        graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN,
                                alignment);
                    }
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SetStyleAction extends AbstractAction {

        protected String value;

        public SetStyleAction(String value) {
            this.value = value;
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.setCellStyle(value);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class KeyValueAction extends AbstractAction {

        protected String key, value;

        public KeyValueAction(String key) {
            this(key, null);
        }

        public KeyValueAction(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.setCellStyles(key, value);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class PromptValueAction extends AbstractAction {

        protected String key, message;

        public PromptValueAction(String key, String message) {
            this.key = key;
            this.message = message;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component) {
                mxGraph graph = mxGraphActions.getGraph(e);

                if (graph != null && !graph.isSelectionEmpty()) {
                    String value = (String) JOptionPane.showInputDialog(
                            (Component) e.getSource(),
                            mxResources.get("value"), message,
                            JOptionPane.PLAIN_MESSAGE, null, null, "");

                    if (value != null) {
                        if (value.equals(mxConstants.NONE)) {
                            value = null;
                        }

                        graph.setCellStyles(key, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class AlignCellsAction extends AbstractAction {

        protected String align;

        public AlignCellsAction(String align) {
            this.align = align;
        }

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.alignCells(align);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class AutosizeAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                Object[] cells = graph.getSelectionCells();
                mxIGraphModel model = graph.getModel();

                model.beginUpdate();
                try {
                    for (int i = 0; i < cells.length; i++) {
                        graph.updateCellSize(cells[i]);
                    }
                } finally {
                    model.endUpdate();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ColorAction extends AbstractAction {

        protected String name, key;

        public ColorAction(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxGraph graph = graphComponent.getGraph();

                if (!graph.isSelectionEmpty()) {
                    Color newColor = JColorChooser.showDialog(graphComponent,
                            name, null);

                    if (newColor != null) {
                        graph.setCellStyles(key, mxUtils.hexString(newColor));
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class BackgroundImageAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                String value = (String) JOptionPane.showInputDialog(
                        graphComponent, mxResources.get("backgroundImage"),
                        "URL", JOptionPane.PLAIN_MESSAGE, null, null,
                        "http://www.callatecs.com/images/background2.JPG");
                if (value != null) {
                    if (value.length() == 0) {
                        graphComponent.setBackgroundImage(null);
                    } else {
                        Image background = mxUtils.loadImage(value);
                        if (background != null) {
                            graphComponent.setBackgroundImage(new ImageIcon(
                                    background));
                        }
                    }
                    graphComponent.getGraph().repaint();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class BackgroundAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                Color newColor = JColorChooser.showDialog(graphComponent,
                        mxResources.get("background"), null);

                if (newColor != null) {
                    graphComponent.getViewport().setOpaque(false);
                    graphComponent.setBackground(newColor);
                }
                graphComponent.getGraph().repaint();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class PageBackgroundAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                Color newColor = JColorChooser.showDialog(graphComponent,
                        mxResources.get("pageBackground"), null);
                if (newColor != null) {
                    graphComponent.setPageBackgroundColor(newColor);
                }
                graphComponent.repaint();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class StyleAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
                mxGraph graph = graphComponent.getGraph();
                String initial = graph.getModel().getStyle(
                        graph.getSelectionCell());
                String value = (String) JOptionPane.showInputDialog(
                        graphComponent, mxResources.get("style"),
                        mxResources.get("style"), JOptionPane.PLAIN_MESSAGE,
                        null, null, initial);

                if (value != null) {
                    graph.setCellStyle(value);
                }
            }
        }
    }
}
