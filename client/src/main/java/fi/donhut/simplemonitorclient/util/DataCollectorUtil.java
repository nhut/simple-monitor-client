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
package fi.donhut.simplemonitorclient.util;

import com.sun.management.OperatingSystemMXBean;
import fi.donhut.simplemonitorclient.generated.model.Computer;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Data collector utility class.
 *
 * @author Nhut Do (mr.nhut@gmail.com)
 */
public class DataCollectorUtil {

    private DataCollectorUtil() {
    }

    public static Computer getComputer() throws UnknownHostException {
        final Computer pc = new Computer();
        pc.setName(getHostName());
        pc.setCpuLoadPercent(getOperatingSystemBean().getSystemCpuLoad()*100.00);
        pc.setIpAddress(InetAddress.getLocalHost().toString());
        pc.setMemoryUsageInBytes(getUsedMemory());
        pc.setFreeSpaceLeftInBytes(getDriveFreeSpace());
        return pc;
    }

    private static String getHostName() throws UnknownHostException {
        final InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostName();
    }

    private static OperatingSystemMXBean getOperatingSystemBean() {
        return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    private static long getUsedMemory() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
    }

    private static long getDriveFreeSpace() {
        return new File("/").getFreeSpace();
    }
}
