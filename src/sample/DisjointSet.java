package sample;

public class DisjointSet {

    public static int find(int[] array, int node) {
        return array[node] == node ? node: find(array,array[node]); //TODO make path shorter with each go
    }

    public static void union(int[] array, int parent, int child) {
        int childRoot = find(array,child);
        if(find(array,parent) != childRoot) array[childRoot] = parent; //if child's root is not the same as parent's root: child's ROOT'S parent is now parent
    }
}
