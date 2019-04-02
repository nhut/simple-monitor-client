/*
 * Copyright since 2019 Nhut Do <mr.nhut@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.donhut.simplemonitorclient.scheduler;

import fi.donhut.simplemonitorclient.config.SimpleMonitorApiClient;
import fi.donhut.simplemonitorclient.generated.api.ComputerControllerApi;
import fi.donhut.simplemonitorclient.generated.model.Computer;
import fi.donhut.simplemonitorclient.util.DataCollectorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Nhut Do (mr.nhut@gmail.com)
 */
@Configuration
public class StatusScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(StatusScheduler.class);

    private ComputerControllerApi computerControllerApi;

    @Autowired
    public StatusScheduler(SimpleMonitorApiClient client) {
        computerControllerApi = new ComputerControllerApi(client);
    }

    @Scheduled(fixedDelay = 10000)
    public void sendStatusTask() {
        LOG.debug("Sending data...");
        try {
            final Computer computer = DataCollectorUtil.getComputer();
            computerControllerApi.receivePcDataUsingPUT(computer);
            LOG.info("Data sent... {}", computer.toString());

        } catch (UnknownHostException e) {
            LOG.error("Unable to resolve hostname: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Fail to send heartbeat. Reason: {}", e.getMessage());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Send fail print stack:", e);
            }
        }
    }


}
