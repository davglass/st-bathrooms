/**
 *  Control the bathrooms
 *
 *  Copyright 2015 Dav Glass
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
 */
definition(
    name: "Control the bathrooms",
    namespace: "davglass",
    author: "Dav Glass",
    description: "Mega app for the bathrooms",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


preferences {
    section("Lights that need to be on for motion?") {
        input "lightsMotionOn", "capability.switch", multiple: true, required: true
    }
    section("Lights that need to be off for no motion?") {
        input "lightsMotionOff", "capability.switch", multiple: true, required: true
    }
    section("Lights that need to be on for door closed?") {
        input "lightsDoorClosed", "capability.switch", multiple: true, required: true
    }
    section("Lights that need to be off for door open?") {
        input "lightsDoorOpen", "capability.switch", multiple: true, required: true
    }
    section("Which Door Sensor?") {
        input "door", "capability.contactSensor", multiple: false, required: true
    }
    section("Which Motion Sensor?") {
        input "motion", "capability.motionSensor", multiple: false, required: true
    }
    section("Delay off in seconds after no motion?") {
        input "secondsMotion", "number", title: "Seconds to delay", required: true
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    unsubscribe()
    unschedule()
    initialize()
}

def initialize() {
    subscribe(door, "contact", doorHandler)
    subscribe(motion, "motion", motionHandler)
    subscribe(lightsMotionOn, "switch", switchHandler);
    state.switchOff = false;
}

def resetState() {
    state.switchOff = false;
}

def switchHandler(evt) {
    log.debug "${evt.displayName} is ${evt.value}"
    if (evt.value == "off" && evt.isPhysical()) {
        //Light was turned off by the switch
        state.switchOff = true;
        runIn(60, resetState);
    }
}

def doorHandler(evt) {
    log.debug "${evt.displayName} is ${evt.value}"
    if (evt.value == "open") {
        doorOff()
        runIn(secondsMotion, motionOff)
    }
    if (evt.value == "closed") {
        unschedule("motionOff")
        doorOn()
    }
}

def motionHandler(evt) {
    def doorState = door.currentValue("contact")
    log.debug "${evt.displayName} is ${evt.value}"
    log.trace "doorState = ${doorState}"

    if (doorState == "open" && evt.value == "active") {
        if (!state.switchOff) {
            motionOn()
        }
    }
    if (doorState == "open" && evt.value == "inactive") {
        log.debug "Door is open & motion is inactive, delaying off for ${secondsMotion} seconds"
        runIn(secondsMotion, motionOff)
    }
}
def doorOff() {
    log.debug "Turning off lights (door)"
    lightsDoorOpen.each {
        log.debug "Turning off ${it.displayName}"
        it.off()
    }
}

def doorOn() {
    log.debug "Turning on lights (door)"
    lightsDoorClosed.each {
        log.debug "Turning on ${it.displayName}"
        it.on()
    }
}

def motionOn() {
    log.debug "Turning on lights (motion)"
    lightsMotionOn.each {
        log.debug "Turning on ${it.displayName}"
        it.on()
    }
}

def motionOff() {
    def doorState = door.currentValue("contact")
    log.trace "doorState = ${doorState}"
    if (doorState == "open") {
        log.debug "Turning off lights (motion)"
        lightsMotionOff.each {
            log.debug "Turning off ${it.displayName}"
            it.off()
        }
    } else {
        log.debug "Door is closed, skipping motion detection."
    }
}
