package xvh.gui;

import xvh.nodedata.IDataProcessor;
import xvh.IProgramConfiguration;
import xvh.Main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NodeFetcherGui implements IProgramConfiguration {
    private JLabel labelInput;
    private JLabel labelData;
    private JLabel labelOutput;
    private JLabel labelFilter;
    private JButton buttonInput;
    private JButton buttonData;
    private JButton buttonNoData;
    private JButton buttonBrowseOutput;
    private JButton buttonNewOutput;
    private JButton buttonNoFilter;
    private JButton buttonRun;
    private JButton buttonClear;
    private JCheckBox checkBoxInput;
    private JCheckBox checkBoxData;
    private JCheckBox checkBoxOutput;
    private JCheckBox checkBoxFilter;
    private JPanel panel;

    private JComboBox<String> comboBoxFilter;
    private ComboBoxModelNodeSets comboBoxModel;

    private Frame frame;

    private IDataProcessor processor;

    private JFileChooser chooserInput;
    private JFileChooser chooserData;
    private JFileChooser chooserOutput;

    private String input;
    private String data;
    private String filter;
    private String output;

    public NodeFetcherGui(Frame frame) {
        //set frame pointer
        this.frame = frame;
        //create default processor
        this.processor = Main.createProcessor();
        //define button callbacks
        this.buttonInput.addActionListener(this::onInputButtonAction);
        this.buttonData.addActionListener(this::onDataButtonAction);
        this.buttonNoData.addActionListener(this::onNoDataButtonAction);
        this.buttonBrowseOutput.addActionListener(this::onDataBrowseOutputButtonAction);
        this.buttonNewOutput.addActionListener(this::onNewOutputButtonAction);
        this.buttonNoFilter.addActionListener(this::onNoFilterButtonAction);
        this.buttonRun.addActionListener(this::onRunButtonAction);
        this.buttonClear.addActionListener(this::onClearButtonAction);
        //define combo box list model
        this.comboBoxModel = new ComboBoxModelNodeSets(this.processor);
        this.comboBoxFilter.setModel(this.comboBoxModel);
        this.comboBoxFilter.addActionListener(this::onFilterAction);
        //define file chooser for input file
        this.chooserInput = new JFileChooser();
        this.chooserInput.setFileFilter(new FileNameExtensionFilter("Abaqus input files", "inp"));
        //define file chooser for data file
        this.chooserData = new JFileChooser();
        this.chooserData.setFileFilter(new FileNameExtensionFilter("Abaqus report files", "rpt"));
        //define file chooser for output file
        this.chooserOutput = new JFileChooser();
        this.chooserOutput.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx", "xls"));
    }

    public JPanel getPanel() {
        return this.panel;
    }

    private void onInputButtonAction(ActionEvent action) {
        int ret = this.chooserInput.showOpenDialog(this.frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
            this.input = chooserInput.getSelectedFile().getAbsolutePath();
            Main.readInputData(this.processor, this);
            this.checkBoxInput.setSelected(true);
            this.comboBoxFilter.setEnabled(true);
            this.buttonNoFilter.setEnabled(true);
            this.checkAndUpdateRunButton();
        }
    }

    private void onDataButtonAction(ActionEvent action) {
        int ret = this.chooserData.showOpenDialog(this.frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
            this.data = chooserData.getSelectedFile().getAbsolutePath();
            this.checkBoxData.setSelected(true);
        }
    }

    private void onNoDataButtonAction(ActionEvent action) {
        this.data = null;
        this.checkBoxData.setSelected(true);
    }

    private void onDataBrowseOutputButtonAction(ActionEvent action) {
        int ret = this.chooserOutput.showOpenDialog(this.frame);
        if(ret == JFileChooser.APPROVE_OPTION) {
            this.output = chooserOutput.getSelectedFile().getAbsolutePath();
            this.checkBoxOutput.setSelected(true);
            this.checkAndUpdateRunButton();
        }
    }

    private void onNewOutputButtonAction(ActionEvent action) {
        String file = JOptionPane.showInputDialog("Choose file name");
        if(!(file.endsWith(".xls") || file.endsWith(".xlsx"))) {
            file = file +".xlsx";
        }
        this.output = file;
        this.checkBoxOutput.setSelected(true);
        this.checkAndUpdateRunButton();
    }

    private void onNoFilterButtonAction(ActionEvent action) {
        this.comboBoxModel.setSelectedItem(null);
        this.filter = null;
        this.checkBoxFilter.setSelected(true);
        this.panel.updateUI();
    }

    private void onFilterAction(ActionEvent action) {
        this.filter = this.comboBoxModel.getElementAt(this.comboBoxFilter.getSelectedIndex());
        this.checkBoxFilter.setSelected(true);
    }

    private void onRunButtonAction(ActionEvent action) {
        //read output data
        if(!Main.readOutputData(processor, this)) {
            return;
        }
        //export data
        Main.exportData(processor, this);
    }

    private void onClearButtonAction(ActionEvent action) {
        this.checkBoxInput.setSelected(false);
        this.checkBoxData.setSelected(false);
        this.checkBoxFilter.setSelected(false);
        this.checkBoxOutput.setSelected(false);
        this.input = null;
        this.data = null;
        this.filter = null;
        this.output = null;
        this.processor.clear();
        this.checkAndUpdateRunButton();
        this.comboBoxFilter.setEnabled(false);
        this.buttonNoFilter.setEnabled(false);
        this.comboBoxModel.setSelectedItem(null);
    }

    private void checkAndUpdateRunButton() {
        this.buttonRun.setEnabled(this.input != null && this.output != null);
    }

    @Override
    public String getInputFile() {
        return this.input;
    }

    @Override
    public String getDataFile() {
        return this.data;
    }

    @Override
    public String getFilter() {
        return this.filter;
    }

    @Override
    public String getOutputFile() {
        return this.output;
    }
}
