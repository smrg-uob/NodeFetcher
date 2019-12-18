package xvh.nodedata;

import java.util.List;

public interface IDataProcessor {
    void inputDataFeedStart();

    void inputDataFeedStop();

    void outputDataFeedStart();

    void outputDataFeedStop();

    void processLine(int nr, String line);

    List<String> getCategories();

    String[] getDataLabels();

    List<NodeData> filterNodes(String filter);

    void clear();

}
