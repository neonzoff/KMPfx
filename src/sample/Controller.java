package sample;

/**
 * @author Tseplyaev Dmitry
 */

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class Controller {

    private double ANIMATION_SPEED = 1.5;
    private String source;
    private String sample;
    private ArrayList<Integer> prefix;
    private int prefixCount = 0;
    private boolean isManualy = false;
    private boolean isFindedPref = false;
    private TimeMachine timeMachine;

    @FXML
    private TextField sourceTextField;

    @FXML
    private TextField sampleTextField;

    @FXML
    private Button fillButton;

    @FXML
    private Button findPrefixButton;

    @FXML
    private Button findButton;

    @FXML
    private Label speedLabel;

    @FXML
    private Slider speedSlider;

    @FXML
    private Label currentSpeedLabel;

    @FXML
    private CheckBox manualCheckBox;

    @FXML
    private Button prevActionButton;

    @FXML
    private Button nextActionButton;

    @FXML
    private HBox sourcePanel;

    @FXML
    private HBox samplePanel;

    @FXML
    private HBox prefixPanel;

    @FXML
    private Pane currentActionPanel;

    @FXML
    private Label currentActionLabel;

    @FXML
    private HBox answerPanel;


    @FXML
    void initialize() {
        currentActionLabel.setFont(new Font("Calibri", 23));
        currentActionLabel.setText("Программа запущена!");
        //убираем фокус
//        sourceTextField.setFocusTraversable(false);
//        sampleTextField.setFocusTraversable(false);
//        fillButton.setFocusTraversable(false);
//        findPrefixButton.setFocusTraversable(false);
//        findButton.setFocusTraversable(false);

        speedSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            ANIMATION_SPEED = speedSlider.getValue() / 100;
            currentSpeedLabel.setText(String.format("%.2f", 1 / ANIMATION_SPEED));
            currentActionLabel.setText("Скорость анимации изменена на " + String.format("%.2f", 1 / ANIMATION_SPEED));
        });

        manualCheckBox.selectedProperty().addListener(event -> {
            isManualy = manualCheckBox.isSelected();
            if (isManualy) {
                nextActionButton.setDisable(false);
                prevActionButton.setDisable(false);
                findPrefixButton.setDisable(true);
                findButton.setDisable(true);
            } else {
                nextActionButton.setDisable(true);
                prevActionButton.setDisable(true);
                if (isFindedPref) {
                    findButton.setDisable(false);
                }
            }
        });

        fillButton.setOnAction(event -> {
            if (sourceTextField.getText().length() == 0 || sampleTextField.getText().length() == 0) {
                currentActionLabel.setText("Попытка запуска программы с пустым текстовым полем");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Оба текстовых поля должны быть заполнены!");
                alert.showAndWait();
            } else if (sampleTextField.getText().length() > sourceTextField.getText().length()) {
                currentActionLabel.setText("Образ строки превышает длину строки для поиска");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Образ строки превышает длину строки для поиска");
                alert.showAndWait();
            } else if (sourceTextField.getText().length() <= 50 && sampleTextField.getText().length() <= 50) {
                currentActionLabel.setText("Исходная строка и подстрока для поиска введены в программу");
                findButton.setDisable(true);
                clearPrefixPanel();
                clearAnswerPanel();
                source = sourceTextField.getText();
                sample = sampleTextField.getText();
                prefix = prefixFunction(sample);
                if (!isManualy) {
                    findPrefixButton.setDisable(false);
                    findButton.setStyle("-fx-border-color: null; -fx-border-width: 0");
                    fillButton.setStyle("-fx-border-color: null; -fx-border-width: 0");
                    findPrefixButton.setStyle("-fx-border-color: orange; -fx-border-width: 1");
                    manualCheckBox.setDisable(true);
                }

                fillPanel(sourcePanel, source);
                fillPanel(samplePanel, sample);

                if (isManualy) {
                    timeMachine = new TimeMachine(sourcePanel, samplePanel, prefixPanel, answerPanel, currentActionLabel, sample, source, fillButton, findButton, nextActionButton, manualCheckBox, speedSlider);
                    setDisabledButtons();
                    nextActionButton.setDisable(false);
                }
            } else {
                currentActionLabel.setText("Попытка запуска с текстом длиной более 50 символов");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Введенный текст превышает 50 символов");
                alert.showAndWait();
            }

        });

        findPrefixButton.setOnAction(event -> {
            currentActionLabel.setText("Вычисление префикс-функции");
            visualFillPrefix();
            findPrefixButton.setStyle("-fx-border-color: null; -fx-border-width: 0");
            findButton.setStyle("-fx-border-color: orange; -fx-border-width: 1");
            if (!isManualy) {
                nextActionButton.setDisable(true);
                prevActionButton.setDisable(true);
            }
            findPrefixButton.setDisable(true);
            findButton.setDisable(true);
        });

        findButton.setOnAction(event -> {
            currentActionLabel.setText("Поиск образов в строке");
            visualFind();
            findButton.setStyle("-fx-border-color: null; -fx-border-width: 0");
            fillButton.setStyle("-fx-border-color: orange; -fx-border-width: 1");
//            System.out.println(KMPSearch(source, sample));
        });

        nextActionButton.setOnAction(event -> {
            timeMachine.next();
            isFindedPref = timeMachine.isFindedPrefix();
        });

        prevActionButton.setOnAction(event -> {
            timeMachine.prev();
        });

    }

    private void fillPanel(Pane panel, String text) {
        ObservableList<Node> list = panel.getChildren();
        list.clear();
        for (int i = 0; i < text.length(); i++) {
            Label label = new Label(String.valueOf(text.charAt(i)));
            label.setFont(new Font("Calibri", 32));
            list.add(label);
        }
    }

    private void visualFillPrefix() {       //O(n)
        clearPrefixPanel();
        findButton.setDisable(true);
        speedSlider.setDisable(true);
        findPrefixButton.setDisable(true);
        fillButton.setDisable(true);
        manualCheckBox.setDisable(true);
        currentActionLabel.setText("Первый элемент в префикс функции всегда равен 0");
        ObservableList<Node> prefixList = prefixPanel.getChildren();
        ObservableList<Node> sampleList = samplePanel.getChildren();
        Label label = new Label();
        label.setFont(new Font("Calibri", 32));
        label.setText("0");
        prefixList.add(label);
        IntegerProperty i = new SimpleIntegerProperty(1);
        IntegerProperty j = new SimpleIntegerProperty(0);

        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(ANIMATION_SPEED), event -> {
            Label label2 = new Label();
            label2.setFont(new Font("Calibri", 32));
            if (i.intValue() == sample.length()) {
                clearSampleColor();
                isFindedPref = true;
                setEnabledButtons();
                changeLabelAfterPrefixFill();
                timeline.stop();
            } else {
                Label sampleLabeli = (Label) sampleList.get(i.intValue());
                Label sampleLabelj = (Label) sampleList.get(j.intValue());
                currentActionLabel.setText("Сравниваем элементы: \"" + sampleLabeli.getText() + "\" и \"" + sampleLabelj.getText() + "\"");
                sampleLabeli.setStyle("-fx-background-color: #ff0000");
                sampleLabelj.setStyle("-fx-background-color: #ffff00");
                if (sample.charAt(i.intValue()) != sample.charAt(j.intValue())) {
                    if (j.intValue() == 0) {
                        currentActionLabel.setText("Так как элементы: \"" + sample.charAt(i.intValue()) + "\" и \"" + sample.charAt(j.intValue()) + "\" не совпадают - ставим 0 в префикс-функцию");
                        label2.setText("0");
                        prefixList.add(label2);
                        clearBetweenIndexesPrefix(j.intValue(), i.intValue());
                        i.set(i.intValue() + 1);
                    } else {
                        currentActionLabel.setText("Возвращаем каретку индекс из префикс-функции из под желтой каретки");
                        Label tempLabel = (Label) prefixList.get(j.intValue() - 1);
                        clearBetweenIndexesPrefix(j.intValue(), i.intValue());
                        j.set(Integer.parseInt(tempLabel.getText()));
                    }
                } else {
                    label2.setText(String.valueOf(j.intValue() + 1));
                    prefixList.add(label2);
                    clearBetweenIndexesPrefix(j.intValue(), i.intValue());
                    i.set(i.intValue() + 1);
                    j.set(j.intValue() + 1);
                }
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void changeLabelAfterPrefixFill() {
        currentActionLabel.setText("Префикс функция найдена");
    }

    private void setEnabledButtons() {
        fillButton.setDisable(false);
        findPrefixButton.setDisable(false);
        speedSlider.setDisable(false);
        findButton.setDisable(false);
        manualCheckBox.setDisable(false);
    }

    private void setDisabledButtons() {
        fillButton.setDisable(true);
        findPrefixButton.setDisable(true);
        findButton.setDisable(true);
        speedSlider.setDisable(true);
        manualCheckBox.setDisable(true);
    }

    private void clearBetweenIndexesPrefix(int j, int i) {
        ObservableList<Node> sampleList = samplePanel.getChildren();
        for (int start = 0; start < j; start++) {
            sampleList.get(start).setStyle("-fx-background-color: null");
        }
        for (int from = j + 1; from < i; from++) {
            sampleList.get(from).setStyle("-fx-background-color: null");
        }
    }

    private void clearPrefixPanel() {
        ObservableList<Node> prefixList = prefixPanel.getChildren();
        prefixList.clear();
    }

    private void clearSampleColor() {
        ObservableList<Node> sampleList = samplePanel.getChildren();
        for (Node node : sampleList) {
            node.setStyle("-fx-background-color: null");
        }
    }

    private void visualFind() {
        clearSampleColor();
        clearSourceColor();
        clearAnswerPanel();
        setDisabledButtons();
        ObservableList<Node> sourceList = sourcePanel.getChildren();
        ObservableList<Node> sampleList = samplePanel.getChildren();
        ObservableList<Node> prefixList = prefixPanel.getChildren();
        ObservableList<Node> answerList = answerPanel.getChildren();
        IntegerProperty i = new SimpleIntegerProperty(0);
        IntegerProperty j = new SimpleIntegerProperty(0);


        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(ANIMATION_SPEED), event -> {
            Label labelSource;
            Label labelSample;
            Label labelPrefix;
            Label labelAnswer = new Label();
            labelAnswer.setFont(new Font("Calibri", 32));


            if (i.intValue() == source.length()) {
                syncAfterEnd(i.intValue(), answerList.size());
                timeline.stop();
            } else {
                labelSource = (Label) sourceList.get(i.intValue());
                labelSample = (Label) sampleList.get(j.intValue());
                labelSource.setStyle("-fx-background-color: red");
                labelSample.setStyle("-fx-background-color: red");
                currentActionLabel.setText("Сравниваем элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\"");
                if (labelSample.getText().equals(labelSource.getText())) {
                    currentActionLabel.setText("Элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\" совпадают, движемся дальше");
                    i.set(i.intValue() + 1);
                    j.set(j.intValue() + 1);
                    if (j.intValue() == sample.length()) {
                        labelAnswer.setText((i.intValue() - sample.length()) + " ");
                        answerList.add(labelAnswer);
                        Label temp = (Label) prefixList.get(j.intValue() - 1);
                        j.set(Integer.parseInt(temp.getText()));
                        clearSamplePanelWhileFind(Integer.parseInt(temp.getText()));
                        currentActionLabel.setText("Найден образ строки, начиная с индекса: \" " + labelAnswer.getText() + "\"");
                    }
                } else if (j.intValue() == 0) {
                    currentActionLabel.setText("Обходим образ с начала");
                    i.set(i.intValue() + 1);
                    clearSampleColor();
                } else {
                    labelPrefix = (Label) prefixList.get(j.intValue() - 1);
                    j.set(Integer.parseInt(labelPrefix.getText()));
                    currentActionLabel.setText("Элементы: \"" + labelSample.getText() + "\" и \"" + labelSource.getText() + "\" не совпадают, становимся на позицию \" " + labelPrefix.getText() + " \" из префикс-функции");
                }
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void syncAfterEnd(int i, int sizeOfAnswerArray) {   //sync
        if (sizeOfAnswerArray == 0 && i == source.length()) {
            ObservableList<Node> answerList = answerPanel.getChildren();
            Label labelAnswer = new Label();
            labelAnswer.setFont(new Font("Calibri", 32));
            labelAnswer.setText("Образы отсутствуют в строке!");
            answerList.add(labelAnswer);
        }
        currentActionLabel.setText("Поиск закончен");
        setEnabledButtons();
    }

    private void clearAnswerPanel() {
        ObservableList<Node> answerList = answerPanel.getChildren();
        answerList.clear();
    }

    private void clearSourceColor() {
        ObservableList<Node> sourceList = sourcePanel.getChildren();
        for (Node node : sourceList) {
            node.setStyle("-fx-background-color: null");
        }
    }

    private void clearSamplePanelWhileFind(int from) {
        ObservableList<Node> sampleList = samplePanel.getChildren();
        for (int i = from; i < sampleList.size(); i++) {
            Label label = (Label) sampleList.get(i);
            label.setStyle("-fx-background-color: null;");
        }

    }


    private static ArrayList<Integer> prefixFunction(String sample) {      //O(n)
        ArrayList<Integer> prefixArray = new ArrayList<>();
        for (int i = 0; i < sample.length(); i++) {
            prefixArray.add(0);
        }       //kostil :D


        int i = 1, j = 0;
        while (i < sample.length()) {
            if (sample.charAt(i) == (sample.charAt(j))) {
                prefixArray.set(i, j + 1);     //a[i] == a[j]
                i++;
                j++;
            } else if (j == 0) {
                prefixArray.set(i, 0);       //a[i] != a[j] and j == 0
                i++;
            } else {
                j = prefixArray.get(j - 1);     //a[i] != a[j] and j != 0
            }
        }
        return prefixArray;
    }

    private static ArrayList<Integer> KMPSearch(String text, String sampleString) {
        ArrayList<Integer> indexArray = new ArrayList<>();
        ArrayList<Integer> prefixArray = prefixFunction(sampleString);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (sampleString.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == sampleString.length()) {
                indexArray.add(i - j);
                j = prefixArray.get(j - 1);
            } else if (i < text.length() && sampleString.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = prefixArray.get(j - 1);
                } else {
                    i += 1;
                }
            }
        }

        return indexArray;
    }

}
