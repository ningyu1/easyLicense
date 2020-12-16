package io.ningyu.license;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义需要校验的License参数
 *
 * @author ningyu
 */
public class LicenseCheckModel implements Serializable {

    private static final long serialVersionUID = 8600137500316662317L;
    /**
     * 可被允许的IP地址
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private List<String> macAddress;

    /**
     * 可被允许的CPU序列号
     */
    private List<String> cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    private List<String> mainBoardSerial;

    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }

    public List<String> getCpuSerial() {
        return cpuSerial;
    }

    public void setCpuSerial(List<String> cpuSerial) {
        this.cpuSerial = cpuSerial;
    }

    public List<String> getMainBoardSerial() {
        return mainBoardSerial;
    }

    public void setMainBoardSerial(List<String> mainBoardSerial) {
        this.mainBoardSerial = mainBoardSerial;
    }

    @Override
    public String toString() {
        return "LicenseCheckModel{" +
                "ipAddress=" + ipAddress +
                ", macAddress=" + macAddress +
                ", cpuSerial=" + cpuSerial +
                ", mainBoardSerial=" + mainBoardSerial +
                '}';
    }

    public void copyTo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        //cpu序列号
        setCpuSerial(Arrays.asList(hal.getProcessor().getProcessorIdentifier().getProcessorID()));
        List<NetworkIF> list = hal.getNetworkIFs();
        List<String> ownIpv4Adds = new ArrayList<>();
        List<String> macAdds = new ArrayList<>();
        for (NetworkIF networkIF : list) {
            ownIpv4Adds.addAll(Arrays.asList(networkIF.getIPv4addr()));
            macAdds.add(networkIF.getMacaddr());
        }
        //ip地址
        setIpAddress(ownIpv4Adds);
        //mac地址
        setMacAddress(macAdds);
        //主板序列号
        setMainBoardSerial(Arrays.asList(hal.getComputerSystem().getBaseboard().getSerialNumber()));
    }
}
