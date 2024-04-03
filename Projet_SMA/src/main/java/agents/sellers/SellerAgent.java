package agents.sellers;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class SellerAgent extends GuiAgent {
    protected SellerContainer container;

    @Override
    protected void setup() {
        if(this.getArguments()!=null){
            container = (SellerContainer) this.getArguments()[0];
            container.agent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        this.addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                dfAgentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transition");
                serviceDescription.setName("Sell-Books");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, dfAgentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if(message!=null){
                    ACLMessage message1;
                    switch (message.getPerformative()){
                        case ACLMessage.CFP:
                            message1 = message.createReply();
                            message1.setPerformative(ACLMessage.PROPOSE);
                            message1.setContent(String.valueOf(new Random().nextInt(1000)));
                            send(message1);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            message1 = message.createReply();
                            message1.setPerformative(ACLMessage.AGREE);
                            message1.setContent(message.getContent());
                            send(message1);
                    }
                    container.logMessage(message);
                }else{
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
