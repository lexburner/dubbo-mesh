package com.alibaba.dubbo.performance.demo.agent.cluster.loadbalance;

import com.alibaba.dubbo.performance.demo.agent.rpc.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.rpc.Request;
import com.alibaba.dubbo.performance.demo.agent.transport.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 徐靖峰
 * Date 2018-05-19
 */
public class WeightRoundRobinLoadBalance implements LoadBalance {

    private Logger logger = LoggerFactory.getLogger(WeightRoundRobinLoadBalance.class);

    private volatile ClientsHolder clientsHolder;

    public WeightRoundRobinLoadBalance() {
    }

    /**
     * 为考虑比赛性能不考虑溢出
     */
    class ClientsHolder {

        private int randomKeySize;
        private List<Endpoint> randomKeyList = new ArrayList<>();
        AtomicInteger cursor = new AtomicInteger(0);
        private Map<Endpoint, Client> endpointClientMap = new HashMap<>();

        ClientsHolder(List<Client> clients) {
            logger.info("WeightRoundRobinLoadBalance build new ClientsHolder. weights:" + clients);
            for (Client client : clients) {
                endpointClientMap.put(client.getEndpoint(), client);
            }
            List<Integer> weightsArr = clients.stream().map(Client::getEndpoint).map(Endpoint::getWeight).collect(Collectors.toList());
            // 求出最大公约数，若不为1，对权重做除法
            int weightGcd = findGcd(weightsArr.toArray(new Integer[]{}));
            if (weightGcd != 1) {
                for (Endpoint endpoint : endpointClientMap.keySet()) {
                    endpoint.setWeight(endpoint.getWeight() / weightGcd);
                }
            }
            for (Endpoint endpoint : endpointClientMap.keySet()) {
                for (int i = 0; i < endpoint.getWeight(); i++) {
                    randomKeyList.add(endpoint);
                }
            }
            Collections.shuffle(randomKeyList);
            randomKeySize = randomKeyList.size();
        }

        Client next() {
            Endpoint endpoint = randomKeyList.get(Math.abs(cursor.getAndAdd(1)) % randomKeySize);
            return endpointClientMap.get(endpoint);
        }

        // 求最大公约数
        private int findGcd(int n, int m) {
            return (n == 0 || m == 0) ? n + m : findGcd(m, n % m);
        }

        // 求最大公约数
        private int findGcd(Integer[] arr) {
            if (arr.length == 1) return arr[0];
            int i = 0;
            for (; i < arr.length - 1; i++) {
                arr[i + 1] = findGcd(arr[i], arr[i + 1]);
            }
            return findGcd(arr[i], arr[i - 1]);
        }
    }

    @Override
    public Client select(Request request) {
        return clientsHolder.next();
    }

    @Override
    public void onRefresh(List<Client> clients) {
        this.clientsHolder = new ClientsHolder(clients);
    }

}
