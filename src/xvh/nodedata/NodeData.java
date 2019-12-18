package xvh.nodedata;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class NodeData implements Comparable<NodeData> {
    private final int id;

    private final double x, y, z;

    private Set<String> sets;
    private Map<String, Double> data;

    public NodeData(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sets = Sets.newHashSet();
        this.data = Maps.newHashMap();
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void addToSet(String set) {
        this.sets.add(set);
    }

    public boolean isPartOfSet(String set) {
        return this.sets.contains(set);
    }

    public void appendData(String tag, double value) {
        this.data.put(tag, value);
    }

    public double getData(String tag) {
        return this.data.get(tag);
    }

    @Override
    public String toString() {
        return "" + this.getId() + " (" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")";
    }

    @Override
    public int compareTo(NodeData other) {
        return this.getId() - other.getId();
    }
}
