package agents.buyers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class BuyerContainer extends Application {
    protected BuyerAgent agent;
    protected ObservableList<String> data = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Buyer Interface");
        BorderPane borderPane = new BorderPane();
        ListView<String> lvOrders = new ListView<>(data);
        lvOrders.setPadding(new Insets(10));
        borderPane.setCenter(lvOrders);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, "Buyers");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController controller = container.createNewAgent("Buyer", BuyerAgent.class.getName(), new Object[]{this});
        controller.start();
    }
    protected void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            data.add(aclMessage.getSender().getLocalName()+
                    " :\t"+aclMessage.getContent());
        });
    }
}
