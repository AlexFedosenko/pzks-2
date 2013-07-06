package core;

import gui.GenerationPanel;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
* User: Alex
* Date: 14.05.13
* Time: 22:32
*/
public class Gantt{

    private JPanel matrixPanel = new JPanel(),
                    graphPanel = new JPanel(),
                fanPanel = new JPanel(),
                myPanel = new JPanel();
    public JScrollPane fanPane, myPane;
    public Gantt() {
            MigLayout layout = new MigLayout("", "[][grow]", "[grow]");
            fanPane = createPanel(fanPanel, "Fan planning");
            myPane = createPanel(myPanel, "My planning");
//            tabbedPane = new JTabbedPane();
//            tabbedPane.add(fanPane, "Fan planning");
//            tabbedPane.add(myPane, "My planning");
//            createGanttDiagram();
//            setVisible(true);
        }
    private void createChart(JPanel panel, double[][] data) {
        panel.removeAll();

        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
                "Team ", "", data);
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Gantt diagram",
                "Processors",
                "Time",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                false,
                true);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(Color.white);
        chart.setBorderPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.black);
        ((NumberAxis) plot.getRangeAxis()).setTickUnit(new NumberTickUnit(1));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        LabelGenerator gen = new LabelGenerator();
        renderer.setBaseItemLabelGenerator(gen);
        renderer.setBaseItemLabelsVisible(true);
        for (int i = 0; i < data.length / 2; i++) {
            if (!GenerationPanel.colored.isSelected()) {
                renderer.setSeriesPaint(2 * i + 1, Color.lightGray);
            }
            renderer.setSeriesPaint(2 * i, new Color(0, 0, 0, 0));
            renderer.setDrawBarOutline(true);
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, "w 95%");
    }

    public void createGanttDiagram() {
//        myPane.removeAll();
//        m.makeBasicSoluton();
        double[][] data = MatrixHolder.generateData();
//        createChart(fanPanel, data);
//        m.optimization();
//        data = m.generateData();
        createChart(myPanel, data);
    }
    private JScrollPane createPanel(JPanel panel, String title) {
        panel.setLayout(new MigLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JScrollPane pane = new JScrollPane(panel);
        pane.setBorder(null);
        return pane;
    }

}
//
//        private Planning m;
//        private JPanel matrixPanel = new JPanel(),
//                graphPanel = new JPanel(),
//                fanPanel = new JPanel(),
//                myPanel = new JPanel();
//        private JTabbedPane tabbedPane;
//        private JScrollPane fanPane, myPane;
//        private JTable weightsTable, forwardingTable, perfTable;
//
//        public GUI(Planning model) {
//            this.m = model;
//            setTitle("Fan distribution");
//            setSize(740, 550);
//            MigLayout layout = new MigLayout("", "[][grow]", "[grow]");
//            setLayout(layout);
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            createMatrixPanel();
//            fanPane = createPanel(fanPanel, "Fan planning");
//            myPane = createPanel(myPanel, "My planning");
//            tabbedPane = new JTabbedPane();
//            tabbedPane.add(fanPane, "Fan planning");
//            tabbedPane.add(myPane, "My planning");
//            add(tabbedPane, "growx, growy");
//            createGanttDiagram();
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
//            setVisible(true);
//        }
//
//        private void createMatrixPanel() {
//            matrixPanel.setLayout(new MigLayout("", "", ""));
//            matrixPanel.setBorder(BorderFactory.createTitledBorder("Matrix"));
//            JLabel label = new JLabel("Size: ");
//            matrixPanel.add(label);
//            final JSpinner spinner = new JSpinner();
//            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(8);
//            SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
//            model.setMinimum(1);
//            model.setMaximum(40);
//            model.setValue(m.sendings.length);
//            matrixPanel.add(spinner);
//            JButton button = new JButton("Redraw");
//            button.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e) {
//                    try {
//                        fanPanel.remove(0);
//                        fanPanel.revalidate();
//                        fanPanel.repaint();
//                        myPanel.remove(0);
//                        myPanel.revalidate();
//                        myPanel.repaint();
//                    } catch (Exception ex) {
//                    }
//                    if (m.isCycle() || m.isTabu()) {
//                        JOptionPane.showMessageDialog(null, "Graph is forbidden");
//                    } else {
//                        createGanttDiagram();
//                    }
//                    fanPanel.revalidate();
//                    fanPanel.repaint();
//                    myPanel.revalidate();
//                    myPanel.repaint();
//                }
//            });
//            matrixPanel.add(button, "align right, span");
//            matrixPanel.add(new JLabel("Nodes"), "split, span");
//            matrixPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "growx, wrap, span");
//            Table tableModel = new Table(m, Table.NODES);
//            weightsTable = new JTable(tableModel);
//            weightsTable.setPreferredScrollableViewportSize(new Dimension(220, 16));
//            weightsTable.setFillsViewportHeight(true);
//            JScrollPane scrollPane1 = new JScrollPane(weightsTable);
//            matrixPanel.add(scrollPane1, "span");
//            matrixPanel.add(new JLabel("Perfomance"), "split, span");
//            matrixPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "growx, wrap, span");
//            tableModel = new Table(m, Table.PERF);
//            perfTable = new JTable(tableModel);
//            perfTable.setPreferredScrollableViewportSize(new Dimension(220, 16));
//            perfTable.setFillsViewportHeight(true);
//            JScrollPane scrollPane2 = new JScrollPane(perfTable);
//            matrixPanel.add(scrollPane2, "span");
//
//            matrixPanel.add(new JLabel("Sendings"), "split, span");
//            matrixPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "growx, wrap, span");
//            tableModel = new Table(m, Table.SENDINGS);
//            forwardingTable = new JTable(tableModel);
//            forwardingTable.setPreferredScrollableViewportSize(new Dimension(225, 550));
//            scrollPane1 = new JScrollPane(forwardingTable);
//            matrixPanel.add(scrollPane1, "span");
//
//            spinner.addChangeListener(new ChangeListener() {
//
//                public void stateChanged(ChangeEvent e) {
//                    m.resize((Integer) spinner.getValue());
//                    Table tableModel = new Table(m, Table.NODES);
//                    weightsTable.setModel(tableModel);
//                    tableModel = new Table(m, Table.SENDINGS);
//                    forwardingTable.setModel(tableModel);
//                }
//            });
//
//            JScrollPane pane = new JScrollPane(matrixPanel);
//            pane.setBorder(null);
//            add(pane, "growx, growy, w 250::");
//
//        }
//
//        private JScrollPane createPanel(JPanel panel, String title) {
//            panel.setLayout(new MigLayout());
//            panel.setBorder(BorderFactory.createTitledBorder(title));
//            JScrollPane pane = new JScrollPane(panel);
//            pane.setBorder(null);
//            return pane;
//        }
//
//        private void createGanttDiagram() {
//            m.makeBasicSolution();
//            double[][] data = m.generateData();
//            createChart(fanPanel, data);
//            m.optimization();
//            data = m.generateData();
//            createChart(myPanel, data);
//        }
//

//    }
