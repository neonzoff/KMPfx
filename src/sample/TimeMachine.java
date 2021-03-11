package sample;

/**
 * @author Tseplyaev Dmitry
 */

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.Stack;

public class TimeMachine {
    private final Stack<Action> stack = new Stack<>();
    private ObservableList<Node> sourceList;
    private ObservableList<Node> sampleList;
    private ObservableList<Node> prefixList;
    private ObservableList<Node> answerList;
    private HBox sourcePanel;
    private HBox samplePanel;
    private HBox prefixPanel;
    private HBox answerPanel;
    private Label currentActionLabel;
    private final String sample;
    private final String source;
    private final Button fillButton;
    private final Button findButton;
    private final Button nextActionButton;
    private final CheckBox manualCheckBox;
    private final Slider speedSlider;
    private int i = 1;
    private int j = 0;
    private Label labelSource;
    private Label labelSample;
    private Label labelPrefix;
    private Label labelAnswer = new Label();

    private boolean isFindedPrefix = false;
    private boolean isFindedAns = false;
    private boolean isFirstPrefix = true;
    private boolean isFirstAns = true;
    private boolean isFirstNext = true;

    public TimeMachine(HBox sourcePanel, HBox samplePanel, HBox prefixPanel, HBox answerPanel,
                       Label currentActionLabel, String sample, String source, Button fillButton,
                       Button findButton, Button nextActionButton, CheckBox manualCheckBox, Slider speedSlider) {
        this.sourcePanel = sourcePanel;
        this.samplePanel = samplePanel;
        this.prefixPanel = prefixPanel;
        this.answerPanel = answerPanel;
        this.sourceList = sourcePanel.getChildren();
        this.sampleList = samplePanel.getChildren();
        this.prefixList = prefixPanel.getChildren();
        this.answerList = answerPanel.getChildren();
        this.currentActionLabel = currentActionLabel;
        this.sample = sample;
        this.source = source;
        this.fillButton = fillButton;
        this.findButton = findButton;
        this.nextActionButton = nextActionButton;
        this.manualCheckBox = manualCheckBox;
        this.speedSlider = speedSlider;

    }

    public void next() {
        manualCheckBox.setDisable(true);
        isFirstNext = false;
        visualFindPrefix();
        visualFind();
    }

    public void prev() {
        if (!stack.isEmpty()) {
            Action action = stack.pop();
            clearColor(sampleList);
            clearColor(sourceList);
            prefixList.clear();
            answerList.clear();
            currentActionLabel.setText(action.getCurrentActionText());
            //fill prefix and ans
            for (String str : action.getPrefList()) {
                Label label = new Label();
                label.setFont(new Font("Calibri", 32));
                label.setText(str);
                prefixList.add(label);
            }
            for (String str : action.getAnsList()) {
                Label label = new Label();
                label.setFont(new Font("Calibri", 32));
                label.setText(str);
                answerList.add(label);
            }
            //fill colors
            for (int i = 0; i < sourceList.size(); i++) {
                sourceList.get(i).setStyle(action.getSourceColorList().get(i));
            }
            for (int i = 0; i < sampleList.size(); i++) {
                sampleList.get(i).setStyle(action.getSampleColorList().get(i));
            }
            //
            isFindedAns = action.isFindedAns();
            isFirstPrefix = action.isFirstPrefix();
            isFindedPrefix = action.isFindedPrefix();
            isFirstAns = action.isFirstAns();
            i = action.getI();
            j = action.getJ();
            nextActionButton.setDisable(false);
        } else {
            if (!isFirstNext) {
                prefixList.clear();
                clearColor(sampleList);
                Label label = new Label();
                label.setFont(new Font("Calibri", 32));
                label.setText("0");
                prefixList.add(label);
            }
            currentActionLabel.setText("Предыдущих действий не найдено!");
        }
    }


