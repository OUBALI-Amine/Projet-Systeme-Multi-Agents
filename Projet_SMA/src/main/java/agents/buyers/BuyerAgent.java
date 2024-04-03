package agents.buyers;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class BuyerAgent extends GuiAgent {
    private BuyerContainer container;
    private AID[] sellers;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        if(this.getArguments()!=null){
            container = (BuyerContainer) this.getArguments()[0];
            container.agent = this;
        }

        ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
        this.addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transition");
                serviceDescription.setName("Sell-Books");
                dfAgentDescription.addServices(serviceDescription);

                try {
                    DFAgentDescription[] offers = DFService.search(myAgent, dfAgentDescription);
                    sellers = new AID[offers.length];
                    for(int i=0; i<sellers.length; i++){
                        sellers[i] = offers[i].getName();
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            ArrayList<ACLMessage> messages = new ArrayList<>();
            @Override
            public void action() {
                ACLMessage message = receive();
                if(message != null){
                    ACLMessage message1;
                    switch(message.getPerformative()){
                        case ACLMessage.REQUEST:
                            message1 = new ACLMessage(ACLMessage.CFP);
                            message1.setContent(message.getContent());
                            for(AID aid:sellers){
                                message1.addReceiver(aid);
                            }
                            send(message1);
                            break;
                        case ACLMessage.PROPOSE:
                            messages.add(message);
                            if(sellers.length==messages.size()){
                                ACLMessage message2 = messages.get(0);
                                double min= Double.parseDouble(messages.get(0).getContent());
                                for (ACLMessage offre:messages){
                                    double price= Double.parseDouble(offre.getContent());
                                    if(price<min){
                                        min=price;
                                        message2 = offre;
                                    }
                                }
                                ACLMessage response = message2.createReply();
                                response.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                response.setContent(message2.getContent());
                                send(response);
                            }
                            break;
                        case ACLMessage.AGREE:
                            message1 = new ACLMessage(ACLMessage.CONFIRM);
                            message1.addReceiver(new AID("Customer 1", AID.ISLOCALNAME));
                            message1.setContent(message.getContent());
                            send(message1);
                            break;
                        case ACLMessage.REFUSE:
                            break;
                    }
                    container.logMessage(message);
                        //Response to Customer
                    /*ACLMessage response = message.createReply();
                    response.setContent("is searching for "+message.getContent());
                    response.setPerformative(ACLMessage.INFORM);
                    send(response);*/
                        //Message to Seller
                    /*ACLMessage message2 = new ACLMessage(ACLMessage.CFP);
                    message2.setContent(message.getContent());
                    message2.addReceiver(new AID("Seller", AID.ISLOCALNAME));
                    send(message2);*/
                }else{
                    block();
                }
            }
        });
    }
}
