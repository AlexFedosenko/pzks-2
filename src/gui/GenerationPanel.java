package gui;

import core.MatrixHolder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Alex
 * Date: 02.04.13
 * Time: 22:30
 */
public class GenerationPanel extends JPanel {
    private JPanel nodesPanel;
    private JPanel linksPanel;
    private JButton generate;
    private JButton plan;
    private JButton simulation;
    private BasicGraphEditor editor;
    private int nodesAmount = 5;
    private JTextField amountNodes;
    private int nodesMin = 1;
    private JTextField minNodes;
    private int nodesMax = 5;
    private JTextField maxNodes;
    private double correlation = 0.1;
    private JTextField correlationLinks;
    private int linksMin = 1;
    private JTextField minLinks;
    private int linksMax = 5;
    private JTextField maxLinks;
    private JPanel v2Panel;
    private JPanel v5Panel;
    public static JRadioButton q2_2;
    public static JRadioButton q2_5;
    public static JRadioButton q4_2;
    public static JRadioButton q4_5;
    public static JRadioButton q16_2;
    public static JRadioButton q16_5;
    public static ButtonGroup v2;
    public static ButtonGroup v5;
    public static JCheckBox colored;

    private int width;
    private int height;

    public GenerationPanel(final BasicGraphEditor editor, int width, int height) {
        this.width = width;
        this.height = height;
        this.editor = editor;

        setLayout(new MigLayout());

        nodesPanel = new JPanel();
        linksPanel = new JPanel();
        generate = new JButton("Generate");
        plan = new JButton("Planning");
        simulation = new JButton("Simulation");
        nodesPanel.setBorder(BorderFactory.createTitledBorder("Nodes"));
        linksPanel.setBorder(BorderFactory.createTitledBorder("Links"));


        nodesPanel.setLayout(new MigLayout());
        nodesPanel.add(new JLabel("Amount"), "w 50");
        nodesPanel.add(amountNodes = new JTextField(Integer.toString(nodesAmount)), "w 100, span");
        nodesPanel.add(new JLabel("Min weight"), "w 50");
        nodesPanel.add(minNodes = new JTextField(Integer.toString(nodesMin)), "w 100, span");
        nodesPanel.add(new JLabel("Max weight"), "w 50");
        nodesPanel.add(maxNodes = new JTextField(Integer.toString(nodesMax)), "w 100, span");

        linksPanel.setLayout(new MigLayout());
        linksPanel.add(new JLabel("Connectivity"), "w 50");
        linksPanel.add(correlationLinks = new JTextField(Double.toString(correlation)), "w 100, span");
        linksPanel.add(new JLabel("Min weight"), "w 50");
        linksPanel.add(minLinks = new JTextField(Integer.toString(linksMin)), "w 100, span");
        linksPanel.add(new JLabel("Max weight"), "w 50");
        linksPanel.add(maxLinks = new JTextField(Integer.toString(linksMax)), "w 100, span");


        add(nodesPanel, "span");
        add(linksPanel, "span");
        add(generate, "span");
        v2Panel = new JPanel();
        v2Panel.setBorder(BorderFactory.createTitledBorder("Algorithm #2"));
        v2 = new ButtonGroup();
        q2_2 = new JRadioButton("#2");
        q4_2 = new JRadioButton("#4");
        q16_2 = new JRadioButton("#16");
        q2_2.setSelected(true);
        v2.add(q2_2);
        v2.add(q4_2);
        v2.add(q16_2);
        v2Panel.add(q2_2);
        v2Panel.add(q4_2);
        v2Panel.add(q16_2);
        add(v2Panel, "span");

        v5Panel = new JPanel();
        v5Panel.setBorder(BorderFactory.createTitledBorder("Algorithm # 5"));
        v5 = new ButtonGroup();
        q2_5 = new JRadioButton("#2");
        q4_5 = new JRadioButton("#4");
        q16_5 = new JRadioButton("#16");
        v2.add(q2_5);
        v2.add(q4_5);
        v2.add(q16_5);
        v5Panel.add(q2_5);
        v5Panel.add(q4_5);
        v5Panel.add(q16_5);
        add(v5Panel, "span");
        colored = new JCheckBox("Colored diagram");
        add(colored, "span");
        add(plan, "wrap");
        add(simulation);

        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nodesAmount = Integer.parseInt(amountNodes.getText());
                nodesMax = Integer.parseInt(maxNodes.getText());
                nodesMin = Integer.parseInt(minNodes.getText());
                linksMax = Integer.parseInt(maxLinks.getText());
                linksMin = Integer.parseInt(minLinks.getText());
                correlation = Double.parseDouble(correlationLinks.getText());
                editor.generateGraph();
            }
        });
        plan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatrixHolder.run();
                BasicGraphEditor.gantt.createGanttDiagram();
            }
        });

        simulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nodesMax = Integer.parseInt(maxNodes.getText());
                nodesMin = Integer.parseInt(minNodes.getText());
                linksMax = Integer.parseInt(maxLinks.getText());
                linksMin = Integer.parseInt(minLinks.getText());
                nodesAmount = MatrixHolder.getSystemSize();
                correlation = 0.9;
                double average = 0;
                for (int i = 0; i < MatrixHolder.Ke.length; i++) {
                    correlation = 0.9 - i * 0.2;
                    for (int j = 0; j < MatrixHolder.Ke[i].length; j++) {
                        nodesAmount = MatrixHolder.getSystemSize() + j * MatrixHolder.getSystemSize();
                        for (int k = 0; k < 10; k++) {
                            editor.generateGraph();
                            MatrixHolder.run();
                            MatrixHolder.Ky[i][j] += 1.0 * MatrixHolder.getTotalGraphWeight() / MatrixHolder.time;
                            MatrixHolder.Ke[i][j] += 1.0 * MatrixHolder.Ky[i][j] / nodesAmount;
                            MatrixHolder.Kea[i][j] += 1.0 * Math.max(MatrixHolder.Tg, MatrixHolder.Ke[i][j] * MatrixHolder.time) / MatrixHolder.time;
                        }
                        MatrixHolder.Ky[i][j] /= 10;
                        MatrixHolder.Ke[i][j] /= 10;
                        MatrixHolder.Kea[i][j] /= 10;
                    }
                }
                System.out.println("");
//                editor.generateGraph();
//                MatrixHolder.run();
//                BasicGraphEditor.gantt.createGanttDiagram();
            }
        });
        setPreferredSize(new Dimension(width, height));
    }

    public int getNodesAmount() {
        return nodesAmount;
    }

    public void setNodesAmount(int nodesAmount) {
        this.nodesAmount = nodesAmount;
    }

    public int getNodesMin() {
        return nodesMin;
    }

    public void setNodesMin(int nodesMin) {
        this.nodesMin = nodesMin;
    }

    public int getNodesMax() {
        return nodesMax;
    }

    public void setNodesMax(int nodesMax) {
        this.nodesMax = nodesMax;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public int getLinksMin() {
        return linksMin;
    }

    public void setLinksMin(int linksMin) {
        this.linksMin = linksMin;
    }

    public int getLinksMax() {
        return linksMax;
    }

    public void setLinksMax(int linksMax) {
        this.linksMax = linksMax;
    }

}
