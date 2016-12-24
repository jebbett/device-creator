/**
 *  Device Creator
 *
 *  Copyright 2016 Jake Tebbett
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * 
 * VERSION CONTROL
 * ###############
 *
 *  v1.00 - Initial Version
 *
 */

definition(
    name: "Device Creator",
    namespace: "jebbett",
    author: "Jake Tebbett",
    description: "Create Devices Without IDE",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/jebbett/device-creator/master/icons/switcher.png",
    iconX2Url: "https://raw.githubusercontent.com/jebbett/device-creator/master/icons/switcher.png",
    iconX3Url: "https://raw.githubusercontent.com/jebbett/device-creator/master/icons/switcher.png",
)


def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
	state.installed = true
}

preferences {
    page name: "pageDevice"
    page name: "pageDevDetails"
    page name: "pageDevDelete"
    page name: "pageDevAdd"
}

private getSortedDevices() {
	return getChildDevices().sort{it.displayName}
}

def pageDevice() {
    dynamicPage(name: "pageDevice", title: "Create Device", install: true, uninstall: false) {        
        section() {
          def childDevs = []
          def i = 1 as int
            getSortedDevices().each { dev ->
                href(name: "pageDevDetails$i", title:"$dev.label", description: "", params: [devId: dev.deviceNetworkId, devLabel: dev.label], page: "pageDevDetails", state: "complete", required: false, image: "https://cdn0.iconfinder.com/data/icons/round-ui-icons/128/setting_blue.png")
                i++
        	}
        }
        section(){
            href(name: "pageDevDetails", title:"Create New Device", description: "Please ensure the device type is installed under My Device Handlers", params: [devi: false], page: "pageDevDetails", image: "https://cdn0.iconfinder.com/data/icons/round-ui-icons/128/settings_red.png")
        }
    }
}

def pageDevDetails(params) {
    dynamicPage(name: "pageDevDetails", title: "Device Details", install: false, uninstall: false) {
		if(params.devLabel){
			section("Details") {
        		paragraph("Label: ${params.devLabel}\nID: ${params.devId}")
            }
            section("DELETE") {
            	href(name: "pageDevDelete", title:"DELETE DEVICE", description: "ONLY PRESS IF YOU ARE SURE!", params: [devId: "$params.devId"], page: "pageDevDelete", required: false, image: "https://cdn0.iconfinder.com/data/icons/round-ui-icons/128/close_red.png")
        	}
   		}else{
       		section() {
        		paragraph("Create A New Device")
                input "devName", type: "text", title: "Name:", required:false, submitOnChange: true, defaultValue: "New Device"
            	input "newDevType", "enum", title: "Device Type", description: "", required: true, submitOnChange: true, options: devTypesList()
            	href(name: "pageDevAdd", title:"Create Device", description: "", params: [devId: "$params.devId"], page: "pageDevAdd", required: false, image: "https://cdn0.iconfinder.com/data/icons/round-ui-icons/128/add_green.png")
        	}
		}        
   }
}

def pageDevAdd(params) {
	def DeviceID = "Virtual:"+settings.devName
	def existingDevice = getChildDevice(DeviceID)
    if(settings.devName && !existingDevice){
        def newDev = addChildDevice(settings.newDevType.split(":")[0], settings.newDevType.split(":")[1], DeviceID, null, [name: settings.devName, label: settings.devName])
        pageDevice()
	}else{
    	dynamicPage(name: "pageDevAdd", title: "Device Details", install: false, uninstall: false) {        
			section() {
            	paragraph("Name not set or already in use")
        	}
		}
	}
}

def pageDevDelete(params) {
    deleteChildDevice(params.devId)
	pageDevice()
}

def devTypesList(){
	return [
    	// Add your devices here, no space either side of ":"
    	"smartthings:Momentary Button Tile",
        "smartthings:On/Off Button Tile",
        "statusbits:VLC Thing"
	]
}