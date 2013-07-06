/*
 * ControlPanel.java
 *
 * Created on 09.10.2010, 18:50:39
 */
package core;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {

    JTabbedPane treepane = new JTabbedPane();
    JTabbedPane simpane = new JTabbedPane();
    JPanel panel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton treeBut = new JButton("Tree");
    JButton graphBut = new JButton("Graph");
    JScrollPane treePane = new JScrollPane();
    JPanel treePanel = new JPanel();
    JCheckBox real = new JCheckBox("Real", true);
    JCheckBox note = new JCheckBox("Note", true);
    JTable table, tableReport, tableQuery, tableProbability;
    private JPanel frequencyPanel = new JPanel(new GridLayout()),
            markovProbabilityPanel = new JPanel(),
            stayTimePanel = new JPanel(new GridLayout()),
            returnTimePanel = new JPanel(new GridLayout()),
            probabilityPanel = new JPanel(new GridLayout());

    public ControlPanel() {
        initComponents();
        setPreferredSize(new Dimension(300, Container.HEIGHT));
        treepane.add(panel, "Tree");
        treepane.add("Table", createTablePanel());
        treepane.setTabPlacement(treepane.LEFT);
        jTabbedPane1.add(treepane, "Tree");
        simpane.add("List", createQueryPanel());
        simpane.add("Report", createReportPanel());
        jTabbedPane1.add(simpane, "Simulation");
        simpane.setTabPlacement(simpane.LEFT);
        panel.setLayout(new BorderLayout());
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttonPanel.add(real);
        buttonPanel.add(note);
        buttonPanel.add(treeBut);
        buttonPanel.add(graphBut);
        panel.add(buttonPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(treePane, BorderLayout.CENTER);
        treePane.add(treePanel);
        treePane.setViewportView(treePanel);
        simpane.add("Probability", createTableProbabilityPanel());


        treeBut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                try {
                    treePanel.remove(0);
                } catch (Exception q) {
                }
                revalidate();
                treePane.repaint();
            }
        });
        graphBut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
            }
        });
        real.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });
        note.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });
        treePane.setVisible(true);

    }

    private Component createReportPanel() {
        JPanel panel = new JPanel(new GridLayout());
        tableReport = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableReport);
        scrollPane.setPreferredSize(new Dimension(1600, 1600));
        panel.add(scrollPane);
        return panel;
    }

    public JTable getTableQuery() {
        return tableQuery;
    }

    public JTable getTable() {
        return table;
    }

    public JTable getTableProbability() {
        return tableProbability;
    }

    public JPanel getMarkovProbabilityPanel() {
        return markovProbabilityPanel;
    }

    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new GridLayout());
        tableQuery = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableQuery);
        scrollPane.setPreferredSize(new Dimension(1600, 1600));
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new GridLayout());
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1600, 1600));
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createTableProbabilityPanel() {
        JPanel panel = new JPanel(new GridLayout());
        tableProbability = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableProbability);
        scrollPane.setPreferredSize(new Dimension(1600, 1600));
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createMarkovProbabilityPanel(JComponent component) {
        JPanel wrapped = new JPanel(new GridLayout());
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setPreferredSize(new Dimension(1600, 1600));
        wrapped.add(scrollPane, "wrap");
        return wrapped;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new JScrollPane();
        jTable2 = new JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new JScrollPane();
        jTable3 = new JTable();

        jLabel1.setText("Matrix Di");

        jTable1.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("Matrix Dq");

        jTable2.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel3.setText("Marking");

        jTable3.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Matrix", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(256, Short.MAX_VALUE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Matrix");
    }// </editor-fold>//GEN-END:initComponents


    public JPanel getFrequencyPanel() {
        return frequencyPanel;
    }

    public JPanel getReturnTimePanel() {
        return returnTimePanel;
    }

    public JPanel getStayTimePanel() {
        return stayTimePanel;
    }

    public JPanel getProbabilityPanel() {
        return probabilityPanel;
    }

    public JTable getTableReport() {
        return tableReport;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTabbedPane jTabbedPane1;
    private JTable jTable1;
    private JTable jTable2;
    private JTable jTable3;
    // End of variables declaration//GEN-END:variables
}
