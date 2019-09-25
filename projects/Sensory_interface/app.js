function processData() {
    var input = document.getElementById("textInput").value;
    var lines = input.split("\n");
    var table = document.createElement("table");
    table.style.width = "100%";
    table.setAttribute("border", "1");
    var line;
    for (line of lines) {
        var tr = document.createElement("tr");
        var values = line.split("\t");
        fillRow(values, tr);
        table.appendChild(tr);
    }
    
    document.getElementById("tableContainer").appendChild(table);
}

function fillRow(values, currentTr) {
    var myValue;
    for (myValue of values) {
        var td = document.createElement("td");
        td.appendChild(document.createTextNode(myValue));
        currentTr.appendChild(td);
    }
}