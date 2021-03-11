package sample;

/**
 * @author Tseplyaev Dmitry
 */

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import java.util.ArrayList;


public class Action {
    private final int i;
    private final int j;
    private final String currentActionText;
    private final ArrayList<String> sourceColorList = new ArrayList<>();
    private final ArrayList<String> sampleColorList = new ArrayList<>();
    private final ArrayList<String> prefList = new ArrayList<>();
    private final ArrayList<String> ansList = new ArrayList<>();
    private final boolean isFindedPrefix;
    private final boolean isFindedAns;
    private final boolean isFirstPrefix;
    private final boolean isFirstAns;

    public Action(int i, int j, String currentActionText, ObservableList<Node> sourceList,
                  ObservableList<Node> sampleList, ObservableList<Node> prefixList,
                  ObservableList<Node> answerList, boolean isFindedPrefix, boolean isFindedAns,
                  boolean isFirstPrefix, boolean isFirstAns) {
        this.i = i;
        this.j = j;
        this.currentActionText = currentActionText;
        //copy colors
        for (Node node : sourceList) {
            sourceColorList.add(node.getStyle());
        }
        for (Node node : sampleList) {
            sampleColorList.add(node.getStyle());
        }
        //copy values
        for (Node node : prefixList) {
            Label label = (Label) node;
            prefList.add(label.getText());
        }
        for (Node node : answerList) {
            Label temp = (Label) node;
            ansList.add(temp.getText());
        }


        this.isFindedPrefix = isFindedPrefix;
        this.isFindedAns = isFindedAns;
        this.isFirstPrefix = isFirstPrefix;
        this.isFirstAns = isFirstAns;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public String getCurrentActionText() {
        return currentActionText;
    }

    public ArrayList<String> getSourceColorList() {
        return sourceColorList;
    }

    public ArrayList<String> getSampleColorList() {
        return sampleColorList;
    }

    public ArrayList<String> getPrefList() {
        return prefList;
    }

    public ArrayList<String> getAnsList() {
        return ansList;
    }

    public boolean isFindedPrefix() {
        return isFindedPrefix;
    }

    public boolean isFindedAns() {
        return isFindedAns;
    }

    public boolean isFirstPrefix() {
        return isFirstPrefix;
    }

    public boolean isFirstAns() {
        return isFirstPrefix;
    }
}
