package com.github.novotnyr.idea.consul.ui;

import com.github.novotnyr.idea.consul.tree.ConsulTreeModel;
import com.github.novotnyr.idea.consul.tree.KVNode;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.github.novotnyr.idea.consul.tree.TreeUtils;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FolderContentsTablePanel extends JPanel {
    private FolderContentsTableModel tableModel;

    private JBTable table;

    private JBScrollPane tableScrollPane;

    public FolderContentsTablePanel(ConsulTreeModel consulTree, KeyAndValue keyAndValue) {
        setLayout(new BorderLayout());

        this.table = new JBTable();
        this.tableScrollPane = new JBScrollPane(this.table);
        refresh(consulTree, keyAndValue);
        add(this.tableScrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(-1, -1));
    }

    public void refresh(ConsulTreeModel consulTree, KeyAndValue keyAndValue) {
        this.tableModel = new FolderContentsTableModel(consulTree, keyAndValue);
        this.table.setModel(this.tableModel);
    }

    public static class FolderContentsTableModel extends AbstractTableModel {
        public enum Column {
            KEY("Key"),
            VALUE("Value");

            private String description;

            Column(String description) {
                this.description = description;
            }

            public String getDescription() {
                return description;
            }
        }

        private List<Column> columns = Arrays.asList(Column.values());

        private KeyAndValue keyAndValue;

        private ConsulTreeModel consulTree;

        public FolderContentsTableModel(ConsulTreeModel consulTree, KeyAndValue keyAndValue) {
            this.keyAndValue = keyAndValue;
            this.consulTree = consulTree;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return this.columns.get(columnIndex).getDescription();
        }

        @Override
        public int getRowCount() {
            return getNonFolderKeysAndValues().size();
        }

        @Override
        public int getColumnCount() {
            return this.columns.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KeyAndValue childKv = getNonFolderKeysAndValues().get(rowIndex);

            Column column = this.columns.get(columnIndex);
            switch (column) {
                case KEY:
                    return childKv.getKey();
                case VALUE:
                    return childKv.getValue();
                default:
                    return "N/A";
            }
        }

        public void setKeyAndValue(KeyAndValue keyAndValue) {
            this.keyAndValue = keyAndValue;
            this.fireTableDataChanged();
        }

        private List<KeyAndValue> getNonFolderKeysAndValues() {
            List<KeyAndValue> keyAndValues = new ArrayList<>();
            if (this.consulTree == null || this.keyAndValue == null) {
                return keyAndValues;
            }
            KVNode node = this.consulTree.getNode(this.keyAndValue);
            if (node == null) {
                return keyAndValues;
            }
            for (KVNode child : TreeUtils.iterableChildren(node)) {
                KeyAndValue kv = child.getKeyAndValue();
                if(! kv.isContainer()) {
                    keyAndValues.add(kv);
                }
            }

            return keyAndValues;
        }
    }
}
