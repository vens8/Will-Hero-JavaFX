package Game;

import java.util.ArrayList;

public class GameObjectTemplate <T> {
    private ArrayList<T> objectList;
    private int listSize;

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getListSize() {
        return listSize;
    }

    public void addItem(T item) {
        objectList.add(item);
    }

    public ArrayList<T> getObjectList() {
        return objectList;
    }
}
