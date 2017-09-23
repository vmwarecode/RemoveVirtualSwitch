/*
 * ****************************************************************************
 * Copyright VMware, Inc. 2010-2016.  All Rights Reserved.
 * ****************************************************************************
 *
 * This software is made available for use under the terms of the BSD
 * 3-Clause license:
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.vmware.host;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.*;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.Map;

/**
 * <pre>
 * RemoveVirtualSwitch
 *
 * This sample removes a virtual switch
 *
 * <b>Parameters:</b>
 * url             [required] : url of the web service
 * username        [required] : username for the authentication
 * password        [required] : password for the authentication
 * vswitchid       [required] : Name of the switch to be added
 * hostname        [required] : Name of the host
 *
 * <b>Command Line:</b>
 * Remove a Virtual Switch
 * run.bat com.vmware.host.RemoveVirtualSwitch --url [webserviceurl]
 * --username [username] --password  [password]
 * --vswitchid [mySwitch] --hostname [hostname]
 * </pre>
 */

@Sample(name = "remove-virtual-switch", description = "removes a virtual switch")
public class RemoveVirtualSwitch extends ConnectedVimServiceBase {
    private String host;
    private String virtualswitchid;

    @Option(name = "hostname", description = "Name of the host")
    public void setHost(String host) {
        this.host = host;
    }

    @Option(name = "vswitchid", description = "Name of the switch to be added")
    public void setVirtualswitchid(String virtualswitchid) {
        this.virtualswitchid = virtualswitchid;
    }

    void removeVirtualSwitch() throws InvalidPropertyFaultMsg,
            RuntimeFaultFaultMsg {
        Map<String, ManagedObjectReference> hostList =
                getMOREFs.inFolderByType(serviceContent.getRootFolder(),
                        "HostSystem");
        ManagedObjectReference hostmor = hostList.get(host);
        if (hostmor != null) {
            try {
                HostConfigManager configMgr =
                        (HostConfigManager) getMOREFs.entityProps(hostmor,
                                new String[]{"configManager"}).get("configManager");
                ManagedObjectReference nwSystem = configMgr.getNetworkSystem();
                vimPort.removeVirtualSwitch(nwSystem, virtualswitchid);
                System.out.println(" : Successful removing : " + virtualswitchid);
            } catch (HostConfigFaultFaultMsg ex) {
                System.out.println(" : Failed : Configuration falilures. ");
            } catch (NotFoundFaultMsg ex) {
                System.out.println("Failed : " + ex);
            } catch (ResourceInUseFaultMsg ex) {
                System.out.println(" : Failed removing switch " + virtualswitchid);
                System.out.println("There are virtual network adapters "
                        + "associated with the virtual switch.");
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println(" : Failed removing switch: " + virtualswitchid);
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            }
        } else {
            System.out.println("Host not found");
        }
    }

    void printSoapFaultException(SOAPFaultException sfe) {
        System.out.println("SOAP Fault -");
        if (sfe.getFault().hasDetail()) {
            System.out.println(sfe.getFault().getDetail().getFirstChild()
                    .getLocalName());
        }
        if (sfe.getFault().getFaultString() != null) {
            System.out.println("\n Message: " + sfe.getFault().getFaultString());
        }
    }

    @Action
    public void run() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        removeVirtualSwitch();
    }

}