    private void visualFindPrefix() {
        if (!isFindedPrefix) {
            if (isFirstPrefix) {
                Label label = new Label();
                label.setFont(new Font("Calibri", 32));
                label.setText("0");
                prefixList.add(label);
                currentActionLabel.setText("Первый элемент в префикс функции всегда равен 0");
                stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                isFirstPrefix = false;

            }
            if (i == sample.length()) {
                currentActionLabel.setText("Префикс функция найдена");
                isFindedPrefix = true;
                manualCheckBox.setDisable(false);
//                stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix));
                fillButton.setDisable(false);
                speedSlider.setDisable(false);
                for (Node node : sampleList) {
                    node.setStyle("-fx-background-color: null");
                }
                fillButton.setStyle("-fx-border-width: 0; -fx-border-color: null");
                findButton.setStyle("-fx-border-width: 1; -fx-border-color: orange");
                stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                i = j = 0;
            } else {
                Label sampleLabeli = (Label) sampleList.get(i);
                Label sampleLabelj = (Label) sampleList.get(j);
                currentActionLabel.setText("Сравниваем элементы: \"" + sampleLabeli.getText() + "\" и \"" + sampleLabelj.getText() + "\"");
                sampleLabeli.setStyle("-fx-background-color: #ff0000");
                sampleLabelj.setStyle("-fx-background-color: #ffff00");
                if (sample.charAt(i) == sample.charAt(j)) {
                    currentActionLabel.setText("Элементы: \"" + sample.charAt(i) + "\" и \"" + sample.charAt(j) + "\" совпадают");
                    Label label = new Label();
                    label.setFont(new Font("Calibri", 32));
                    label.setText(String.valueOf(j + 1));
                    prefixList.add(label);
//                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix));
                    clearBetweenIndexesPrefix(j, i);
                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                    i++;
                    j++;
                } else if (j == 0) {        //a[i] != a[j] and j == 0
                    Label label = new Label();
                    label.setFont(new Font("Calibri", 32));
                    currentActionLabel.setText("Так как элементы: \"" + sample.charAt(i) + "\" и \"" + sample.charAt(j) + "\" не совпадают - ставим 0 в префикс-функцию");
                    label.setText("0");
//                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix));
                    prefixList.add(label);
                    clearBetweenIndexesPrefix(j, i);
                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                    i++;
                } else {        //a[i] != a[j] and j != 0
                    currentActionLabel.setText("Возвращаем каретку индекс из префикс-функции из под желтой каретки");
                    Label tempLabel = (Label) prefixList.get(j - 1);
//                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix));
                    clearBetweenIndexesPrefix(j, i);
                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                    j = Integer.parseInt(tempLabel.getText());
                }
            }
        }
    }

    private void visualFind() {
        if (isFindedPrefix && isFirstAns) {
            isFirstAns = false;
            findButton.setStyle("-fx-border-color: null; -fx-border-width: 0");
        } else {
            if (isFindedPrefix && !isFindedAns) {
                fillButton.setDisable(true);
                findButton.setDisable(true);
                labelAnswer = new Label();
                labelAnswer.setFont(new Font("Calibri", 32));
                if (i == source.length()) {
                    if (answerList.size() == 0 && i == source.length()) {
                        Label labelAnswer = new Label();
                        labelAnswer.setFont(new Font("Calibri", 32));
                        labelAnswer.setText("Образы отсутствуют в строке!");
                        answerList.add(labelAnswer);
                        stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                        manualCheckBox.setDisable(false);
                        nextActionButton.setDisable(true);
                        fillButton.setStyle("-fx-border-width: 1; -fx-border-color: orange");

                    }
                    isFindedAns = true;
                    currentActionLabel.setText("Поиск закончен");
                    speedSlider.setDisable(false);
                    fillButton.setDisable(false);
                    manualCheckBox.setDisable(false);
                    nextActionButton.setDisable(true);
                    fillButton.setStyle("-fx-border-width: 1; -fx-border-color: orange");
                } else {
                    labelSource = (Label) sourceList.get(i);
                    labelSample = (Label) sampleList.get(j);
                    labelSource.setStyle("-fx-background-color: red");
                    labelSample.setStyle("-fx-background-color: red");
                    currentActionLabel.setText("Сравниваем элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\"");
                    stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                    if (labelSample.getText().equals(labelSource.getText())) {
                        currentActionLabel.setText("Элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\" совпадают, движемся дальше");
                        stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                        i++;
                        j++;
                        if (j == sample.length()) {
                            labelAnswer.setText((i - sample.length()) + " ");
                            answerList.add(labelAnswer);
                            Label temp = (Label) prefixList.get(j - 1);
                            j = Integer.parseInt(temp.getText());
                            clearSamplePanelWhileFind(Integer.parseInt(temp.getText()));
                            currentActionLabel.setText("Найден образ строки, начиная с индекса: \" " + labelAnswer.getText() + "\"");
                            stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                        }
                    } else if (j == 0) {
                        currentActionLabel.setText("Обходим образ с начала");
                        stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                        i++;
                        clearColor(sampleList);
                    } else {
                        labelPrefix = (Label) prefixList.get(j - 1);
                        j = Integer.parseInt(labelPrefix.getText());
                        currentActionLabel.setText("Элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\" не совпадают, становимся на позицию \" " + labelPrefix.getText() + " \" из префикс-функции");
                        stack.push(new Action(i, j, currentActionLabel.getText(), sourceList, sampleList, prefixList, answerList, isFindedPrefix, isFindedAns, isFindedPrefix, isFirstAns));
                    }
                }
            }
        }
    }

    public boolean isFindedPrefix() {
        return isFindedPrefix;
    }

    private void clearSamplePanelWhileFind(int from) {
        for (int i = from; i < sampleList.size(); i++) {
            Label label = (Label) sampleList.get(i);
            label.setStyle("-fx-background-color: null;");
        }

    }

    private void clearColor(ObservableList<Node> list) {
        for (Node node : list) {
            node.setStyle("-fx-background-color: null");
        }
    }

    private void clearBetweenIndexesPrefix(int j, int i) {
        for (int start = 0; start < j; start++) {
            sampleList.get(start).setStyle("-fx-background-color: null");
        }
        for (int from = j + 1; from < i; from++) {
            sampleList.get(from).setStyle("-fx-background-color: null");
        }
    }
}
