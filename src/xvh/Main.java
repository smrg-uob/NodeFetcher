package xvh;

import xvh.gui.GuiLauncher;
import xvh.nodedata.DataProcessorDefault;
import xvh.nodedata.IDataProcessor;
import xvh.nodedata.NodeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            //Launch GUI
            GuiLauncher.launch();
        } else {
            //Launch program from arguments
            ProgramConfiguration config = parseConfiguration(args);
            IDataProcessor processor = createProcessor();
            runProgram(processor, config);
        }
    }

    public static void runProgram(IDataProcessor processor, IProgramConfiguration config) {
        //Parse arguments
        //read input data
        if(!readInputData(processor, config)) {
            return;
        }
        //read output data
        if(!readOutputData(processor, config)) {
            return;
        }
        //export data
        exportData(processor, config);
    }

    public static ProgramConfiguration parseConfiguration(String[] args) {
        System.out.println("Parsing configuration");
        ProgramConfiguration config = new ProgramConfiguration(args);
        System.out.println(" -> Completed");
        System.out.println();
        return config;
    }

    public static IDataProcessor createProcessor(){
        return new DataProcessorDefault();
    }

    public static boolean readInputData(IDataProcessor processor, IProgramConfiguration config) {
        System.out.println("Reading input node data");
        String inputFileName = config.getInputFile();
        if(inputFileName == null) {
            System.out.println(" -> ERROR: No input file has been specified");
            return false;
        } else {
            processor.inputDataFeedStart();
            try {
                readFileLineByLine(processor, inputFileName);
            } catch (IOException e) {
                System.out.println(" -> ERROR: Exception encountered while reading input file");
                e.printStackTrace();
                processor.inputDataFeedStop();
                return false;
            }
            processor.inputDataFeedStop();
        }
        System.out.println(" -> Completed");
        System.out.println();
        return true;
    }

    public static boolean readOutputData(IDataProcessor processor, IProgramConfiguration config) {
        System.out.println("Reading output node data");
        String dataFileName = config.getDataFile();
        if(dataFileName == null) {
            System.out.println(" -> No data file has been specified, no output data will be appended");
            return true;
        } else {
            processor.outputDataFeedStart();
            try {
                readFileLineByLine(processor, dataFileName);
            } catch (IOException e) {
                System.out.println(" -> Error while reading data file");
                e.printStackTrace();
            }
            processor.outputDataFeedStop();
        }
        System.out.println(" -> Completed");
        System.out.println();
        return true;
    }

    public static void exportData(IDataProcessor processor, IProgramConfiguration config) {
        System.out.println("Exporting data");
        String file = config.getOutputFile();
        String[] headers = processor.getDataLabels();
        List<NodeData> filtered = processor.filterNodes(config.getFilter());
        try {
            ExcelWriter.writeToExcel(file, config.getFilter(), headers, filtered);
        } catch (IOException e) {
            System.out.println(" -> Error encountered while exporting to Excel");
            e.printStackTrace();
        }
        System.out.println(" -> Completed");
        System.out.println();
    }

    public static void readFileLineByLine(IDataProcessor processor, String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int nr = 0;
        String line = reader.readLine();
        while(line != null) {
            nr++;
            processor.processLine(nr, line);
            line = reader.readLine();
        }
        reader.close();
    }
}
