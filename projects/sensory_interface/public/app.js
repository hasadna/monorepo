// initialize Audio context on page load.
let AudioContext = window.webkitAudioContext || window.AudioContext;
let audioContext = new AudioContext();
let oscillator = null;
// This variable is not used currently.
let source = null;
let currentCellUnderTouchPoint = null;
let timeOut = null;

function processData() {
    document.getElementById("updateButton").disabled = false;
    let input = document.getElementById("dataInput").value;
    let lines = input.split("\n");
    let table = document.createElement("table");
    table.style.width = "100%";
    table.style.height = "70%";
    table.setAttribute("border", "1");
    let line;
    for (line of lines) {
        let tr = document.createElement("tr");
        let values = line.split("\t");
        fillRow(values, tr);
        table.appendChild(tr);
    }
    let oldTable = document.getElementById("tableContainer").firstChild;
    if (oldTable != null) {
        document.getElementById("tableContainer").removeChild(oldTable);
    }
    document.getElementById("tableContainer").appendChild(table);
    addOnClickAndTouchSoundToTable();
    if (document.getElementById("autoOption").checked) {
        findMinAndMaxValues();
    }
}

function fillRow(values, currentTr) {
    let myValue;
    for (myValue of values) {
        let td = document.createElement("td");
        td.appendChild(document.createTextNode(myValue));
        currentTr.appendChild(td);
    }
}

function addOnClickAndTouchSoundToTable() {
    let element;
    for (element of document.getElementsByTagName("td")) {
        element.addEventListener("click", startSoundPlaybackOnClick);
        element.addEventListener("touchstart", startSoundPlaybackOnClick);
        element.addEventListener("touchmove", onCellChange);
        element.addEventListener("touchleave", stopSoundPlayback);
        element.addEventListener("touchcancel", stopSoundPlayback);
    }
}

function startSoundPlaybackOnClick(event) {
    currentCellUnderTouchPoint = event.currentTarget;
    event.preventDefault();
    stopSoundPlayback(event);
    let selectedValue = event.currentTarget.firstChild.data;
    startSoundPlayback(selectedValue);
}

function startSoundPlayback(selectedValue) {
    if (audioContext.state == "suspended") {
        audioContext.resume();
    }
    oscillator = audioContext.createOscillator();
    const MAX_FREQUENCY = 1000;
    const MIN_FREQUENCY = 100;
    let minValue = document.getElementById("minValue").value;
    let maxValue = document.getElementById("maxValue").value;
    maxValue = parseInt(maxValue);
    minValue = parseInt(minValue);
    selectedValue = parseInt(selectedValue);
    if (selectedValue > maxValue) {
        selectedValue = maxValue;
    }
    if (selectedValue < minValue) {
        selectedValue = minValue;
    }
    let frequency = MIN_FREQUENCY + (selectedValue - minValue) / (maxValue - minValue) * (MAX_FREQUENCY - MIN_FREQUENCY);
    oscillator.frequency.value = frequency;
    oscillator.connect(audioContext.destination);
    oscillator.start(audioContext.currentTime);
    timeOut = setTimeout(() => {
        stopSoundPlayback(event);
    }, 1000);
}

// This function is not used currently.
function playAudioFile() {
    let request = new XMLHttpRequest();
    request.open("get", "assets/beep_digital.mp3", true);
    request.responseType = "arraybuffer";
    request.onload = function () {
        let data = request.response;
        playSoundFromData(data);
    };
    request.send();
}

// This function is not used currently.
function playSoundFromData(data) {
    source = audioContext.createBufferSource();
    audioContext.decodeAudioData(data, function (buffer) {
        source.buffer = buffer;
        source.connect(audioContext.destination);
        playSoundFromBufferSource();
    });
}

// This function is not used currently.
function playSoundFromBufferSource() {
    source.start(audioContext.currentTime);
}

function stopSoundPlayback(event) {
    try {
        if (oscillator != null) {
            oscillator.stop(audioContext.currentTime);
        }
        if (timeOut != null) {
            window.clearTimeout(timeOut);
        }
        if (event != undefined) {
            event.preventDefault();
        }
    } catch (e) {
        console.log(e);
    }
}

function onCellChange(event) {
    // Get the first changed touch point. We surely have one because we are listening to touchmove event, and surely a touch point have changed since the last event.
    let changedTouch = event.changedTouches[0];
    let elementUnderTouch = document.elementFromPoint(changedTouch.clientX, changedTouch.clientY);
    if (elementUnderTouch == currentCellUnderTouchPoint) {
        return;
    }
    if (elementUnderTouch == null || elementUnderTouch.tagName != "TD") {
        return;
    }
    currentCellUnderTouchPoint = elementUnderTouch;
    stopSoundPlayback(event);
    let selectedValue = elementUnderTouch.firstChild.data;
    startSoundPlayback(selectedValue);
    event.stopPropagation();
}

function onRadioChange(radio) {
    let minValuePicker = document.getElementById("minValue");
    let maxValuePicker = document.getElementById("maxValue");
    if (radio.value == "auto") {
        minValuePicker.disabled = true;
        maxValuePicker.disabled = true;
    } else {
        minValuePicker.disabled = false;
        maxValuePicker.disabled = false;
    }
    findMinAndMaxValues();
}

function findMinAndMaxValues() {
    let input = document.getElementById("dataInput").value;
    let maxValue = -Infinity;
    let minValue = Infinity;
    let lines = input.split("\n");
    let line;
    for (line of lines) {
        let values = line.split("\t");
        let value;
        for (value of values) {
            if (value > maxValue) {
                maxValue = value;
            }
            if (value < minValue) {
                minValue = value;
            }
        }
    }
    document.getElementById("maxValue").value = maxValue;
    document.getElementById("minValue").value = minValue;
}