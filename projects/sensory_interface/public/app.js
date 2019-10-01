// initialize Audio context on page load.
let AudioContext = window.webkitAudioContext || window.AudioContext;
let audioContext = new AudioContext();

function processData() {
    let input = document.getElementById("textInput").value;
    let lines = input.split("\n");
    let table = document.createElement("table");
    table.style.width = "100%";
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
    addOnClickSoundToTable();
}

function fillRow(values, currentTr) {
    let myValue;
    for (myValue of values) {
        let td = document.createElement("td");
        td.appendChild(document.createTextNode(myValue));
        currentTr.appendChild(td);
    }
}

function addOnClickSoundToTable() {
    let element;
    for (element of document.getElementsByTagName("td")) {
        element.addEventListener("click", startSoundPlayback);
    }
}

function startSoundPlayback() {
    if (audioContext.state == "suspended") {
        audioContext.resume();
    }
    let request = new XMLHttpRequest();
    request.open("get", "assets/beep_digital.mp3", true);
    request.responseType = "arraybuffer";
    request.onload = function () {
        let data = request.response;
        playSoundFromData(data);
    };
    request.send();
}

function playSoundFromData(data) {
    let source = audioContext.createBufferSource();
    audioContext.decodeAudioData(data, function (buffer) {
        source.buffer = buffer;
        source.connect(audioContext.destination);
        playSoundFromBufferSource(source);
    });
}

function playSoundFromBufferSource(source) {
    source.start(audioContext.currentTime);
}

// Currently, this function is not used, we may need it in the future though.
function stopSoundPlayback(source) {
    source.stop(audioContext.currentTime);
}