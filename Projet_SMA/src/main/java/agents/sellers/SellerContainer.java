package agents.sellers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;

public class SellerContainer extends Application {
    protected SellerAgent agent;
    private AgentContainer container;
    protected ObservableList<String> data;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Seller Interface");
        BorderPane borderPane = new BorderPane();
        HBox hBox= new HBox();
        Label label = new Label("Seller Name :\t");
        javafx.scene.control.TextField txtSellerName = new javafx.scene.control.TextField();
        javafx.scene.control.Button button = new javafx.scene.control.Button("Deploy");
        hBox.getChildren().addAll(label, txtSellerName, button);
        borderPane.setTop(hBox);
        data = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(data);
        listView.setPadding(new Insets(10));
        borderPane.setCenter(listView);
        button.setOnAction(event -> {
            try {
                AgentController controller = container.createNewAgent(txtSellerName.getText(), SellerAgent.class.getName(), new Object[]{this});
                controller.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        });
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, "Sellers");
        container = runtime.createAgentContainer(profile);
        container.start();
    }
    protected void logMessage(ACLMessage aclMessage){
        data.add(aclMessage.getContent());
    }

}
