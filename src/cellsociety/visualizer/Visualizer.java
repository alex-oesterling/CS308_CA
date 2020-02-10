package cellsociety.visualizer;


import static cellsociety.SimulationApp.DEFAULT_RESOURCE_FOLDER;

import cellsociety.config.Config;
import cellsociety.simulation.grid.Grid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public abstract class Visualizer {

  protected static final int SIZE = 400;
  private static final String STYLESHEET = "default.css";

  protected Grid myGrid;
  private BorderPane bundle;
  protected ArrayList<ArrayList<Shape>> cellGrid;
  protected Map<Integer, Color> myColorMap;
  private LineChart<Number, Number> myGraph;
  private List<Series> mySeries;
  private long stepsElapsed;


  public Visualizer(Grid grid) {
    myGrid = grid;
    stepsElapsed = -1;
  }

  /**
   * Instantiates a grid of rectangles in a gridpane to be rendered by the scene. Takes color data
   * from the Grid class and uses it to create scaled rectangles at the correct size and dimension
   * and collects these rectangles into a gridpane.
   *
   * @return A gridpane containing all the rectangles in the simulation
   */
  public abstract Node instantiateCellGrid();

  private Node setGraph() {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("Steps");
    yAxis.setLabel("Population");

    myGraph = new LineChart<>(xAxis, yAxis);

    myGraph.applyCss();
    myGraph.setCreateSymbols(false);
    myGraph.getStylesheets()
        .add(getClass().getClassLoader().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET)
            .toExternalForm());
    myGraph.setTitle("Cell Population");

    mySeries = new ArrayList<XYChart.Series>();
    int[] populations = myGrid.getPopulations();
    for (int i = 0; i < populations.length; i++) {
      XYChart.Series tempSeries = new XYChart.Series<>();
      mySeries.add(tempSeries);
      myGraph.getData().add(tempSeries);
    }
    updateChart();
    return myGraph;
  }

  public void updateChart() {
    stepsElapsed += 1;
    for (int i = 0; i < mySeries.size(); i++) {
      XYChart.Data point = new XYChart.Data(stepsElapsed, myGrid.getPopulations()[i]);
      mySeries.get(i).getData().add(point);
      Set<Node> nodes = myGraph.lookupAll(".series"+i);
      for(Node series : nodes){
        StringBuilder style = new StringBuilder();
        style.append("-fx-stroke: " + "#"+myColorMap.get(i).toString().substring(2, 8)+"; ");
        style.append("-fx-background-color: "+"#"+myColorMap.get(i).toString().substring(2,8)+", white;");
        series.setStyle(style.toString());
      }
    }
  }

  public Node bundledUI() {
    bundle = new BorderPane();
    bundle.setCenter(instantiateCellGrid());
    bundle.setRight(setGraph());
    bundle.setBottom(setParamBar());
    return bundle;
  }

  public void stepGrid() {
    if(myGrid.update()){
      bundle.setCenter(instantiateCellGrid());
    }
    drawGrid();
  }

  public void drawGrid() {
    for (int i = 0; i < cellGrid.size(); i++) {
      for (int j = 0; j < cellGrid.get(i).size(); j++) {
        cellGrid.get(i).get(j).setFill(myColorMap.get(myGrid.getState(i, j)));
      }
    }
  }

//    public void setStateColor (int state, Color color){
//      myColorMap.put(state, color);
//    }

  public void setColorMap(Map<Integer, Color> newMap) {
    myColorMap = newMap;
  }

  protected Color[][] getColorGrid() {
    Color[][] colorgrid = new Color[myGrid.getHeight()][myGrid.getWidth()];
    for (int i = 0; i < colorgrid.length; i++) {
      for (int j = 0; j < colorgrid[i].length; j++) {
        colorgrid[i][j] = myColorMap.get(myGrid.getState(i, j));
      }
    }
    return colorgrid;
  }

  public void setGrid(Grid newGrid) {
    myGrid = newGrid;
  }

  private Node setParamBar() {
    HBox parameters = new HBox();
    String[] paramList = getParameters();
    for (String s : paramList) {
      TextField paramField = makeParamField(s);
      parameters.getChildren().add(paramField);
      final Pane spacer = new Pane();
      Label label = new Label(s);
      label.setMinWidth(50);
      parameters.getChildren().add(label);
      HBox.setHgrow(spacer, Priority.ALWAYS);
      parameters.getChildren().add(spacer);
    }
    return parameters;
  }

  private TextField makeParamField(String param) {
    TextField paramField = new TextField();
    paramField.setPrefColumnCount(50);
    paramField.setMaxWidth(50);
    paramField.setOnAction(e -> {
      if (paramField.getText() != null && !paramField.getText().isEmpty()) {
        double value = Double.parseDouble(paramField.getText());
        setParameters(param, value);
      } else {
        Alert errorAlert = new Alert(AlertType.WARNING);
        errorAlert.setHeaderText("Enter a valid double");
        errorAlert.setContentText("please");
        errorAlert.showAndWait();
      }
    });
    return paramField;
  }

  public String[] getParameters() {
    return myGrid.getParams();
  }

  public void setParameters(String param, double newValue) {
    myGrid.setParam(param, newValue);
  }

  public int[] getPopulations() {
    return myGrid.getPopulations();
  }

  public int getWidth() {
    return myGrid.getWidth();
  }

  public int getHeight() {
    return myGrid.getHeight();
  }
}
