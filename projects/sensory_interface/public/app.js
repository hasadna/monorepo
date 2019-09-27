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
}

function fillRow(values, currentTr) {
    let myValue;
    for (myValue of values) {
        let td = document.createElement("td");
        td.appendChild(document.createTextNode(myValue));
        currentTr.appendChild(td);
    }
}