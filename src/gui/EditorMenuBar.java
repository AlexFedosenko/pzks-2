package gui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorMenuBar extends JMenuBar {

    private static final long serialVersionUID = 4060203894740766714L;

    @SuppressWarnings("serial")
    public EditorMenuBar(final BasicGraphEditor editor) {
        final mxGraphComponent taskComponent = editor.getTaskComponent();
        final mxGraphComponent sysComponent = editor.getSysComponent();
        final mxGraph graph = taskComponent.getGraph();
        JMenu menu = null;
        JMenu submenu = null;
        menu = add(new JMenu(mxResources.get("file")));

        menu.add(editor.bind(mxResources.get("new"), new EditorActions.NewAction(),
                "/images/new.gif"));
        menu.add(editor.bind(mxResources.get("openFile"), new EditorActions.OpenAction(),
                "/images/open.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("save"), new EditorActions.SaveAction(false),
                "/images/save.gif"));
        menu.add(editor.bind(mxResources.get("saveAs"), new EditorActions.SaveAction(true),
                "/images/saveas.gif"));
        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("pageSetup"),
                new EditorActions.PageSetupAction(),
                "/images/pagesetup.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("exit"), new EditorActions.ExitAction()));
        menu = add(new JMenu(mxResources.get("edit")));

        menu.add(editor.bind(mxResources.get("undo"), new EditorActions.HistoryAction(true),
                "/images/undo.gif"));
        menu.add(editor.bind(mxResources.get("redo"), new EditorActions.HistoryAction(false),
                "/images/redo.gif"));
        menu.addSeparator();
        menu.add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/images/cut.gif"));
        menu.add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(),
                "/images/copy.gif"));
        menu.add(editor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(),
                "/images/paste.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("delete"), mxGraphActions.getDeleteAction(),
                "/images/delete.gif"));

        menu.addSeparator();

        menu.add(editor.bind(mxResources.get("selectAll"), mxGraphActions.getSelectAllAction()));
        menu.add(editor.bind(mxResources.get("selectNone"), mxGraphActions.getSelectNoneAction()));

        menu.addSeparator();
        menu = add(new JMenu(mxResources.get("view")));

        JMenuItem item = menu.add(new EditorActions.TogglePropertyItem(taskComponent,
                mxResources.get("pageLayout"), "PageVisible", true,
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (taskComponent.isPageVisible()
                                && taskComponent.isCenterPage()) {
                            taskComponent.zoomAndCenter();
                            sysComponent.zoomAndCenter();
                        } else {
                            taskComponent.getGraphControl().updatePreferredSize();
                            sysComponent.getGraphControl().updatePreferredSize();
                        }
                    }
                }));

        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof EditorActions.TogglePropertyItem) {
                    final mxGraphComponent taskComponent = editor.getTaskComponent();
                    final mxGraphComponent sysComponent = editor.getSysComponent();
                    EditorActions.TogglePropertyItem toggleItem = (EditorActions.TogglePropertyItem) e.getSource();

                    if (toggleItem.isSelected()) {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                taskComponent.scrollToCenter(true);
                                taskComponent.scrollToCenter(false);
                                sysComponent.scrollToCenter(true);
                                sysComponent.scrollToCenter(false);
                            }
                        });
                    } else {
                        mxPoint tr = taskComponent.getGraph().getView().getTranslate();

                        if (tr.getX() != 0 || tr.getY() != 0) {
                            taskComponent.getGraph().getView().setTranslate(
                                    new mxPoint());
                        }

                        tr = sysComponent.getGraph().getView().getTranslate();

                        if (tr.getX() != 0 || tr.getY() != 0) {
                            taskComponent.getGraph().getView().setTranslate(
                                    new mxPoint());
                        }
                    }
                }
            }
        });

        menu.addSeparator();

//        submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));
//
//        submenu.add(editor.bind("400%", new EditorActions.ScaleAction(4)));
//        submenu.add(editor.bind("200%", new EditorActions.ScaleAction(2)));
//        submenu.add(editor.bind("150%", new EditorActions.ScaleAction(1.5)));
//        submenu.add(editor.bind("100%", new EditorActions.ScaleAction(1)));
//        submenu.add(editor.bind("75%", new EditorActions.ScaleAction(0.75)));
//        submenu.add(editor.bind("50%", new EditorActions.ScaleAction(0.5)));
//
//        submenu.addSeparator();
//
//        menu.add(editor.bind(mxResources.get("zoomIn"), mxGraphActions.getZoomInAction()));
//        menu.add(editor.bind(mxResources.get("zoomOut"), mxGraphActions.getZoomOutAction()));
//
//        menu.addSeparator();
//
//
//        menu.add(editor.bind(mxResources.get("actualSize"), mxGraphActions.getZoomActualAction()));
//
//
//        menu = add(new JMenu(mxResources.get("diagram")));


//        submenu = (JMenu) menu.add(new JMenu(mxResources.get("layout")));
//
//        submenu.add(editor.graphLayout("verticalHierarchical", true));
//        submenu.add(editor.graphLayout("horizontalHierarchical", true));
//
//        submenu.addSeparator();
//
//        submenu.add(editor.graphLayout("verticalPartition", false));
//        submenu.add(editor.graphLayout("horizontalPartition", false));
//
//        submenu.addSeparator();
//
//        submenu.add(editor.graphLayout("verticalStack", false));
//        submenu.add(editor.graphLayout("horizontalStack", false));
//
//        submenu.addSeparator();
//
//        submenu.add(editor.graphLayout("verticalTree", true));
//        submenu.add(editor.graphLayout("horizontalTree", true));
//
//        submenu.addSeparator();
//
//        submenu.add(editor.graphLayout("placeEdgeLabels", false));
//        submenu.add(editor.graphLayout("parallelEdges", false));
//
//        submenu.addSeparator();
//
//        submenu.add(editor.graphLayout("organicLayout", true));
//        submenu.add(editor.graphLayout("circleLayout", true));


//        submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));
//
//        submenu.add(new EditorActions.TogglePropertyItem(taskComponent, mxResources.get("centerZoom"), "CenterZoom", true));
//        submenu.add(new EditorActions.TogglePropertyItem(taskComponent, mxResources.get("zoomToSelection"), "KeepSelectionVisibleOnZoom", true));
//
//        submenu.addSeparator();
//
//        submenu.add(new EditorActions.TogglePropertyItem(taskComponent, mxResources.get("centerPage"), "CenterPage", true, new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                if (taskComponent.isPageVisible()
//                        && taskComponent.isCenterPage()) {
//                    taskComponent.zoomAndCenter();
//                }
//            }
//        }));
//
//
//        submenu.addSeparator();
//
//        submenu.add(new EditorActions.TogglePropertyItem(graph,
//                mxResources.get("allowLoops"), "AllowLoops"));
//        submenu.add(new EditorActions.TogglePropertyItem(graph,
//                mxResources.get("multigraph"), "Multigraph"));

    }

    public static void populateShapeMenu(JMenu menu, BasicGraphEditor editor) {
    }

    public static void populateFormatMenu(JMenu menu, BasicGraphEditor editor) {
    }
}
