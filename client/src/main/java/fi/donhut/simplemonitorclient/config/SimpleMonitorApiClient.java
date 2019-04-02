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
package fi.donhut.simplemonitorclient.config;

import fi.donhut.simplemonitorclient.generated.handler.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Simple Monitor http client.
 *
 * @author Nhut Do (mr.nhut@gmail.com)
 */
@Component
@Primary
public class SimpleMonitorApiClient extends ApiClient {

    @Autowired
    public SimpleMonitorApiClient(final AppConfig config) {
        super();
        this.setBasePath(config.getUrl());
        this.setUsername(config.getUsername());
        this.setPassword(config.getPassword());
    }

}
