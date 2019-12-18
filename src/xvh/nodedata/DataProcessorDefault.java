package xvh.nodedata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProcessorDefault implements IDataProcessor {
    private Map<Integer,NodeData> nodes;
    private List<String> sets;

    private Status status;

    private String currentSet;
    private String[] dataTags;

    public DataProcessorDefault() {
        this.status = Status.INITIALIZED;
        this.sets = Lists.newArrayList();
        this.nodes = Maps.newHashMap();
    }

    @Override
    public void inputDataFeedStart() {
        this.status = Status.SCANNING_INPUT;
    }

    @Override
    public void inputDataFeedStop() {
        this.status = Status.COMPLETED;
        this.currentSet = null;
        this.dataTags = new String[] {"Node", "x", "y", "z"};
    }

    @Override
    public void outputDataFeedStart() {
        this.status = Status.SCANNING_OUTPUT;
    }

    @Override
    public void outputDataFeedStop() {
        this.status = Status.COMPLETED;
    }

    @Override
    public void processLine(int nr, String line) {
        switch (this.status) {
            case INITIALIZED:
                throw new IllegalStateException("Data processor has not been properly configured and can not process the line");
            case SCANNING_INPUT:
                this.scanInput(nr, line);
                break;
            case SCANNING_OUTPUT:
                this.scanOutput(nr, line);
                break;
            case READING_NODES:
                this.readNode(nr, line);
                break;
            case READING_CATEGORIES:
                this.categorize(nr, line);
                break;
            case READING_OUTPUT:
                this.readOutput(nr, line);
            case COMPLETED:
                break;
        }
    }

    @Override
    public List<String> getCategories() {
        return this.sets;
    }

    @Override
    public String[] getDataLabels() {
        return this.dataTags;
    }

    private void scanInput(int nr, String line) {
        if(line.contains("*Node")) {
            this.status = Status.READING_NODES;
        }
        if(line.contains("*Nset")) {
            this.status = Status.READING_CATEGORIES;
            this.categorize(nr, line);
        }
    }

    private void scanOutput(int nr, String line) {
        if(line.contains("Node")) {
            if(line.contains("\"Node Label\"")) {
                //skip this line
                return;
            }
            //Cut the string
            line = line.substring(20).trim();
            //read data headers
            String[] split = line.split("\\s+");
            this.dataTags = new String[split.length + 4];
            this.dataTags[0] = "Node";
            this.dataTags[1] = "x";
            this.dataTags[2] = "y";
            this.dataTags[3] = "z";
            for(int i = 4; i < this.dataTags.length; i++) {
                this.dataTags[i] = split[i - 4];
            }
            //do not set the status to read output yet, as two more lines will follow
            return;
        }
        if(line.contains("----------------")) {
            this.status = Status.READING_OUTPUT;
        }
    }

    private void readNode(int nr, String line) {
        if(line.contains("*")) {
            this.status = Status.SCANNING_INPUT;
            return;
        }
        String[] data = line.split(",");
        if(data.length != 4) {
            System.out.println("could not read node data from line " + nr +": " + line);
        }
        try {
            int id = Integer.parseInt(data[0].trim());
            double x = Double.parseDouble(data[1].trim());
            double y = Double.parseDouble(data[2].trim());
            double z = Double.parseDouble(data[3].trim());
            NodeData nodeData = new NodeData(id, x, y, z);
            this.nodes.put(nodeData.getId(), nodeData);
        } catch(Exception e) {
            System.out.println("error parsing node data from line " + nr +": " + line);
            e.printStackTrace();
        }
    }

    private void categorize(int nr, String line) {
        if(line.contains("*Nset")) {
            String[] split = line.split(",");
            this.currentSet = split[1].trim().split("=")[1].trim();
            this.sets.add(currentSet);
            Collections.sort(this.sets);
        } else if (line.contains("*Elset")) {
            this.currentSet = null;
            this.status = Status.SCANNING_INPUT;
        } else {
            String[] data = line.split(",");
            Arrays.stream(data).forEach(s -> {
                try {
                    int node = Integer.parseInt(s.trim());
                    this.nodes.get(node).addToSet(this.currentSet);
                } catch (Exception e) {
                    System.out.println("error parsing node set " + s + "  from line " + nr +": " + line);
                    e.printStackTrace();
                }
            });
        }
    }

    private void readOutput(int nr, String line) {
        if(line.equals("")) {
            return;
        }
        String[] data = line.split("\\s+");
        if(data.length != this.dataTags.length - 2) {
            System.out.println("Output data length for line " + nr + " does not match the header data count");
            return;
        }
        NodeData node;
        try {
            node = this.nodes.get(Integer.parseInt(data[1]));
        } catch (Exception e) {
            System.out.println("error parsing node data id from line " + nr +": " + line);
            e.printStackTrace();
            return;
        }
        for(int i = 2; i < data.length; i++) {
            node.appendData(this.dataTags[i + 2], Double.parseDouble(data[i]));
        }
    }

    @Override
    public List<NodeData> filterNodes(String filter) {
        return this.nodes.values().stream()
                .filter(node -> filter == null || node.isPartOfSet(filter))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        this.nodes.clear();
        this.sets.clear();
        this.status = Status.INITIALIZED;
        this.currentSet = null;
        this.dataTags = null;
    }

    private enum Status {
        INITIALIZED,
        SCANNING_INPUT,
        SCANNING_OUTPUT,
        READING_NODES,
        READING_CATEGORIES,
        READING_OUTPUT,
        COMPLETED,
    }
}
