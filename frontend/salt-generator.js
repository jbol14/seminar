//Erzeugt einen zufälligen String mit saltLength Zeichen
function saltGenerator(saltLength = 16){
    
    // TODO: Sicherstellen dass kein Backslash dabei ist

    // Obere- und untere Grenze, druckbare ASCII Zeichen
    const lowerBound = 33;
    const upperBound = 125;

    // Übergabeparameter überprüfen
    //Sicherstellen dass saltLength eine Zahl ist und mind. 16 ist
    if(typeof saltLength !== "number" || saltLength < 16){
        saltLength = 16;
    }

    // abrunden, um sicher zu stellen dass saltLength eine ganze Zahl ist
    return new Array(Math.floor(saltLength))
    // Array mit Nullen auffüllen
    .fill(0)
    // Zufallszahlen erzeugen und in ASCII Zeichen umwandeln
    .map(x => String.fromCharCode(Math.floor(lowerBound + Math.random()*(upperBound - lowerBound))))
    // Einzelne Zeichen konkatenieren
    .reduce((str,char)=>{return str + char});
}

