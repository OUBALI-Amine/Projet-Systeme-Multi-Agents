package agents.customers;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;

public class CustomerAgent extends GuiAgent {

    private CustomerContainer container;
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if(guiEvent.getType()==1){
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.addReceiver(new AID("Buyer",AID.ISLOCALNAME));
            message.setContent((String) guiEvent.getParameter(0));
            send(message);
        }
    }

    @Override
    protected void setup() {
        if(this.getArguments()!=null){
            container = (CustomerContainer) this.getArguments()[0];
            container.agent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        this.addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if(message!=null){
                    //container.logMessage(message);
                    // ON PEUT UTILISER CETTE MANIERE
                    switch(message.getPerformative()){
                        case ACLMessage.CONFIRM:
                            Platform.runLater(()->{
                                container.data.add(message.getSender().getLocalName()+
                                        " "+message.getContent());
                            });
                            break;
                    }
                }else{
                    block();
                }
            }
        });
    }

}
