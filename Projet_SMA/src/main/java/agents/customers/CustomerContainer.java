package agents.customers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CustomerContainer extends Application {
    protected CustomerAgent agent;
    protected ObservableList<String> data = FXCollections.observableArrayList();

    public static void main(String[] args) throws StaleProxyException {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Costumer Interface");
        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();
        Label lblBookName = new Label("Book Name :\t");
        TextField txtBookName = new TextField();
        Button btnSend = new Button("Send");
        hBox.getChildren().addAll(lblBookName, txtBookName, btnSend);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        borderPane.setTop(hBox);
        ListView<String> lvMessages = new ListView<>(data);
        borderPane.setCenter(lvMessages);
        btnSend.setOnAction(event -> {
            GuiEvent evt = new GuiEvent(this, 1);
            evt.addParameter(txtBookName.getText());
            agent.onGuiEvent(evt);
        });

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }


    void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, "Customers");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController controller = container.createNewAgent("Customer 1", CustomerAgent.class.getName(), new Object[]{this});
        controller.start();
    }

    protected void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            data.add(aclMessage.getSender().getLocalName()+
                    " "+aclMessage.getContent());
        });
    }
}
