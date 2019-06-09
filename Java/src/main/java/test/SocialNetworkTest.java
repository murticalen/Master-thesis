package main.java.test;

import main.java.configuration.Constants;
import main.java.dataset.util.AbstractReader;
import main.java.generator.Generator;

import java.io.IOException;
import java.util.*;

public class SocialNetworkTest extends AbstractReader {
    
    public static void main(String[] args) throws IOException {
        Map<Integer, Map<Integer, Double>> userConnections = new HashMap<>();
        readInputAndDoStuffNoSkip(Generator.SOCIAL_NETWORK, line -> {
            String[]             parts              = line.split(Constants.SEPARATOR);
            int                  caller             = Integer.parseInt(parts[0]);
            int                  receiver           = Integer.parseInt(parts[1]);
            double               connection         = Double.parseDouble(parts[2]);
            Map<Integer, Double> receiverConnection = userConnections.getOrDefault(caller, new HashMap<>());
            receiverConnection.put(receiver, connection);
            userConnections.put(caller, receiverConnection);
        });
        
        Queue<OrderedTuple> orderedTupleQueue = new PriorityQueue<>();
        
        for (Map.Entry<Integer, Map<Integer, Double>> userConnectedUsers : userConnections.entrySet()) {
            
            double sum = 0;
            
            for (Map.Entry<Integer, Double> connectedConnection : userConnectedUsers.getValue().entrySet()) {
                sum += connectedConnection.getValue();
            }
            
            orderedTupleQueue.add(new OrderedTuple(userConnectedUsers.getKey(), sum));
        }
        
        for (int i = 0; i < 100; i++) {
            System.out.println(orderedTupleQueue.poll());
        }
    }
    
    public static class OrderedTuple implements Comparable<OrderedTuple> {
        
        public int    user;
        public double connection;
        
        public OrderedTuple(int user, double connection) {
            this.user = user;
            this.connection = connection;
        }
        
        @Override
        public int compareTo(OrderedTuple o) {
            return Double.compare(connection, o.connection);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OrderedTuple that = (OrderedTuple)o;
            return user == that.user &&
                    Double.compare(that.connection, connection) == 0;
        }
        
        @Override
        public int hashCode() {
            
            return Objects.hash(user, connection);
        }
        
        @Override
        public String toString() {
            return user + ": " + connection;
        }
    }
    
}
