package xvh.gui;

import com.google.common.collect.Sets;
import xvh.nodedata.IDataProcessor;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;
import java.util.Set;

public class ComboBoxModelNodeSets implements ComboBoxModel<String> {
    private static final String NONE = "none";

    private final IDataProcessor processor;
    private String selected;

    private final Set<ListDataListener> listeners;

    public ComboBoxModelNodeSets(IDataProcessor processor) {
        this.processor = processor;
        this.listeners = Sets.newHashSet();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(anItem == null) {
            this.selected = NONE;
        }
        if(anItem instanceof String) {
            this.selected = (String) anItem;
        }
    }

    @Override
    public Object getSelectedItem() {
        return this.selected;
    }

    @Override
    public int getSize() {
        return this.processor.getCategories().size();
    }

    @Override
    public String getElementAt(int index) {
        List<String> options = this.processor.getCategories();
        index = index < 0 ? 0 : index >= options.size() ? options.size() - 1 : index;
        return options.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }
}
